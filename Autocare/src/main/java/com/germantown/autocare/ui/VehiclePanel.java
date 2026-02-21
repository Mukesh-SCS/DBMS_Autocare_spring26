package com.germantown.autocare.ui;

import com.germantown.autocare.dao.CustomerDAO;
import com.germantown.autocare.dao.VehicleDAO;
import com.germantown.autocare.model.Customer;
import com.germantown.autocare.model.Vehicle;
import com.germantown.autocare.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vehicle management: list, add, update, delete. Requires selecting a customer.
 */
public class VehiclePanel extends JPanel {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JComboBox<CustomerItem> customerCombo;
    private final JTextField vinField, makeField, modelField, yearField, plateField;
    private final JButton addBtn, updateBtn, deleteBtn, clearBtn, refreshBtn;

    private static final String[] COLUMNS = { "ID", "Customer", "VIN", "Make", "Model", "Year", "License Plate" };

    public VehiclePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectRowToForm();
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.add(new JLabel("Customer:"));
        customerCombo = new JComboBox<>();
        customerCombo.addItem(new CustomerItem(0, "-- Select Customer --"));
        loadCustomersIntoCombo();
        form.add(customerCombo);
        form.add(new JLabel("VIN:"));
        vinField = new JTextField(20);
        form.add(vinField);
        form.add(new JLabel("Make:"));
        makeField = new JTextField(20);
        form.add(makeField);
        form.add(new JLabel("Model:"));
        modelField = new JTextField(20);
        form.add(modelField);
        form.add(new JLabel("Year:"));
        yearField = new JTextField(8);
        form.add(yearField);
        form.add(new JLabel("License Plate:"));
        plateField = new JTextField(20);
        form.add(plateField);
        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn = new JButton("Clear");
        refreshBtn = new JButton("Refresh");
        addBtn.addActionListener(e -> addVehicle());
        updateBtn.addActionListener(e -> updateVehicle());
        deleteBtn.addActionListener(e -> deleteVehicle());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> {
            refreshCustomerList();
            loadTable();
        });
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);

        loadTable();
    }

    private void loadCustomersIntoCombo() {
        try {
            List<Customer> list = customerDAO.findAll();
            for (Customer c : list) {
                customerCombo.addItem(new CustomerItem(c.getCustomerId(), c.getFirstName() + " " + c.getLastName() + " (" + c.getPhone() + ")"));
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load customers failed: " + ex.getMessage());
        }
    }

    /** Call when Vehicles tab is selected so the customer list is up to date (e.g. after adding customers). */
    public void refreshCustomerList() {
        customerCombo.removeAllItems();
        customerCombo.addItem(new CustomerItem(0, "-- Select Customer --"));
        loadCustomersIntoCombo();
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Vehicle> list = vehicleDAO.findAll();
            List<Customer> customers = customerDAO.findAll();
            for (Vehicle v : list) {
                String custName = customers.stream()
                    .filter(c -> c.getCustomerId() == v.getCustomerId())
                    .findFirst()
                    .map(c -> c.getFirstName() + " " + c.getLastName())
                    .orElse("?");
                tableModel.addRow(new Object[]{
                    v.getVehicleId(),
                    custName,
                    v.getVin() != null ? v.getVin() : "",
                    v.getMake(),
                    v.getModel(),
                    v.getYear(),
                    v.getLicensePlate() != null ? v.getLicensePlate() : ""
                });
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load failed: " + ex.getMessage());
        }
    }

    private void selectRowToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Object custObj = tableModel.getValueAt(row, 1);
        int id = (Integer) tableModel.getValueAt(row, 0);
        try {
            Vehicle v = vehicleDAO.findById(id);
            if (v == null) return;
            for (int i = 0; i < customerCombo.getItemCount(); i++) {
                if (customerCombo.getItemAt(i).id == v.getCustomerId()) {
                    customerCombo.setSelectedIndex(i);
                    break;
                }
            }
            vinField.setText(v.getVin() != null ? v.getVin() : "");
            makeField.setText(v.getMake());
            modelField.setText(v.getModel());
            yearField.setText(String.valueOf(v.getYear()));
            plateField.setText(v.getLicensePlate() != null ? v.getLicensePlate() : "");
        } catch (Exception ignored) {}
    }

    private void addVehicle() {
        CustomerItem ci = (CustomerItem) customerCombo.getSelectedItem();
        if (ci == null || ci.id == 0) {
            UIHelper.showError(this, "Select a customer.");
            return;
        }
        String make = makeField.getText().trim();
        String model = modelField.getText().trim();
        String yearStr = yearField.getText().trim();
        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            UIHelper.showError(this, "Make, model, and year are required.");
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Year must be a number.");
            return;
        }
        try {
            Vehicle v = new Vehicle(ci.id, vinField.getText().trim(), make, model, year, plateField.getText().trim());
            vehicleDAO.insert(v);
            UIHelper.showMessage(this, "Vehicle added.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Add failed: " + ex.getMessage());
        }
    }

    private void updateVehicle() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a vehicle to update.");
            return;
        }
        CustomerItem ci = (CustomerItem) customerCombo.getSelectedItem();
        if (ci == null || ci.id == 0) {
            UIHelper.showError(this, "Select a customer.");
            return;
        }
        String make = makeField.getText().trim();
        String model = modelField.getText().trim();
        String yearStr = yearField.getText().trim();
        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            UIHelper.showError(this, "Make, model, and year are required.");
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Year must be a number.");
            return;
        }
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            Vehicle v = vehicleDAO.findById(id);
            if (v == null) { UIHelper.showError(this, "Vehicle not found."); return; }
            v.setCustomerId(ci.id);
            v.setVin(vinField.getText().trim());
            v.setMake(make);
            v.setModel(model);
            v.setYear(year);
            v.setLicensePlate(plateField.getText().trim());
            vehicleDAO.update(v);
            UIHelper.showMessage(this, "Vehicle updated.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteVehicle() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a vehicle to delete.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete this vehicle?")) return;
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            vehicleDAO.delete(id);
            UIHelper.showMessage(this, "Vehicle deleted.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void clearForm() {
        customerCombo.setSelectedIndex(0);
        vinField.setText("");
        makeField.setText("");
        modelField.setText("");
        yearField.setText("");
        plateField.setText("");
        table.clearSelection();
    }

    private static class CustomerItem {
        final int id;
        private final String label;
        CustomerItem(int id, String label) { this.id = id; this.label = label; }
        @Override
        public String toString() { return label; }
    }
}
