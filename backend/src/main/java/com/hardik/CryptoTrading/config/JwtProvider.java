package com.hardik.CryptoTrading.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import io.jsonwebtoken.SignatureAlgorithm;


public class JwtProvider {
	
	private static SecretKey key =  Keys.hmacShaKeyFor(JwtConstant.SECRETE_KEY.getBytes());
	


public static String generateToken(Authentication auth){
	Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
	String roles = populateAuthorities(authorities);
	
	String jwt = Jwts.builder()
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + 86400000))
			.claim("email", auth.getName())
			.claim("authorities", roles)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	
	return jwt;
}
	
	
	// by this method we can access email by proving jwt token
	public static String getEmailFromToken(String token){
		token=token.substring(7);
		Claims claims= (Claims) Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token )
				.getBody();
		
		
		String email= String.valueOf(claims.get("email"));
		
		return email;
	}
	
	private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Set<String> auth=new HashSet<>();
		for(GrantedAuthority ga: authorities){

			
				String role = ga.getAuthority();
				if (!role.startsWith("ROLE_")) {
					role = "ROLE_" + role;
				}
				auth.add(role);
		}
		return String.join(",", auth);
	}
	
	
}
