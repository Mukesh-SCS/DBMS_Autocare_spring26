
# Germantown AutoCare Management System (GAMS)

## Overview
Germantown AutoCare Management System (GAMS) is a database-driven application for managing a local auto repair shop. It streamlines customer, vehicle, appointment, service, parts, and billing management using a MySQL backend and a Java Swing GUI frontend.

**Business:** Germantown AutoCare  
**Type:** Local Auto Repair & Service Shop  
**Location:** Germantown, MD

---

## Features
- Customer management (add, update, delete, view history)
- Vehicle registration and tracking
- Appointment scheduling and mechanic assignment
- Service and parts inventory management
- Invoice generation and payment tracking
- Reporting (revenue, popular services, workload, inventory)

---

## Technologies Used
- **Java 24** (Swing for GUI)
- **MySQL** (relational database)
- **JDBC** (Java Database Connectivity)
- **Maven** (dependency management)
- **MySQL Connector/J**

---

## Project Structure

```bash
GAMS/
│
├── README.md        # Project overview, requirements, setup instructions, and structure
│ 
│
├── sql/                 #Contains all database-related SQL scripts
│ ├── create_tables.sql
│ ├── insert_sample_data.sql
│ └── reports_queries.sql
│
├── src/
│ └── main/
│ └── java/
│ └── com/
│ └── germantown/
│ └── autocare/
│
│ ├── app/
│ │ └── MainApp.java          #
│ │
│ ├── config/                 # Handles MySQL database connection
│ │ └── DBConnection.java
│ │ 
│ │
│ ├── model/                   #Data model classes (POJOs) 
│ │ ├── Customer.java
│ │ ├── Vehicle.java
│ │ ├── Employee.java
│ │ ├── Service.java
│ │ ├── Part.java
│ │ ├── Appointment.java
│ │ ├── Invoice.java
│ │ └── Payment.java
│ │
│ ├── dao/                         #Data Access Objects (database operations)
│ │ ├── CustomerDAO.java
│ │ ├── VehicleDAO.java
│ │ ├── EmployeeDAO.java
│ │ ├── ServiceDAO.java
│ │ ├── PartDAO.java
│ │ ├── AppointmentDAO.java
│ │ ├── InvoiceDAO.java
│ │ └── PaymentDAO.java
│ │
│ ├── ui/                          #Java Swing user interface screens
│ │ ├── LoginFrame.java
│ │ ├── DashboardFrame.java
│ │ ├── CustomerPanel.java
│ │ ├── VehiclePanel.java
│ │ ├── AppointmentPanel.java
│ │ ├── ServicePartPanel.java
│ │ ├── InvoicePaymentPanel.java
│ │ └── ReportPanel.java
│ │
│ ├── service/                           #Business logic layer 
│ │ ├── CustomerService.java
│ │ ├── AppointmentService.java
│ │ └── BillingService.java
│ │
│ └── util/
│ Utility and helper classes
│ ├── DateUtil.java
│ ├── ValidationUtil.java
│ └── UIHelper.java
│
└── target/


```

---

## Setup Instructions

### 1. Database Setup (MySQL)
- Create a database named `autocare_db` in MySQL.
- Place your table creation and sample data scripts in `Autocare/sql/` (e.g., `create_tables.sql`, `insert_sample_data.sql`).
- Example commands:
  - `mysql -u root -p autocare_db < create_tables.sql`
  - `mysql -u root -p autocare_db < insert_sample_data.sql`

### 2. Java & Maven Setup
- Ensure you have Java 24 and Maven installed.
- Open the project in your IDE (e.g., IntelliJ IDEA).
- Build the project with Maven:
  - `mvn clean install`
- Run the application:
  - `Main.java` (entry point)

### 3. Database Connection
- Edit `DBConnection.java` if your MySQL username/password differs:
  - Default: user=`root`, password=`admin`, database=`autocare_db`

---

## Usage
- Launch the application to access the login screen.
- Navigate through the GUI to manage customers, vehicles, appointments, services, parts, invoices, and reports.

---

## Notes
- Foreign keys enforce data integrity.
- Many-to-many relationships: `Appointment_Service`, `Service_Part`.
- Each appointment has a unique invoice; invoices can have multiple payments.
- All SQL scripts should be placed in the `Autocare/sql/` directory.

---

## Authors & Credits
- Requirements by: Anthony Tran (Mechanic)
- Developed by: [Your Name Here]

---

## License
See LICENSE file for details.
Appointment Management
- Schedule service appointments
- Assign mechanics
- Update appointment status

Service Management
- Maintain list of services (oil change, brake repair, etc.)
- Record services performed per appointment

Parts Inventory
- Track parts and quantities
- Update inventory after service

Billing & Payment
- Generate invoices
- Record payments
- View unpaid invoices

Reporting (Queries)
- Monthly revenue report
- Most popular services
- Mechanic workload
- Low-stock parts

------------------------------------------------------------
3. Database Design (Tables)
------------------------------------------------------------
Main Entities (Tables)
1. Customer
2. Vehicle
3. Employee (Mechanic / Staff)
4. Service
5. Appointment
% 6. Appointment_Service  ( Just require part of DB )
7. Part
% 8. Service_Part
9. Invoice
10. Payment

------------------------------------------------------------
4. Setup Instructions (MySQL)
------------------------------------------------------------
Database name: autocare_db (already created by user)

Step A: Create tables
1) Open MySQL Shell or Workbench SQL editor
2) Run: create_tables.sql

Step B: Insert sample data
1) Run: insert_sample_data.sql

Verification queries:
- SHOW TABLES;
- SELECT * FROM customer;
- SELECT * FROM appointment;

------------------------------------------------------------
5. Java Swing User Interface
------------------------------------------------------------
Screens:
1. Login Screen
2. Customer Management Screen
3. Vehicle Management Screen
4. Appointment Scheduling Screen
5. Service & Parts Screen
6. Invoice & Payment Screen
7. Reports Screen

Technology:
- Java Swing GUI
- JDBC connection to MySQL
- MySQL Connector/J
- IntelliJ IDEA

------------------------------------------------------------
6. Suggested Project Folder Structure
------------------------------------------------------------
GAMS/
  sql/
    create_tables.sql
    insert_sample_data.sql
  src/
    (Java source code)
  README.md

------------------------------------------------------------
7. Notes
------------------------------------------------------------
- Foreign keys enforce data integrity across tables.
- Appointment_Service and Service_Part represent many-to-many relationships.
- Invoice is 1-to-1 with appointment (unique appointment_id).
- Payment is 1-to-many with invoice.


