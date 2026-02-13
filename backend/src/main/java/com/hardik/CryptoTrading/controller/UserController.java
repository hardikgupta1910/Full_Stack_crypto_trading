package com.hardik.CryptoTrading.controller;


import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	
	@GetMapping("/api/users/profile")
	public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
		User user=userService.findUserProfileByJwt(jwt);
		
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	

	

	@GetMapping("/api/admin/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> users = userService.findAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
}
