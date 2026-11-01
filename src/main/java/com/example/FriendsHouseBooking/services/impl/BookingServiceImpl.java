package com.example.FriendsHouseBooking.services.impl;

import com.example.FriendsHouseBooking.dtos.BookingDTO;
import com.example.FriendsHouseBooking.dtos.NotificationDTO;
import com.example.FriendsHouseBooking.dtos.Response;
import com.example.FriendsHouseBooking.entities.Booking;
import com.example.FriendsHouseBooking.entities.Room;
import com.example.FriendsHouseBooking.entities.User;
import com.example.FriendsHouseBooking.enums.BookingStatus;
import com.example.FriendsHouseBooking.enums.PaymentStatus;
import com.example.FriendsHouseBooking.exceptions.InvalidBookingStateAndDateException;
import com.example.FriendsHouseBooking.exceptions.NotFoundException;
import com.example.FriendsHouseBooking.notification.NotificationService;
import com.example.FriendsHouseBooking.repositories.BookingRepository;
import com.example.FriendsHouseBooking.repositories.RoomRepository;
import com.example.FriendsHouseBooking.services.BookingCodeGenerator;
import com.example.FriendsHouseBooking.services.BookingService;
import com.example.FriendsHouseBooking.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final BookingCodeGenerator bookingCodeGenerator;

    @Override
    public Response getAllBookings() {

        List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<BookingDTO> bookingDTOList = modelMapper.map(bookingList, new TypeToken<List<BookingDTO>>() {
        }.getType());

        // Set the user and room fields to null for each BookingDTO to not return the user and room details in the response
        for (BookingDTO bookingDTO : bookingDTOList) {
            bookingDTO.setUser(null);
            bookingDTO.setRoom(null);
        }

        return Response.builder()
                .status(200)
                .message("success")
                .bookings(bookingDTOList)
                .build();
    }

    @Override
    public Response createBooking(BookingDTO bookingDTO) {

        User currentUser = userService.getCurrentLoggedInUser();

        Room room = roomRepository.findById(bookingDTO.getRoomId())
                .orElseThrow(() -> new NotFoundException("Room not found"));

        //VALIDATION: ENSURE CHECK IN DATE IS NOT BEFORE TODAY
        if (bookingDTO.getCheckInDate().isBefore(LocalDate.now())) {
            throw new InvalidBookingStateAndDateException("Check in date cannot be before today ");
        }

        //VALIDATION: ENSURE CHECK OUT DATE IS NOT BEFORE CHECK IN DATE
        if (bookingDTO.getCheckOutDate().isBefore((bookingDTO.getCheckInDate()))) {
            throw new InvalidBookingStateAndDateException("Check Out date cannot be before check in date");
        }

        //VALIDATION: ENSURE CHECK IN DATE IS NOT SAME AS TO CHECK OUT DATE
        if ((bookingDTO.getCheckInDate().isEqual(bookingDTO.getCheckOutDate()))) {
            throw new InvalidBookingStateAndDateException("Check In date cannot be equal to check Out date");
        }

        //VALID ROOM AVAILABILITY
        boolean isAvailable = bookingRepository.isRoomAvailable(
                room.getId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate()
        );

        if (!isAvailable) {
            throw new InvalidBookingStateAndDateException("Room is not available to be booked");
        }

        BigDecimal totalPrice = calculateTotalPrice(room, bookingDTO);
        String bookingReference = bookingCodeGenerator.generateBookingReference();

        //CREATE AND SAVE TO DATABASE
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingReference(bookingReference);
        booking.setBookingStatus(BookingStatus.BOOKED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setCreatedAt(LocalDate.now());

        bookingRepository.save(booking);

        String paymentLink = "http://localhost:4200/payment/" + bookingReference + "/" + totalPrice;
        log.info("Booking Successfully PAYMENT LINK IS {}", paymentLink);

        //send email to user via email
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(currentUser.getEmail())
                .subject("BOOKING CONFIRMATION")
                .body(String.format("Your booking has been created successfully. \n Please proceed with your payment using the payment " +
                        "like below \n%s", paymentLink))
                .bookingReference(bookingReference)
                .build();

        notificationService.sendEmail(notificationDTO);

        return Response.builder()
                .status(200)
                .message("Booking is successful")
                .booking(bookingDTO)
                .build();
    }

    @Override
    public Response findBookingByReferenceNo(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);

        return Response.builder()
                .status(200)
                .message("success")
                .booking(bookingDTO)
                .build();
    }

    @Override
    public Response updateBooking(BookingDTO bookingDTO) {
        if (bookingDTO.getId() == null) {
            throw new NotFoundException("Booking Id Is Required");
        }

        Booking existingBooking = bookingRepository.findById(bookingDTO.getId())
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (bookingDTO.getBookingStatus() != null) {
            existingBooking.setBookingStatus(bookingDTO.getBookingStatus());
        }

        if (bookingDTO.getPaymentStatus() != null) {
            existingBooking.setPaymentStatus(bookingDTO.getPaymentStatus());
        }

        bookingRepository.save(existingBooking);

        return Response.builder()
                .status(200)
                .message("Booking Updated Successfully")
                .build();
    }

    private BigDecimal calculateTotalPrice(Room room, BookingDTO bookingDTO) {
        BigDecimal pricePerNight = room.getPricePerNight();
        long days = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }

}






















