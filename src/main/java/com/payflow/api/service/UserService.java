package com.payflow.api.service;

import com.payflow.api.domain.entity.User;
import com.payflow.api.domain.entity.Wallet;
import com.payflow.api.dto.request.CreateUserRequest;
import com.payflow.api.dto.response.UserResponse;
import com.payflow.api.exception.BusinessException;
import com.payflow.api.exception.EntityNotFoundException;
import com.payflow.api.repository.UserRepository;
import com.payflow.api.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByDocument(request.document())) {
            throw new BusinessException("Já existe um usuário cadastrado com este documento (CPF/CNPJ).");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }

        User user = new User(
                null,
                request.fullName(),
                request.document(),
                request.email(),
                request.password(), // Nota: em produção recomenda-se hash BCrypt
                request.userType()
        );

        User savedUser = userRepository.save(user);

        BigDecimal initialBalance = request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO;
        Wallet wallet = new Wallet(savedUser, initialBalance);
        walletRepository.save(wallet);

        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}
