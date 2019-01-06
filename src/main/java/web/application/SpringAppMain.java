package web.application;


import javazoom.jl.decoder.Manager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@Import({ControllerConfiguration.class, ServiceConfiguration.class, ManagerConfiguration.class})
public class SpringAppMain {
    public static void main(String[] args){
        SpringApplication.run(SpringAppMain.class, args);
    }
}
