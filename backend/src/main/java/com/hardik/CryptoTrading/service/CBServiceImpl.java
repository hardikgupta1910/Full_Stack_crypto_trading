package com.hardik.CryptoTrading.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.hardik.CryptoTrading.dto.CBCoinDto;
import com.hardik.CryptoTrading.response.CBApiResponse;
import com.hardik.CryptoTrading.response.CBFunctionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CBServiceImpl implements CBService {
	


private String GEMINI_API_KEY;
	
	{
		GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
		
		if (GEMINI_API_KEY == null || GEMINI_API_KEY.isBlank()) {
			throw new IllegalStateException("GEMINI_API_KEY is not set in environment variables");
		}
	}
	
	
	private double convertToDouble(Object value){
		if (value == null) return 0.0;
		
		if(value instanceof Integer){
			return ((Integer)value).doubleValue();
		} else if (value instanceof Long) {
			return ((Long)value).doubleValue();
		}
		else if(value instanceof Double){
			return (Double)value;
		}
		else throw new IllegalArgumentException("unsupported type"+ value.getClass().getName());
	}
	


public CBCoinDto makeAPIRequest(String currencyName) throws Exception {
	String url = "https://api.coingecko.com/api/v3/coins/" + currencyName;
	
	RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);
	Map<String, Object> responseBody = responseEntity.getBody();
	
	if (responseBody != null) {
		
		Map<String, Object> image =
				(Map<String, Object>) responseBody.get("image");
		Map<String, Object> marketData =
				(Map<String, Object>) responseBody.get("market_data");
		
		if (image == null || marketData == null) {
			throw new Exception("Invalid CoinGecko response");
		}
		
		CBCoinDto cbCoinDto = new CBCoinDto();
		
		cbCoinDto.setId((String) responseBody.get("id"));
		cbCoinDto.setName((String) responseBody.get("name"));
		cbCoinDto.setSymbol((String) responseBody.get("symbol"));
		cbCoinDto.setImage((String) image.getOrDefault("large", ""));
		
		cbCoinDto.setCurrentPrice(
				convertToDouble(((Map<String, Object>) marketData.get("current_price")).get("usd"))
		);
		
		cbCoinDto.setMarketCap(
				convertToDouble(((Map<String, Object>) marketData.get("market_cap")).get("usd"))
		);
		
		Object rank = marketData.get("market_cap_rank");
		cbCoinDto.setMarketCapRank(rank == null ? 0.0 : convertToDouble(rank));
		
		cbCoinDto.setTotalVolume(
				convertToDouble(((Map<String, Object>) marketData.get("total_volume")).get("usd"))
		);
		
		cbCoinDto.setHigh24h(
				convertToDouble(((Map<String, Object>) marketData.get("high_24h")).get("usd"))
		);
		
		cbCoinDto.setLow24h(
				convertToDouble(((Map<String, Object>) marketData.get("low_24h")).get("usd"))
		);
		
		cbCoinDto.setPriceChange24h(convertToDouble(marketData.get("price_change_24h")));
		cbCoinDto.setPriceChangePercentage24h(convertToDouble(marketData.get("price_change_percentage_24h")));
		cbCoinDto.setMarketCapChange24h(convertToDouble(marketData.get("market_cap_change_24h")));
		cbCoinDto.setMarketCapChangePercentage24h(convertToDouble(marketData.get("market_cap_change_percentage_24h")));
		cbCoinDto.setCirculatingSupply(convertToDouble(marketData.get("circulating_supply")));
		cbCoinDto.setTotalSupply(convertToDouble(marketData.get("total_supply")));
		
		return cbCoinDto;
	}
	
	throw new Exception("coin not found");
}
	


