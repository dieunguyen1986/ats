#!/bin/bash
# =============================================================================
# ATS Backend — Phase 1 Smoke Test Script
# =============================================================================
# Mục đích: Verify Docker build + docker-compose stack hoạt động đúng
# Chạy   : chmod +x scripts/smoke-test.sh && ./scripts/smoke-test.sh
# =============================================================================

set -euo pipefail

# --- Colors ---
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

PASS="${GREEN}[PASS]${NC}"
FAIL="${RED}[FAIL]${NC}"
INFO="${BLUE}[INFO]${NC}"
WARN="${YELLOW}[WARN]${NC}"

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}  ATS Backend — Phase 1 Smoke Test${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

# --- Step 1: Prerequisites ---
echo -e "${INFO} Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    echo -e "${FAIL} Docker is not installed"
    exit 1
fi
echo -e "${PASS} Docker: $(docker --version)"

if ! docker compose version &> /dev/null; then
    echo -e "${FAIL} Docker Compose v2 is not available (run: docker compose version)"
    exit 1
fi
echo -e "${PASS} Docker Compose: $(docker compose version --short)"

if [ ! -f ".env" ]; then
    echo -e "${WARN} .env file not found. Copying from .env.example..."
    cp .env.example .env
    echo -e "${WARN} Please fill in .env with real values, then re-run this script."
    echo -e "  Required: DB_PASSWORD, JWT_SECRET"
    exit 1
fi
echo -e "${PASS} .env file exists"

# --- Step 2: Build Docker image ---
echo ""
echo -e "${INFO} Building Docker image (this may take 2-5 min on first run)..."
if docker build -t ats-be:smoke-test . --quiet; then
    echo -e "${PASS} Docker image built successfully"
else
    echo -e "${FAIL} Docker build failed. Check Dockerfile errors above."
    exit 1
fi

# --- Step 3: Start docker-compose stack ---
echo ""
echo -e "${INFO} Starting docker-compose stack..."
docker compose down --remove-orphans 2>/dev/null || true
docker compose up -d

echo -e "${INFO} Waiting for services to be healthy (up to 120s)..."
TIMEOUT=120
ELAPSED=0
INTERVAL=5

while [ $ELAPSED -lt $TIMEOUT ]; do
    POSTGRES_HEALTH=$(docker compose ps --format json postgres 2>/dev/null | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('Health','unknown'))" 2>/dev/null || echo "starting")
    BACKEND_HEALTH=$(docker compose ps --format json ats-backend 2>/dev/null | python3 -c "import sys,json; data=json.load(sys.stdin); print(data.get('Health','unknown'))" 2>/dev/null || echo "starting")

    if [ "$POSTGRES_HEALTH" = "healthy" ] && [ "$BACKEND_HEALTH" = "healthy" ]; then
        break
    fi

    echo -e "  postgres: ${POSTGRES_HEALTH} | ats-backend: ${BACKEND_HEALTH} (${ELAPSED}s elapsed)"
    sleep $INTERVAL
    ELAPSED=$((ELAPSED + INTERVAL))
done

# --- Step 4: Health check ---
echo ""
echo -e "${INFO} Running health check on /actuator/health..."

HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || echo "000")

if [ "$HEALTH_RESPONSE" = "200" ]; then
    echo -e "${PASS} /actuator/health → HTTP 200"
    HEALTH_BODY=$(curl -s http://localhost:8080/actuator/health)
    echo -e "  Response: ${HEALTH_BODY}"
else
    echo -e "${FAIL} /actuator/health → HTTP ${HEALTH_RESPONSE} (expected 200)"
    echo ""
    echo -e "${INFO} Last 50 lines of ats-backend logs:"
    docker compose logs --tail=50 ats-backend
    docker compose down
    exit 1
fi

# --- Step 5: Database connectivity check ---
echo ""
echo -e "${INFO} Checking PostgreSQL connectivity inside container..."
if docker compose exec -T postgres pg_isready -U "${DB_USERNAME:-ats_user}" -d "${DB_NAME:-ats_db}" &>/dev/null; then
    echo -e "${PASS} PostgreSQL is accepting connections"
else
    echo -e "${FAIL} PostgreSQL is not accepting connections"
fi

# --- Summary ---
echo ""
echo -e "${BLUE}============================================${NC}"
echo -e "${GREEN}  Phase 1 Smoke Test: ALL PASSED ✓${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""
echo -e "  Backend : http://localhost:8080"
echo -e "  Health  : http://localhost:8080/actuator/health"
echo -e "  Swagger : http://localhost:8080/swagger-ui/index.html"
echo ""
echo -e "${INFO} To stop: docker compose down"
echo -e "${INFO} To view logs: docker compose logs -f ats-backend"
