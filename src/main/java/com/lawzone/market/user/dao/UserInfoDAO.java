package com.lawzone.market.user.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.user.service.UserInfo;

public interface UserInfoDAO extends JpaRepository<UserInfo, Long>{
	Optional<UserInfo> findByuserName(String userName);
	List<UserInfo> findBySocialIdAndEmailAndUserName(String socialId, String email, String userName);
	Optional<UserInfo> findBySocialId(String socialId);
	Optional<UserInfo> findByEmail(String email);
	List<UserInfo> findByUserId(String userId);
	Optional<UserInfo> findByNickname(String nickname);
}
