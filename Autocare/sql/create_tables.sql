CREATE DATABASE IF NOT EXISTS autocare_db;
USE autocare_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS invoice;
DROP TABLE IF EXISTS appointment_service;
DROP TABLE IF EXISTS service_part;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS service;
DROP TABLE IF EXISTS part;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS customer;

SET FOREIGN_KEY_CHECKS = 1;

-- CUSTOMER
CREATE TABLE customer (
                          customer_id INT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(100) NOT NULL,
                          last_name VARCHAR(100) NOT NULL,
                          email VARCHAR(255),
                          phone VARCHAR(20) NOT NULL,
                          address VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);



-- VEHICLE
CREATE TABLE vehicle (
                         vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
                         customer_id INT NOT NULL,
                         vin VARCHAR(17),
                         make VARCHAR(50) NOT NULL,
                         model VARCHAR(50) NOT NULL,
                         year SMALLINT NOT NULL,
                         license_plate VARCHAR(20),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id)
                             REFERENCES customer(customer_id) ON DELETE CASCADE
);

-- SERVICE
CREATE TABLE service (
    service_id                 INT AUTO_INCREMENT PRIMARY KEY,
    service_name               VARCHAR(150) NOT NULL,
    estimated_duration_minutes INT,
    base_price                 DECIMAL(10, 2) NOT NULL,
    created_at                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- EMPLOYEE
CREATE TABLE employee (
    employee_id  INT AUTO_INCREMENT PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    job_title    VARCHAR(100) NOT NULL,
    email        VARCHAR(255),
    phone        VARCHAR(20),
    hire_date    DATE NOT NULL,
    hourly_rate  DECIMAL(10, 2),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- PART
CREATE TABLE Part (
                      Part_ID INT AUTO_INCREMENT PRIMARY KEY,
                      Part_Name VARCHAR(100) NOT NULL,
                      Description VARCHAR(255),
                      Unit_Price DECIMAL(10, 2) NOT NULL,
                      Quantity_In_Stock INT NOT NULL DEFAULT 0,
                      Reorder_Level INT DEFAULT 0
) ENGINE=InnoDB;

-- SERVICE_PART (M:N) — which parts a service uses
CREATE TABLE service_part (
    service_id INT NOT NULL,
    Part_ID    INT NOT NULL,
    quantity   INT NOT NULL DEFAULT 1,
    PRIMARY KEY (service_id, Part_ID),
    CONSTRAINT fk_service_part_service FOREIGN KEY (service_id)
        REFERENCES service(service_id) ON DELETE CASCADE,
    CONSTRAINT fk_service_part_part FOREIGN KEY (Part_ID)
        REFERENCES Part(Part_ID) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- APPOINTMENT
CREATE TABLE Appointment (
                             Appointment_ID INT AUTO_INCREMENT PRIMARY KEY,
                             Customer_ID INT NOT NULL,
                             Vehicle_ID INT NOT NULL,
                             Appointment_Date DATETIME NOT NULL,
                             Status VARCHAR(20) DEFAULT 'Scheduled',
                             Notes VARCHAR(255),
                             CONSTRAINT fk_appointment_customer FOREIGN KEY (Customer_ID)
                                 REFERENCES customer(customer_id) ON DELETE CASCADE,
                             CONSTRAINT fk_appointment_vehicle FOREIGN KEY (Vehicle_ID)
                                 REFERENCES vehicle(vehicle_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- APPOINTMENT_SERVICE (M:N)
CREATE TABLE Appointment_Service (
                                     Appointment_ID INT NOT NULL,
                                     Service_ID INT NOT NULL,
                                     Quantity INT NOT NULL DEFAULT 1,
                                     Line_Price DECIMAL(10, 2) NOT NULL,
                                     PRIMARY KEY (Appointment_ID, Service_ID),
                                     CONSTRAINT fk_app_svc_appointment FOREIGN KEY (Appointment_ID)
                                         REFERENCES Appointment(Appointment_ID) ON DELETE CASCADE,
                                     CONSTRAINT fk_app_svc_service FOREIGN KEY (Service_ID)
                                         REFERENCES service(service_id) ON DELETE RESTRICT
) ENGINE=InnoDB;



-- INVOICE
CREATE TABLE Invoice (
                         Invoice_ID INT PRIMARY KEY AUTO_INCREMENT,
                         Appointment_ID INT NOT NULL,
                         Invoice_Date DATE NOT NULL,
                         Total_Amount DECIMAL(10, 2) NOT NULL,
                         Payment_Status VARCHAR(20) DEFAULT 'Unpaid',
                         CONSTRAINT fk_appointment FOREIGN KEY (Appointment_ID)
                             REFERENCES Appointment(Appointment_ID)
) ENGINE=InnoDB;


-- PAYMENT
CREATE TABLE Payment (
                         Payment_ID INT PRIMARY KEY AUTO_INCREMENT,
                         Invoice_ID INT NOT NULL,
                         Payment_Date DATE NOT NULL,
                         Payment_Amount DECIMAL(10, 2) NOT NULL,
                         Payment_Method VARCHAR(50),
                         CONSTRAINT fk_invoice FOREIGN KEY (Invoice_ID)
                             REFERENCES Invoice(Invoice_ID)
) ENGINE=InnoDB;


CREATE INDEX idx_vehicle_customer ON vehicle(customer_id);