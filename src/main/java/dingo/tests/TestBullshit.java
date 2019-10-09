package dingo.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestBullshit {

    public static void main(String[] args){
        String testURL = "http://www.youtube.com/watch?v=_HSylqgVYQI";
        String parameters = testURL;
        ProcessBuilder processBuilder = new ProcessBuilder("ytdl", parameters);
        try{
            Process process = processBuilder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in .readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            System.out.println();
            in.close();
        }
        catch(IOException ex){

        }
        catch(InterruptedException interruptedException){

        }

    }
}
