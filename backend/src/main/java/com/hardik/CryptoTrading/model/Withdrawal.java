package com.hardik.CryptoTrading.model;

import com.hardik.CryptoTrading.domain.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Withdrawal {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private WithdrawalStatus status;
	
	private Long amount;
	
	@ManyToOne
	private User user;
	
	 private LocalDateTime date = LocalDateTime.now();
}
