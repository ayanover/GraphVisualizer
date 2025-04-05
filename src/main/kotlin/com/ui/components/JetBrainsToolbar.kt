package com.ui.components

import com.ui.theme.DarculaTheme
import java.awt.Color
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JToolBar
import javax.swing.border.MatteBorder

class JetBrainsToolbar : JToolBar() {
    init {
        isFloatable = false
        background = DarculaTheme.toolbarColor
        border = MatteBorder(0, 0, 1, 0, DarculaTheme.borderColor)
    }


    fun createToolbarButton(text: String, tooltip: String): JButton {
        return JButton(text).apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
            isBorderPainted = false
            isFocusPainted = false
            toolTipText = tooltip
            font = java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12)
            margin = Insets(4, 8, 4, 8)
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent) {
                    background = Color(80, 80, 80)
                }

                override fun mouseExited(e: MouseEvent) {
                    background = DarculaTheme.toolbarColor
                }
            })
        }
    }

    fun addButton(text: String, tooltip: String, action: () -> Unit): JButton {
        val button = createToolbarButton(text, tooltip)
        button.addActionListener { action() }
        add(button)
        return button
    }
}