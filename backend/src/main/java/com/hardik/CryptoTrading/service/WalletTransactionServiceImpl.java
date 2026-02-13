package com.hardik.CryptoTrading.service;


import com.hardik.CryptoTrading.model.WalletTransaction;
import com.hardik.CryptoTrading.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService  {
	@Autowired
	private WalletTransactionRepository walletTransactionRepository;
	
	
	
	
	
	@Override
	public WalletTransaction createTransaction(WalletTransaction transaction) {
		return walletTransactionRepository.save(transaction);
	}
	
	@Override
	public WalletTransaction getTransactionById(Long id) throws Exception {
		return walletTransactionRepository.findById(id)
				.orElseThrow(() -> new Exception("Transaction not found with id: " + id));
	}
	
	@Override
	public List<WalletTransaction> getTransactionsForWallet(Long walletId) {
		return walletTransactionRepository.findByWalletId(walletId);
	}
	
}