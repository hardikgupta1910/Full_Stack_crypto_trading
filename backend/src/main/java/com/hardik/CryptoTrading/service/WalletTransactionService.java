package com.hardik.CryptoTrading.service;

import com.hardik.CryptoTrading.model.Wallet;
import com.hardik.CryptoTrading.model.WalletTransaction;
import java.util.List;

public interface WalletTransactionService {
	
	WalletTransaction createTransaction(WalletTransaction transaction);
	WalletTransaction getTransactionById(Long id) throws Exception;
	List<WalletTransaction> getTransactionsForWallet(Long walletId);
	
}