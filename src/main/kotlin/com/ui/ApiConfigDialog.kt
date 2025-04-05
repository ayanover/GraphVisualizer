package com.ui.dialog

import com.ui.theme.DarculaTheme
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.*
import javax.swing.border.EmptyBorder

class ApiConfigDialog(parent: JFrame) : JDialog(parent, "API Configuration", true) {

    private val urlField = JTextField("http://localhost:8080").apply {
        font = Font(Font.MONOSPACED, Font.PLAIN, 13)
        preferredSize = Dimension(300, 28)
        background = DarculaTheme.editorBackgroundColor
        foreground = DarculaTheme.foregroundColor
        caretColor = DarculaTheme.caretColor
        selectionColor = DarculaTheme.selectionColor
    }

    private val endpointField = JTextField("/api/analyze").apply {
        font = Font(Font.MONOSPACED, Font.PLAIN, 13)
        preferredSize = Dimension(300, 28)
        background = DarculaTheme.editorBackgroundColor
        foreground = DarculaTheme.foregroundColor
        caretColor = DarculaTheme.caretColor
        selectionColor = DarculaTheme.selectionColor
    }

    private var apiUrl = "http://localhost:8080"
    private var apiEndpoint = "/api/analyze"
    private var confirmed = false

    init {
        setSize(400, 200)
        setLocationRelativeTo(parent)
        layout = BorderLayout()

        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = DarculaTheme.backgroundColor
            border = EmptyBorder(10, 10, 10, 10)
        }

        val urlPanel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            add(JLabel("API Base URL:").apply {
                foreground = DarculaTheme.foregroundColor
            }, BorderLayout.NORTH)
            add(urlField, BorderLayout.CENTER)
        }

        val endpointPanel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            add(JLabel("API Endpoint:").apply {
                foreground = DarculaTheme.foregroundColor
            }, BorderLayout.NORTH)
            add(endpointField, BorderLayout.CENTER)
        }

        panel.add(urlPanel)
        panel.add(Box.createVerticalStrut(10))
        panel.add(endpointPanel)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
            background = DarculaTheme.backgroundColor
        }

        val okButton = JButton("OK").apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
        }

        okButton.addActionListener {
            apiUrl = urlField.text
            apiEndpoint = endpointField.text
            confirmed = true
            dispose()
        }

        val cancelButton = JButton("Cancel").apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
        }

        cancelButton.addActionListener {
            dispose()
        }

        buttonPanel.add(okButton)
        buttonPanel.add(cancelButton)

        add(panel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)

        rootPane.defaultButton = okButton
        rootPane.registerKeyboardAction(
            { dispose() },
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        )
    }


    fun showDialog(): Pair<String, String>? {
        isVisible = true
        return if (confirmed) Pair(apiUrl, apiEndpoint) else null
    }
}