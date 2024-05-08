package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpringBoot3WebHibernate6Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBoot3WebHibernate6Application.class, args);
	}

}
