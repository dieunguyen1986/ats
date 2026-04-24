package com.ats.auth.dto;

import com.ats.auth.entity.SsoProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SsoLoginRequest(
        @NotBlank(message = "SSO Token is required")
        String ssoToken,

        @NotNull(message = "Provider is required")
        SsoProviderType provider
) {
}
