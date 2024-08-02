package com.yechat.messages;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableFeignClients
public class MessagesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessagesApplication.class, args);
	}

}
