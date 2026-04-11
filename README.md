
# Germantown AutoCare Management System (GAMS)

## Overview
Germantown AutoCare Management System (GAMS) is a database-driven application for managing a local auto repair shop. It streamlines customer, vehicle, appointment, service, parts, and billing management using a MySQL backend and a Java Swing GUI frontend.

**Business:** Germantown AutoCare  
**Type:** Local Auto Repair & Service Shop  
**Location:** Germantown, MD

---

## Features
- Customer management (add, update, delete, list)
- Vehicle registration and tracking
- Appointment scheduling (customer, vehicle, date/time, status)
- Service catalog and parts inventory (Services & Parts tab, wired to `ServiceDAO` / `PartDAO`)
- Invoice creation and payment recording (Billing tab)
- Reporting queries (see course materials / future work)

---

## Technologies Used
- **Java 17** (Swing GUI; matches `Autocare/pom.xml` and CI)
- **MySQL** (relational database)
- **JDBC** (Java Database Connectivity)
- **Maven** (build and dependencies)
- **MySQL Connector/J** (bundled in the release fat JAR via Maven Shade)

---

## Repository layout

```
DBMS_Autocare_spring26/
├── README.md
├── .github/workflows/build-and-release.yml   # CI: build + release on tags
├── Autocare/
│   ├── pom.xml                                 # Maven project (artifact: gams, output JAR: autocare-germantown.jar)
│   ├── sql/
│   │   └── create_tables.sql                   # Schema (run against autocare_db)
│   └── src/main/java/com/germantown/autocare/
│       ├── app/MainApp.java                    # Entry point
│       ├── config/DBConnection.java
│       ├── model/                              # Customer, Vehicle, Employee, Service, Part, Appointment, Invoice, Payment
│       ├── dao/                                # JDBC access per table
│       ├── service/                            # AppointmentService, BillingService
│       ├── ui/                                 # LoginFrame, DashboardFrame, CustomerPanel, VehiclePanel, AppointmentPanel, ServicePartPanel, InvoicePaymentPanel
│       └── util/UIHelper.java
└── (optional) insert_sample_data.sql           # Add if you maintain sample data scripts
```

---

## Setup Instructions

### 1. Database (MySQL)
- Create database `autocare_db` (or use the `CREATE DATABASE` in `Autocare/sql/create_tables.sql`).
- Run the schema script (from project root, paths may vary):

```bash
mysql -u root -p < "Autocare/sql/create_tables.sql"
```

- Optional: add `insert_sample_data.sql` under `Autocare/sql/` if you use one.

### 2. Java & Maven (local development)
- Install **JDK 17** and **Maven**.
- Build and run from the `Autocare` folder:

```bash
cd Autocare
mvn clean package
java -jar target/autocare-germantown.jar
```

Or run **`com.germantown.autocare.app.MainApp`** from your IDE.

### 3. Database connection
- Edit `Autocare/src/main/java/com/germantown/autocare/config/DBConnection.java` if your MySQL user, password, host, or database name differs.
- Default in code is typically `root` / `admin` / `jdbc:mysql://localhost:3306/autocare_db` (adjust for your machine).

---

## GitHub Actions / release JAR
- Workflow: `.github/workflows/build-and-release.yml` builds with JDK 17 and uploads **`autocare-germantown.jar`** (fat JAR including the MySQL driver).
- Releases are created when you push a version tag (e.g. `v1.0.0`), not on every push to `main`.

### Run the downloaded JAR

```bash
java -jar autocare-germantown.jar
```

Ensure MySQL is running and `autocare_db` exists with tables applied.

---

## Database design (summary)
Main entities: customer, vehicle, employee, service, part, appointment, appointment_service, service_part, invoice, payment.  
Foreign keys link appointments to customers/vehicles, invoices to appointments, payments to invoices, etc.

---

## Authors & Credits
- Requirements by: Anthony Tran (Mechanic)
- Developed by: [Your Name Here]

---

## License
See LICENSE file for details.
