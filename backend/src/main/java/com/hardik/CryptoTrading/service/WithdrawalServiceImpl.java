package com.hardik.CryptoTrading.service;

import com.hardik.CryptoTrading.domain.WalletTransactionType;
import com.hardik.CryptoTrading.domain.WithdrawalStatus;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.model.Wallet;
import com.hardik.CryptoTrading.model.WalletTransaction;
import com.hardik.CryptoTrading.model.Withdrawal;
import com.hardik.CryptoTrading.repository.WalletRepository;
import com.hardik.CryptoTrading.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service

public class WithdrawalServiceImpl implements  WithdrawalService{
	
	@Autowired
	private WithdrawalRepository withdrawalRepository;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private WalletTransactionService walletTransactionService;
	
	
//	@Override
//	public Withdrawal requestWithdrawal(Long amount, User user) throws Exception {
//
//		  Withdrawal withdrawal=new Withdrawal();
//		  withdrawal.setAmount(amount);
//		  withdrawal.setUser(user);
//		  withdrawal.setStatus(WithdrawalStatus.PENDING);
//
//		Wallet wallet = walletService.getUserWallet(user);
//
//		if(wallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0){
//			throw new Exception("Insufficient balance");
//		}
//
//		wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
//
//		walletRepository.save(wallet);
//
//		WalletTransaction txn = new WalletTransaction();
//		txn.setWallet(wallet);
//		txn.setAmount(BigDecimal.valueOf(amount));
//		txn.setType(WalletTransactionType.WITHDRAW);
//		txn.setDate(LocalDate.now());
//
//		walletTransactionService.createTransaction(txn);
//
//		  return withdrawalRepository.save(withdrawal);
//	}
	
	@Override
	public Withdrawal requestWithdrawal(Long amount, User user) {
		
		Wallet wallet = walletService.getUserWallet(user);
		
		BigDecimal withdrawalAmount = BigDecimal.valueOf(amount);
		
		if (wallet.getBalance().compareTo(withdrawalAmount) < 0) {
			throw new RuntimeException("Insufficient balance");
		}
		
		// Deduct
		wallet.setBalance(wallet.getBalance().subtract(withdrawalAmount));
		
		// Ledger entry
		WalletTransaction txn = new WalletTransaction();
		txn.setWallet(wallet);
		txn.setAmount(withdrawalAmount);
		txn.setType(WalletTransactionType.WITHDRAWAL);
		txn.setDate(LocalDate.now());
		
		walletTransactionService.createTransaction(txn);
		
		// Save withdrawal record
		Withdrawal withdrawal = new Withdrawal();
		withdrawal.setAmount(amount);
		withdrawal.setUser(user);
		withdrawal.setStatus(WithdrawalStatus.PENDING);
		
		return withdrawalRepository.save(withdrawal);
	}
	
	@Override
	public Withdrawal proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception {
		
		Optional<Withdrawal> withdrawalOpt =
				withdrawalRepository.findById(withdrawalId);
		
		if (withdrawalOpt.isEmpty()) {
			throw new Exception("Withdrawal not found");
		}
		
		Withdrawal withdrawal = withdrawalOpt.get();
		
		if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
			throw new Exception("Withdrawal already processed");
		}
		
		withdrawal.setDate(LocalDateTime.now());
		
		if (accept) {
			withdrawal.setStatus(WithdrawalStatus.SUCCESS);
		} else {
			withdrawal.setStatus(WithdrawalStatus.DECLINE);
		}
		
		return withdrawalRepository.save(withdrawal);
	}
	
	
	@Override
	public List<Withdrawal> getUsersWithdrawalHistory(User user) {
		return withdrawalRepository.findByUserId(user.getId());
	}
	
	@Override
	public List<Withdrawal> getAllWithdrawalRequest() {
		return withdrawalRepository.findAll();
	}
}
