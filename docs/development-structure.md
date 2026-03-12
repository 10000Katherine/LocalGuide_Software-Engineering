# Development Structure

This repository uses a module-first structure so 4 developers can work in parallel with minimal merge conflicts.

## Folder Layout

```text
apps/
  backend/
    src/main/java/com/localguide/
      common/
      modules/
        auth-user/
        guide-tour/
        booking-payment/
        review-admin/
  web/
    src/
      shared/
      modules/
        auth-user/
        guide-tour/
        booking-payment/
        review-admin/

contracts/
  openapi/
  postman/

infra/
  docker/
  nginx/
  github/

scripts/
```

## Collaboration Rules

1. Each developer works mainly in their own module folder.
   - For Java backend packages, use package-safe names (example: `authuser` for `auth-user`).
2. Cross-module API changes must be updated in `contracts/openapi/` first.
3. Shared code should go in `apps/backend/.../common/` or `apps/web/src/shared/`.
4. Create pull requests from `feature/*` into `develop`.
5. Merge to `main` only after integration test on `develop` passes.

## Recommended Branch Naming

- `feature/dev1-auth-user-*`
- `feature/dev2-guide-tour-*`
- `feature/dev3-booking-payment-*`
- `feature/dev4-review-admin-*`

## Dev1 Quick Start

Backend (`apps/backend`):
1. Create `.env` from `apps/backend/.env.example` and set `JWT_SECRET`.
2. Run with Java 17 and Maven: `mvn spring-boot:run`.

Frontend (`apps/web`):
1. Create `.env` from `apps/web/.env.example`.
2. Install dependencies: `npm install`.
3. Start dev server: `npm run dev`.

Auth session behavior:
- Access token is attached via axios request interceptor.
- On `401`, frontend attempts `/auth/refresh` once.
- If refresh fails, frontend clears tokens and redirects to `/login`.

Auth API base path: `/api/v1`
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/reset-password/request`
- `POST /auth/reset-password/confirm`
- `GET /tourists/me`
- `PUT /tourists/me`
