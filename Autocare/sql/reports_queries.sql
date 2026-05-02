-- Report queries for Germantown AutoCare (autocare_db)
-- Run against the same database as the application. Use after create_tables.sql
-- and optionally insert_sample_data.sql.

USE autocare_db;

-- =============================================================================
-- 1. REVENUE SUMMARY BY MONTH (from paid / partial invoice totals)
-- =============================================================================
SELECT
    DATE_FORMAT(i.Invoice_Date, '%Y-%m') AS revenue_month,
    COUNT(DISTINCT i.Invoice_ID)         AS invoice_count,
    SUM(i.Total_Amount)                 AS total_invoiced,
    COALESCE(SUM(p.pay_sum), 0)         AS total_payments_received
FROM Invoice i
LEFT JOIN (
    SELECT Invoice_ID, SUM(Payment_Amount) AS pay_sum
    FROM Payment
    GROUP BY Invoice_ID
) p ON p.Invoice_ID = i.Invoice_ID
GROUP BY DATE_FORMAT(i.Invoice_Date, '%Y-%m')
ORDER BY revenue_month;

-- =============================================================================
-- 2. APPOINTMENTS BY STATUS
-- =============================================================================
SELECT
    Status,
    COUNT(*) AS appointment_count
FROM Appointment
GROUP BY Status
ORDER BY appointment_count DESC;

-- =============================================================================
-- 3. TOP CUSTOMERS BY LIFETIME INVOICE TOTAL
-- =============================================================================
SELECT
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    COUNT(DISTINCT i.Invoice_ID)          AS invoices_count,
    SUM(i.Total_Amount)                   AS total_billed
FROM customer c
JOIN Appointment a ON a.Customer_ID = c.customer_id
JOIN Invoice i ON i.Appointment_ID = a.Appointment_ID
GROUP BY c.customer_id, c.first_name, c.last_name
ORDER BY total_billed DESC
LIMIT 10;

-- =============================================================================
-- 4. MOST BOOKED SERVICES (by appointment line rows)
-- =============================================================================
SELECT
    s.service_id,
    s.service_name,
    COUNT(*)           AS times_booked,
    SUM(aps.Quantity) AS total_quantity
FROM Appointment_Service aps
JOIN service s ON s.service_id = aps.Service_ID
GROUP BY s.service_id, s.service_name
ORDER BY times_booked DESC;

-- =============================================================================
-- 5. LOW STOCK PARTS (at or below reorder level)
-- =============================================================================
SELECT
    Part_ID,
    Part_Name,
    Quantity_In_Stock,
    Reorder_Level,
    Unit_Price
FROM Part
WHERE Quantity_In_Stock <= Reorder_Level
ORDER BY Quantity_In_Stock ASC, Part_Name;

-- =============================================================================
-- 6. OUTSTANDING BALANCES (invoices not fully covered by payments)
-- =============================================================================
SELECT
    i.Invoice_ID,
    i.Invoice_Date,
    i.Total_Amount,
    i.Payment_Status,
    COALESCE(SUM(p.Payment_Amount), 0) AS amount_paid,
    i.Total_Amount - COALESCE(SUM(p.Payment_Amount), 0) AS balance_due
FROM Invoice i
LEFT JOIN Payment p ON p.Invoice_ID = i.Invoice_ID
GROUP BY i.Invoice_ID, i.Invoice_Date, i.Total_Amount, i.Payment_Status
HAVING balance_due > 0.01
ORDER BY i.Invoice_Date;

-- =============================================================================
-- 7. DAILY APPOINTMENT LOAD (for a selected month — example: January 2026)
-- =============================================================================
SELECT
    DATE(Appointment_Date) AS appt_day,
    COUNT(*)               AS appointments_scheduled
FROM Appointment
WHERE Appointment_Date >= '2026-01-01'
  AND Appointment_Date < '2026-02-01'
GROUP BY DATE(Appointment_Date)
ORDER BY appt_day;

-- =============================================================================
-- 8. ACTIVE VEHICLE COUNT PER CUSTOMER
-- =============================================================================
SELECT
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    COUNT(v.vehicle_id)                    AS vehicle_count
FROM customer c
LEFT JOIN vehicle v ON v.customer_id = c.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name
HAVING vehicle_count > 0
ORDER BY vehicle_count DESC, customer_name;
