package com.ui

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.JPanel


class DiagramPanel : JPanel() {
    var diagram: BufferedImage? = null
    var backgroundColor = Color(43, 43, 43)

    init {
        background = backgroundColor
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g2d.color = backgroundColor
        g2d.fillRect(0, 0, width, height)

        diagram?.let {
            val panelWidth = width
            val panelHeight = height
            val imgWidth = it.width
            val imgHeight = it.height

            val scale = minOf(
                panelWidth.toDouble() / imgWidth,
                panelHeight.toDouble() / imgHeight
            )

            val scaledWidth = (imgWidth * scale).toInt()
            val scaledHeight = (imgHeight * scale).toInt()

            val x = (panelWidth - scaledWidth) / 2
            val y = (panelHeight - scaledHeight) / 2

            g2d.drawImage(it, x, y, scaledWidth, scaledHeight, null)
        } ?: run {
            g2d.color = Color(120, 120, 120)
            g2d.font = Font("Segoe UI", Font.PLAIN, 16)
            val message = "No diagram to display"
            val fm = g2d.fontMetrics
            val x = (width - fm.stringWidth(message)) / 2
            val y = (height + fm.height) / 2
            g2d.drawString(message, x, y)
        }
    }

    fun updateDiagram(newDiagram: BufferedImage?) {
        diagram = newDiagram
        repaint()
    }
}