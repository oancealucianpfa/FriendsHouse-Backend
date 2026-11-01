package com.example.FriendsHouseBooking;

import com.example.FriendsHouseBooking.dtos.NotificationDTO;
import com.example.FriendsHouseBooking.notification.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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


















