package com.hardik.CryptoTrading.service;

import com.hardik.CryptoTrading.domain.PaymentMethod;
import com.hardik.CryptoTrading.model.PaymentOrder;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {

	PaymentOrder createOrder (User user, Long amount, PaymentMethod paymentMethod);

	PaymentOrder getPaymentOrderById(Long id) throws Exception;
	
	Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, User user) throws RazorpayException;

	PaymentResponse createRazorPaymentLink(User user , Long amount , Long orderId) throws RazorpayException;
	
	PaymentResponse createStripePaymentLink(User user , Long amount, Long orderId ) throws StripeException;
	
}
