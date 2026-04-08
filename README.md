# LocalGuide 🌍
**AI-Powered C2C Tour Guide Platform**

LocalGuide Connect is a Consumer-to-Consumer (C2C) tour guide booking platform founded by a team of software engineering students in Canada. The platform addresses inefficiencies in the tourism industry by directly connecting travelers with verified local guides, reducing intermediary costs while maintaining high standards of trust and safety. 

## 🎯 Project Vision
Our mission is to build a transparent, AI-powered marketplace that benefits both travelers seeking authentic local experiences and professional guides who want to establish sustainable businesses without excessive platform fees.

## 💎 Core Value Propositions
- **Affordable Access**: Low commission model (12% vs industry standard 20-30%).
- **Smart Discovery**: Smart search and filtering to reduce booking time.
- **Authentic Experiences**: Focus on hidden cultural treasures and local lifestyles.
- **Verified Trust**: Transparent pricing and verified guide credentials for traveler safety.

## 📂 Project Documentation
For detailed information regarding our project requirements, business case, and development roadmap, please refer to the following resources:

- **📄 Project Proposal 1**: [LocalGuide-Group3-proposal.pdf](./docs/proposal1/LocalGuide-Group3-proposal.pdf)
- **📄 Project Proposal 2**: [LocalGuide-Group3-proposal2.pdf](./docs/proposal2/LocalGuide-Group3-proposal2.pdf)
- **☁️ Requirements & Design Document**: [Google Docs Link](https://docs.google.com/document/d/1ccwN8AG62yl9nib6weKiWco2B2Lt0h56zSaBtOYWsWg/edit?tab=t.0)
- **🎥 Product Demo & Presentation**: [Online Video Link](https://drive.google.com/file/d/1kuOv6p57LE3pItfNREmhj3w_pTsPurky/view?usp=drive_link)
- **🚀 Team Testing Quickstart**: [team-testing-quickstart.md](./docs/team-testing-quickstart.md)

## 📅 Roadmap Overview
This project follows an **Agile/Scrum** methodology with a 12-week development timeline, focusing on delivering a Minimum Viable Product (MVP) that includes core features for tourists, guides, and administrators. 

## 🧩 Development Structure
To support parallel collaboration, the repository uses a module-based folder structure aligned with the 4 developer responsibilities:

```text
apps/backend/src/main/java/com/localguide/modules/
  authuser/         -> Dev1 (Auth and User)
  guidetour/        -> Dev2 (Guide, Tour, Search, Availability)
  bookingpayment/   -> Dev3 (Booking, Payment, Notification)
  reviewadmin/      -> Dev4 (Review, Favorites, Admin)

apps/web/src/modules/
  auth-user/        -> Dev1
  guide-tour/       -> Dev2
  booking-payment/  -> Dev3
  review-admin/     -> Dev4
```

LocalGuide Connect – Run Instructions
1. Prerequisites

Please make sure the following software is installed on your device:

Java 17
Maven
Node.js and npm

2. Clone the Repository
git clone https://github.com/10000Katherine/LocalGuide_Software-Engineering.git
cd LocalGuide_Software-Engineering

3. Run the Backend

Open a PowerShell terminal and run:

cd apps/backend
$env:JWT_SECRET = "localguide-dev-secret-please-change"
mvn spring-boot:run

The backend will start on:

http://localhost:8080/api/v1

4. Run the Frontend

Open a new PowerShell terminal and run:

cd apps/web
npm install
npm run dev

The frontend will start on:

http://localhost:5173

5. Development Database

The project uses an H2 in-memory database in development mode.

H2 Console:

http://localhost:8080/h2-console

If needed, the default backend startup will automatically seed the development database with test data.

6. Default Test Accounts

All seeded accounts use the following password:

Password123!
Admin
admin@localguide.dev

Tourist
tourist@localguide.dev

Guide
guide@localguide.dev

7. Quick End-to-End Test Flow
Log in as a tourist using tourist@localguide.dev.
Search for a guide and submit a booking request for any available tour.
Open My Bookings and verify that the new booking status is CREATED.
Log out and sign in as the corresponding guide.
Open the guide booking request page and accept the booking.
Log out and return to the tourist account.
Open the booking and click Pay.
Confirm payment success and verify that the booking status is updated to CONFIRMED.

8. Notes
If seeded guide or tour data does not appear correctly, restart the backend once.
The development database is in-memory, so data resets when the backend is restarted.
If the frontend shows stale state, restart the frontend and refresh the browser.
Payment is currently implemented using a development-mode simulation/stub for demonstration purposes.

