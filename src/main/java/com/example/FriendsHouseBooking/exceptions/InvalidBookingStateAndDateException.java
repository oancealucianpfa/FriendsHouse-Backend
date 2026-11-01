package com.example.FriendsHouseBooking.exceptions;

public class InvalidBookingStateAndDateException extends RuntimeException{

    public InvalidBookingStateAndDateException(String message){
        super(message);
    }
}
