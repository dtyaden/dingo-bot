package dingov2.bot.commands;

        import discord4j.core.event.domain.message.MessageCreateEvent;
        import discord4j.core.object.entity.Message;
        import discord4j.core.object.entity.channel.MessageChannel;
        import discord4j.discordjson.json.ApplicationCommandRequest;
        import reactor.core.publisher.Mono;

        import java.util.ArrayList;
        import java.util.List;

public abstract class AbstractMessageEventAction implements DingoAction {

    protected MessageCreateEvent event;
    protected List<String> arguments;
    public ApplicationCommandRequest commandRequest;
    public AbstractMessageEventAction(MessageCreateEvent event, List<String> arguments) {
        this.event = event;
        this.arguments = arguments;
    }

    public Mono<MessageChannel> getMessageChannel() {
        return Mono.justOrEmpty(event.getMessage())
                .flatMap(Message::getChannel);
    }

    /**
     * Convert arguments to a String List starting from the first argument that isn't the command.
     * @param args
     * @return
     */
    public List<String> getArgumentList(String[] args){
        List<String> list = new ArrayList<>();
        for(int i = 1; i<args.length; i++){
            list.add(args[i]);
        }
        return list;
    }

    /**
     * return a Mono containing the list of arguments as Strings without the command.
     * @return
     */
    public Mono<List<String>> getArguments() {
        return getMessageContent().map(str -> getArgumentList(str.split(" ")));
    }

    public Mono<String> getMessageContent() {
        return Mono.justOrEmpty(event.getMessage().getContent());
    }
}

