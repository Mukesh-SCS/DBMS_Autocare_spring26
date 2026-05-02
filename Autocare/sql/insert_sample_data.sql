-- Sample data for Germantown AutoCare (autocare_db)
-- Run after: create_tables.sql
-- Safe to re-run: clears existing rows in dependency order, then inserts.

USE autocare_db;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE Payment;
TRUNCATE TABLE Invoice;
TRUNCATE TABLE Appointment_Service;
TRUNCATE TABLE Appointment;
TRUNCATE TABLE service_part;
TRUNCATE TABLE vehicle;
TRUNCATE TABLE customer;
TRUNCATE TABLE service;
TRUNCATE TABLE Part;
TRUNCATE TABLE employee;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------------
-- CUSTOMERS
-- ---------------------------------------------------------------------------
INSERT INTO customer (customer_id, first_name, last_name, email, phone, address) VALUES
(1, 'John', 'Smith', 'john.smith@email.com', '301-555-0101', '12 Maple Ave, Germantown, MD'),
(2, 'Maria', 'Garcia', 'maria.g@email.com', '301-555-0102', '44 Oak Ln, Gaithersburg, MD'),
(3, 'James', 'Wilson', 'jwilson@email.com', '301-555-0103', '9 Cedar Ct, Rockville, MD'),
(4, 'Priya', 'Patel', 'priya.p@email.com', '301-555-0104', '200 Main St, Germantown, MD'),
(5, 'David', 'Chen', 'dchen@email.com', '301-555-0105', '77 Elm Rd, Clarksburg, MD');

-- ---------------------------------------------------------------------------
-- VEHICLES
-- ---------------------------------------------------------------------------
INSERT INTO vehicle (vehicle_id, customer_id, vin, make, model, year, license_plate) VALUES
(1, 1, '1HGBH41JXMN109186', 'Honda', 'Civic', 2020, 'MD-ABC123'),
(2, 1, '5YJ3E1EA1KF123456', 'Toyota', 'Camry', 2018, 'MD-XYZ789'),
(3, 2, '1FTFW1ET5DFC12345', 'Ford', 'F-150', 2019, 'MD-TRK901'),
(4, 3, '4S4BTACC6M3210987', 'Subaru', 'Outback', 2021, 'MD-SUB445'),
(5, 4, 'WVWZZZ3CZWE123456', 'Volkswagen', 'Jetta', 2017, 'MD-VW332'),
(6, 5, 'KM8K3CA35JU654321', 'Hyundai', 'Tucson', 2022, 'MD-HYU778');

-- ---------------------------------------------------------------------------
-- SERVICES
-- ---------------------------------------------------------------------------
INSERT INTO service (service_id, service_name, estimated_duration_minutes, base_price) VALUES
(1, 'Oil Change (Full Synthetic)', 45, 49.99),
(2, 'Brake Inspection & Pad Replacement', 90, 189.99),
(3, 'Tire Rotation & Balance', 45, 39.99),
(4, 'Battery Test & Replacement', 60, 159.99),
(5, 'Multi-Point Inspection', 30, 29.99),
(6, 'AC Recharge & Leak Check', 75, 129.99);

-- ---------------------------------------------------------------------------
-- EMPLOYEES
-- ---------------------------------------------------------------------------
INSERT INTO employee (employee_id, first_name, last_name, job_title, email, phone, hire_date, hourly_rate) VALUES
(1, 'Michael', 'Johnson', 'Lead Technician', 'mjohnson@germantownautocare.com', '301-555-1001', '2019-03-15', 32.50),
(2, 'Sarah', 'Lee', 'Service Advisor', 'slee@germantownautocare.com', '301-555-1002', '2020-06-01', 28.00),
(3, 'Thomas', 'Brown', 'Technician', 'tbrown@germantownautocare.com', '301-555-1003', '2021-01-10', 26.75),
(4, 'Elena', 'Volkov', 'Shop Manager', 'evolkov@germantownautocare.com', '301-555-1004', '2018-11-20', 35.00);