public CBFunctionResponse getFunctionResponse(String prompt){
		String GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="+GEMINI_API_KEY;
		
		// Create JSON request body using method chaining
		JSONObject requestBodyJson=new JSONObject()
				.put("contents", new JSONArray()
						.put(new JSONObject()
								.put("parts", new JSONArray()
										.put(new JSONObject()
												.put("text", prompt)
										)
								)
						)
				)
				.put("tools", new JSONArray()
						.put(new JSONObject()
								.put("functionDeclarations", new JSONArray()
										.put(new JSONObject()
												.put("name", "getCoinDetails")
												.put("description", "Get the coin details from given currency object")
												.put("parameters", new JSONObject()
														.put("type", "OBJECT")
														.put("properties", new JSONObject()
																.put("currencyName", new JSONObject()
																		.put("type", "STRING")
																		.put("description",
																				"The currency name, "+
																						"id, symbol.")
																)
																.put("currencyData", new JSONObject()
																		.put("type", "STRING")
																		.put("description",
																				"Currency Data id, "+
																						"symbol, "+
																						"name, "+
																						"image, "+
																						"current_price ,"+
																						"market_cap, "+
																						"market_cap_rank, "+
																						"fully_diluted_valuation,"+
																						"total_volume, high_24h, "+
																						"low_24h, price_change_24h, "+
																						"price_change_percentage_24h, "+
																						"market_cap_change_24h, "+
																						"market_cap_change_percentage_24h, "+
																						"circulating_supply, "+
																						"total_supply, "+
																						"max_supply, "+
																						"ath, "+
																						"ath_change_percentage, "+
																						"ath_date, "+
																						"atl, "+
																						"atl_change_percentage, "+
																						"atl_date, last_updated.")
																)
														)
														.put("required", new JSONArray()
																.put("currencyData")
																.put("currencyName")
														
														)
												)
										)
								)
						)
				);
		
		
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson.toString(), headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity, String.class);
			
			String responseBody = response.getBody();
			JSONObject jsonObject = new JSONObject(responseBody);
			
			JSONArray candidates = jsonObject.getJSONArray("candidates");
			JSONObject firstCandidate = candidates.getJSONObject(0);
			JSONObject content = firstCandidate.getJSONObject("content");
			JSONArray parts = content.getJSONArray("parts");
			JSONObject firstPart = parts.getJSONObject(0);
			
			
			
			if (!firstPart.has("functionCall")) {
				
				String text = firstPart.optString("text", "").toLowerCase();
				
				String currencyName = "not_found";
				
				if (text.contains("bitcoin") || text.contains("btc")) currencyName = "bitcoin";
				else if (text.contains("ethereum") || text.contains("eth")) currencyName = "ethereum";
				else if (text.contains("dogecoin") || text.contains("doge")) currencyName = "dogecoin";
				else if (text.contains("xrp")) currencyName = "ripple";
				else if (text.contains("usdc")) currencyName = "usd-coin";
				
				CBFunctionResponse fallback = new CBFunctionResponse();
				fallback.setFunctionName("fallback");
				fallback.setCurrencyName(currencyName);
				fallback.setCurrencyData("");
				
				return fallback;
			}
			
			JSONObject functionCall = firstPart.getJSONObject("functionCall");
			String functionName = functionCall.getString("name");
			JSONObject args = functionCall.getJSONObject("args");
			String currencyName = args.getString("currencyName");
			String currencyData = args.getString("currencyData");
			
			CBFunctionResponse res = new CBFunctionResponse();
			res.setFunctionName(functionName);
			res.setCurrencyName(currencyName);
			res.setCurrencyData(currencyData);
			return res;
			
		} catch (Exception e) {
			// Catch any JSON issues
			CBFunctionResponse errorRes = new CBFunctionResponse();
			errorRes.setFunctionName("not_found");
			errorRes.setCurrencyName("not_found");
			errorRes.setCurrencyData("not_found");
			e.printStackTrace();
			return errorRes;
		}
	}
	
	


	
	@Override
	public CBApiResponse getCoinDetails(String prompt) throws Exception {
		
		String input = prompt == null ? "" : prompt.toLowerCase().trim();

// remove quotes if any
		input = input.replace("\"", "").replace("\r", "").replace("\n", "");
		
		System.out.println("PROMPT RAW = [" + prompt + "]");
		System.out.println("INPUT CLEAN = [" + input + "]");
		System.out.println("LENGTH = " + input.length());
		
		// 🔥 STEP 1: Intent detection
		boolean askMarketCap = input.contains("market cap");
		boolean askVolume = input.contains("volume");
		boolean askHigh = input.contains("high");
		boolean askLow = input.contains("low");
		boolean askComparison = input.contains("compare") || input.contains("vs");
		
		// 🔥 STEP 2: COMPARISON (NO GEMINI NEEDED)
		if (askComparison) {
			
			List<String> coins = extractCoins(input);
			
			if (coins.size() < 2) {
				CBApiResponse res = new CBApiResponse();
				res.setMessage("Please provide two coins (e.g., BTC vs ETH)");
				return res;
			}
			
			CBCoinDto coin1 = makeAPIRequest(coins.get(0));
			CBCoinDto coin2 = makeAPIRequest(coins.get(1));
			
			double val1, val2;
			String metric;
			
			if (askMarketCap) {
				val1 = coin1.getMarketCap();
				val2 = coin2.getMarketCap();
				metric = "market cap";
			}
			else if (askVolume) {
				val1 = coin1.getTotalVolume();
				val2 = coin2.getTotalVolume();
				metric = "volume";
			}
			else {
				val1 = coin1.getCurrentPrice();
				val2 = coin2.getCurrentPrice();
				metric = "price";
			}
			
			String higher = val1 > val2 ? coin1.getName() : coin2.getName();
			double diff = Math.abs(val1 - val2);
			double percent = Math.min(val1, val2) != 0 ? (diff / Math.min(val1, val2)) * 100 : 0;
			
			CBApiResponse response = new CBApiResponse();
			response.setMessage(
					coin1.getName() + " " + metric + " is $" + val1 + ", " +
							coin2.getName() + " " + metric + " is $" + val2 + ". " +
							higher + " is higher by $" + diff + " (" + String.format("%.2f", percent) + "%)"
			);
			
			return response;
		}
		
		// 🔥 STEP 3: Manual coin detection
		String currencyName = null;
		
		if (input.contains("bitcoin") || input.contains("btc")) currencyName = "bitcoin";
		else if (input.matches(".*\\beth\\b.*")) currencyName = "ethereum";
		else if (input.contains("xrp")) currencyName = "ripple";
		else if (input.contains("sol")) currencyName = "solana";
		else if (input.contains("bnb")) currencyName = "binancecoin";
		else if (input.contains("doge")) currencyName = "dogecoin";
		else if (input.contains("polkadot") || input.contains("dot")) currencyName = "polkadot";
		
		// 🔥 STEP 4: Gemini fallback (ONLY ONCE)
		if (currencyName == null) {
			try {
				System.out.println("PROMPT RAW = [" + prompt + "]");
				System.out.println("INPUT CLEAN = [" + input + "]");
				System.out.println("LENGTH = " + input.length());
				CBFunctionResponse res = getFunctionResponse(prompt);
				
				currencyName = res.getCurrencyName();
				System.out.println("Gemini detected: " + currencyName);
			} catch (Exception e) {
				System.out.println("Gemini failed: " + e.getMessage());
			}
		}
		
		// 🔥 STEP 5: Validation
		if (currencyName == null || currencyName.isEmpty()
				|| currencyName.equalsIgnoreCase("not_found")
				|| currencyName.equalsIgnoreCase("undefined")) {
			
			CBApiResponse error = new CBApiResponse();
			error.setMessage("AI is busy (rate limited). Try again.");
			return error;
		}
		
		// 🔥 STEP 6: Fetch real data
		CBCoinDto apiResponse = makeAPIRequest(currencyName.toLowerCase());
		
		// 🔥 STEP 7: BACKEND FORMATTING (NO GEMINI)
		String message;
		
		if (askMarketCap) {
			message = "The market cap of " + apiResponse.getName() + " is $" + apiResponse.getMarketCap();
		}
		else if (askVolume) {
			message = "The trading volume of " + apiResponse.getName() + " is $" + apiResponse.getTotalVolume();
		}
		else if (askHigh) {
			message = "The 24-hour high of " + apiResponse.getName() + " is $" + apiResponse.getHigh24h();
		}
		else if (askLow) {
			message = "The 24-hour low of " + apiResponse.getName() + " is $" + apiResponse.getLow24h();
		}
		else {
			message = "The price of " + apiResponse.getName() + " is $" + apiResponse.getCurrentPrice();
		}
		
		CBApiResponse response = new CBApiResponse();
		response.setMessage(message);
		
		return response;
	}
	
	private List<String> extractCoins(String input) {
		
		List<String> coins = new ArrayList<>();
		
		if (input.contains("bitcoin") || input.contains("btc")) coins.add("bitcoin");
		if (input.contains("ethereum") || input.contains("eth")) coins.add("ethereum");
		if (input.contains("sol")) coins.add("solana");
		if (input.contains("bnb")) coins.add("binancecoin");
		if (input.contains("xrp")) coins.add("ripple");
		if (input.contains("doge")) coins.add("dogecoin");
		if (input.contains("dot") || input.contains("polkadot")) coins.add("polkadot");
		
		return coins;
	}




	// simple chat
	@Override
	public String simpleChat(String prompt) {
		String GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="+GEMINI_API_KEY ;
		
		HttpHeaders headers=new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String requestBody=new JSONObject()
				.put("contents", new JSONArray()
						.put(new JSONObject()
								.put("parts", new JSONArray()
										.put(new JSONObject().put("text", prompt))))).toString();
		
		HttpEntity<String> requestEntity=new HttpEntity<>(requestBody,headers);
		
		RestTemplate restTemplate=new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(GEMINI_API_URL, requestEntity , String.class);
		
		return response.getBody();
	}
}