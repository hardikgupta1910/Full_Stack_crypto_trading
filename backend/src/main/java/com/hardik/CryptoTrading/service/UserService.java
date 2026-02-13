package com.hardik.CryptoTrading.service;

import com.hardik.CryptoTrading.model.User;

import java.util.List;

public interface UserService {
	
	public User findUserProfileByJwt(String jwt) throws Exception;
	public User findUserByEmail(String email) throws Exception;
	public User findUserById(Long userId) throws Exception;
	

	
	User updatePassword(User user , String newPassword);
	List<User> findAllUsers();
}
