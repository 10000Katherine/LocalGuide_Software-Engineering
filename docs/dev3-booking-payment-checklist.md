# Dev3 Booking-Payment Checklist

This checklist tracks Dev3 delivery status in the current branch.

## Scope

- Owner: Dev3
- Frontend module: `apps/web/src/modules/booking-payment/`
- Backend module: `apps/backend/src/main/java/com/localguide/modules/bookingpayment/`
- API contract: `contracts/openapi/booking-payment.yaml`

## Backend

- [x] Booking and payment domain models (`Booking`, `Payment`, status enums)
- [x] Booking and payment repositories
- [x] Booking and payment service flow (create, list, detail, cancel, payment intent, webhook)
- [x] Booking/payment controllers and DTOs
- [x] Booking service unit tests (`BookingServiceTest`)
- [x] Security whitelist for payment webhook endpoint
- [ ] Replace stub adapters with real integrations (availability, payment gateway, notification)

## Frontend

- [x] Booking-payment API layer in frontend
- [x] Booking-payment state management (Pinia store)
- [x] Booking list page (`/bookings`)
- [x] Booking create page (`/bookings/new`)
- [x] Booking detail page (`/bookings/:id`)
- [x] Payment flow page (`/bookings/:id/pay`)
- [x] Booking-payment routes registered in router
- [x] App shell navigation entry for bookings

## Integration and QA

- [ ] Manual flow verified: create booking -> create payment intent -> webhook confirm -> status updates
- [x] Frontend build passes (`npm run build`)
- [ ] Backend tests pass in Windows Java environment

## Plan (Step-by-step)

1. [x] Implement frontend API layer and store.
2. [x] Implement booking list/create/detail/payment pages.
3. [x] Register routes and shell navigation.
4. [ ] Run frontend build and verify flow against backend.
5. [x] Mark completed items in this checklist.
