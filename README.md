<p align="center">
  <img width="677" height="369" alt="logo-removebg-preview" src="https://github.com/user-attachments/assets/a11db0ef-5c29-48ad-8cf9-c97f0be21a72" />
</p>

<h1 align="center">🏢 Smart Coworking Space Management System (SCMS)</h1>

<p align="center">
  <em>A feature-complete Java desktop application for managing modern coworking spaces — built with ❤️ by <strong>Trinova Tech</strong></em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/SQL%20Server-2019+-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white" alt="SQL Server"/>
  <img src="https://img.shields.io/badge/Swing-GUI-007396?style=for-the-badge&logo=java&logoColor=white" alt="Swing GUI"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Architecture-MVC-blue?style=flat-square" alt="MVC"/>
  <img src="https://img.shields.io/badge/Design_Pattern-Singleton_|_DAO_|_Service_Layer_|_Factory-blueviolet?style=flat-square" alt="Design Patterns"/>
  <img src="https://img.shields.io/badge/Security-BCrypt-orange?style=flat-square" alt="BCrypt"/>
  <img src="https://img.shields.io/badge/Testing-JUnit_5-25A162?style=flat-square" alt="JUnit 5"/>
  <img src="https://img.shields.io/badge/Status-Complete-brightgreen?style=flat-square" alt="Complete"/>
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Architecture & Design Patterns](#-architecture--design-patterns)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [Running Tests](#-running-tests)
- [Screenshots](#-screenshots)
- [Development Roadmap](#-development-roadmap)
- [Team — Trinova Tech](#-team--trinova-tech)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

**SCMS** is a comprehensive, production-ready desktop application designed to streamline every aspect of modern coworking space operations. The system covers the full lifecycle — from member registration and secure authentication, through space browsing and real-time booking with smart cost calculation, to subscription management, invoice generation, payment processing, and administrative reporting.

Both **Members** and **Administrators** get dedicated, role-based dashboards with a modern dark-themed UI featuring gradient backgrounds, rounded cards, animated sidebar navigation, and a centralized design system (`UITheme`).

> Built as a **Software Engineering Semester Project** following industry-standard practices including MVC architecture, the DAO pattern, Singleton database connections, Service Layer abstraction, Factory methods for UI components, and comprehensive JUnit 5 testing.

---

## 🚀 Key Features

### 🔐 Authentication & Security
| Feature | Description |
|---|---|
| **Secure Registration** | Email validation, password strength enforcement (uppercase, digit, special char) |
| **BCrypt Hashing** | Passwords are hashed using BCrypt with salt rounds — never stored in plain text |
| **Account Lockout** | Automatic lockout after 5 failed login attempts to prevent brute-force attacks |
| **Password Reset** | Token-based password recovery with 30-minute expiry and email simulation |
| **Email Notifications** | Simulated email alerts for registration, bookings, and cancellations |

### 🏠 Space & Booking Management
| Feature | Description |
|---|---|
| **Space Browser** | Browse available rooms filtered by type (Hot Desk, Meeting Room, Private Office) with rich detail cards |
| **Real-Time Booking** | Book rooms with date-time slots; automatic conflict detection prevents double-booking |
| **Booking Types** | Support for hourly, daily, and monthly booking modes depending on room type |
| **Cost Preview** | Interactive booking summary with live cost breakdown before confirmation |
| **Facility Add-Ons** | Select optional extras (coffee, printing, lockers, parking) with plan-aware free/paid indicators |
| **Booking History** | View all past and active bookings with full details and status tracking |
| **Cancellation** | Cancel bookings with mandatory reason tracking and email notification |
| **Room CRUD** | Admins can add, update, and soft-delete (deactivate) rooms with validation |

### 💳 Subscription & Billing
| Feature | Description |
|---|---|
| **Subscription Plans** | Basic & Premium plans with tiered benefits (free desk hours, meeting room credits, facility perks) |
| **Smart Cost Calculator** | Plan-aware pricing engine: free hours for Basic, unlimited desk for Premium, VAT calculation (17%) |
| **Promo Codes** | Percentage and flat-amount discount codes with usage tracking; admin CRUD management |
| **Payment Processing** | Simulated multi-method payments (Visa, Mastercard, Digital Wallet) with transaction references |
| **Auto-Invoicing** | Invoices auto-generated for every booking with base, facility, VAT, and total breakdowns |
| **Invoice History** | Members view their own invoices; admins view all invoices system-wide |

### 📊 Admin Dashboard
| Feature | Description |
|---|---|
| **Quick Stats** | Real-time counts of members, spaces, and bookings on the admin home panel |
| **All Bookings** | View and cancel any booking system-wide with reason tracking |
| **All Invoices** | Browse every invoice across all members |
| **Manage Spaces** | Full CRUD for coworking spaces (add/edit/deactivate) with active-booking protection |
| **Promo Codes** | Create, toggle active/inactive, and track usage of promotional discount codes |
| **Occupancy Report** | Generate daily occupancy data grouped by room type with totals and revenue |
| **Report Export** | Download occupancy reports in **PDF** (PDFBox), **Word** (Apache POI), or **TXT** formats |
| **Member Directory** | Search, browse, and lock/unlock member accounts |

### 👤 Member Dashboard
| Feature | Description |
|---|---|
| **Role-Based Dashboard** | Dedicated member home with greeting card, plan status, and sidebar navigation |
| **Browse Spaces** | Filter and book from available Hot Desks, Meeting Rooms, and Private Rooms |
| **My Bookings** | Personal booking history with cancellation support |
| **Subscription** | View and subscribe to Basic/Premium plans with live plan comparison cards |
| **My Invoices** | Personal invoice ledger with detailed cost breakdowns |
| **My Profile** | Update name, phone, bio, and view account info |

### 🎨 UI & Design System
| Feature | Description |
|---|---|
| **Centralized UITheme** | Single `UITheme.java` class with 50+ design tokens (colors, fonts, dimensions) and factory methods |
| **Dark Theme** | Premium dark gradient backgrounds for top bars and sidebars |
| **Rounded Cards** | Custom-painted `JPanel` components with `RoundRectangle2D` corners |
| **Gradient Buttons** | Primary action buttons with gradient paint and hand cursors |
| **Styled Tables** | Alternating row colors, dark headers, selection highlighting |
| **Initials Avatar** | Auto-generated circular avatar with user initials |
| **Stat Cards** | Color-coded metric cards with accent stripes and icon circles |
| **Hover Effects** | Sidebar buttons with translucent hover highlights |

---

## 🏗 Architecture & Design Patterns

SCMS follows the **Model-View-Controller (MVC)** architectural pattern with additional design patterns for maintainability and scalability:

```
┌──────────────────────────────────────────────────────────────────────┐
│                            VIEW LAYER                                │
│  LoginFrame │ RegisterFrame │ ForgotPasswordFrame                    │
│  MemberDashboard │ AdminDashboard │ SpaceBrowserPanel               │
│  BookingHistoryPanel │ SubscriptionPanel │ InvoicePanel              │
│  CostPreviewFrame │ UITheme (Factory)                                │
├──────────────────────────────────────────────────────────────────────┤
│                          SERVICE LAYER                               │
│  AuthService │ BookingService │ BillingService │ CostCalculatorService│
│  ReportExporter (PDF / DOCX / TXT)                                   │
│            (Business Logic, Validations, Orchestration)              │
├──────────────────────────────────────────────────────────────────────┤
│                        DAO / DATA LAYER                              │
│  MemberDAO │ RoomDAO │ BookingDAO │ InvoiceDAO                       │
│  SubscriptionDAO │ PaymentDAO │ FacilityDAO                          │
│           DatabaseConnection (Singleton Pattern)                     │
├──────────────────────────────────────────────────────────────────────┤
│                           DATABASE                                   │
│                   Microsoft SQL Server Express                       │
│                  Database: CoworkingSpace_db                         │
└──────────────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

| Pattern | Where | Purpose |
|---|---|---|
| **MVC** | Entire Application | Separation of concerns between UI, logic, and data |
| **Singleton** | `DatabaseConnection` | Single shared database connection instance across all DAOs |
| **DAO** | `MemberDAO`, `RoomDAO`, `BookingDAO`, `InvoiceDAO`, `SubscriptionDAO`, `PaymentDAO`, `FacilityDAO` | Encapsulates data access; abstracts SQL from business logic |
| **Service Layer** | `AuthService`, `BookingService`, `BillingService`, `CostCalculatorService` | Centralizes business rules, validations, and cross-cutting concerns |
| **Factory Method** | `UITheme` | Produces styled UI components (`primaryButton()`, `cardPanel()`, `styledField()`, etc.) |

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| **Language** | Java (OpenJDK) | 17 |
| **GUI** | Java Swing + Custom Design System (`UITheme`) | — |
| **Build Tool** | Apache Maven | 3.9+ |
| **Database** | Microsoft SQL Server Express | 2019+ |
| **JDBC Driver** | `mssql-jdbc` | 12.4.2 |
| **Password Hashing** | jBCrypt | 0.4.3 |
| **PDF Generation** | Apache PDFBox | 3.0.1 |
| **Word Generation** | Apache POI (OOXML) | 5.2.5 |
| **Unit Testing** | JUnit Jupiter | 5.10.0 |
| **Packaging** | Maven Shade Plugin (Fat JAR) | 3.5.0 |

---

## 📁 Project Structure

```
Smart-Coworking-Space-Management-System/
│
├── pom.xml                                        # Maven build configuration
├── README.md                                      # Project documentation
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/trinova/scms/
│   │           │
│   │           ├── Main.java                      # Application entry point
│   │           │
│   │           ├── model/                         # Data models (POJOs)
│   │           │   ├── Member.java                # User entity with auth, plan, and profile fields
│   │           │   ├── Room.java                  # Coworking space entity (Hot Desk/Meeting/Private)
│   │           │   ├── Booking.java               # Booking entity with cost tracking & status
│   │           │   ├── SubscriptionPlan.java       # Plan entity (Basic / Premium)
│   │           │   ├── Invoice.java               # Invoice entity with VAT & facility breakdown
│   │           │   ├── Payment.java               # Payment record entity
│   │           │   ├── PromoCode.java             # Promo code entity (Percentage / Flat)
│   │           │   └── Facility.java              # Add-on facility entity with plan-aware pricing
│   │           │
│   │           ├── dao/                           # Data Access Objects
│   │           │   ├── DatabaseConnection.java    # Singleton DB connection manager
│   │           │   ├── MemberDAO.java             # Member CRUD + auth + profile operations
│   │           │   ├── RoomDAO.java               # Room CRUD + availability queries
│   │           │   ├── BookingDAO.java            # Booking CRUD + conflict detection + usage queries
│   │           │   ├── InvoiceDAO.java            # Invoice creation + retrieval by member/all
│   │           │   ├── SubscriptionDAO.java       # Plan management + promo code CRUD
│   │           │   ├── PaymentDAO.java            # Payment recording for bookings & subscriptions
│   │           │   └── FacilityDAO.java           # Facility listing + booking-facility linking
│   │           │
│   │           ├── service/                       # Business logic layer
│   │           │   ├── AuthService.java           # Login, registration, lockout, password reset
│   │           │   ├── BookingService.java        # Booking & cancellation workflow with invoicing
│   │           │   ├── BillingService.java        # Invoice generation, promo codes, plan assignment
│   │           │   ├── CostCalculatorService.java # Smart plan-aware cost engine (Base + Facilities + VAT)
│   │           │   └── ReportExporter.java        # Occupancy report export (PDF / DOCX / TXT)
│   │           │
│   │           ├── util/                          # Utility classes
│   │           │   ├── PasswordUtil.java          # BCrypt hash & verify wrapper
│   │           │   └── EmailSimulator.java        # Console-based email simulation
│   │           │
│   │           └── view/                          # Swing GUI components
│   │               ├── UITheme.java               # Centralized design system (colors, fonts, factories)
│   │               ├── LoginFrame.java            # Login screen with validation feedback
│   │               ├── RegisterFrame.java         # Registration form with password strength
│   │               ├── ForgotPasswordFrame.java   # Token-based password recovery
│   │               ├── MemberDashboard.java       # Member home with sidebar navigation
│   │               ├── AdminDashboard.java        # Admin control panel (7 management sections)
│   │               ├── SpaceBrowserPanel.java     # Room browsing, filtering & booking
│   │               ├── BookingHistoryPanel.java   # Booking history & cancellation
│   │               ├── CostPreviewFrame.java      # Booking cost preview with facility add-ons
│   │               ├── SubscriptionPanel.java     # Plan comparison cards & subscription payment
│   │               └── InvoicePanel.java          # Personal invoice ledger
│   │
│   └── test/
│       └── java/
│           └── com/trinova/scms/
│               ├── model/
│               │   └── ModelValidationTest.java   # Model field validation tests
│               └── service/
│                   ├── BookingValidationTest.java  # Booking logic validation tests
│                   ├── BillingServiceTest.java     # Billing & promo code tests
│                   └── CostCalculatorServiceTest.java # Cost calculation tests
```

---

## 💾 Database Schema

The application uses **Microsoft SQL Server Express** with the database `CoworkingSpace_db`. Below is the complete schema:

### `members` Table
```sql
CREATE TABLE members (
    member_id       INT IDENTITY(1,1) PRIMARY KEY,
    full_name       NVARCHAR(100)  NOT NULL,
    email           NVARCHAR(150)  NOT NULL UNIQUE,
    password_hash   NVARCHAR(255)  NOT NULL,
    role            NVARCHAR(20)   DEFAULT 'MEMBER',
    phone           NVARCHAR(20),
    bio             NVARCHAR(500),
    profile_photo   NVARCHAR(255),
    plan_id         INT,
    plan_expiry     DATE,
    is_locked       BIT            DEFAULT 0,
    failed_attempts INT            DEFAULT 0
);
```

### `rooms` Table
```sql
CREATE TABLE rooms (
    room_id       INT IDENTITY(1,1) PRIMARY KEY,
    room_name     NVARCHAR(100)  NOT NULL,
    room_type     NVARCHAR(50)   NOT NULL,       -- HOT_DESK, MEETING_ROOM, PRIVATE_ROOM
    capacity      INT            NOT NULL,
    amenities     NVARCHAR(500),
    private_size  NVARCHAR(20),                   -- SMALL, MEDIUM, LARGE (for private rooms)
    hourly_price  DECIMAL(10,2)  DEFAULT 0,
    daily_price   DECIMAL(10,2)  DEFAULT 0,
    monthly_price DECIMAL(10,2)  DEFAULT 0,
    is_active     BIT            DEFAULT 1
);
```

### `bookings` Table
```sql
CREATE TABLE bookings (
    booking_id     INT IDENTITY(1,1) PRIMARY KEY,
    member_id      INT            NOT NULL REFERENCES members(member_id),
    room_id        INT            NOT NULL REFERENCES rooms(room_id),
    booking_type   NVARCHAR(20)   NOT NULL,       -- HOURLY, DAILY, MONTHLY
    start_time     DATETIME       NOT NULL,
    end_time       DATETIME       NOT NULL,
    duration_hours DECIMAL(10,2),
    base_cost      DECIMAL(10,2)  DEFAULT 0,
    facility_cost  DECIMAL(10,2)  DEFAULT 0,
    vat_amount     DECIMAL(10,2)  DEFAULT 0,
    total_cost     DECIMAL(10,2)  DEFAULT 0,
    status         NVARCHAR(20)   DEFAULT 'CONFIRMED',
    cancel_reason  NVARCHAR(500)
);
```

### `subscription_plans` Table
```sql
CREATE TABLE subscription_plans (
    plan_id       INT IDENTITY(1,1) PRIMARY KEY,
    plan_name     NVARCHAR(100)  NOT NULL,
    plan_type     NVARCHAR(20)   NOT NULL,        -- BASIC, PREMIUM
    duration_days INT            NOT NULL,
    price         DECIMAL(10,2)  NOT NULL
);
```

### `invoices` Table
```sql
CREATE TABLE invoices (
    invoice_id    INT IDENTITY(1,1) PRIMARY KEY,
    booking_id    INT            NOT NULL REFERENCES bookings(booking_id),
    member_id     INT            NOT NULL REFERENCES members(member_id),
    base_amount   DECIMAL(10,2)  NOT NULL,
    facility_cost DECIMAL(10,2)  DEFAULT 0,
    vat_amount    DECIMAL(10,2)  DEFAULT 0,
    total_amount  DECIMAL(10,2)  NOT NULL,
    issue_date    DATE           NOT NULL,
    pdf_path      NVARCHAR(500)
);
```

### `payments` Table
```sql
CREATE TABLE payments (
    payment_id      INT IDENTITY(1,1) PRIMARY KEY,
    member_id       INT            NOT NULL REFERENCES members(member_id),
    booking_id      INT            REFERENCES bookings(booking_id),
    subscription_id INT,
    amount          DECIMAL(10,2)  NOT NULL,
    payment_method  NVARCHAR(50)   NOT NULL,      -- Visa, Mastercard, Digital Wallet
    status          NVARCHAR(20)   DEFAULT 'COMPLETED',
    transaction_ref NVARCHAR(100),
    paid_at         DATETIME       DEFAULT GETDATE()
);
```

### `promo_codes` Table
```sql
CREATE TABLE promo_codes (
    promo_id       INT IDENTITY(1,1) PRIMARY KEY,
    code           NVARCHAR(50)   NOT NULL UNIQUE,
    discount_type  NVARCHAR(20)   NOT NULL,       -- PERCENTAGE, FLAT
    discount_value DECIMAL(10,2)  NOT NULL,
    is_active      BIT            DEFAULT 1,
    usage_count    INT            DEFAULT 0
);
```

### `facilities` Table
```sql
CREATE TABLE facilities (
    facility_id      INT IDENTITY(1,1) PRIMARY KEY,
    facility_name    NVARCHAR(100)  NOT NULL,
    price            DECIMAL(10,2)  NOT NULL,
    unit             NVARCHAR(20)   NOT NULL,     -- per hour, per session, etc.
    free_for_basic   BIT            DEFAULT 0,
    free_for_premium BIT            DEFAULT 0
);
```

### `booking_facilities` Table
```sql
CREATE TABLE booking_facilities (
    id           INT IDENTITY(1,1) PRIMARY KEY,
    booking_id   INT            NOT NULL REFERENCES bookings(booking_id),
    facility_id  INT            NOT NULL REFERENCES facilities(facility_id),
    quantity     INT            DEFAULT 1,
    cost         DECIMAL(10,2)  DEFAULT 0
);
```

---

## 🚀 Getting Started

### Prerequisites

| Requirement | Details |
|---|---|
| **Java JDK** | Version 17 or higher |
| **Apache Maven** | Version 3.9 or higher |
| **SQL Server Express** | With a running instance named `SQLEXPRESS` |
| **IDE (Optional)** | IntelliJ IDEA, Eclipse, or VS Code |

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/code-with-idrees/Smart-Coworking-Space-Management-System.git
cd Smart-Coworking-Space-Management-System
```

### 2️⃣ Set Up the Database

1. Open **SQL Server Management Studio (SSMS)** or **Azure Data Studio**
2. Create the database:
   ```sql
   CREATE DATABASE CoworkingSpace_db;
   ```
3. Run all the table creation scripts from the [Database Schema](#-database-schema) section above
4. Insert sample data:
   ```sql
   -- Subscription Plans
   INSERT INTO subscription_plans (plan_name, plan_type, duration_days, price) VALUES
   ('Basic Plan',   'BASIC',   30, 3000),
   ('Premium Plan', 'PREMIUM', 30, 7000);

   -- Sample Rooms
   INSERT INTO rooms (room_name, room_type, capacity, amenities, hourly_price) VALUES
   ('Alpha Hub',       'HOT_DESK',     10, 'Wi-Fi, Power Outlets, Standing Desk', 200),
   ('Brainstorm Room', 'MEETING_ROOM',  8, 'Projector, Whiteboard, Video Conferencing', 1000),
   ('Open Lounge',     'HOT_DESK',     20, 'Wi-Fi, Bean Bags, Coffee Machine', 200);

   INSERT INTO rooms (room_name, room_type, capacity, private_size, daily_price, monthly_price) VALUES
   ('Executive Suite', 'PRIVATE_ROOM',  4, 'MEDIUM', 5000, 80000);

   -- Sample Facilities
   INSERT INTO facilities (facility_name, price, unit, free_for_basic, free_for_premium) VALUES
   ('Coffee',        50,  'per cup',     0, 1),
   ('Printing',     100,  'per 50 pages', 0, 1),
   ('Locker',       200,  'per day',     0, 1),
   ('Parking',      500,  'per day',     0, 1);

   -- Sample Promo Codes
   INSERT INTO promo_codes (code, discount_type, discount_value) VALUES
   ('WELCOME10', 'PERCENTAGE', 10),
   ('FLAT500',   'FLAT',       500);

   -- Admin Account (password: Admin@123)
   INSERT INTO members (full_name, email, password_hash, role) VALUES
   ('System Admin', 'admin@scms.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
   ```

### 3️⃣ Configure Database Connection

Update the credentials in `src/main/java/com/trinova/scms/dao/DatabaseConnection.java` if your setup differs:

```java
private static final String URL  = "jdbc:sqlserver://localhost\\SQLEXPRESS;"
                                  + "databaseName=CoworkingSpace_db;"
                                  + "encrypt=true;trustServerCertificate=true;";
private static final String USER = "sa";
private static final String PASS = "your_password_here";
```

### 4️⃣ Build & Run

```bash
# Compile and package
mvn clean package

# Run the application
java -jar target/scms-1.0.0.jar
```

Or run directly from your IDE by executing `com.trinova.scms.Main`.

---

## 🧪 Running Tests

The project includes JUnit 5 tests covering model validation, booking logic, billing, and cost calculation:

```bash
# Run all tests
mvn test
```

| Test Class | Coverage Area |
|---|---|
| `ModelValidationTest` | Model field validation and entity integrity |
| `BookingValidationTest` | Booking workflow validation and edge cases |
| `BillingServiceTest` | Invoice generation and promo code application |
| `CostCalculatorServiceTest` | Plan-aware cost calculation with VAT and facilities |

---

## 📸 Screenshots

<img width="764" height="900" alt="image" src="https://github.com/user-attachments/assets/d7f34d91-d211-482a-a1d0-a1f9a324fbe2" />
<img width="1919" height="1004" alt="image" src="https://github.com/user-attachments/assets/fd67bf1c-b1e7-4800-bf16-2c2cff13515d" />
<img width="1919" height="1007" alt="image" src="https://github.com/user-attachments/assets/6331f949-2a46-42bf-9887-fbc42a08b392" />
<img width="1917" height="1007" alt="image" src="https://github.com/user-attachments/assets/ba7dc8d9-8f11-4cce-9ad3-4a664694f4f2" />
<img width="1919" height="1011" alt="image" src="https://github.com/user-attachments/assets/b77d2591-95fa-491e-ae39-4abae59cd21e" />
<img width="1919" height="1009" alt="image" src="https://github.com/user-attachments/assets/14f635d2-5f3f-4e3d-be08-a5cc768da8fb" />
<img width="1919" height="1010" alt="image" src="https://github.com/user-attachments/assets/c10560ba-96fa-46cf-8cba-e11bc2332d24" />
<img width="1917" height="1003" alt="image" src="https://github.com/user-attachments/assets/fea494f1-a0ac-496c-bbad-f2364e59ec3a" />
<img width="1919" height="1006" alt="image" src="https://github.com/user-attachments/assets/b1a81e95-6f66-4e55-84de-21a04629a3f5" />
<img width="1919" height="1009" alt="image" src="https://github.com/user-attachments/assets/94df51da-2b29-4f9e-acae-5b03a4544183" />










| Screen | Description |
|---|---|
| **Login** | Dark gradient background with rounded card form and validation feedback |
| **Registration** | Full registration with password strength enforcement |
| **Member Dashboard** | Gradient greeting card with plan status, sidebar navigation |
| **Browse Spaces** | Filter by room type, view details, and book with one click |
| **Cost Preview** | Booking summary with facility add-ons and live cost breakdown |
| **Subscription Plans** | Side-by-side plan cards with gradient headers and benefit lists |
| **My Invoices** | Styled table with sortable invoice history |
| **Admin Dashboard** | Stat cards with member/space/booking counts |
| **Manage Spaces** | Full CRUD table with add/edit/deactivate dialogs |
| **Occupancy Report** | Date-grouped report with PDF/DOCX/TXT export |
| **Member Directory** | Searchable member list with lock/unlock controls |
| **Promo Codes** | Admin promo code management with toggle active/inactive |

---

## 🗺 Development Roadmap

The project followed an **Agile Scrum** methodology with incremental sprints:

| Sprint | Focus Area | Status |
|---|---|---|
| **Sprint 1** | Core Auth (Login, Register, Password Reset) + DB Setup + MVC Foundation | ✅ Completed |
| **Sprint 2** | Room Browsing, Booking Engine, Booking History, Cancellation | ✅ Completed |
| **Sprint 3** | Subscription Plans, Promo Codes, Cost Calculator, Facilities, Invoicing, Payment Processing | ✅ Completed |
| **Sprint 4** | Admin Dashboard (Manage Spaces, Occupancy Reports, Member Directory, Report Export), UI Modernization & Design System | ✅ Completed |

---

## 👥 Team — Trinova Tech

<p align="center">
  <strong>We are Trinova Tech</strong> — a team of three passionate software engineering students committed to building elegant, professional-grade solutions.
</p>

| | Name | Student ID | Role | GitHub |
|---|---|---|---|---|
| 👨‍💻 | **Muhammad Idrees** | i230582 | Team Lead , Product Owner & Developer | [@code-with-idrees](https://github.com/code-with-idrees) |
| 👨‍💻 | **Haris** | i230814 | Scrum Master, Developer & Analyst | [@muhammad-haris2](https://github.com/muhammad-haris2) |
| 👨‍💻 | **Farzeen** | i230721 | UI, Developer & Tester | [@farzeentareen](https://github.com/farzeentareen) |

> **Section:** B  
> **Course:** Software Engineering  
> **University Semester Project — Spring 2026**

---

## 🤝 Contributing

We welcome contributions! To contribute:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/your-feature`
3. **Commit** your changes: `git commit -m "Add: your feature description"`
4. **Push** to the branch: `git push origin feature/your-feature`
5. **Open** a Pull Request

Please follow the existing code style and include appropriate documentation.

---

## 📝 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <strong>Built with ❤️ by Trinova Tech</strong><br/>
  <em>Smart Coworking Space Management System — Spring 2026</em>
</p>

<p align="center">
  <a href="https://github.com/code-with-idrees/Smart-Coworking-Space-Management-System">
    <img src="https://img.shields.io/badge/⭐_Star_this_repo-yellow?style=for-the-badge" alt="Star"/>
  </a>
</p>
