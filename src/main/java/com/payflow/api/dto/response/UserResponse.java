package com.payflow.api.dto.response;

import com.payflow.api.domain.entity.User;
import com.payflow.api.domain.enums.UserType;
import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String fullName,
    String document,
    String email,
    UserType userType,
    LocalDateTime createdAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            user.getFullName(),
            user.getDocument(),
            user.getEmail(),
            user.getUserType(),
            user.getCreatedAt()
        );
    }
}
