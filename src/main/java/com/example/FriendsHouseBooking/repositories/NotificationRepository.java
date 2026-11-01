package com.example.FriendsHouseBooking.repositories;

import com.example.FriendsHouseBooking.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
