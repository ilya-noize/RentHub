package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {
	public static final String HEADER_USER_ID = "X-Sharer-User-Id";
	public static final String USER_WITH_ID_NOT_EXIST = "User with id:(%d) not exist";
	public static final String ITEM_WITH_ID_NOT_EXIST = "Item with id:(%d) not exist";

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

}
