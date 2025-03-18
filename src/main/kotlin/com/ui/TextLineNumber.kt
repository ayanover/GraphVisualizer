package com.ui

import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Point2D
import javax.swing.JPanel
import javax.swing.border.EmptyBorder
import javax.swing.text.JTextComponent
import java.awt.Point

/**
 * Component that displays line numbers for a text component.
 *
 * @property textComponent The text component to display line numbers for
 */
class TextLineNumber(private val textComponent: JTextComponent) : JPanel() {
    private val MARGIN = 5
    private var lastDigits = 0
    private var lastHeight = 0

    init {
        font = Font("JetBrains Mono", Font.PLAIN, 13)
        border = EmptyBorder(0, MARGIN, 0, MARGIN)
        preferredSize = Dimension(30, 100)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g2d.color = background
        g2d.fillRect(0, 0, width, height)

        val fontMetrics = g.fontMetrics
        val lineHeight = fontMetrics.height

        val root = textComponent.document.defaultRootElement
        val lineCount = root.elementCount

        val digits = maxOf(3, Math.log10(lineCount.toDouble()).toInt() + 1)
        if (lastDigits != digits || lastHeight != textComponent.height) {
            lastDigits = digits
            lastHeight = textComponent.height
            val width = fontMetrics.charWidth('0') * digits + 2 * MARGIN
            preferredSize = Dimension(width, textComponent.height)
            revalidate()
        }

        var fontAscent = fontMetrics.ascent
        val rectangle = textComponent.visibleRect

        val firstIndex = textComponent.viewToModel2D(rectangle.location.toPoint2D())
        val firstLine = root.getElementIndex(firstIndex)

        val lastPos = Point2D.Double(rectangle.x.toDouble(), (rectangle.y + rectangle.height - 1).toDouble())
        val lastIndex = textComponent.viewToModel2D(lastPos)
        var lastLine = root.getElementIndex(lastIndex)

        val lastElem = root.getElement(lastLine)
        if (lastElem.startOffset + lastElem.endOffset <= lastIndex) {
            lastLine++
        }

        g2d.color = foreground
        for (line in firstLine..lastLine) {
            if (line < lineCount) {
                val lineElem = root.getElement(line)
                val lineStart = lineElem.startOffset

                val viewRect = textComponent.modelToView2D(lineStart)
                val yPos = viewRect.y.toInt() + fontAscent

                val lineStr = (line + 1).toString()
                val x = width - fontMetrics.stringWidth(lineStr) - MARGIN

                g2d.drawString(lineStr, x, yPos)
            }
        }
    }

    private fun Point.toPoint2D(): Point2D = Point2D.Double(x.toDouble(), y.toDouble())
}