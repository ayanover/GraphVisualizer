package com
import com.ui.theme.DarculaTheme
import javax.swing.SwingUtilities

fun main() {
    DarculaTheme.apply()

    SwingUtilities.invokeLater {
        GraphVisualizerApp().isVisible = true
    }
}