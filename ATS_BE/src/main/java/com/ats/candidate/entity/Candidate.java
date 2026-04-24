package com.ats.candidate.entity;

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
@Table(name = "candidates")
@SQLRestriction("is_deleted = false")
public class Candidate extends BaseEntity {

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", length = 50)
    private AuthProvider authProvider;

    @Column(name = "oauth_provider_id", length = 255)
    private String oauthProviderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CandidateStatus status = CandidateStatus.ACTIVE;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "source", length = 150)
    private String source;

    @Column(name = "utm_source", length = 150)
    private String utmSource;

    @Column(name = "utm_medium", length = 150)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 255)
    private String utmCampaign;

    @Column(name = "is_duplicate", nullable = false)
    private boolean isDuplicate = false;
}
