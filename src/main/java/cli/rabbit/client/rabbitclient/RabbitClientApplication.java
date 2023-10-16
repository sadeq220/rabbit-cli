package cli.rabbit.client.rabbitclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RabbitClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitClientApplication.class, args);
	}

}
