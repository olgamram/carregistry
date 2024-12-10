package com.name.carregistry.service;

import com.name.carregistry.domain.UserEntity;
import com.name.carregistry.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    public UserEntity save(UserEntity newUser);

    byte[] getUserImage(Long id);

    void addUserImage(Long id, MultipartFile file) throws IOException;

}