-- ---------------------------------------------------------------------------
-- PARTS
-- ---------------------------------------------------------------------------
INSERT INTO Part (Part_ID, Part_Name, Description, Unit_Price, Quantity_In_Stock, Reorder_Level) VALUES
(1, 'Oil Filter (Standard)', 'Spin-on filter, most sedans/SUVs', 12.99, 48, 10),
(2, 'Full Synthetic Oil 5qt', '5W-30 full synthetic', 34.99, 36, 12),
(3, 'Brake Pad Set (Front)', 'Ceramic pads, pair', 89.99, 14, 6),
(4, 'Automotive Battery (Group 48)', '12V maintenance-free', 119.99, 9, 4),
(5, 'Cabin Air Filter', 'OEM-style replacement', 24.99, 22, 8),
(6, 'R134a Refrigerant 12oz', 'AC recharge', 18.99, 30, 10);

-- ---------------------------------------------------------------------------
-- SERVICE_PART (parts consumed by a service type)
-- ---------------------------------------------------------------------------
INSERT INTO service_part (service_id, Part_ID, quantity) VALUES
(1, 1, 1),
(1, 2, 1),
(2, 3, 1),
(4, 4, 1),
(6, 6, 2);

-- ---------------------------------------------------------------------------
-- APPOINTMENTS
-- ---------------------------------------------------------------------------
INSERT INTO Appointment (Appointment_ID, Customer_ID, Vehicle_ID, Appointment_Date, Status, Notes) VALUES
(1, 1, 1, '2026-01-08 09:00:00', 'Completed', 'Routine maintenance'),
(2, 2, 3, '2026-01-10 10:30:00', 'Completed', 'Truck brake noise'),
(3, 1, 2, '2026-01-14 14:00:00', 'Completed', 'Long trip prep'),
(4, 3, 4, '2026-01-15 11:00:00', 'Completed', 'No-start issue'),
(5, 4, 5, '2026-02-01 09:00:00', 'Scheduled', 'Annual inspection'),
(6, 5, 6, '2026-01-20 16:00:00', 'Cancelled', 'Customer rescheduled'),
(7, 2, 3, '2026-01-22 08:00:00', 'Completed', 'Follow-up'),
(8, 4, 5, '2026-01-25 13:00:00', 'Completed', 'AC not cold');

-- ---------------------------------------------------------------------------
-- APPOINTMENT_SERVICE (line items)
-- ---------------------------------------------------------------------------
INSERT INTO Appointment_Service (Appointment_ID, Service_ID, Quantity, Line_Price) VALUES
(1, 1, 1, 49.99),
(2, 2, 1, 219.99),
(3, 3, 1, 39.99),
(3, 5, 1, 29.99),
(4, 4, 1, 159.99),
(7, 1, 1, 49.99),
(8, 6, 1, 139.99);

-- ---------------------------------------------------------------------------
-- INVOICES
-- ---------------------------------------------------------------------------
INSERT INTO Invoice (Invoice_ID, Appointment_ID, Invoice_Date, Total_Amount, Payment_Status) VALUES
(1, 1, '2026-01-08', 49.99, 'Paid'),
(2, 2, '2026-01-10', 219.99, 'Paid'),
(3, 3, '2026-01-14', 69.98, 'Paid'),
(4, 4, '2026-01-15', 159.99, 'Partial'),
(5, 7, '2026-01-22', 49.99, 'Paid'),
(6, 8, '2026-01-25', 139.99, 'Unpaid');

-- ---------------------------------------------------------------------------
-- PAYMENTS
-- ---------------------------------------------------------------------------
INSERT INTO Payment (Payment_ID, Invoice_ID, Payment_Date, Payment_Amount, Payment_Method) VALUES
(1, 1, '2026-01-08', 49.99, 'Credit Card'),
(2, 2, '2026-01-10', 219.99, 'Debit Card'),
(3, 3, '2026-01-14', 69.98, 'Cash'),
(4, 4, '2026-01-15', 100.00, 'Credit Card'),
(5, 7, '2026-01-22', 49.99, 'Credit Card');
