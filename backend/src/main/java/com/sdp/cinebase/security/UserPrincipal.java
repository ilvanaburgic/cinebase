package com.sdp.cinebase.security;

import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username;

    public UserPrincipal(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() { return id; }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return username; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public java.util.Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return java.util.List.of();
    }
}
