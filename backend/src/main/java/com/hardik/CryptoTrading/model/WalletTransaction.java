
package com.hardik.CryptoTrading.model;

import com.hardik.CryptoTrading.domain.WalletTransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class WalletTransaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private Wallet wallet;
	
	@Enumerated(EnumType.STRING)
	private WalletTransactionType type;
	
	private LocalDate date;
	
	private String transferId;
	
	private String purpose;
	
	private BigDecimal amount;
}