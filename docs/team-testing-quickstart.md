# LocalGuide Team Testing Quickstart

This file is for internal team testing.

## 1) Run Commands (PowerShell)

### Backend
```powershell
cd "D:\cs\software engineering\slides\project\LocalGuide_Software-Engineering\apps\backend"
$env:JWT_SECRET = "localguide-dev-secret-please-change"
mvn spring-boot:run
```

### Frontend
```powershell
cd "D:\cs\software engineering\slides\project\LocalGuide_Software-Engineering\apps\web"
npm install
npm run dev
```

## 2) URLs

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080/api/v1`
- H2 Console (dev): `http://localhost:8080/h2-console`

## 3) Default Test Accounts

All seeded accounts use password: `Password123!`

### Admin
- `admin@localguide.dev`
Password123!

### Tourist
- `tourist@localguide.dev`

### Guides (20)
- `guide@localguide.dev`
- `guide.toronto@localguide.dev`
- `guide.montreal@localguide.dev`
- `guide.calgary@localguide.dev`
- `guide.ottawa@localguide.dev`
- `guide.edmonton@localguide.dev`
- `guide.winnipeg@localguide.dev`
- `guide.halifax@localguide.dev`
- `guide.victoria@localguide.dev`
- `guide.quebeccity@localguide.dev`
- `guide.saskatoon@localguide.dev`
- `guide.regina@localguide.dev`
- `guide.stjohns@localguide.dev`
- `guide.charlottetown@localguide.dev`
- `guide.kelowna@localguide.dev`
- `guide.surrey@localguide.dev`
- `guide.hamilton@localguide.dev`
- `guide.mississauga@localguide.dev`
- `guide.laval@localguide.dev`
- `guide.burnaby@localguide.dev`

## 4) Fast End-to-End Test Flow

1. Login as tourist (`tourist@localguide.dev`) and request a booking from any tour.
2. Go to `My Bookings` and confirm new booking status is `CREATED`.
3. Logout and login as the corresponding guide, open `/guide/booking-requests`.
4. Accept booking request.
5. Logout and return to tourist account, open booking and pay.
6. Confirm payment success and booking status updates.

## 5) Notes

- If new seeded guides do not appear, restart backend once more.
- Guide/tour dev data seeding is idempotent (existing emails are not duplicated).
- For stale frontend state, restart frontend and hard refresh browser (`Ctrl+F5`).
