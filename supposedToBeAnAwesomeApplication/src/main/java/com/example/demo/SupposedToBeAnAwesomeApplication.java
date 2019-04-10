package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//(scanBasePackages = { "com.example.demo", "com.example.demo.Controllers", "com.example.demo.dl",
//		"com.example.demo.repository", "com.example.demo.sl", "com.example.demo.Models" })
public class SupposedToBeAnAwesomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupposedToBeAnAwesomeApplication.class, args);
	}

}
