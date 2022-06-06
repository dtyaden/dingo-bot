package dingov2.bot.commands.actions;

import dingo.engine.DingoEngine;
import org.apache.commons.io.IOUtils;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.List;

public class DownloadThread extends Thread {

    private List<String> fullCommandList;
    private MessageReceivedEvent event;
    private String downloadTitle;

    public DownloadThread(List<String> fullCommandList, MessageReceivedEvent event, String downloadTitle){
        this.fullCommandList = fullCommandList;
        this.event = event;
        this.downloadTitle = downloadTitle;
    }

    @Override
    public void run(){
        Process process;
        try {
            process = new ProcessBuilder().command(fullCommandList).start();
            System.out.println(process.waitFor());
            System.out.println(IOUtils.toString(process.getErrorStream(), "UTF-8"));
            System.out.println("input stream (output from process): ");
            System.out.println(IOUtils.toString(process.getInputStream(), "UTF-8"));
            new SendMessageAction().sendMessage(DingoEngine.getBot(), event.getMessage().getAuthor(), "Upload of "
                    + downloadTitle + "should be done by now?");
        } catch (IOException e) {
            System.out.println("IOEXCEPTION: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("INTERRUPTED EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
