package com.germantown.autocare.ui;

import com.germantown.autocare.dao.CustomerDAO;
import com.germantown.autocare.model.Customer;
import com.germantown.autocare.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Customer management: list, add, update, delete.
 */
public class CustomerPanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField firstField, lastField, emailField, phoneField, addressField;
    private final JButton addBtn, updateBtn, deleteBtn, clearBtn;

    private static final String[] COLUMNS = { "ID", "First Name", "Last Name", "Email", "Phone", "Address" };

    public CustomerPanel() {
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
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("First Name:"));
        firstField = new JTextField(20);
        form.add(firstField);
        form.add(new JLabel("Last Name:"));
        lastField = new JTextField(20);
        form.add(lastField);
        form.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        phoneField = new JTextField(20);
        form.add(phoneField);
        form.add(new JLabel("Address:"));
        addressField = new JTextField(20);
        form.add(addressField);
        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn = new JButton("Clear");
        addBtn.addActionListener(e -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        clearBtn.addActionListener(e -> clearForm());
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);
        add(buttons, BorderLayout.SOUTH);

        loadTable();
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Customer> list = customerDAO.findAll();
            for (Customer c : list) {
                tableModel.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getEmail() != null ? c.getEmail() : "",
                    c.getPhone(),
                    c.getAddress() != null ? c.getAddress() : ""
                });
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load failed: " + ex.getMessage());
        }
    }

    private void selectRowToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        firstField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        lastField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        emailField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        addressField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void addCustomer() {
        String first = firstField.getText().trim();
        String last = lastField.getText().trim();
        String phone = phoneField.getText().trim();
        if (first.isEmpty() || last.isEmpty() || phone.isEmpty()) {
            UIHelper.showError(this, "First name, last name, and phone are required.");
            return;
        }
        try {
            Customer c = new Customer(first, last, emailField.getText().trim(), phone, addressField.getText().trim());
            customerDAO.insert(c);
            UIHelper.showMessage(this, "Customer added.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Add failed: " + ex.getMessage());
        }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a customer to update.");
            return;
        }
        String first = firstField.getText().trim();
        String last = lastField.getText().trim();
        String phone = phoneField.getText().trim();
        if (first.isEmpty() || last.isEmpty() || phone.isEmpty()) {
            UIHelper.showError(this, "First name, last name, and phone are required.");
            return;
        }
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            Customer c = customerDAO.findById(id);
            if (c == null) { UIHelper.showError(this, "Customer not found."); return; }
            c.setFirstName(first);
            c.setLastName(last);
            c.setEmail(emailField.getText().trim());
            c.setPhone(phone);
            c.setAddress(addressField.getText().trim());
            customerDAO.update(c);
            UIHelper.showMessage(this, "Customer updated.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a customer to delete.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete this customer? Vehicles linked to this customer will also be removed.")) return;
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            customerDAO.delete(id);
            UIHelper.showMessage(this, "Customer deleted.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void clearForm() {
        firstField.setText("");
        lastField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        table.clearSelection();
    }
}
