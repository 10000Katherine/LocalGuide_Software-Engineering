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
2. Cross-module API changes must be updated in `contracts/openapi/` first.
3. Shared code should go in `apps/backend/.../common/` or `apps/web/src/shared/`.
4. Create pull requests from `feature/*` into `develop`.
5. Merge to `main` only after integration test on `develop` passes.

## Recommended Branch Naming

- `feature/dev1-auth-user-*`
- `feature/dev2-guide-tour-*`
- `feature/dev3-booking-payment-*`
- `feature/dev4-review-admin-*`
