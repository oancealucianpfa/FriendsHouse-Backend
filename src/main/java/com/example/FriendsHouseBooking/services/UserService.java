package com.example.FriendsHouseBooking.services;

import com.example.FriendsHouseBooking.dtos.LoginRequest;
import com.example.FriendsHouseBooking.dtos.RegistrationRequest;
import com.example.FriendsHouseBooking.dtos.Response;
import com.example.FriendsHouseBooking.dtos.UserDTO;
import com.example.FriendsHouseBooking.entities.User;

public interface UserService {

    Response registerUser(RegistrationRequest registrationRequest);

    Response loginUser(LoginRequest loginRequest);

    Response getAllUsers();

    Response getOwnAccountDetails();

    User getCurrentLoggedInUser();

    Response updateOwnAccount(UserDTO userDTO);

    Response deleteOwnAccount();

    Response getMyBookingHistory();

}
