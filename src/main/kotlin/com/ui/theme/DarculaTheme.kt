package com.ui.theme

import java.awt.Color
import javax.swing.UIManager

object DarculaTheme {
    val backgroundColor = Color(43, 43, 43) // Dark background
    val foregroundColor = Color(187, 187, 187) // Light text
    val borderColor = Color(53, 53, 53) // Darker border
    val selectionColor = Color(75, 110, 175) // Blue selection
    val caretColor = Color(187, 187, 187) // Light cursor
    val editorBackgroundColor = Color(43, 43, 43) // Editor background
    val toolbarColor = Color(60, 63, 65) // Toolbar background
    val activeTabColor = Color(75, 75, 75) // Active tab

    fun apply() {
        try {
            for (info in UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus" == info.name) {
                    UIManager.setLookAndFeel(info.className)
                    break
                }
            }

            UIManager.put("control", Color(60, 63, 65))
            UIManager.put("info", Color(60, 63, 65))
            UIManager.put("nimbusBase", Color(60, 63, 65))
            UIManager.put("nimbusBlueGrey", Color(60, 63, 65))
            UIManager.put("nimbusDisabledText", Color(128, 128, 128))
            UIManager.put("nimbusFocus", Color(115, 164, 209))
            UIManager.put("nimbusLightBackground", Color(43, 43, 43))
            UIManager.put("nimbusOrange", Color(191, 98, 4))
            UIManager.put("nimbusSelectedText", Color(255, 255, 255))
            UIManager.put("nimbusSelectionBackground", Color(75, 110, 175))
            UIManager.put("text", Color(187, 187, 187))

            UIManager.put("ScrollBar.thumb", Color(85, 85, 85))
            UIManager.put("ScrollBar.thumbDarkShadow", Color(85, 85, 85))
            UIManager.put("ScrollBar.thumbHighlight", Color(85, 85, 85))
            UIManager.put("ScrollBar.thumbShadow", Color(85, 85, 85))
            UIManager.put("ScrollBar.track", Color(43, 43, 43))

            UIManager.put("Button.background", Color(60, 63, 65))
            UIManager.put("Button.foreground", Color(187, 187, 187))
            UIManager.put("Button.select", Color(75, 110, 175))

            UIManager.put("CheckBox.background", Color(43, 43, 43))
            UIManager.put("CheckBox.foreground", Color(187, 187, 187))

            UIManager.put("Panel.background", backgroundColor)
            UIManager.put("ToolBar.background", toolbarColor)

            UIManager.put("MenuItem.selectionBackground", selectionColor)
            UIManager.put("MenuItem.selectionForeground", Color.WHITE)
            UIManager.put("Menu.selectionBackground", selectionColor)
            UIManager.put("Menu.selectionForeground", Color.WHITE)
            UIManager.put("Menu.borderPainted", false)

        } catch (e: Exception) {
            e.printStackTrace()
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        System.setProperty("awt.useSystemAAFontSettings", "on")
        System.setProperty("swing.aatext", "true")
    }
}