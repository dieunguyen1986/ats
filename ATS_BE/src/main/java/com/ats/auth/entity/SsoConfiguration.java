package com.ats.auth.entity;

import com.ats.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "sso_configurations")
@SQLRestriction("is_deleted = false")
public class SsoConfiguration extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, length = 50)
    private SsoProviderType providerType;

    @Column(name = "ldap_url", length = 500)
    private String ldapUrl;

    @Column(name = "base_dn", length = 500)
    private String baseDn;

    @Column(name = "bind_user", length = 255)
    private String bindUser;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
