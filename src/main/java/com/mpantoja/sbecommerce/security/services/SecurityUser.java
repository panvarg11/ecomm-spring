package com.mpantoja.sbecommerce.security.services;


import com.mpantoja.sbecommerce.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//this class decouples user and UserdDetails contracts
public class SecurityUser implements UserDetails {


    private final User user;

    public SecurityUser(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().map(role->new SimpleGrantedAuthority(role.getRoleName().name())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SecurityUser that)) return false;
        return Objects.equals(user, that.user);
    }

}
