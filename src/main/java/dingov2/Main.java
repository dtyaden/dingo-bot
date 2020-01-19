package dingov2;

import dingov2.discordapi.DingoClient;

public class Main {

    public static void main(String args[]){
        if(args.length < 1){
            throw new RuntimeException("No token supplied for the bot. gg.");
        }

        DingoClient dingoClient = new DingoClient(args[1]);
        dingoClient.login();
    }
}
