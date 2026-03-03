# Module Ownership

This document defines module boundaries and ownership for collaboration.

## Dev1 - Auth and User

- Backend: `apps/backend/src/main/java/com/localguide/modules/auth-user/`
- Java package implementation: `apps/backend/src/main/java/com/localguide/modules/authuser/` (hyphen is not valid in Java package names)
- Frontend: `apps/web/src/modules/auth-user/`
- Main scope: register, login, refresh token, reset password, tourist profile (`/tourists/me`)
- Upstream dependency: none (this is the startup blocker module)
- Downstream consumers: Dev2, Dev3, Dev4 all depend on JWT/auth foundation

## Dev2 - Guide, Tour, Search, Availability

- Backend: `apps/backend/src/main/java/com/localguide/modules/guide-tour/`
- Frontend: `apps/web/src/modules/guide-tour/`
- Main scope: guide profile, tour CRUD, search/filter, availability slots
- Depends on: Dev1 auth foundation
- Provides to: Dev3 booking/tour references, Dev4 review/guide references

## Dev3 - Booking and Payment

- Backend: `apps/backend/src/main/java/com/localguide/modules/booking-payment/`
- Frontend: `apps/web/src/modules/booking-payment/`
- Main scope: bookings, Stripe payment intent/webhook, refund, email notifications
- Depends on: Dev1 auth foundation, Dev2 tour/availability data
- Provides to: Dev4 review trigger from completed booking

## Dev4 - Review, Favorites, Admin, DevOps

- Backend: `apps/backend/src/main/java/com/localguide/modules/review-admin/`
- Frontend: `apps/web/src/modules/review-admin/`
- Main scope: reviews/replies, favorites, admin verification, user management, CI/CD and deployment
- Depends on: Dev1 auth, Dev2 guide/tour, Dev3 completed booking/payment status

## Shared Areas

- Backend shared: `apps/backend/src/main/java/com/localguide/common/`
- Frontend shared: `apps/web/src/shared/`
- API contracts: `contracts/openapi/`
- Infra and CI: `infra/`

## Merge Safety Rules

1. Only touch another module when integrating across module boundaries.
2. If touching another module, mention owner in PR description.
3. Keep PRs small and module-scoped whenever possible.
4. API changes must include contract updates in `contracts/openapi/`.
