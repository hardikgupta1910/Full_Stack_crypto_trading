package com.hardik.CryptoTrading.service;

import com.hardik.CryptoTrading.domain.PaymentMethod;
import com.hardik.CryptoTrading.domain.PaymentOrderStatus;
import com.hardik.CryptoTrading.domain.WalletTransactionType;
import com.hardik.CryptoTrading.model.PaymentOrder;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.model.Wallet;
import com.hardik.CryptoTrading.model.WalletTransaction;
import com.hardik.CryptoTrading.repository.PaymentOrderRepository;
import com.hardik.CryptoTrading.repository.WalletRepository;
import com.hardik.CryptoTrading.repository.WalletTransactionRepository;
import com.hardik.CryptoTrading.response.PaymentResponse;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentOrderServiceImpl implements PaymentService {
	
	@Autowired
	private PaymentOrderRepository paymentOrderRepository;
	
	@Autowired
	private WalletTransactionRepository walletTransactionRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	
	@Value("${razorpay.api.key}")
	private String apiKey;
	
	@Value(("${razorpay.api.secret}"))
	private String apiSecretKey;
	
	@Override
	public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
		
		PaymentOrder paymentOrder = new PaymentOrder();
		paymentOrder.setUser(user);
		paymentOrder.setAmount(amount);
		paymentOrder.setPaymentMethod(paymentMethod);
		paymentOrder.setStatus(PaymentOrderStatus.PENDING);
		
		return paymentOrderRepository.save(paymentOrder);
		
	}
	
	@Override
	public PaymentOrder getPaymentOrderById(Long id) throws Exception {
		return paymentOrderRepository.findById(id).orElseThrow(() -> new Exception("payment order not found"));
	}
	

	
	@Override
	public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, User user)
			throws RazorpayException {

		if (!paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)) {
			return false;
		}

		RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecretKey);
		Payment payment = razorpay.payments.fetch(paymentId);

		String status = payment.get("status");

		if ("captured".equals(status)) {

			paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
			
			Long amount = paymentOrder.getAmount();
			BigDecimal amountBD = BigDecimal.valueOf(amount);
			
			Wallet wallet = walletRepository.findByUserId(user.getId());
			List<WalletTransaction> transactions =
					walletTransactionRepository.findByWalletId(wallet.getId());
			
			wallet.setBalance(wallet.getBalance().add(amountBD));

			
			if (wallet == null) {
				throw new RuntimeException("Wallet not found");
			}
			
			wallet.setBalance(wallet.getBalance().add(amountBD));

			WalletTransaction txn = new WalletTransaction();
			txn.setWallet(wallet);
			txn.setAmount(BigDecimal.valueOf(amount));
			txn.setType(WalletTransactionType.ADD_MONEY);
			txn.setDate(LocalDate.now());

			walletTransactionRepository.save(txn);
			walletRepository.save(wallet);

		} else {
			paymentOrder.setStatus(PaymentOrderStatus.FAILED);
		}

		paymentOrderRepository.save(paymentOrder);

		return paymentOrder.getStatus() == PaymentOrderStatus.SUCCESS;
	}
	
	
	@Override
	public PaymentResponse createRazorPaymentLink(User user, Long amount, Long orderId) throws RazorpayException {

		Long Amount=amount*100;
		try{
			RazorpayClient razorpay=new RazorpayClient(apiKey,apiSecretKey);

			JSONObject paymentLinkRequest=new JSONObject();
			paymentLinkRequest.put("amount", Amount);
			paymentLinkRequest.put("currency", "INR");

			JSONObject customer=new JSONObject();
			customer.put("name", user.getFullName());

			customer.put("email", user.getEmail());
			paymentLinkRequest.put("customer", customer);

			JSONObject notify=new JSONObject();
			notify.put("email", true);
			paymentLinkRequest.put("notify", notify);

			paymentLinkRequest.put("reminder_enable", true);


			paymentLinkRequest.put("callback_url","http://localhost:5173/wallet?order_id=" + orderId);
			paymentLinkRequest.put("callback_method", "get");

			PaymentLink payment= razorpay.paymentLink.create(paymentLinkRequest);

			String paymentLinkId=payment.get("id");
			String paymentLinkUrl=payment.get("short_url");

			PaymentResponse res = new PaymentResponse();
			res.setPaymentUrl(paymentLinkUrl);

			return res;

		}catch (RazorpayException e){
			System.out.println("Error creating payment link: "+ e.getMessage());

			throw new RazorpayException(e.getMessage());

		}
		
		
		
	}
	
	
}
