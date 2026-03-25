<p align="center">
  <img src="docs/assets/banner.png" alt="SCMS Banner" width="100%"/>
</p>

<h1 align="center">🏢 Smart Coworking Space Management System (SCMS)</h1>

<p align="center">
  <em>A robust Java desktop application for managing modern coworking spaces — built with love by <strong>Trinova Tech</strong></em>
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
  <img src="https://img.shields.io/badge/Design_Pattern-Singleton_|_DAO-blueviolet?style=flat-square" alt="Design Patterns"/>
  <img src="https://img.shields.io/badge/Security-BCrypt-orange?style=flat-square" alt="BCrypt"/>
  <img src="https://img.shields.io/badge/Testing-JUnit_5-25A162?style=flat-square" alt="JUnit 5"/>
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
- [Screenshots](#-screenshots)
- [Development Roadmap](#-development-roadmap)
- [Team — Trinova Tech](#-team--trinova-tech)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

**SCMS** is a comprehensive desktop application designed to streamline the operations of a modern coworking space. From member registration and authentication to room browsing and booking management, SCMS provides an intuitive interface for both **members** and **administrators**.

> Built as a **Software Engineering Semester Project** following industry-standard practices including MVC architecture, the DAO pattern, Singleton database connections, and secure password hashing.

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
| **Space Browser** | Browse available rooms filtered by type (Hot Desk, Meeting Room, Private Office, etc.) |
| **Real-Time Booking** | Book rooms with date-time slots; automatic conflict detection prevents double-booking |
| **Booking History** | View all past and active bookings with full details |
| **Cancellation** | Cancel bookings with mandatory reason tracking |
| **Room CRUD** | Admins can add, update, and soft-delete rooms |

### 👤 User Experience
| Feature | Description |
|---|---|
| **Role-Based Dashboards** | Separate Member and Admin dashboards with tailored functionality |
| **Profile Management** | Members can update name, phone, bio, and profile photo |
| **Sidebar Navigation** | Intuitive card-layout based navigation between sections |
| **Modern UI** | Clean, professional Swing interface with custom color schemes |

---

## 🏗 Architecture & Design Patterns

SCMS follows the **Model-View-Controller (MVC)** architectural pattern with additional design patterns for maintainability and scalability:

```
┌─────────────────────────────────────────────────────────┐
│                        VIEW LAYER                       │
│    LoginFrame │ RegisterFrame │ MemberDashboard │ Admin │
│    ForgotPasswordFrame │ SpaceBrowserPanel │ Booking    │
├─────────────────────────────────────────────────────────┤
│                      SERVICE LAYER                      │
│           AuthService  │  BookingService                │
│     (Business Logic, Validations, Orchestration)        │
├─────────────────────────────────────────────────────────┤
│                    DAO / DATA LAYER                     │
│        MemberDAO  │  RoomDAO  │  BookingDAO             │
│         DatabaseConnection (Singleton Pattern)          │
├─────────────────────────────────────────────────────────┤
│                       DATABASE                          │
│              Microsoft SQL Server Express               │
│             Database: CoworkingSpace_db                 │
└─────────────────────────────────────────────────────────┘
```

### Design Patterns Used

| Pattern | Where | Purpose |
|---|---|---|
| **MVC** | Entire Application | Separation of concerns between UI, logic, and data |
| **Singleton** | `DatabaseConnection` | Single shared database connection instance |
| **DAO** | `MemberDAO`, `RoomDAO`, `BookingDAO` | Encapsulates data access; abstracts SQL from business logic |
| **Service Layer** | `AuthService`, `BookingService` | Centralizes business rules and validation |

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| **Language** | Java (OpenJDK) | 17 |
| **GUI** | Java Swing | — |
| **Build Tool** | Apache Maven | 3.9+ |
| **Database** | Microsoft SQL Server Express | 2019+ |
| **JDBC Driver** | `mssql-jdbc` | 12.4.2 |
| **Password Hashing** | jBCrypt | 0.4.3 |
| **PDF Generation** | Apache PDFBox | 3.0.1 |
| **Unit Testing** | JUnit Jupiter | 5.10.0 |
| **Packaging** | Maven Shade Plugin (Fat JAR) | 3.5.0 |

---

## 📁 Project Structure

```
Smart-Coworking-Space-Management-System/
│
├── pom.xml                                    # Maven build configuration
├── README.md                                  # Project documentation
│
└── src/
    └── main/
        └── java/
            └── com/trinova/scms/
                │
                ├── Main.java                  # Application entry point
                │
                ├── model/                     # Data models (POJOs)
                │   ├── Member.java            # User entity with auth fields
                │   ├── Room.java              # Coworking space entity
                │   └── Booking.java           # Booking entity with status tracking
                │
                ├── dao/                       # Data Access Objects
                │   ├── DatabaseConnection.java# Singleton DB connection manager
                │   ├── MemberDAO.java         # Member CRUD + auth operations
                │   ├── RoomDAO.java           # Room CRUD + availability queries
                │   └── BookingDAO.java        # Booking CRUD + conflict detection
                │
                ├── service/                   # Business logic layer
                │   ├── AuthService.java       # Login, registration, password reset
                │   └── BookingService.java    # Booking & cancellation workflow
                │
                ├── util/                      # Utility classes
                │   ├── PasswordUtil.java      # BCrypt hash & verify wrapper
                │   └── EmailSimulator.java    # Console-based email simulation
                │
                └── view/                      # Swing GUI components
                    ├── LoginFrame.java        # Login screen
                    ├── RegisterFrame.java     # Registration form
                    ├── ForgotPasswordFrame.java # Password recovery
                    ├── MemberDashboard.java   # Member home with sidebar nav
                    ├── AdminDashboard.java    # Admin control panel
                    ├── SpaceBrowserPanel.java # Room browsing & booking
                    └── BookingHistoryPanel.java# Booking history & cancellation
```

---

## 💾 Database Schema

The application uses **Microsoft SQL Server Express** with the database `CoworkingSpace_db`. Below is the schema:

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
    is_locked       BIT            DEFAULT 0,
    failed_attempts INT            DEFAULT 0
);
```

### `rooms` Table
```sql
CREATE TABLE rooms (
    room_id    INT IDENTITY(1,1) PRIMARY KEY,
    room_name  NVARCHAR(100)  NOT NULL,
    room_type  NVARCHAR(50)   NOT NULL,
    capacity   INT            NOT NULL,
    amenities  NVARCHAR(500),
    is_active  BIT            DEFAULT 1
);
```

### `bookings` Table
```sql
CREATE TABLE bookings (
    booking_id    INT IDENTITY(1,1) PRIMARY KEY,
    member_id     INT            NOT NULL REFERENCES members(member_id),
    room_id       INT            NOT NULL REFERENCES rooms(room_id),
    start_time    DATETIME       NOT NULL,
    end_time      DATETIME       NOT NULL,
    status        NVARCHAR(20)   DEFAULT 'ACTIVE',
    cancel_reason NVARCHAR(500)
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
3. Run the table creation scripts from the [Database Schema](#-database-schema) section above
4. *(Optional)* Insert sample room data:
   ```sql
   INSERT INTO rooms (room_name, room_type, capacity, amenities) VALUES
   ('Alpha Hub',       'HOT_DESK',       10, 'Wi-Fi, Power Outlets, Standing Desk'),
   ('Brainstorm Room', 'MEETING_ROOM',    8, 'Projector, Whiteboard, Video Conferencing'),
   ('Executive Suite', 'PRIVATE_OFFICE',  4, 'AC, Locker, Printer Access'),
   ('Open Lounge',     'HOT_DESK',       20, 'Wi-Fi, Bean Bags, Coffee Machine');
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

## 📸 Screenshots

> *Screenshots will be added as sprints progress.*

| Screen | Description |
|---|---|
| **Login** | Clean sign-in form with validation feedback |
| **Registration** | Full registration with password strength meter |
| **Member Dashboard** | Sidebar navigation with space browsing and booking |
| **Admin Dashboard** | Admin control panel with management tools |
| **Space Browser** | Filter and book coworking spaces |
| **Booking History** | View and cancel past bookings |

---

## 🗺 Development Roadmap

The project follows an **Agile Scrum** methodology with incremental sprints:

| Sprint | Focus Area | Status |
|---|---|---|
| **Sprint 1** | Core Auth (Login, Register, Password Reset) + DB Setup + MVC Foundation | ✅ Completed |
| **Sprint 2** | Room Browsing, Booking Engine, Booking History, Cancellation | ✅ Completed |
| **Sprint 3** | Subscription Plans, Promo Codes, Invoice Generation (PDFBox) | 🔄 In Progress |
| **Sprint 4** | Admin — Manage Spaces, Occupancy Reports, Member Directory | 📋 Planned |

---

## 👥 Team — Trinova Tech

<p align="center">
  <strong>We are Trinova Tech</strong> — a team of three passionate software engineering students committed to building elegant, professional-grade solutions.
</p>

| | Name | Student ID | Role | GitHub |
|---|---|---|---|---|
| 👨‍💻 | **Muhammad Idrees** | i230721 | Team Lead & Backend Developer | [@code-with-idrees](https://github.com/code-with-idrees) |
| 👨‍💻 | **Haris** | i230582 | Frontend Developer & UI/UX | — |
| 👨‍💻 | **Farzeen** | i230814 | Database & Testing Engineer | — |

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
