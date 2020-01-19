package dingo.interactions.actions.responses;

import sx.blah.discord.handle.obj.IMessage;

public class DeleteMessageResponse implements Runnable{
    private IMessage targetMessage;
    private static final long DELETE_DELAY = 30000;
    public IMessage getMessage() {
        return targetMessage;
    }

    public void run(){
        try {
            Thread.sleep(DELETE_DELAY);
        } catch (InterruptedException e) {
            // Don't really care if this happens.
        }
        targetMessage.delete();
    }

    public DeleteMessageResponse(IMessage targetMessage){
        this.targetMessage = targetMessage;
    }
}
