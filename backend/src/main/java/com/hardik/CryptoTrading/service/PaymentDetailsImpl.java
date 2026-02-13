


package com.hardik.CryptoTrading.service;

import com.hardik.CryptoTrading.model.PaymentDetails;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.repository.PaymentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentDetailsImpl implements PaymentDetailsService {
	
	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Override
	public PaymentDetails addPaymentDetails(String accNo, String holder, String ifsc, String bank, User user) {
		Optional<PaymentDetails> existing = paymentDetailsRepository.findByUserId(user.getId());
		
		if (existing.isPresent()) {
			throw new RuntimeException("Payment details already exist for this user.");
		}
		
		PaymentDetails details = new PaymentDetails();
		details.setAccountNumber(accNo);
		details.setAccountHolderName(holder);
		details.setIfsc(ifsc);
		details.setBankName(bank);
		details.setUser(user);
		
		return paymentDetailsRepository.save(details);
	}
	
	@Override
	public PaymentDetails getUsersPaymentDetails(User user) {
		return paymentDetailsRepository.findByUserId(user.getId())
				.orElseThrow(() -> new RuntimeException("No payment details found for this user."));
	}
	
//	@Override
//	public PaymentDetails getUsersPaymentDetails(User user) {
//		return paymentDetailsRepository.findByUserId(user.getId());
//	}
}
