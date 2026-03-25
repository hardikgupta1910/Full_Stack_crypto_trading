package com.hardik.CryptoTrading.controller;

import com.hardik.CryptoTrading.dto.PromptBody;
import com.hardik.CryptoTrading.response.CBApiResponse;
import com.hardik.CryptoTrading.service.CBService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ai/chat")
public class ChatbotController {
	
	private final CBService cbService;
	
	public ChatbotController(CBService cbService) {
		this.cbService = cbService;
	}
	
	
	@PostMapping
	public ResponseEntity<CBApiResponse> getCoinDetails(@RequestBody PromptBody prompt) throws Exception {
		
		CBApiResponse response=cbService.getCoinDetails(prompt.getPrompt());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/simple")
	public ResponseEntity<String> simpleChatHandler(@RequestBody PromptBody prompt) throws Exception {
		
		String response= cbService.simpleChat(prompt.getPrompt());
		

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
