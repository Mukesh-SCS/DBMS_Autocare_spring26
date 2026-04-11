package com.germantown.autocare.ui;

import com.germantown.autocare.dao.PartDAO;
import com.germantown.autocare.dao.ServiceDAO;
import com.germantown.autocare.model.Part;
import com.germantown.autocare.model.Service;
import com.germantown.autocare.util.UIHelper;
import com.germantown.autocare.util.UiTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Services (labor catalog) and parts (inventory) management.
 * Wired to {@link ServiceDAO} and {@link PartDAO}.
 */
public class ServicePartPanel extends JPanel {

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final PartDAO partDAO = new PartDAO();

    private JTable serviceTable;
    private DefaultTableModel serviceModel;
    private JTextField svcNameField;
    private JTextField svcDurationField;
    private JTextField svcPriceField;

    private JTable partTable;
    private DefaultTableModel partModel;
    private JTextField partNameField;
    private JTextField partDescField;
    private JTextField partPriceField;
    private JTextField partQtyField;
    private JTextField partReorderField;

    public ServicePartPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        UiTheme.paintPanelBackground(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Services", buildServicesTab());
        tabs.addTab("Parts", buildPartsTab());
        add(tabs, BorderLayout.CENTER);

        loadServices();
        loadParts();
    }

    private JPanel buildServicesTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        UiTheme.paintPanelBackground(root);

        serviceModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Duration (min)", "Base price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        serviceTable = new JTable(serviceModel);
        UiTheme.styleTable(serviceTable);
        serviceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serviceTable.getTableHeader().setReorderingAllowed(false);
        serviceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectServiceRow();
        });
        JScrollPane svcScroll = new JScrollPane(serviceTable);
        UiTheme.styleScrollPane(svcScroll);
        root.add(svcScroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(UiTheme.sectionBorder("Service details"));
        UiTheme.paintPanelBackground(form);

        form.add(new JLabel("Name:"));
        svcNameField = new JTextField();
        form.add(svcNameField);

        form.add(new JLabel("Duration (min):"));
        svcDurationField = new JTextField();
        form.add(svcDurationField);

        form.add(new JLabel("Base price:"));
        svcPriceField = new JTextField();
        form.add(svcPriceField);

        root.add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        UiTheme.paintPanelBackground(buttons);
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        for (JButton b : new JButton[]{add, update, delete, clear}) {
            UiTheme.styleToolbarButton(b);
        }
        add.addActionListener(e -> addService());
        update.addActionListener(e -> updateService());
        delete.addActionListener(e -> deleteService());
        clear.addActionListener(e -> clearServiceForm());
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private JPanel buildPartsTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        UiTheme.paintPanelBackground(root);

        partModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Description", "Unit price", "In stock", "Reorder"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        partTable = new JTable(partModel);
        UiTheme.styleTable(partTable);
        partTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        partTable.getTableHeader().setReorderingAllowed(false);
        partTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectPartRow();
        });
        JScrollPane partScroll = new JScrollPane(partTable);
        UiTheme.styleScrollPane(partScroll);
        root.add(partScroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(UiTheme.sectionBorder("Part details"));
        UiTheme.paintPanelBackground(form);

        form.add(new JLabel("Name:"));
        partNameField = new JTextField();
        form.add(partNameField);

        form.add(new JLabel("Unit price:"));
        partPriceField = new JTextField();
        form.add(partPriceField);

        form.add(new JLabel("Description:"));
        partDescField = new JTextField();
        form.add(partDescField);

        form.add(new JLabel("In stock:"));
        partQtyField = new JTextField();
        form.add(partQtyField);

        form.add(new JLabel("Reorder level:"));
        partReorderField = new JTextField();
        form.add(partReorderField);

        root.add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        UiTheme.paintPanelBackground(buttons);
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        for (JButton b : new JButton[]{add, update, delete, clear}) {
            UiTheme.styleToolbarButton(b);
        }
        add.addActionListener(e -> addPart());
        update.addActionListener(e -> updatePart());
        delete.addActionListener(e -> deletePart());
        clear.addActionListener(e -> clearPartForm());
        buttons.add(add);
        buttons.add(update);
        buttons.add(delete);
        buttons.add(clear);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void loadServices() {
        serviceModel.setRowCount(0);
        try {
            List<Service> list = serviceDAO.findAll();
            for (Service s : list) {
                serviceModel.addRow(new Object[]{
                        s.getServiceId(),
                        s.getServiceName(),
                        s.getEstimatedDurationMinutes() != null ? s.getEstimatedDurationMinutes() : "",
                        s.getBasePrice() != null ? s.getBasePrice().toPlainString() : ""
                });
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load services failed: " + ex.getMessage());
        }
    }

    private void selectServiceRow() {
        int row = serviceTable.getSelectedRow();
        if (row < 0) return;
        svcNameField.setText(String.valueOf(serviceModel.getValueAt(row, 1)));
        svcDurationField.setText(String.valueOf(serviceModel.getValueAt(row, 2)));
        svcPriceField.setText(String.valueOf(serviceModel.getValueAt(row, 3)));
    }

    private void addService() {
        String name = svcNameField.getText().trim();
        if (name.isEmpty()) {
            UIHelper.showError(this, "Name is required.");
            return;
        }
        Integer duration = null;
        String durStr = svcDurationField.getText().trim();
        if (!durStr.isEmpty()) {
            try {
                duration = Integer.parseInt(durStr);
            } catch (NumberFormatException e) {
                UIHelper.showError(this, "Duration must be an integer.");
                return;
            }
        }
        BigDecimal price;
        try {
            price = new BigDecimal(svcPriceField.getText().trim());
        } catch (Exception e) {
            UIHelper.showError(this, "Base price must be a number.");
            return;
        }
        try {
            Service s = new Service(name, duration, price);
            serviceDAO.insert(s);
            UIHelper.showMessage(this, "Service added.");
            clearServiceForm();
            loadServices();
        } catch (Exception ex) {
            UIHelper.showError(this, "Add failed: " + ex.getMessage());
        }
    }

    private void updateService() {
        int row = serviceTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a service to update.");
            return;
        }
        String name = svcNameField.getText().trim();
        if (name.isEmpty()) {
            UIHelper.showError(this, "Name is required.");
            return;
        }
        Integer duration = null;
        String durStr = svcDurationField.getText().trim();
        if (!durStr.isEmpty()) {
            try {
                duration = Integer.parseInt(durStr);
            } catch (NumberFormatException e) {
                UIHelper.showError(this, "Duration must be an integer.");
                return;
            }
        }
        BigDecimal price;
        try {
            price = new BigDecimal(svcPriceField.getText().trim());
        } catch (Exception e) {
            UIHelper.showError(this, "Base price must be a number.");
            return;
        }
        try {
            int id = (Integer) serviceModel.getValueAt(row, 0);
            Service s = serviceDAO.findById(id);
            if (s == null) {
                UIHelper.showError(this, "Service not found.");
                return;
            }
            s.setServiceName(name);
            s.setEstimatedDurationMinutes(duration);
            s.setBasePrice(price);
            serviceDAO.update(s);
            UIHelper.showMessage(this, "Service updated.");
            clearServiceForm();
            loadServices();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deleteService() {
        int row = serviceTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a service to delete.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete this service? Linked appointment rows may prevent deletion.")) return;
        try {
            int id = (Integer) serviceModel.getValueAt(row, 0);
            serviceDAO.delete(id);
            UIHelper.showMessage(this, "Service deleted.");
            clearServiceForm();
            loadServices();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void clearServiceForm() {
        svcNameField.setText("");
        svcDurationField.setText("");
        svcPriceField.setText("");
        serviceTable.clearSelection();
    }

    private void loadParts() {
        partModel.setRowCount(0);
        try {
            List<Part> list = partDAO.findAll();
            for (Part p : list) {
                partModel.addRow(new Object[]{
                        p.getPartId(),
                        p.getPartName(),
                        p.getDescription() != null ? p.getDescription() : "",
                        p.getUnitPrice(),
                        p.getQuantityInStock(),
                        p.getReorderLevel()
                });
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Load parts failed: " + ex.getMessage());
        }
    }

    private void selectPartRow() {
        int row = partTable.getSelectedRow();
        if (row < 0) return;
        partNameField.setText(String.valueOf(partModel.getValueAt(row, 1)));
        partDescField.setText(String.valueOf(partModel.getValueAt(row, 2)));
        partPriceField.setText(String.valueOf(partModel.getValueAt(row, 3)));
        partQtyField.setText(String.valueOf(partModel.getValueAt(row, 4)));
        partReorderField.setText(String.valueOf(partModel.getValueAt(row, 5)));
    }

    private void addPart() {
        String name = partNameField.getText().trim();
        String priceStr = partPriceField.getText().trim();
        String qtyStr = partQtyField.getText().trim();
        if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            UIHelper.showError(this, "Name, unit price, and quantity in stock are required.");
            return;
        }
        double price;
        int qty;
        int reorder = 0;
        try {
            price = Double.parseDouble(priceStr);
            qty = Integer.parseInt(qtyStr);
            String r = partReorderField.getText().trim();
            if (!r.isEmpty()) reorder = Integer.parseInt(r);
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Price and quantities must be numeric.");
            return;
        }
        try {
            Part p = new Part(name, partDescField.getText().trim(), price, qty, reorder);
            partDAO.insert(p);
            UIHelper.showMessage(this, "Part added.");
            clearPartForm();
            loadParts();
        } catch (Exception ex) {
            UIHelper.showError(this, "Add failed: " + ex.getMessage());
        }
    }

    private void updatePart() {
        int row = partTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a part to update.");
            return;
        }
        String name = partNameField.getText().trim();
        String priceStr = partPriceField.getText().trim();
        String qtyStr = partQtyField.getText().trim();
        if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            UIHelper.showError(this, "Name, unit price, and quantity in stock are required.");
            return;
        }
        double price;
        int qty;
        int reorder = 0;
        try {
            price = Double.parseDouble(priceStr);
            qty = Integer.parseInt(qtyStr);
            String r = partReorderField.getText().trim();
            if (!r.isEmpty()) reorder = Integer.parseInt(r);
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Price and quantities must be numeric.");
            return;
        }
        try {
            int id = (Integer) partModel.getValueAt(row, 0);
            Part p = partDAO.findById(id);
            if (p == null) {
                UIHelper.showError(this, "Part not found.");
                return;
            }
            p.setPartName(name);
            p.setDescription(partDescField.getText().trim());
            p.setUnitPrice(price);
            p.setQuantityInStock(qty);
            p.setReorderLevel(reorder);
            partDAO.update(p);
            UIHelper.showMessage(this, "Part updated.");
            clearPartForm();
            loadParts();
        } catch (Exception ex) {
            UIHelper.showError(this, "Update failed: " + ex.getMessage());
        }
    }

    private void deletePart() {
        int row = partTable.getSelectedRow();
        if (row < 0) {
            UIHelper.showError(this, "Select a part to delete.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete this part?")) return;
        try {
            int id = (Integer) partModel.getValueAt(row, 0);
            partDAO.delete(id);
            UIHelper.showMessage(this, "Part deleted.");
            clearPartForm();
            loadParts();
        } catch (Exception ex) {
            UIHelper.showError(this, "Delete failed: " + ex.getMessage());
        }
    }

    private void clearPartForm() {
        partNameField.setText("");
        partDescField.setText("");
        partPriceField.setText("");
        partQtyField.setText("");
        partReorderField.setText("");
        partTable.clearSelection();
    }
}
