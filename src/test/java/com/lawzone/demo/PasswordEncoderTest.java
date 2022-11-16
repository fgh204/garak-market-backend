package com.lawzone.demo;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {
	@Autowired
	private PasswordEncoder passwordEncoder;
  
	@Test
	@DisplayName("패스워드 암호화 테스트")
	public void main() {
		// given
		String rawPassword = "12345678";

		// when
		String encodedPassword = passwordEncoder.encode(rawPassword);
		System.out.println("Enc:"
				+ ""+encodedPassword);
		// then
		assertAll(
            () -> assertNotEquals(rawPassword, encodedPassword),
            () -> assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
      );
   }
}
