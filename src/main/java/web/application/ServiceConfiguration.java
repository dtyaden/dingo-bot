package web.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.DingoFileService;

@Configuration
public class ServiceConfiguration {
    @Bean
    public DingoFileService dingoFileService(){
        return new DingoFileService();
    }
}
