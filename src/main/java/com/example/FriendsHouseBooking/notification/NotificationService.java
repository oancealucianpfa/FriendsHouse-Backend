package com.example.FriendsHouseBooking.notification;

import com.example.FriendsHouseBooking.dtos.NotificationDTO;


public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO);
    void sendSMS();
    void sendWhatsapp();

}
