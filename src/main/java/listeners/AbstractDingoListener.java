package listeners;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;

public abstract class AbstractDingoListener<T extends Event> implements IListener<T> {

    protected long clientID;

    public AbstractDingoListener(long clientID){
        this.clientID = clientID;
    }

}
