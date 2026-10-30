# SeatReserve - Enterprise Bus Ticket Booking & Seat Reservation System

An end-to-end, production-grade **Passenger Seat Reservation & Ticket Booking System** built with **Java 21**, **Spring Boot 3.3.4**, **Spring Data JPA**, **Spring Security (JWT)**, and a modern, responsive web application served directly from the Spring Boot embedded container.

---

## 🌟 Key Features

### 1. 🚌 Multi-Deck Bus Architecture & Cabin Layout
- Supports multi-axle luxury coach configurations:
  - **Volvo B11R Multi-Axle AC Sleeper** (2+1 upper and lower deck layout).
  - **Scania Multi-Axle AC Semi-Sleeper** (2+2 recliner seating).
- Visual bus cabin layout featuring:
  - Driver cabin steering wheel indicator.
  - Interactive Lower Deck & Upper Deck berths.
  - Realistic window indicators (`W`), aisle spacing, and seat classification (Seater vs. Sleeper).
  - Color-coded availability: Green (Available), Red/Gray (Booked), Orange (Selected).

### 2. 🔒 Concurrency & Pessimistic Locking
- High-concurrency seat reservation engine eliminates race conditions and 0% double-booking conflicts.
- Employs **JPA Pessimistic Write Locking** (`PESSIMISTIC_WRITE`) with `READ_COMMITTED` transaction isolation on `JourneySeat` records during atomic booking execution.

### 3. 💰 Dynamic Fare Calculation & Promo Codes
- Dynamic pricing engine factoring in:
  - Route distance (km) and base rates.
  - Deck surcharges (e.g. Upper Deck sleeper vs. Lower Deck).
  - Window seat preference surcharges.
  - GST / Passenger Service Tax calculation (5%).
  - Promo code discounts (`SAVE10` for 10% off, `FIRST50` for ₹50 off, `FESTIVE15` for 15% off).

### 4. 🎫 Printable E-Tickets with QR Validation
- Instant E-Ticket generation with unique PNR reference (e.g. `PNR-V2K7HT`).
- Itemized passenger seat allocation table with individual ticket numbers (e.g. `TKT-PNR-V2K7HT-L1C`).
- Scalable QR code pass for boarding gate scanning.
- Native browser print stylesheet for PDF download and printing.

### 5. 🔍 Real-Time PNR Status Inquiry & Tiered Cancellation
- Real-time PNR retrieval showing active reservation status, boarding/dropping landmarks, and contact details.
- Intelligent tiered cancellation policy engine:
  - **> 24 hours before departure**: 90% refund (10% cancellation charge).
  - **12 to 24 hours before departure**: 75% refund (25% cancellation charge).
  - **4 to 12 hours before departure**: 50% refund (50% cancellation charge).
  - **< 4 hours before departure**: Non-refundable (100% cancellation charge).
- Pre-cancellation quote preview with itemized refund and instant cancellation execution.

### 6. 🛡️ Official Ticket Verification Terminal
- Conductor and station inspector verification scanner.
- Instant validation of ticket status, assigned berth, and boarding clearance.

### 7. 📊 Executive Operations Admin Dashboard
- Real-time operational metrics:
  - Total Bookings count.
  - Active Confirmed reservations.
  - Cancellations count.
  - Net Realized Revenue.
  - Fleet and route utilization.
- Live recent transactions log table.

### 8. 🔐 Authentication & Quick Demo Logins
- Stateless JWT authentication filter.
- Role-based authorization (`ROLE_ADMIN`, `ROLE_USER`).
- 1-click **Quick Demo Login** for passenger (`demo@seatreserve.com`) and admin (`admin@seatreserve.com`).

---

## 🏗️ Technology Stack

| Component | Technology |
|---|---|
| **Language** | Java 21 LTS |
| **Framework** | Spring Boot 3.3.4 |
| **Persistence** | Spring Data JPA / Hibernate 6 |
| **Database** | H2 In-Memory (MySQL compatibility mode) / MySQL 8.x |
| **Security** | Spring Security 6 with JJWT 0.12.5 |
| **Build Tool** | Apache Maven 3.9+ |
| **Frontend** | Vanilla JavaScript (ES6+), HTML5, Custom CSS3 Design System |
| **Fonts** | Google Outfit & Inter typography |

---

## 📁 Repository Structure

