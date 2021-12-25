package krendel.spring.boot.advanced.sample.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"krendel.spring.boot.advanced.sample.web"})
@EntityScan("krendel.spring.boot.advanced.sample.data.domain")
@EnableJpaAuditing
public class WebApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.location", "classpath:/application-data.yml,classpath:/application.yml");

        SpringApplication.run(WebApplication.class, args);
    }

}
