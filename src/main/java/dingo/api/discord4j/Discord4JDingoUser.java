package dingo.api.discord4j;

import dingo.api.base.entity.DingoUser;
import discord4j.core.object.entity.User;

public class Discord4JDingoUser implements DingoUser {
    private final User user;

    public Discord4JDingoUser(User user){
        this.user = user;
    }
}
