package com.hardik.CryptoTrading.controller;

import com.hardik.CryptoTrading.model.*;
import java.time.LocalDate;
import com.hardik.CryptoTrading.domain.WalletTransactionType;
import com.hardik.CryptoTrading.service.WalletService;
import com.hardik.CryptoTrading.response.PaymentResponse;
import com.hardik.CryptoTrading.service.OrderService;
import com.hardik.CryptoTrading.service.PaymentService;
import com.hardik.CryptoTrading.service.UserService;
import com.hardik.CryptoTrading.service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
//@RequestMapping("/api/wallet")
public class WalletController {
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WalletTransactionService walletTransactionService;
	
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PaymentService paymentService;
	
	@GetMapping("/api/wallet")
	public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception {
		User user=userService.findUserProfileByJwt(jwt);
		
		Wallet wallet=walletService.getUserWallet(user);
		
		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/api/wallet/{walletId}/transfer")
	public ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization") String jwt,
														 @PathVariable Long walletId,
														 @RequestBody WalletTransaction req) throws Exception {
		
		User senderUser=userService.findUserProfileByJwt(jwt);
		Wallet recieverWallet=walletService.findWalletById(walletId);
//		Wallet wallet=walletService.walletToWalletTransfer(senderUser,recieverWallet,req.getAmount());
		Wallet wallet = walletService.walletToWalletTransfer(senderUser, recieverWallet, req.getAmount().longValue());
		
		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/api/wallet/order/{orderId}/pay")
	public ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization") String jwt,
												  @PathVariable Long orderId
	) throws Exception {
		
		User user=userService.findUserProfileByJwt(jwt);
		
		Order order= orderService.getOrderById(orderId);
		
		Wallet wallet=walletService.payOrderPayment(order,user);
		
		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/api/wallet/deposit")
	public ResponseEntity<Wallet> addBalanceToWallet(@RequestHeader("Authorization") String jwt,
													 @RequestParam(name = "order_id") Long orderId,
													 @RequestParam(name = "payment_id") String paymentId
	) throws Exception {
		
		User user=userService.findUserProfileByJwt(jwt);
		
		
		Wallet wallet=walletService.getUserWallet(user);
		
		
		
		PaymentOrder order=paymentService.getPaymentOrderById(orderId);
		
		Boolean status=paymentService.ProceedPaymentOrder(order, paymentId);
		
		PaymentResponse response = new PaymentResponse();
//		response.setPaymentUrl("deposit success");
		
		if(wallet.getBalance()==null){
			wallet.setBalance(BigDecimal.valueOf(0));
		}
		
		if(status){
			wallet=walletService.addBalance(wallet, order.getAmount());
			
		}
		
		return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
	}
	
	
	

	
	@PatchMapping("/api/wallet/test/add/amount/{amount}")
	public ResponseEntity<Wallet> addTestBalance(
			@PathVariable Long amount,
			@RequestHeader("Authorization") String jwt) throws Exception {
		
		// Step 1: Get the user from JWT
		User user = userService.findUserProfileByJwt(jwt);
		
		// Step 2: Get the user's wallet
		Wallet wallet = walletService.getUserWallet(user);
		
		// Step 3: Add balance to wallet
		Wallet updatedWallet = walletService.addBalance(wallet, amount);
		
		// Step 4: Create and save test transaction
		WalletTransaction txn = new WalletTransaction();
		txn.setWallet(wallet);
		txn.setAmount(BigDecimal.valueOf(amount));
		txn.setType(WalletTransactionType.TEST_ADD); // ✅ Enum value
		txn.setDate(LocalDate.now());
		
		
		walletTransactionService.createTransaction(txn);
		
		// Step 5: Return updated wallet
		return new ResponseEntity<>(updatedWallet, HttpStatus.OK);
	}
	
}