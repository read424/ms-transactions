package com.bootcamp.ms_transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTransactionApplication.class, args);
	}

}
