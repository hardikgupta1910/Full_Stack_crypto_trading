package com.hardik.CryptoTrading.service;

import com.hardik.CryptoTrading.domain.WithdrawalStatus;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.model.Withdrawal;
import com.hardik.CryptoTrading.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service

public class WithdrawalServiceImpl implements  WithdrawalService{
	
	@Autowired
	private WithdrawalRepository withdrawalRepository;
	
	
	@Override
	public Withdrawal requestWithdrawal(Long amount, User user) {
	
		  Withdrawal withdrawal=new Withdrawal();
		  withdrawal.setAmount(amount);
		  withdrawal.setUser(user);
		  withdrawal.setStatus(WithdrawalStatus.PENDING);
		  
		  return withdrawalRepository.save(withdrawal);
	}
	
//	@Override
//	public Withdrawal proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception {
//
//		Optional<Withdrawal> withdrawal=withdrawalRepository.findById(withdrawalId);
//
//		if(withdrawal.isEmpty()){
//			throw  new Exception("withdrawal not found");
//		}
//		Withdrawal withdrawal1=withdrawal.get();
//
//		withdrawal1.setDate(LocalDateTime.now());
//
//		if(accept){
//			withdrawal1.setStatus(WithdrawalStatus.SUCCESS);
//		}else{
//			withdrawal1.setStatus(WithdrawalStatus.PENDING);
//		}
//
//		   return withdrawalRepository.save(withdrawal1);
//	}
	
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
