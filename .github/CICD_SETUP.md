# CI/CD Setup Guide — ATS Backend (Render)

## Overview

Pipeline gồm 3 workflows + Render IaC:

| File | Trigger | Thời gian | Mục đích |
|---|---|---|---|
| `ci-backend.yml` | Push/PR → `develop`, `main` | ~4 min | Build + Test + Docker validate |
| `deploy-backend.yml` | Push → `main` only | ~7-10 min | Trigger Render redeploy → verify health |
| `health-check.yml` | Mỗi 15 phút + manual | ~30 sec | Monitor Render service uptime |
| `render.yaml` | Render Blueprint | One-time | Định nghĩa toàn bộ infrastructure |

---

## Bước 1: Tạo Render Account & Services

### 1.1 Đăng ký Render

1. Vào [render.com](https://render.com) → Sign up bằng GitHub account
2. Connect GitHub repository

### 1.2 Apply Blueprint (Khuyến nghị — tự động tạo services)

1. Vào **Dashboard → Blueprints → New Blueprint Instance**
2. Chọn GitHub repo → Render tự đọc `render.yaml`
3. Render tạo:
   - **Web Service** `ats-backend` (từ Dockerfile)
   - **Managed PostgreSQL** `ats-postgres`
4. Sau khi tạo: điền `JWT_SECRET` thủ công (Render sẽ hiện popup)

> **Lấy JWT_SECRET:** `openssl rand -hex 32` (chạy trên terminal)

### 1.3 Hoặc tạo thủ công từng service

**Web Service:**
- New → Web Service → Connect GitHub repo
- Runtime: **Docker**
- Dockerfile Path: `./ATS_BE/Dockerfile`
- Docker Context: `./ATS_BE`
- Branch: `main`
- Auto-Deploy: **No** (GitHub Actions quản lý)
- Health Check Path: `/actuator/health`
- Plan: **Starter** ($7/tháng)

**PostgreSQL Database:**
- New → PostgreSQL
- Name: `ats-postgres`
- Database: `ats_db`
- User: `ats_user`
- Plan: **Free** (dev) / **Starter** (prod)

---

## Bước 2: Lấy Render Credentials

### 2.1 Render API Key

1. **Render Dashboard → Account Settings → API Keys**
2. Click **Create API Key**
3. Đặt tên: `github-actions-ats`
4. Copy key (dạng `rnd_xxxxxxxxxxxxxxxxxx`)

### 2.2 Service ID

1. **Render Dashboard → Web Services → ats-backend**
2. Vào tab **Settings**
3. Copy **Service ID** (dạng `srv-xxxxxxxxxxxxxxxxxx`)

### 2.3 Service URL

- Render tự assign URL dạng: `ats-backend-xxxx.onrender.com`
- Lấy từ Dashboard → Web Services → ats-backend → URL

---

## Bước 3: Thêm GitHub Repository Secrets

Vào: **GitHub repo → Settings → Secrets and variables → Actions → New repository secret**

### Secrets bắt buộc (chỉ 3 secrets!)

| Secret Name | Giá trị | Lấy từ đâu |
|---|---|---|
| `RENDER_API_KEY` | `rnd_xxxxxxxxxxxx` | Render → Account Settings → API Keys |
| `RENDER_SERVICE_ID` | `srv-xxxxxxxxxxxx` | Render → ats-backend → Settings → Service ID |
| `RENDER_SERVICE_URL` | `ats-backend-xxxx.onrender.com` | Render → ats-backend → URL (không có `https://`) |

> **Lưu ý:** DB credentials và JWT_SECRET được quản lý trực tiếp trên **Render Dashboard → Environment** — KHÔNG cần thêm vào GitHub Secrets.

---

## Bước 4: Cấu hình Environment Variables trên Render

Vào **Render Dashboard → ats-backend → Environment**:

| Variable | Value | Source |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` | Manual |
| `SERVER_PORT` | `8080` | Manual |
| `JWT_SECRET` | `<openssl rand -hex 32>` | Manual |
| `JWT_EXPIRATION_MS` | `86400000` | Manual |
| `DB_HOST` | Auto | Linked từ ats-postgres |
| `DB_PORT` | Auto | Linked từ ats-postgres |
| `DB_NAME` | Auto | Linked từ ats-postgres |
| `DB_USERNAME` | Auto | Linked từ ats-postgres |
| `DB_PASSWORD` | Auto | Linked từ ats-postgres |

**Link Database với Web Service:**
- Render Dashboard → ats-backend → Environment → Add Environment Variable
- Chọn **Link to Database** → chọn `ats-postgres` → chọn property

---

## Branching Strategy

```
feature/xxx  →  develop  →  main
                  │              │
                  │              └── CI pass → deploy-backend.yml triggers Render
                  │
                  └── CI only (build + test, NO deploy)
```

---

## Architecture on Render

```
GitHub Push (main)
       │
       ▼
  [ci-backend.yml]          Runs tests, validates
       │ pass
       ▼
  [deploy-backend.yml]
  ┌────────────────────────────────────────┐
  │ 1. POST /api/render.com/v1/deploys     │
  │ 2. Poll deploy status (max 10 min)     │
  │ 3. Verify /actuator/health → HTTPS     │
  └────────────────────────────────────────┘
       │
       ▼ Render Infrastructure
  ┌─────────────────────────────────────────────┐
  │  Render Web Service (ats-backend)           │
  │  ┌──────────────────────────────────────┐   │
  │  │  Docker container (eclipse-temurin)  │   │
  │  │  Spring Boot :8080                   │   │
  │  │  /actuator/health ✓                  │   │
  │  └──────────────────────────────────────┘   │
  │                    │ JDBC                    │
  │  ┌──────────────────────────────────────┐   │
  │  │  Render Managed PostgreSQL           │   │
  │  │  Auto backup + restore              │   │
  │  └──────────────────────────────────────┘   │
  │                                             │
  │  URL: https://ats-backend-xxxx.onrender.com │
  │  TLS: Auto (Let's Encrypt managed by Render)│
  └─────────────────────────────────────────────┘
```

---

## Cost Estimate (Render)

| Service | Plan | Cost/month |
|---|---|---|
| Web Service (ats-backend) | Starter | $7 |
| PostgreSQL | Free | $0 (dev) / $7 (prod starter) |
| **Total** | | **$7-14/mo** |

> So với DigitalOcean VPS $12-24/mo + quản lý manual, Render **đơn giản hơn** và **tương đương giá** với managed services.

---

## Troubleshooting

### Deploy triggered nhưng Render không build

```
Kiểm tra: Render Dashboard → ats-backend → Events
Nguyên nhân thường gặp:
- Branch sai (phải là main)
- Dockerfile có lỗi syntax
- Build timeout (default 20 min)
```

### Health check fail sau deploy

```
1. Render Dashboard → ats-backend → Logs (real-time)
2. Tìm exception trong Spring Boot startup
3. Thường gặp: DB connection fail → kiểm tra Environment variables DB_HOST, DB_PASSWORD
4. Flyway migration fail → xem logs chi tiết
```

### Service trả về 503

```
Render tự động restart service nếu health check fail.
Kiểm tra: Dashboard → Events → "Health check failed" events
→ Xem Logs tại thời điểm đó
```

### Free tier PostgreSQL hết hạn sau 90 ngày

```
Render Free PostgreSQL tự xóa sau 90 ngày inactive.
→ Upgrade lên Starter ($7/mo) trước khi ra production
→ Hoặc dùng Supabase (free tier không expire)
```
