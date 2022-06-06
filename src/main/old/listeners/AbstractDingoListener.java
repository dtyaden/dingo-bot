package listeners;


public abstract class AbstractDingoListener {

    protected long clientID;

    public AbstractDingoListener(long clientID){
        this.clientID = clientID;
    }

}
