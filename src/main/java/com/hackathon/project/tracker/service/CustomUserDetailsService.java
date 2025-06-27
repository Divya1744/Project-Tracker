package com.hackathon.project.tracker.service;

import com.hackathon.project.tracker.model.UserEntity;
import com.hackathon.project.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for the email "+email));
        return new User(userEntity.getEmail(),userEntity.getPassword(), Collections.singleton(new SimpleGrantedAuthority(userEntity.getRole())));
    }
}
