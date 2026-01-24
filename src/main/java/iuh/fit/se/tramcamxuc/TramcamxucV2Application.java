package iuh.fit.se.tramcamxuc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class TramcamxucV2Application {

    public static void main(String[] args) {
        SpringApplication.run(TramcamxucV2Application.class, args);
    }

}
