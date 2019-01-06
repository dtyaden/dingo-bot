package web.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.DingoFileService;

@Configuration
public class ManagerConfiguration {
    @Bean
    public DingoFileService filManager(){
        return new DingoFileService();
    }
}
