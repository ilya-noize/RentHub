package ru.practicum.shareit;

import org.jeasy.random.EasyRandom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServer {
    public static final EasyRandom RANDOM = new EasyRandom();

	public static void main(String[] args) {
		SpringApplication.run(ShareItServer.class, args);
	}

}
