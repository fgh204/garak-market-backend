package com.lawzone.market.user;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lawzone.market.DataNotFoundException;
import com.lawzone.market.config.exception.AdminException;
import com.lawzone.market.file.controller.FileController;
import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.service.UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final UserInfoDAO userInfoDAO;
	private final PasswordEncoder passwordEncoder;
	
	public SiteUser create(String username, String email, String password) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		this.userRepository.save(user);
		return user;
	}
	
	public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
	
	public Optional<UserInfo> getLogInInfo(String email, String password) {
        Optional<UserInfo> userInfo = this.userInfoDAO.findByEmailAndPasswordIsNotNull(email);

        if (userInfo.isPresent()) {
            if(!passwordEncoder.matches(password, userInfo.get().getPassword())) {
            	return java.util.Optional.empty();
            }
        } 
        
        return userInfo;
    }
}
