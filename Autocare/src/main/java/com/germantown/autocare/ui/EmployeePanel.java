package com.germantown.autocare.ui;

import com.germantown.autocare.dao.EmployeeDAO;
import com.germantown.autocare.model.Employee;
import com.germantown.autocare.util.UIHelper;
import com.germantown.autocare.util.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Employee management: list, add, update, delete.
 */
public class EmployeePanel extends JPanel {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField firstField, lastField, jobTitleField, emailField, phoneField;
    private final JTextField hireDateField, hourlyRateField;
    private final JButton addBtn, updateBtn, deleteBtn, clearBtn;

    private static final String[] COLUMNS = {
            "ID", "First Name", "Last Name", "Job Title", "Email", "Phone", "Hire Date", "Hourly Rate"
    };
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public EmployeePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        UiTheme.paintPanelBackground(this);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        UiTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectRowToForm();
        });
        JScrollPane scroll = new JScrollPane(table);
        UiTheme.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(UiTheme.sectionBorder("Employee details"));
        UiTheme.paintPanelBackground(form);
        form.add(new JLabel("First Name:"));
        firstField = new JTextField(20);
        form.add(firstField);
        form.add(new JLabel("Last Name:"));
        lastField = new JTextField(20);
        form.add(lastField);
        form.add(new JLabel("Job Title:"));
        jobTitleField = new JTextField(20);
        form.add(jobTitleField);
        form.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        phoneField = new JTextField(20);
        form.add(phoneField);
        form.add(new JLabel("Hire Date (YYYY-MM-DD):"));
        hireDateField = new JTextField(20);
        form.add(hireDateField);
        form.add(new JLabel("Hourly Rate:"));
        hourlyRateField = new JTextField(20);
        form.add(hourlyRateField);
        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        UiTheme.paintPanelBackground(buttons);
        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn = new JButton("Clear");
        for (JButton b : new JButton[]{addBtn, updateBtn, deleteBtn, clearBtn}) {
            UiTheme.styleToolbarButton(b);
        }
        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
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
            List<Employee> list = employeeDAO.findAll();
            for (Employee emp : list) {
                String hireStr = emp.getHireDate() != null ? emp.getHireDate().format(DATE_FMT) : "";
                String rateStr = emp.getHourlyRate() != null ? emp.getHourlyRate().toPlainString() : "";
                tableModel.addRow(new Object[]{
                        emp.getEmployeeId(),
                        emp.getFirstName(),
                        emp.getLastName(),
                        emp.getJobTitle(),
                        emp.getEmail() != null ? emp.getEmail() : "",
                        emp.getPhone() != null ? emp.getPhone() : "",
                        hireStr,
                        rateStr
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
        jobTitleField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        emailField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        hireDateField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        hourlyRateField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private LocalDate parseHireDate() throws DateTimeParseException {
        String s = hireDateField.getText().trim();
        return LocalDate.parse(s, DATE_FMT);
    }

    private BigDecimal parseHourlyRate() {
        String s = hourlyRateField.getText().trim();
        if (s.isEmpty()) {
            return null;
        }
        return new BigDecimal(s);
    }

    private void addEmployee() {
        String first = firstField.getText().trim();
        String last = lastField.getText().trim();
        String job = jobTitleField.getText().trim();
        if (first.isEmpty() || last.isEmpty() || job.isEmpty()) {
            UIHelper.showError(this, "First name, last name, and job title are required.");
            return;
        }
        LocalDate hireDate;
        try {
            hireDate = parseHireDate();
        } catch (DateTimeParseException ex) {
            UIHelper.showError(this, "Hire date must be YYYY-MM-DD.");
            return;
        }
        BigDecimal rate;
        try {
            rate = parseHourlyRate();
        } catch (NumberFormatException ex) {
            UIHelper.showError(this, "Hourly rate must be a number (or leave blank).");
            return;
        }
        try {
            Employee e = new Employee(first, last, job,
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    hireDate,
                    rate);
            employeeDAO.insert(e);
            UIHelper.showMessage(this, "Employee added.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Add failed: " + ex.getMessage());
        }
    }

    private void updateEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select an employee to update.");
            return;
        }
        String first = firstField.getText().trim();
        String last = lastField.getText().trim();
        String job = jobTitleField.getText().trim();
        if (first.isEmpty() || last.isEmpty() || job.isEmpty()) {
            UIHelper.showError(this, "First name, last name, and job title are required.");
            return;
        }
        LocalDate hireDate;
        try {
            hireDate = parseHireDate();
        } catch (DateTimeParseException ex) {
            UIHelper.showError(this, "Hire date must be YYYY-MM-DD.");
            return;
        }
        BigDecimal rate;
        try {
            rate = parseHourlyRate();
        } catch (NumberFormatException ex) {
            UIHelper.showError(this, "Hourly rate must be a number (or leave blank).");
            return;
        }
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            Employee e = employeeDAO.findById(id);
            if (e == null) {
                UIHelper.showError(this, "Employee not found.");
                return;
            }
            e.setFirstName(first);
            e.setLastName(last);
            e.setJobTitle(job);
            e.setEmail(emailField.getText().trim());
            e.setPhone(phoneField.getText().trim());
            e.setHireDate(hireDate);
            e.setHourlyRate(rate);
            employeeDAO.update(e);
            UIHelper.showMessage(this, "Employee updated.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select an employee to delete.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete this employee?")) return;
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            employeeDAO.delete(id);
            UIHelper.showMessage(this, "Employee deleted.");
            clearForm();
            loadTable();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void clearForm() {
        firstField.setText("");
        lastField.setText("");
        jobTitleField.setText("");
        emailField.setText("");
        phoneField.setText("");
        hireDateField.setText("");
        hourlyRateField.setText("");
        table.clearSelection();
    }
}
