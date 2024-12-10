package com.name.carregistry.service.impl;

import com.name.carregistry.domain.UserEntity;
import com.name.carregistry.repository.UserRepository;
import com.name.carregistry.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public UserEntity save(UserEntity newUser) {
        return userRepository.save(newUser);
    }

    @Override
    public byte[] getUserImage(Long id) {
        UserEntity entity = userRepository.findById(id).orElseThrow(RuntimeException::new);

        return Base64.getDecoder().decode(entity.getImage());
    }

    @Override
    public void addUserImage(Long id, MultipartFile file) throws IOException {
        UserEntity entity = userRepository.findById(id).orElseThrow(RuntimeException::new);

        log.info("Saving user image...");
        entity.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
        userRepository.save(entity);

    }
}