package com.example.FriendsHouseBooking.dtos;

import com.example.FriendsHouseBooking.enums.PaymentGateway;
import com.example.FriendsHouseBooking.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) //ignore fields that are not present in the JSON input
@JsonInclude(JsonInclude.Include.NON_NULL) //ignore null fields during serialization
public class PaymentDTO {

    private Long id;

    private BookingDTO booking;

    private String transactionId;

    private BigDecimal amount;

    private PaymentGateway paymentGateway;//eg: PAYPAL, STRIPE, PAYSTACK

    private LocalDateTime paymentDate;

    private PaymentStatus paymentStatus;//failed, etc

    private String bookingReference;

    private String failureReason;

    private String  approvalLink; //Paypal payment approval UEL
}
