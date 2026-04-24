package com.ats.auth.repository;

import com.ats.auth.entity.SsoConfiguration;
import com.ats.auth.entity.SsoProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SsoConfigurationRepository extends JpaRepository<SsoConfiguration, Long> {
    Optional<SsoConfiguration> findByProviderTypeAndIsActiveTrue(SsoProviderType providerType);
}
