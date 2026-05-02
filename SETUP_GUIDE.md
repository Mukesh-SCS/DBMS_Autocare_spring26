# Germantown AutoCare Management System - Setup Guide

## Quick Start

### Prerequisites
- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/#java17)
- **MySQL Server** - [Download](https://dev.mysql.com/downloads/mysql/)

### Installation Steps

#### 1. Download the Application
- Go to GitHub Releases and download `autocare-germantown.jar`
- Place it in your desired directory

#### 2. Create the Database
Open MySQL and create the database:
```bash
# Using MySQL command line
mysql -u root -p

# In MySQL prompt
CREATE DATABASE autocare_db;
USE autocare_db;
```

#### 3. Import Database Schema
```bash
# Import the schema file
mysql -u root -p autocare_db < sql/create_tables.sql

# (Optional) Import sample data
mysql -u root -p autocare_db < sql/insert_sample_data.sql
```

#### 4. Run the Application
```bash
java -jar autocare-germantown.jar
```

The main dashboard opens when the database connection succeeds. Defaults: **localhost:3306**, database **autocare_db**, user **root**, password **root**.

### Custom database settings
If you need a different host, port, database, or MySQL user, create `config.properties` next to the JAR:

```properties
db.host=192.168.1.100
db.port=3306
db.database=autocare_db
db.username=root
db.password=root
```

The file is read at startup before the UI appears.

---

## Configuration File

### Default config.properties Location
Place `config.properties` in the same directory as the JAR (the working directory when you run `java -jar`). The application checks for this file first at startup and loads it automatically.

As a fallback for development, the file can also be placed on the application's classpath (e.g., in `src/main/resources`).
### Configuration Options
| Property | Description | Default |
|----------|-------------|---------|
| `db.host` | MySQL server hostname or IP | `localhost` |
| `db.port` | MySQL port number | `3306` |
| `db.database` | Database name | `autocare_db` |
| `db.username` | MySQL username | `root` |
| `db.password` | MySQL password | `root` |

---

## Connecting to Remote MySQL

To connect to a remote MySQL server:

1. **Create config.properties:**
```properties
db.host=192.168.1.100      # Your server IP
db.port=3306               # MySQL port
db.database=autocare_db
db.username=admin
db.password=secure_password
```

2. **Place in same directory as JAR**
3. **Run** `java -jar autocare-germantown.jar` **from that directory** (or set the working directory so the file is found)

---

## Features

### Customer Management
- Add, update, and view customer information
- Track customer contact details and vehicles

### Vehicle Tracking
- Register vehicles with make, model, year
- Link vehicles to customers
- Track vehicle maintenance history

### Appointment Scheduling
- Schedule service appointments
- View appointment calendar
- Track appointment status

### Service & Parts Management
- Manage service catalog
- Track parts inventory
- Link services to appointments

### Invoicing
- Generate invoices for completed services
- Track parts and labor costs
- Include payment terms

### Payment Processing
- Record customer payments
- Track payment methods
- Monitor outstanding balances

### Reporting
- Generate business reports
- View sales statistics
- Track inventory levels

---

## Troubleshooting

### Issue: "Authentication failed"
**Solution:**
- Verify username and password are correct
- Check that MySQL user exists and has permissions
- Try connecting with MySQL Workbench to verify credentials

### Issue: "Database connection failed"
**Solution:**
- Ensure MySQL is running
- Verify `autocare_db` database exists
- Check database was created with `create_tables.sql`

### Issue: "Unknown database 'autocare_db'"
**Solution:**
```bash
# Create database and import schema
mysql -u root -p
CREATE DATABASE autocare_db;
USE autocare_db;
SOURCE path/to/sql/create_tables.sql;
```

### Issue: Can't connect to remote MySQL
**Solution:**
- Create `config.properties` with correct host/port
- Verify MySQL remote access is enabled
- Check firewall allows port 3306

---

## Building from Source

### Prerequisites
- Java 17 SDK
- Apache Maven 3.8+
- Git

### Steps
```bash
# Clone the repository
git clone https://github.com/your-repo/DBMS_Autocare_spring26.git
cd DBMS_Autocare_spring26/Autocare

# Build the JAR
mvn clean package

# Run the application
java -jar target/autocare-germantown.jar
```

---

## System Architecture

```
Germantown AutoCare Management System
├── UI Layer (Swing)
│   ├── DashboardFrame (main window)
│   ├── DashboardFrame
│   └── [Business Panels]
├── Service Layer
│   ├── CustomerService
│   ├── AppointmentService
│   └── BillingService
├── DAO Layer (Data Access)
│   ├── CustomerDAO
│   ├── EmployeeDAO
│   └── [Other DAOs]
└── Database Layer (MySQL)
    └── autocare_db (localhost:3306)
```

---

## GitHub Actions CI/CD

The project includes automated build and release workflow:
- Automatically builds JAR on version tags
- Creates GitHub releases with assets
- Generates release notes with installation guide

### Creating a Release
```bash
git tag -a v1.0.1 -m "Version 1.0.1"
git push origin v1.0.1
```

GitHub Actions will automatically build and release the JAR!

---

## Support & Documentation

- **Report Issues:** Create an issue on GitHub
- **Database Schema:** See `sql/create_tables.sql`
- **Sample Data:** See `sql/insert_sample_data.sql`
- **Reports Guide:** See `sql/reports_queries.sql`

---

## Version History

- **v1.0.0** - Initial release
- **v1.1.0** - Login system with configurable database
- **v1.2.0** - Simplified login (username/password only, auto-configured database)
