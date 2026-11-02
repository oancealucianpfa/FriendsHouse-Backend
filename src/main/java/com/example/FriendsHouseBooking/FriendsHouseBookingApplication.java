package com.example.FriendsHouseBooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // we needed because we want to send email asynchronously
public class FriendsHouseBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FriendsHouseBookingApplication.class, args);

	}


}


















