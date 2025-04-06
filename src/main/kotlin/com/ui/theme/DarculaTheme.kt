package com.ui.theme

import java.awt.Color
import javax.swing.UIManager

/**
 * Dark theme implementation inspired by JetBrains Darcula theme.
 */
object DarculaTheme {
    val backgroundColor = Color(43, 43, 43)
    val foregroundColor = Color(187, 187, 187)
    val toolbarColor = Color(60, 63, 65)
    val editorBackgroundColor = Color(43, 43, 43)
    val selectionColor = Color(75, 110, 175)
    val borderColor = Color(53, 53, 53)
    val activeTabColor = Color(85, 85, 85)
    val caretColor = Color.WHITE

    /**
     * Apply the theme to the application.
     */
    fun apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        UIManager.put("Panel.background", backgroundColor)
        UIManager.put("Panel.foreground", foregroundColor)
        UIManager.put("Label.foreground", foregroundColor)
        UIManager.put("Button.background", toolbarColor)
        UIManager.put("Button.foreground", foregroundColor)
        UIManager.put("TextField.background", editorBackgroundColor)
        UIManager.put("TextField.foreground", foregroundColor)
        UIManager.put("TextArea.background", editorBackgroundColor)
        UIManager.put("TextArea.foreground", foregroundColor)
        UIManager.put("ScrollPane.background", backgroundColor)
        UIManager.put("TabbedPane.background", toolbarColor)
        UIManager.put("TabbedPane.foreground", foregroundColor)
        UIManager.put("TabbedPane.selected", activeTabColor)
        UIManager.put("ToolBar.background", toolbarColor)
        UIManager.put("ComboBox.background", toolbarColor)
        UIManager.put("ComboBox.foreground", foregroundColor)
        UIManager.put("MenuItem.background", toolbarColor)
        UIManager.put("MenuItem.foreground", foregroundColor)
        UIManager.put("Menu.background", toolbarColor)
        UIManager.put("Menu.foreground", foregroundColor)
        UIManager.put("MenuBar.background", toolbarColor)
        UIManager.put("MenuBar.foreground", foregroundColor)
        UIManager.put("CheckBox.background", backgroundColor)
        UIManager.put("CheckBox.foreground", foregroundColor)
    }
}