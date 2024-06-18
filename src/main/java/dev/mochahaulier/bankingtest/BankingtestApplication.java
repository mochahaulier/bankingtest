package dev.mochahaulier.bankingtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// For the scheduled tasks.
@EnableScheduling
// To cache frequently used stuff
@EnableCaching
// For asyn tasks
@EnableAsync
public class BankingtestApplication {
	public static void main(String[] args) {
		SpringApplication.run(BankingtestApplication.class, args);
	}
}
