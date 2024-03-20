package dingov2.bot.services;

import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageContent;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.CreateThreadAndRunRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DingoOpenAIQueryService implements OpenAIQueryService {

    private String apiKey;
    public static final List<String> runningStates = Arrays.asList("queued", "in_progress");
    public static final List<String> failedStates = Arrays.asList("requires_action", "failed", "expired", "cancelled", "cancelling");
    private Logger logger;

    public DingoOpenAIQueryService(String apiKey){
        this.apiKey = apiKey;
        logger = LoggerFactory.getLogger(DingoOpenAIQueryService.class);
    }

    public static void main(String[] args){
        Logger logger2 = LoggerFactory.getLogger(DingoOpenAIQueryService.class);

        new DingoOpenAIQueryService("")
                .sendChatMessage("who is jerry seinfeld?")
                .subscribe(logger2::info);
    }

    @Override
    public Flux<String> sendChatMessage(String chatMessage){
        return Flux.create(emitter -> {
            logger.info("processing chat message");
            OpenAiService service = new OpenAiService(apiKey);
            OpenAiResponse<Assistant> test = service.listAssistants(ListSearchParameters.builder().build());
            Assistant dingoAssistant = test.getData().stream().filter(assistant -> {
                        return !StringUtils.isBlank(assistant.getName()) && assistant.getName().toLowerCase().contains("dingo");
                    })
                    .findFirst()
                    .orElseThrow();

            MessageRequest messageRequest = MessageRequest.builder().content(chatMessage).build();
            ThreadRequest threadRequest = ThreadRequest.builder()
                    .messages(Collections.singletonList(messageRequest))
                    .build();
            CreateThreadAndRunRequest createThreadAndRunRequest = CreateThreadAndRunRequest
                    .builder()
                    .thread(threadRequest)
                    .assistantId(dingoAssistant.getId())
                    .build();
            var createThreadAndRunResponse = service.createThreadAndRun(createThreadAndRunRequest);
            var runResponse = service.retrieveRun(createThreadAndRunResponse.getThreadId(), createThreadAndRunResponse.getId());
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
            var messages = service.listMessages(createThreadAndRunResponse.getThreadId());
            for (Message m : messages.getData()) {
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
