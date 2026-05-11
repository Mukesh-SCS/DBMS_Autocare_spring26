package com.germantown.autocare.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.germantown.autocare.dao.CustomerDAO;
import com.germantown.autocare.dao.EmployeeDAO;
import com.germantown.autocare.dao.VehicleDAO;
import com.germantown.autocare.model.Appointment;
import com.germantown.autocare.model.Customer;
import com.germantown.autocare.model.Employee;
import com.germantown.autocare.model.Vehicle;
import com.germantown.autocare.service.AppointmentService;
import com.germantown.autocare.util.UIHelper;
import com.germantown.autocare.util.UiTheme;

/**
 * Appointment scheduling and management screen.
 */
public class AppointmentPanel extends JPanel {

    private final AppointmentService appointmentService = new AppointmentService();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JComboBox<CustomerItem> customerCombo;
    private final JComboBox<VehicleItem> vehicleCombo;
    private final JComboBox<EmployeeItem> employeeCombo;
    private final JTextField dateTimeField;
    private final JTextField notesField;
    private final JComboBox<String> statusCombo;
    private final JButton addBtn, updateBtn, cancelBtn, refreshBtn, clearBtn;

    private static final String[] COLUMNS = { "ID", "Customer", "Vehicle", "Employee", "When", "Status", "Notes" };
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        UiTheme.paintPanelBackground(this);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UiTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectRowToForm();
        });
        JScrollPane tableScroll = new JScrollPane(table);
        UiTheme.styleScrollPane(tableScroll);
        add(tableScroll, BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(UiTheme.sectionBorder("Schedule / update appointment"));
        UiTheme.paintPanelBackground(form);

        form.add(new JLabel("Customer:"));
        customerCombo = new JComboBox<>();
        form.add(customerCombo);

        form.add(new JLabel("Vehicle:"));
        vehicleCombo = new JComboBox<>();
        form.add(vehicleCombo);

        form.add(new JLabel("Employee:"));
        employeeCombo = new JComboBox<>();
        form.add(employeeCombo);

        form.add(new JLabel("When (YYYY-MM-DD HH:MM):"));
        dateTimeField = new JTextField();
        form.add(dateTimeField);

        form.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[] { "Scheduled", "Completed", "Cancelled" });
        form.add(statusCombo);

        form.add(new JLabel("Notes:"));
        notesField = new JTextField();
        form.add(notesField);

        add(form, BorderLayout.NORTH);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        UiTheme.paintPanelBackground(buttons);
        addBtn = new JButton("Schedule");
        updateBtn = new JButton("Save changes");
        cancelBtn = new JButton("Cancel Appointment");
        refreshBtn = new JButton("Refresh");
        clearBtn = new JButton("Clear");
        for (JButton b : new JButton[]{addBtn, updateBtn, cancelBtn, refreshBtn, clearBtn}) {
            UiTheme.styleToolbarButton(b);
        }

        customerCombo.addActionListener(e -> {
            CustomerItem ci = (CustomerItem) customerCombo.getSelectedItem();
            vehicleCombo.removeAllItems();
            vehicleCombo.addItem(new VehicleItem(0, "-- Select Vehicle --"));
            if (ci != null && ci.id != 0) {
                try {
                    for (Vehicle v : vehicleDAO.findByCustomerId(ci.id)) {
                        vehicleCombo.addItem(new VehicleItem(
                                v.getVehicleId(),
                                v.getMake() + " " + v.getModel() + " (" + v.getYear() + ")"
                        ));
                    }
                } catch (Exception ex) {
                    UIHelper.showError(this, "Load vehicles failed: " + ex.getMessage());
                }
            }
        });

        addBtn.addActionListener(e -> schedule());
        updateBtn.addActionListener(e -> updateAppointment());
        cancelBtn.addActionListener(e -> cancel());
        refreshBtn.addActionListener(e -> {
            loadCombos();
            loadTable();
        });
        clearBtn.addActionListener(e -> clearForm());

        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(cancelBtn);
        buttons.add(refreshBtn);
        buttons.add(clearBtn);
        add(buttons, BorderLayout.SOUTH);

        loadCombos();
        loadTable();
    }

    private void loadCombos() {
        customerCombo.removeAllItems();
        customerCombo.addItem(new CustomerItem(0, "-- Select Customer --"));
        try {
            List<Customer> customers = customerDAO.findAll();
            for (Customer c : customers) {
                customerCombo.addItem(new CustomerItem(
                        c.getCustomerId(),
                        c.getFirstName() + " " + c.getLastName() + " (" + c.getPhone() + ")"
                ));
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load customers failed: " + ex.getMessage());
        }

        vehicleCombo.removeAllItems();
        vehicleCombo.addItem(new VehicleItem(0, "-- Select Vehicle --"));

        employeeCombo.removeAllItems();
        employeeCombo.addItem(new EmployeeItem(0, "-- No employee --"));
        try {
            for (Employee emp : employeeDAO.findAll()) {
                employeeCombo.addItem(new EmployeeItem(emp.getEmployeeId(), emp.toString()));
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load employees failed: " + ex.getMessage());
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Appointment> list = appointmentService.listAll();
            List<Customer> customers = customerDAO.findAll();
            List<Vehicle> vehicles = vehicleDAO.findAll();
            Map<Integer, String> employeeNames = new HashMap<>();
            for (Employee emp : employeeDAO.findAll()) {
                employeeNames.put(emp.getEmployeeId(), emp.getFirstName() + " " + emp.getLastName());
            }
            for (Appointment a : list) {
                String custName = customers.stream()
                        .filter(c -> c.getCustomerId() == a.getCustomerId())
                        .findFirst()
                        .map(c -> c.getFirstName() + " " + c.getLastName())
                        .orElse("?");
                String vehicleLabel = vehicles.stream()
                        .filter(v -> v.getVehicleId() == a.getVehicleId())
                        .findFirst()
                        .map(v -> v.getMake() + " " + v.getModel())
                        .orElse("?");
                String empLabel = "—";
                if (a.getEmployeeId() != null) {
                    empLabel = employeeNames.getOrDefault(a.getEmployeeId(), "#" + a.getEmployeeId());
                }
                tableModel.addRow(new Object[]{
                        a.getAppointmentId(),
                        custName,
                        vehicleLabel,
                        empLabel,
                        a.getAppointmentDate() != null ? a.getAppointmentDate().format(FORMATTER) : "",
                        a.getStatus(),
                        a.getNotes() != null ? a.getNotes() : ""
                });
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load appointments failed: " + ex.getMessage());
        }
    }

    private void selectRowToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        dateTimeField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 5));
        notesField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        int apptId = (Integer) tableModel.getValueAt(row, 0);
        try {
            Appointment a = appointmentService.findById(apptId);
            selectEmployeeById(a != null ? a.getEmployeeId() : null);
        } catch (Exception ex) {
            UIHelper.showError(this, "Could not load appointment: " + ex.getMessage());
        }
    }

    private void selectEmployeeById(Integer employeeId) {
        int wantId = employeeId != null ? employeeId : 0;
        for (int i = 0; i < employeeCombo.getItemCount(); i++) {
            EmployeeItem ei = employeeCombo.getItemAt(i);
            if (ei.id == wantId) {
                employeeCombo.setSelectedIndex(i);
                return;
            }
        }
        employeeCombo.setSelectedIndex(0);
    }

    private void schedule() {
        CustomerItem ci = (CustomerItem) customerCombo.getSelectedItem();
        VehicleItem vi = (VehicleItem) vehicleCombo.getSelectedItem();
        if (ci == null || ci.id == 0) {
            UIHelper.showError(this, "Select a customer.");
            return;
        }
        if (vi == null || vi.id == 0) {
            UIHelper.showError(this, "Select a vehicle.");
            return;
        }
        String whenStr = dateTimeField.getText().trim();
        if (whenStr.isEmpty()) {
            UIHelper.showError(this, "Enter a date/time.");
            return;
        }
        LocalDateTime when;
        try {
            when = LocalDateTime.parse(whenStr, FORMATTER);
        } catch (DateTimeParseException ex) {
            UIHelper.showError(this, "Invalid date/time format. Use YYYY-MM-DD HH:MM.");
            return;
        }
        String notes = notesField.getText().trim();
        EmployeeItem ei = (EmployeeItem) employeeCombo.getSelectedItem();
        Integer empId = ei != null && ei.id != 0 ? ei.id : null;
        try {
            appointmentService.scheduleAppointment(ci.id, vi.id, when, notes, empId);
            UIHelper.showMessage(this, "Appointment scheduled.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Schedule failed: " + ex.getMessage());
        }
    }

    private void updateAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select an appointment first.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        String status = (String) statusCombo.getSelectedItem();
        EmployeeItem ei = (EmployeeItem) employeeCombo.getSelectedItem();
        Integer empId = ei != null && ei.id != 0 ? ei.id : null;
        try {
            appointmentService.updateAppointmentDetails(id, status, empId);
            UIHelper.showMessage(this, "Appointment updated.");
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void cancel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select an appointment to cancel.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        
        // Check if appointment has linked invoices
        try {
            List<Integer> linkedInvoices = appointmentService.getLinkedInvoices(id);
            
            if (!linkedInvoices.isEmpty()) {
                // Appointment has invoices - prevent deletion
                String invoiceList = String.join(", ", linkedInvoices.stream()
                    .map(String::valueOf)
                    .toArray(String[]::new));
                
                String message = String.format(
                    "Cannot cancel this appointment!\n\n" +
                    "There are %d linked invoice(s) associated with this appointment:\n" +
                    "Invoice ID(s): %s\n\n" +
                    "Please handle the invoice(s) first, then try again.\n\n" +
                    "Options:\n" +
                    "1. Go to the Billing tab to review the invoice and record payment or mark it as paid\n" +
                    "2. Contact the office if you need assistance",
                    linkedInvoices.size(),
                    invoiceList
                );
                
                JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Cannot Cancel Appointment",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            // No invoices - safe to cancel
            if (!UIHelper.confirm(this, "Cancel this appointment?")) return;
            
            appointmentService.cancelAppointment(id);
            UIHelper.showMessage(this, "Appointment cancelled.");
            clearForm();
            loadTable();
            
        } catch (Exception ex) {
            UIHelper.showError(this, "Cancel failed: " + ex.getMessage());
        }
    }

    private void clearForm() {
        customerCombo.setSelectedIndex(0);
        vehicleCombo.setSelectedIndex(0);
        employeeCombo.setSelectedIndex(0);
        dateTimeField.setText("");
        notesField.setText("");
        statusCombo.setSelectedItem("Scheduled");
        table.clearSelection();
    }

    private static class EmployeeItem {
        final int id;
        private final String label;
        EmployeeItem(int id, String label) {
            this.id = id;
            this.label = label;
        }
        @Override public String toString() { return label; }
    }

    private static class CustomerItem {
        final int id;
        private final String label;
        CustomerItem(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static class VehicleItem {
        final int id;
        private final String label;
        VehicleItem(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}

