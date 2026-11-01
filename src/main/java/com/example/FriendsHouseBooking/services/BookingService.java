package com.example.FriendsHouseBooking.services;

import com.example.FriendsHouseBooking.dtos.BookingDTO;
import com.example.FriendsHouseBooking.dtos.Response;

public interface BookingService {
    Response getAllBookings();

    Response createBooking(BookingDTO bookingDTO);

    Response findBookingByReferenceNo(String bookingReference);

    Response updateBooking(BookingDTO bookingDTO);
}
