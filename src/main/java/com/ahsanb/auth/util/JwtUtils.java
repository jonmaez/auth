package com.ahsanb.auth.util;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.ahsanb.auth.tenant.entities.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {
	
    @Value("${security.jwt.expiration:#{24*60*60}}")
    private int expiration;

    @Value("${security.jwt.secret:JwtSecretKey}")
    private String secret;

	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				// Convert to list of strings. 
				// This is important because it affects the way we get them back in the Gateway.
				.claim("authorities", authentication.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + expiration * 1000))
				.signWith(SignatureAlgorithm.HS512, secret.getBytes())
				.compact();
	}
}