```
d:/busticket booking java/
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/seatreserve/busbooking/
│       │   │   ├── BusBookingApplication.java      # Spring Boot Entry Point
│       │   │   ├── config/                         # SecurityConfig, JwtService, DataSeeder
│       │   │   ├── controller/                     # REST Controllers (Auth, Route, Journey, Fare, Booking, Ticket, Admin)
│       │   │   ├── exception/                      # GlobalExceptionHandler & custom exceptions
│       │   │   ├── model/                          # Entities, DTOs, Enums
│       │   │   ├── repository/                     # Spring Data JPA Repositories
│       │   │   └── service/                        # Business Logic (BookingService, FareService, etc.)
│       │   └── resources/
│       │       ├── application.yml                 # Default H2 in-memory configuration
│       │       ├── application-mysql.yml           # Production MySQL configuration
│       │       └── static/
│       │           ├── index.html                  # Single Page Application
│       │           ├── css/app.css                 # Custom Responsive Travel UI
│       │           └── js/app.js                   # Dynamic Frontend Controller
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- **JDK 21** or higher installed.
- **Maven 3.8+** installed.

### 1. Clone & Build
```bash
git clone https://github.com/naveenyenuganti30-cell/Bus-Ticket-Booking-System-Java.git
cd Bus-Ticket-Booking-System-Java/backend
mvn clean package -DskipTests
```

### 2. Run Application
```bash
# Run with Spring Boot Maven plugin
mvn spring-boot:run

# Or run the packaged executable JAR
java -jar target/bus-booking-system-1.0.0.jar
```

### 3. Open in Browser
Open your browser and navigate to:
```
http://localhost:8080/
```

- **H2 Database Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:busbookingdb`
  - User: `sa`
  - Password: *(empty)*

---

## 🔑 Default Seed Accounts & Demo Data

| Role | Email | Password |
|---|---|---|
| **Administrator** | `admin@seatreserve.com` | `Admin@123` |
| **Passenger** | `demo@seatreserve.com` | `Demo@123` |

### Pre-loaded Intercity Routes
- **Bangalore ➔ Hyderabad** (570 km via Silk Board, Electronic City, Anantapur, Kurnool, Gachibowli, Ameerpet)
- **Bangalore ➔ Chennai** (350 km via Hosur, Krishnagiri, Vellore, Sriperumbudur, Koyambedu)
- **Mumbai ➔ Pune** (150 km via Dadar, Chembur, Vashi, Navi Mumbai, Wakad, Swargate)
- **Delhi ➔ Jaipur** (280 km via Dhaula Kuan, Gurgaon IFFCO Chowk, Kotputli, Sindhi Camp)

### Sample Seed Booking
- **PNR Reference**: `PNR-DEMO77`
- **Passenger**: `Naveen Yenuganti`
- **Email**: `demo@seatreserve.com`
- **Sample Ticket**: `TKT-PNR-DEMO77-L1A`

---

## 📡 Core REST API Endpoints

### Route & Search APIs
- `GET /api/routes/cities/source` - List of departure cities.
- `GET /api/routes/cities/destination` - List of arrival cities.
- `GET /api/journeys/search?source={city}&destination={city}&date={yyyy-MM-dd}` - Search scheduled services.
- `GET /api/journeys/{id}/seats` - Retrieve Lower Deck & Upper Deck cabin layout with live seat status.

### Fare Calculation API
- `POST /api/fares/calculate` - Calculate itemized fare breakdown, surcharges, taxes, and promo code discounts.
  ```json
  {
    "journeyId": 1,
    "seatIds": [3, 4],
    "promoCode": "SAVE10"
  }
  ```

### Booking APIs
- `POST /api/bookings` - Create reservation with pessimistic seat lock.
- `GET /api/bookings/reference/{pnr}` - Lookup booking details by PNR.
- `GET /api/bookings/lookup?pnr={pnr}&email={email}` - Secure booking lookup by PNR and email.
- `GET /api/bookings/{pnr}/cancel-quote` - Get real-time cancellation policy & refund estimate.
- `POST /api/bookings/{id}/cancel` - Execute booking cancellation and refund.

### Ticket & Admin APIs
- `GET /api/tickets/{ticketNumber}` - Ticket scanner and validator.
- `GET /api/admin/dashboard` - Operational metrics and revenue summary.
- `GET /api/admin/bookings` - Complete transactions history.

---

## 🛡️ License
This project is open-source and available under the [MIT License](LICENSE).
