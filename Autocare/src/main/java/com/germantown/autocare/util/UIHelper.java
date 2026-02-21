package com.germantown.autocare.util;

import javax.swing.*;

/**
 * Common UI helpers (dialogs, etc.).
 */
public final class UIHelper {

    private UIHelper() {}

    public static void showMessage(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "GAMS", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "GAMS - Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(java.awt.Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "GAMS", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
