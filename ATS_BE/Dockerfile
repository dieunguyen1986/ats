# =============================================================================
# ATS Backend — Production-Ready Dockerfile
# =============================================================================
# Project     : Applicant Tracking System (ATS)
# Stack       : Spring Boot 3.5.x / Java 21 LTS / Maven
# Architecture: Modular Monolith (ADR-001)
# Author      : DevOps Engineer
# Updated     : 2026-04-24
#
# NOTE: Uses Java 21 LTS (eclipse-temurin:21) as mandated by ADR-001 and
#       coding_conventions/01_backend.md. Task requirement specified :17, but
#       the project's pom.xml declares <java.version>21</java.version>.
#       Using 17 would cause a compilation failure at build time.
# =============================================================================

# -----------------------------------------------------------------------------
# STAGE 1 — BUILD
# Base image: Full JDK 21 on Alpine for minimal footprint during compilation
# -----------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

LABEL stage="builder"

WORKDIR /workspace

# ── Dependency Cache Layer ────────────────────────────────────────────────────
# Copy only the POM first so Docker can cache this layer independently.
# Maven dependencies are only re-downloaded when pom.xml changes, NOT on
# every source code change — critical for fast CI/CD build times.
COPY pom.xml .

# Download all dependencies offline (cache them in the image layer).
# -B  = batch mode (no interactive prompts)
# -q  = quiet output
# dependency:go-offline ensures all artifacts are fetched including plugins.
RUN mvn -B -q dependency:go-offline

# ── Application Source ────────────────────────────────────────────────────────
# Copy source code AFTER dependency layer — keeps the cache hit for deps.
COPY src ./src

# ── Build the Fat JAR ─────────────────────────────────────────────────────────
# -DskipTests: Skip unit tests in Docker build (tests run in CI pipeline before
#              Docker build step, not inside the image build itself).
# -Dspring-boot.repackage.skip=false: Ensure spring-boot:repackage runs so the
#              executable fat JAR is produced by the spring-boot-maven-plugin.
RUN mvn -B -q clean package -DskipTests

# Verify the artifact was produced (fast-fail before runtime stage)
RUN ls -lh target/ats-be-*.jar

# -----------------------------------------------------------------------------
# STAGE 2 — RUNTIME
# Base image: Minimal JRE 21 on Alpine (~85MB) — no compiler, no build tools
# Security posture: Non-root user, read-only JAR, no shell login
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# ── OS Security Hardening ─────────────────────────────────────────────────────
# Install curl for HEALTHCHECK (Alpine ships without it by default on JRE image)
# Then clean apk cache to minimize image layer size.
RUN apk add --no-cache curl \
    && rm -rf /var/cache/apk/*

# ── Non-Root User Setup ───────────────────────────────────────────────────────
# Running as root inside a container is a critical security anti-pattern.
# Create a dedicated system user/group with no home directory and no shell.
# UID/GID 1001 avoids conflicts with common system UIDs (0=root, 1000=default).
RUN addgroup -S -g 1001 atsgroup \
    && adduser -S -u 1001 -G atsgroup -H -s /sbin/nologin atsuser

WORKDIR /app

# ── Copy Artifact ─────────────────────────────────────────────────────────────
# Copy only the executable JAR from the builder stage — nothing else.
# Rename to a stable, version-independent filename for predictable CMD usage.
COPY --from=builder /workspace/target/ats-be-*.jar app.jar

# Transfer file ownership to the non-root user before switching context.
RUN chown atsuser:atsgroup app.jar

# ── Switch to Non-Root User ───────────────────────────────────────────────────
USER atsuser

# ── Port Declaration ──────────────────────────────────────────────────────────
# Matches server.port=${SERVER_PORT:8080} in application.yml
EXPOSE 8080

# ── JVM Tuning ────────────────────────────────────────────────────────────────
# JAVA_OPTS is consumed by the ENTRYPOINT below.
# -XX:+UseContainerSupport    : JVM respects cgroup CPU/memory limits (default in JDK 11+, kept explicit)
# -XX:MaxRAMPercentage=75.0   : Use 75% of container memory for heap (leaves 25% for OS/metaspace)
# -Djava.security.egd=...     : Faster SecureRandom startup (avoids /dev/random blocking on Linux)
# -Dspring.profiles.active    : Override via env var SPRING_PROFILES_ACTIVE at runtime
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -Djava.security.egd=file:/dev/./urandom"

# ── Health Check ──────────────────────────────────────────────────────────────
# Uses Spring Boot Actuator's /actuator/health endpoint (requires
# spring-boot-starter-actuator on the classpath and management.endpoints
# configured in application.yml or via env var).
#
# interval=30s  : Check every 30 seconds
# timeout=10s   : Fail if no response within 10 seconds
# retries=3     : Mark UNHEALTHY after 3 consecutive failures
# start-period=60s : Grace period for JVM + Spring context startup (Flyway migrations
#                    can take 10-30s on first boot with full schema)
HEALTHCHECK --interval=30s \
            --timeout=10s \
            --retries=3 \
            --start-period=60s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# ── Entrypoint ────────────────────────────────────────────────────────────────
# Use exec form (JSON array) — avoids shell wrapper so signals (SIGTERM from
# `docker stop`) reach the JVM directly for graceful shutdown.
# $JAVA_OPTS is evaluated by the shell wrapper via sh -c.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
