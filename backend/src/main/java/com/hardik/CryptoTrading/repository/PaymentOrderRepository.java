package com.hardik.CryptoTrading.repository;

import com.hardik.CryptoTrading.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {
	List<PaymentOrder> findByUserId(Long userId);
}
