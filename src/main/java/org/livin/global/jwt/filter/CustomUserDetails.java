package org.livin.global.jwt.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.livin.user.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

	private String provider;      // ex: kakao, naver
	private String providerId;    // ex: 12345678
	private UserRole role;        // ex: TENANT, LANDLORD

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override public String getPassword() { return null; }
	// @Override public String getUsername() { return provider + ":" + providerId; }
	@Override public String getUsername() { return providerId; }
	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }
	@Override public boolean isEnabled() { return true; }
}
