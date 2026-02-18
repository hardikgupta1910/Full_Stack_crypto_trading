package com.hardik.CryptoTrading.controller;

import com.hardik.CryptoTrading.model.Coin;
import com.hardik.CryptoTrading.model.User;
import com.hardik.CryptoTrading.model.Watchlist;
import com.hardik.CryptoTrading.service.CoinService;
import com.hardik.CryptoTrading.service.UserService;
import com.hardik.CryptoTrading.service.WatchlistService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
	
	@Autowired
	private WatchlistService watchlistService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CoinService coinService;
	
	
	@GetMapping("/user")
	public ResponseEntity<Watchlist> getUserWatchList(@RequestHeader("Authorization") String jwt) throws Exception {
		
		User user=userService.findUserProfileByJwt(jwt);
		
		Watchlist watchlist=watchlistService.findUserWatchlist(user.getId());
		
		return ResponseEntity.ok(watchlist);
	}
	
	@PostMapping("/create")
	public ResponseEntity<Watchlist> createWatchlist(@RequestHeader("Authorization") String jwt) throws Exception {
		
		User user=userService.findUserProfileByJwt(jwt);
		
		Watchlist createWatchlist=watchlistService.createWatchlist(user);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(createWatchlist);
	}
	
	@GetMapping("{watchlistId}")
	public ResponseEntity<Watchlist>getWatchlistById(@PathVariable Long watchListId) throws Exception {
		
		Watchlist watchlist=watchlistService.findById(watchListId);
		
		return  ResponseEntity.ok(watchlist);
	}
	

	
	@PostMapping("/add/coin/{coinId}")
	public ResponseEntity<Coin> addItemToWatchlist(
			@RequestHeader("Authorization") String jwt,
			@PathVariable String coinId) throws Exception {
		
		User user = userService.findUserProfileByJwt(jwt);
		Coin coin = coinService.findById(coinId);
		Coin addedCoin = watchlistService.addItemToWatchlist(coin, user);
		
		return ResponseEntity.ok(addedCoin);
	}
}
