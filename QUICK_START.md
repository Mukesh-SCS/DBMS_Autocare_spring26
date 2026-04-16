# Quick Start Guide - Germantown AutoCare Management System

## Installation (3 Steps)

### Step 1: Download & Extract
1. Go to [GitHub Releases](https://github.com/your-repo/DBMS_Autocare_spring26/releases)
2. Download `autocare-germantown.jar`
3. Save it to a directory (e.g., `C:\AutoCare\`)

### Step 2: Setup MySQL Database
```bash
# Open MySQL command line
mysql -u root -p

# Create the database
CREATE DATABASE autocare_db;
USE autocare_db;
SOURCE path/to/sql/create_tables.sql;

# (Optional) Add sample data
SOURCE path/to/sql/insert_sample_data.sql;
```

### Step 3: Start the Application
```bash
cd C:\AutoCare
java -jar autocare-germantown.jar
```

---

## Login

When the application starts, you'll see the login screen:

| Field | Enter | Example |
|-------|-------|---------|
| **Username** | Your MySQL username | `root` |
| **Password** | Your MySQL password | `root` |

Then click **Login**

**Note:** The application automatically connects to:
- **Host**: `localhost`
- **Port**: `3306` 
- **Database**: `autocare_db`

If you need different settings, edit `config.properties` (see below).

---

## Configuration File (Optional)

Create `config.properties` in the same folder as the JAR:

```properties
db.host=localhost
db.port=3306
db.database=autocare_db
db.username=root
db.password=root
```

When you run the JAR, the application will automatically load this file and use those settings for the database connection. This means you won't need to enter the host, port, and database name in the login dialog—just enter your MySQL username and password.

---

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Connection failed" | Check MySQL is running, verify credentials |
| "Unknown database" | Run `create_tables.sql` first |
| "Access denied" | Fix username/password in login or config.properties |
| "Port 3306 in use" | Change port in login dialog or config.properties |

---

## Features Overview

- **👥 Customers** - Manage client information
- **🚗 Vehicles** - Track customer vehicles
- **📅 Appointments** - Schedule service appointments
- **🔧 Services** - Manage services & parts
- **💰 Billing** - Generate invoices & process payments
- **📊 Reports** - View business statistics

---

## System Requirements

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/#java17)
- **MySQL 5.7+** - [Download](https://dev.mysql.com/downloads/mysql/)
- **Windows/Mac/Linux** - Cross-platform compatible
- **Disk Space**: 50MB minimum

---

## Need Help?

See **SETUP_GUIDE.md** for detailed instructions.

Create an issue on GitHub for bugs or feature requests.
