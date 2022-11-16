package com.lawzone.market.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lawzone.market.user.dao.UserInfoDAO;
import com.lawzone.market.user.service.UserInfo;
import com.lawzone.market.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserInfoDAO userInfoDAO;
    private final JwtTokenUtil jwtTokenUtil;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> _userInfo = this.userInfoDAO.findByEmailAndPasswordIsNotNull(username);
        if (_userInfo.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        UserInfo userInfo = _userInfo.get();
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        //HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.add("Authorization", jwtTokenUtil.generateToken(userInfo.getSocialId()));
        return new User(userInfo.getUserName(), userInfo.getPassword(), authorities);
    }
}
