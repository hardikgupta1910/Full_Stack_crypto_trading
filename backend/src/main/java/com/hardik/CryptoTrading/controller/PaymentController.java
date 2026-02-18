package com.hardik.CryptoTrading.controller;

import com.hardik.CryptoTrading.domain.PaymentMethod;
import com.hardik.CryptoTrading.model.PaymentOrder;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.response.PaymentResponse;
import com.hardik.CryptoTrading.service.PaymentService;
import com.hardik.CryptoTrading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api")
public class PaymentController {

	@Autowired
	private UserService userService;

	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
	public ResponseEntity<PaymentResponse> paymentHandler(@PathVariable PaymentMethod paymentMethod,
														  @PathVariable Long amount,
														  @RequestHeader("Authorization") String jwt) throws Exception {
		
		User user=userService.findUserProfileByJwt(jwt);
		
		PaymentResponse paymentResponse;
		
		PaymentOrder order=paymentService.createOrder(user, amount, paymentMethod);
		
		if(paymentMethod.equals(PaymentMethod.RAZORPAY)){
			paymentResponse=paymentService.createRazorPaymentLink(user, amount, order.getId());
		}
		else {
			paymentResponse=paymentService.createStripePaymentLink(user, amount, order.getId());
			
		}
		
		return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
		
	}
	
	

}
