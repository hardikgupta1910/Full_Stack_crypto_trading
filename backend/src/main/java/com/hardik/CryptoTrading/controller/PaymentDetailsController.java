

package com.hardik.CryptoTrading.controller;

import com.hardik.CryptoTrading.dto.PaymentDetailsRequest;
import com.hardik.CryptoTrading.model.PaymentDetails;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.service.PaymentDetailsService;
import com.hardik.CryptoTrading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentDetailsController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;
	
	
	@PostMapping("/payment-details")
	public ResponseEntity<PaymentDetails> addPaymentDetails(
			@RequestBody PaymentDetailsRequest request,
			@RequestHeader("Authorization") String jwt
	) throws Exception {
		User user = userService.findUserProfileByJwt(jwt);
		
		PaymentDetails paymentDetails = paymentDetailsService.addPaymentDetails(
				request.getAccountNumber(),
				request.getAccountHolderName(),
				request.getIfsc(),
				request.getBankName(),
				user
		);
		
		return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
	}
	
	
	@GetMapping("/payment-details")
	public ResponseEntity<PaymentDetails> getUserPaymentDetails(
			@RequestHeader("Authorization") String jwt) throws Exception {
		
		User user = userService.findUserProfileByJwt(jwt);
		PaymentDetails paymentDetails = paymentDetailsService.getUsersPaymentDetails(user);
		return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
	}
}
