package com.germantown.autocare.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Consistent colors, fonts, and component styling for the Swing UI.
 * Safe to call once after {@link UIManager#setLookAndFeel}.
 */
public final class UiTheme {

    public static final Color PANEL_BG = new Color(245, 247, 250);
    public static final Color TABLE_HEADER_BG = new Color(241, 245, 249);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color ACCENT = new Color(37, 99, 235);
    public static final Color SELECTION_BG = new Color(219, 234, 254);
    public static final Color SELECTION_FG = new Color(30, 41, 59);

    private UiTheme() {}

    /**
     * Applies theme defaults (works best with system or cross-platform LAF).
     */
    public static void applyGlobal() {
        Font base = UIManager.getFont("Label.font");
        if (base == null) {
            base = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        }
        Font uiFont = base.deriveFont(Font.PLAIN, 13f);
        Font headerFont = base.deriveFont(Font.BOLD, 13f);

        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("Table.font", uiFont);
        UIManager.put("TextField.font", uiFont);
        UIManager.put("ComboBox.font", uiFont);
        UIManager.put("TabbedPane.font", headerFont);
        UIManager.put("TitledBorder.font", headerFont);

        UIManager.put("Panel.background", PANEL_BG);
        UIManager.put("TabbedPane.background", PANEL_BG);
        UIManager.put("TabbedPane.contentAreaColor", Color.WHITE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.selectionBackground", SELECTION_BG);
        UIManager.put("Table.selectionForeground", SELECTION_FG);
        UIManager.put("TableHeader.background", TABLE_HEADER_BG);
        UIManager.put("TableHeader.font", headerFont);
        UIManager.put("ScrollPane.background", PANEL_BG);
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(SELECTION_BG);
        table.setSelectionForeground(SELECTION_FG);
    }

    public static void styleScrollPane(JScrollPane scroll) {
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
    }

    public static void paintPanelBackground(JComponent c) {
        c.setOpaque(true);
        c.setBackground(PANEL_BG);
    }

    /** Section with a light outline and title (e.g. form areas). */
    public static Border sectionBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), title),
                BorderFactory.createEmptyBorder(8, 10, 10, 10));
    }

    /** Primary action button (accent border + padding). */
    public static void stylePrimaryButton(JButton b) {
        b.setForeground(ACCENT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1, true),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Toolbar-style buttons with consistent padding. */
    public static void styleToolbarButton(JButton b) {
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
