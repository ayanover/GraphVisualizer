package com.ui

import com.Vertex
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.border.EmptyBorder


class VertexListPanel : JPanel() {
    private val vertexBoxes = mutableMapOf<String, JCheckBox>()
    private val verticesPanel = JPanel()
    private var onVertexToggleListener: ((List<Vertex>) -> Unit)? = null

    private var backgroundColor = Color.WHITE
    private var foregroundColor = Color.BLACK
    private var borderColor = Color.LIGHT_GRAY

    init {
        layout = BorderLayout()
        verticesPanel.layout = BoxLayout(verticesPanel, BoxLayout.Y_AXIS)

        val scrollPane = JScrollPane(verticesPanel)
        scrollPane.border = EmptyBorder(5, 5, 5, 5)

        add(JLabel("Vertices").apply {
            border = EmptyBorder(0, 0, 5, 0)
        }, BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
    }

    fun setupDarculaColors(bgColor: Color, fgColor: Color, brdColor: Color) {
        backgroundColor = bgColor
        foregroundColor = fgColor
        borderColor = brdColor

        background = backgroundColor
        foreground = foregroundColor

        verticesPanel.background = backgroundColor
        verticesPanel.foreground = foregroundColor
    }

    fun updateVertices(vertices: List<Vertex>) {
        val currentStates = vertexBoxes.mapValues { it.value.isSelected }

        verticesPanel.removeAll()
        vertexBoxes.clear()

        for (vertex in vertices.sortedBy { it.name }) {
            val checkbox = JCheckBox(vertex.name).apply {
                isSelected = currentStates[vertex.name] ?: vertex.enabled
                background = backgroundColor
                foreground = foregroundColor
                font = Font("Segoe UI", Font.PLAIN, 12)

                addActionListener {
                    val updatedVertices = vertexBoxes.map { (name, box) ->
                        Vertex(name, box.isSelected)
                    }
                    onVertexToggleListener?.invoke(updatedVertices)
                }
            }

            vertexBoxes[vertex.name] = checkbox

            val panel = JPanel(BorderLayout()).apply {
                background = backgroundColor
                add(checkbox, BorderLayout.WEST)
                border = EmptyBorder(2, 2, 2, 2)
            }

            verticesPanel.add(panel)
            verticesPanel.add(JSeparator().apply {
                background = backgroundColor
                foreground = borderColor
            })
        }

        verticesPanel.revalidate()
        verticesPanel.repaint()
    }


    fun setOnVertexToggleListener(listener: (List<Vertex>) -> Unit) {
        onVertexToggleListener = listener
    }

    fun getVertices(): List<Vertex> {
        return vertexBoxes.map { (name, box) ->
            Vertex(name, box.isSelected)
        }
    }
}