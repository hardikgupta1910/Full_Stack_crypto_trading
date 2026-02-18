package com.hardik.CryptoTrading.repository;

import com.hardik.CryptoTrading.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Long> {

	
	Optional<PaymentDetails> findByUserId(Long userId);
	
}
