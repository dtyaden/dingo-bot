package web.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import web.controller.actions.ActionsController;

@Configuration
public class ControllerConfiguration {
    @Bean
    public ActionsController actionsController(){
        return new ActionsController();
    }
}
