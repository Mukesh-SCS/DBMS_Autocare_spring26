
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

### 3. Database Connection (Automatic!)

The application **automatically connects to**:
- **Host**: `localhost`
- **Port**: `3306`
- **Database**: `autocare_db`

Just enter your MySQL username and password in the login dialog!

#### Optional: Custom Database Configuration
Create `config.properties` in the same directory as the JAR:

```properties
db.host=localhost
db.port=3306
db.database=autocare_db
db.username=root
db.password=root
```

---

## GitHub Actions / release JAR
- Workflow: `.github/workflows/build-and-release.yml` automatically builds with JDK 17 when you create a release tag
- Uploads **`autocare-germantown.jar`** (fat JAR including the MySQL driver)
- Create a release by pushing a version tag:
  ```bash
  git tag -a v1.2.0 -m "Release v1.2.0"
  git push origin v1.2.0
  ```

### Run the downloaded JAR

```bash
java -jar autocare-germantown.jar
```

Then:
1. **Enter your MySQL credentials** (username and password)
2. Click **Login** to connect and access the dashboard

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
