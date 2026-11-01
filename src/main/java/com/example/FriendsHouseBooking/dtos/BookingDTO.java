package com.example.FriendsHouseBooking.dtos;

import com.example.FriendsHouseBooking.enums.BookingStatus;
import com.example.FriendsHouseBooking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) //ignore fields that are not present in the JSON input
@JsonInclude(JsonInclude.Include.NON_NULL) //ignore null fields during serialization
public class BookingDTO {

    private Long id;

    private UserDTO user;

    private RoomDTO room;

    private Long roomId;

    private PaymentStatus paymentStatus;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BigDecimal totalPrice;

    private String bookingReference;

    private LocalDate createdAt;

    private BookingStatus bookingStatus;
}
