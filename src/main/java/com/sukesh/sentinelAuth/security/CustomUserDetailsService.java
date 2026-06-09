package com.sukesh.sentinelAuth.security;

import com.sukesh.sentinelAuth.entity.Users;
import com.sukesh.sentinelAuth.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByName(username).orElseThrow(()->{
            throw new UsernameNotFoundException("User not found");
        });
    }

    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException
    {
        return usersRepository.findById(Long.parseLong(userId)).orElseThrow(()->{
            throw new UsernameNotFoundException("user not found");
        });
    }
}
