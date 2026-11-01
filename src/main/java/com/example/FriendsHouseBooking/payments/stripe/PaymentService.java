package com.example.FriendsHouseBooking.payments.stripe;

import com.example.FriendsHouseBooking.dtos.NotificationDTO;
import com.example.FriendsHouseBooking.dtos.Response;
import com.example.FriendsHouseBooking.entities.Booking;
import com.example.FriendsHouseBooking.entities.PaymentEntity;
import com.example.FriendsHouseBooking.enums.NotificationType;
import com.example.FriendsHouseBooking.enums.PaymentGateway;
import com.example.FriendsHouseBooking.enums.PaymentStatus;
import com.example.FriendsHouseBooking.exceptions.NotFoundException;
import com.example.FriendsHouseBooking.notification.NotificationService;
import com.example.FriendsHouseBooking.repositories.BookingRepository;
import com.example.FriendsHouseBooking.repositories.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService emailService;

    @Value("${stripe.api.secret.key}")
    private String secretKey;

    //We are initializing a payment, this will return a unique transactiodId called secrete which
    // will be used to process, complete the payment in the frontend
    public Response initializePayment(PaymentRequest paymentRequest) {

        log.info("Inside initializePayment()");
        Stripe.apiKey= secretKey;

        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if(booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            new NotFoundException("Payment Already Made For This Booking");
        }

        if (booking.getTotalPrice().compareTo(paymentRequest.getAmount()) != 0) {
            new NotFoundException("Payment Amount Does Not Tally. Please contact us");
        }

        //todo maybe we need to integrate to RON
        try{
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // converting to cents
                    .setCurrency("usd")
                    .putMetadata("bookingReference", bookingReference)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            String uniqueTransactionId = intent.getClientSecret();

            return  Response.builder()
                    .status(200)
                    .message("success")
                    .transactionId(uniqueTransactionId)
                    .build();

        }catch (Exception e){
            throw new RuntimeException("Error Creating payment unique transaction id");
        }
    }

    public void updatePaymentBooking(PaymentRequest paymentRequest) {
        log.info("Inside updatePaymentBooking()");

        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentGateway(PaymentGateway.STRIPE);
        payment.setAmount(paymentRequest.getAmount());
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentStatus(paymentRequest.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookingReference(bookingReference);
        payment.setUser(booking.getUser());

        if(!paymentRequest.isSuccess()) {
            payment.setFailureReason(paymentRequest.getFailureReason());
        }

        paymentRepository.save(payment);//save to our payments table

        //Create and send notification via email
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(booking.getUser().getEmail())
                .type(NotificationType.EMAIL)
                .bookingReference(bookingReference)
                .build();

        if (paymentRequest.isSuccess()) {
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingRepository.save(booking);//update Booking Status to Successful

            notificationDTO.setSubject("BOOKING PAYMENT SUCCESSFUL");
            notificationDTO.setBody("Congratulations!! Your payment for booking with reference: " + bookingReference + " is successful");
            emailService.sendEmail(notificationDTO);

        }else {

            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking);// UPDATE THE BOOKING

            notificationDTO.setSubject("BOOKING PAYMENT FAILED");
            notificationDTO.setBody("Your payment for booking with reference: " + bookingReference + " failed with reason: " + paymentRequest.getFailureReason());
            emailService.sendEmail(notificationDTO);
        }
    }

}
