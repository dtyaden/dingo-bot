package dingov2.bot.services;

import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageContent;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DingoOpenAIQueryService implements OpenAIQueryService {

    private String apiKey;
    public static final List<String> runningStates = Arrays.asList("queued", "in_progress");
    public static final List<String> failedStates = Arrays.asList("requires_action", "failed", "expired", "cancelled", "cancelling");
    private Logger logger;
    private HashMap<String, String> userToThreadId = new HashMap<>();
    private HashMap<String, String> threadIdToLastMessageId = new HashMap<>();

    public DingoOpenAIQueryService(String apiKey){
        this.apiKey = apiKey;
        logger = LoggerFactory.getLogger(DingoOpenAIQueryService.class);
    }

    //Lazy testing method lmao
    public static void main(String[] args){
        Logger logger2 = LoggerFactory.getLogger(DingoOpenAIQueryService.class);

        var service = new DingoOpenAIQueryService("");
        var testWrapper = new DingoEventWrapper() {
            @Override
            public Mono<Void> reply(Object content) {
                return null;
            }

            @Override
            public List<Attachment> getAttachments() {
                return null;
            }

            @Override
            public Mono<MessageChannel> getChannel() {
                return null;
            }

            @Override
            public Optional<Member> getMember() {
                return Optional.empty();
            }

            @Override
            public Instant getTimestamp() {
                return null;
            }
        };
        service.sendChatMessage("reply to this message with \"this is the first message\"", testWrapper).subscribe();
        service.sendChatMessage("reply to this message with \"this is the second message\"", testWrapper).subscribe();
        service.sendChatMessage("reply to this message with \"this is the third message\"", testWrapper).subscribe();
        service.sendChatMessage("reply to this message with \"this is the fourth message\"", testWrapper).subscribe();
        service.sendChatMessage("reply to this message with \"this is the fifth message\"", testWrapper).subscribe();
    }

    /**
     * Query the open ai service for up to 2 messages that aren't from the user, starting from the given message id.
     * @param service
     * @param thread
     * @param searchStartMessageId
     * @return
     */
    private List<Message> getMessagesForRange(OpenAiService service, Thread thread, String searchStartMessageId){
        return service.listMessages(thread.getId(), ListSearchParameters.builder().before(searchStartMessageId).limit(2).build())
                .getData()
                .stream()
                .filter(message -> !message.getRole().equalsIgnoreCase("user"))
                .collect(Collectors.toList());
    }

    /**
     * continuously poll openai for messages from a thread 2 at a time
     * @param service
     * @param thread
     * @return
     */
    private List<Message> getMessages(OpenAiService service, Thread thread){
        String cachedLastReceivedMessageId = threadIdToLastMessageId.getOrDefault(thread.getId(), "");
        List<Message> allNewMessagesFromOpenAI = new ArrayList<>();
        var messages = getMessagesForRange(service, thread, cachedLastReceivedMessageId);
        while(!messages.isEmpty()){
            allNewMessagesFromOpenAI.addAll(messages);
            cachedLastReceivedMessageId = messages.get(messages.size()-1).getId(); //update cached id to new starting point
            messages = getMessagesForRange(service, thread, cachedLastReceivedMessageId);
        }
        // cached final message id received
        threadIdToLastMessageId.put(thread.getId(), cachedLastReceivedMessageId);
        return allNewMessagesFromOpenAI;
    }

    private String getMemberId(DingoEventWrapper event){
        if(event.getMember().isPresent()){
            return event.getMember().get().getId().asString();
        }
        return "default";
    }

    private Thread getOrCreateThread(String memberSnowflakeId, OpenAiService service, Assistant assistant){
        // every event should have a member

        if(userToThreadId.containsKey(memberSnowflakeId)){
            return service.retrieveThread(userToThreadId.get(memberSnowflakeId));
        }
        ThreadRequest threadRequest = ThreadRequest.builder()
                .build();
        Thread newThread = service.createThread(threadRequest);
        userToThreadId.put(memberSnowflakeId, newThread.getId());
        return newThread;
    }

    @Override
    public Flux<String> sendChatMessage(String chatMessage, DingoEventWrapper event){
        return Flux.create(emitter -> {
            String memberSnowflakeId = getMemberId(event);
            logger.info("processing chat message");
            OpenAiService service = new OpenAiService(apiKey);
            OpenAiResponse<Assistant> test = service.listAssistants(ListSearchParameters.builder().build());
            Assistant dingoAssistant = test.getData().stream().filter(assistant -> {
                        return !StringUtils.isBlank(assistant.getName()) && assistant.getName().toLowerCase().contains("dingo");
                    })
                    .findFirst()
                    .orElseThrow();

            MessageRequest messageRequest = MessageRequest.builder().content(chatMessage).build();

            Thread thread = getOrCreateThread(memberSnowflakeId, service, dingoAssistant);
            Message createdMessage = service.createMessage(thread.getId(), messageRequest);
            RunCreateRequest runCreateRequest = RunCreateRequest.builder().assistantId(dingoAssistant.getId()).build();
            Run createdRun = service.createRun(thread.getId(), runCreateRequest);
            var runResponse = service.retrieveRun(thread.getId(), createdRun.getId());
            while (runningStates.contains(runResponse.getStatus().toLowerCase())) {
                logger.info("waiting for response");
                Mono.delay(Duration.ofSeconds(1)).block();
                runResponse = service.retrieveRun(runResponse.getThreadId(), runResponse.getId());
            }
            if(failedStates.contains(runResponse.getStatus())){
                String errorMessage = "Run stopped unexpectedly... Reason: " + runResponse.getStatus() + " " + runResponse.toString();
                logger.error(errorMessage);
                emitter.next(errorMessage);
                emitter.complete();
                return;
            }

            var messages = getMessages(service, thread);
            for (Message m : messages) {
                for (MessageContent c :
                        m.getContent()) {
                    if(!m.getRole().equalsIgnoreCase("user")){
                        emitter.next(c.getText().getValue());
                        logger.info(c.getText().getValue());
                    }
                }
            }
            emitter.complete();
        });
    }
}
