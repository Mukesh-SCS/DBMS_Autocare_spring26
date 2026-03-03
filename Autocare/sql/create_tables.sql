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


-- PART


-- APPOINTMENT


-- APPOINTMENT_SERVICE (M:N)


-- SERVICE_PART (M:N)


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