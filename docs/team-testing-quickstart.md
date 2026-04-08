LocalGuide Connect – Run Instructions
1. Prerequisites

Please make sure the following software is installed on your device(needs these environments)

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

cd LocalGuide_Software-Engineering
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
- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:mem:localguide
- User Name: sa
- Password: 


6. Default Test Accounts
All seeded accounts use the following password:

Password123!


Admin
admin@localguide.dev

Tourist
tourist@localguide.dev

Guide
guide@localguide.dev

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

9. Optional IDE Support

The backend may also be opened and run in IntelliJ IDEA as a standard Maven/Spring Boot project. However, the recommended method for testing is the command-line setup shown above.