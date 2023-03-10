package com.lawzone.market.user.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lawzone.market.user.service.UserInfo;

public interface UserInfoDAO extends JpaRepository<UserInfo, Long>{
	Optional<UserInfo> findByuserName(String userName);
	List<UserInfo> findBySocialIdAndEmailAndUserName(String socialId, String email, String userName);
	List<UserInfo> findBySocialIdAndEmailAndUseYn(String socialId, String email, String useYn);
	List<UserInfo> findBySocialId(String socialId);
	List<UserInfo> findBySocialIdAndUseYn(String socialId, String useYn);
	Optional<UserInfo> findByEmail(String email);
	Optional<UserInfo> findByLoginId(String loginId);
	Optional<UserInfo> findByLoginIdAndUseYn(String loginId, String useYn);
	Optional<UserInfo> findByPhoneNumber(String phoneNumber);
	List<UserInfo> findByUserId(String userId);
	List<UserInfo> findByUserIdAndUseYn(String userId, String useYn);
	Optional<UserInfo> findByNickname(String nickname);
	Optional<UserInfo> findByLoginIdAndPasswordAndUseYn(String loginId, String pssword, String useYn);
}
