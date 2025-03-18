package com

import com.model.Graph
import com.parser.GraphParser
import com.renderer.DiagramRenderer
import com.renderer.MermaidGenerator
import com.renderer.MermaidRenderer
import com.renderer.SimpleGraphRenderer
import com.ui.DiagramPanel
import com.ui.InteractiveGraphPanel
import com.ui.TextLineNumber
import com.ui.VertexListPanel
import com.ui.components.JetBrainsToolbar
import com.ui.theme.DarculaTheme

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font

import java.io.File
import javax.swing.*
import javax.swing.border.MatteBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.plaf.basic.BasicSplitPaneDivider
import javax.swing.plaf.basic.BasicSplitPaneUI
import kotlinx.coroutines.*
import java.awt.image.BufferedImage


class GraphVisualizerApp : JFrame("Directed Graph Visualizer") {
    private val parser = GraphParser()
    private val mermaidGenerator = MermaidGenerator()
    private val mermaidRenderer = MermaidRenderer(mermaidGenerator)
    private val simpleRenderer = SimpleGraphRenderer()
    private lateinit var diagramTabs: JTabbedPane

    private val useExternalRenderer = false


    private val activeRenderer: DiagramRenderer
        get() = if (useExternalRenderer) mermaidRenderer else simpleRenderer


    private val inputTextArea = JTextArea().apply {
        text = "A -> B\nB -> C\nC -> D\nD -> A"
        font = Font(Font.MONOSPACED, Font.PLAIN, 13)
        background = DarculaTheme.editorBackgroundColor
        foreground = DarculaTheme.foregroundColor
        caretColor = DarculaTheme.caretColor
        selectionColor = DarculaTheme.selectionColor
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
    }

    private val vertexListPanel = VertexListPanel().apply {
        setupDarculaColors(
            DarculaTheme.backgroundColor,
            DarculaTheme.foregroundColor,
            DarculaTheme.borderColor
        )
    }

    private val diagramPanel = DiagramPanel().apply {
        background = DarculaTheme.backgroundColor
        border = BorderFactory.createLineBorder(DarculaTheme.borderColor)
    }

    private val interactiveGraphPanel = InteractiveGraphPanel().apply {
        background = DarculaTheme.backgroundColor
        border = BorderFactory.createLineBorder(DarculaTheme.borderColor)
    }

    private val loadingLabel = JLabel("Loading diagram...").apply {
        isVisible = false
        foreground = DarculaTheme.foregroundColor
    }

    private var coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var graph = Graph(emptyList(), emptyList())

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(1200, 800)
        setLocationRelativeTo(null)

        val toolbar = JetBrainsToolbar()

        val newButton = toolbar.addButton("New", "Creates a new graph") {
            createNewGraph()
        }

        val openButton = toolbar.addButton("Open", "Open a graph from file") {
            openGraph()
        }

        val saveButton = toolbar.addButton("Save", "Save current graph to file") {
            saveGraph()
        }

        val exportButton = toolbar.addButton("Export", "Export diagram as image") {
            exportDiagram()
        }

        toolbar.addSeparator()

        val refreshButton = toolbar.addButton("Refresh", "Refresh the diagram") {
            updateGraph()
        }

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT).apply {
            border = BorderFactory.createEmptyBorder()
            dividerSize = 4
            isContinuousLayout = true
            setUI(object : BasicSplitPaneUI() {
                override fun createDefaultDivider(): BasicSplitPaneDivider {
                    return object : BasicSplitPaneDivider(this) {
                        override fun setBorder(b: javax.swing.border.Border?) {
                            // No border
                        }

                        override fun paint(g: java.awt.Graphics) {
                            g.color = DarculaTheme.borderColor
                            g.fillRect(0, 0, width, height)
                        }
                    }
                }
            })
        }

        val leftPanel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            border = BorderFactory.createEmptyBorder()
        }

        val editorTabs = JTabbedPane().apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
            border = BorderFactory.createEmptyBorder()

            addTab("Graph Definition", null, createEditorPanel(), "Edit graph structure")
            setBackgroundAt(0, DarculaTheme.activeTabColor)
        }

        val rightPanel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            border = BorderFactory.createEmptyBorder()
        }

        splitPane.leftComponent = leftPanel
        splitPane.rightComponent = rightPanel
        splitPane.setDividerLocation(400)

        leftPanel.add(editorTabs, BorderLayout.CENTER)

        val statusBar = JPanel(BorderLayout()).apply {
            background = DarculaTheme.toolbarColor
            border = MatteBorder(1, 0, 0, 0, DarculaTheme.borderColor)
            preferredSize = Dimension(width, 25)
            add(JLabel("Ready").apply {
                foreground = DarculaTheme.foregroundColor
                border = BorderFactory.createEmptyBorder(0, 10, 0, 0)
                font = Font("Segoe UI", Font.PLAIN, 12)
            }, BorderLayout.WEST)
            add(loadingLabel.apply {
                border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
            }, BorderLayout.EAST)
        }

        val rightTabs = JTabbedPane().apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
            border = BorderFactory.createEmptyBorder()

            addTab("Diagram", null, createDiagramPanel(), "View graph diagram")
            setBackgroundAt(0, DarculaTheme.activeTabColor)
        }

        rightPanel.add(rightTabs, BorderLayout.CENTER)

        val contentPanel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            add(toolbar, BorderLayout.NORTH)
            add(splitPane, BorderLayout.CENTER)
            add(statusBar, BorderLayout.SOUTH)
        }

        contentPane = contentPanel

        vertexListPanel.setOnVertexToggleListener { vertices ->
            val updatedGraph = graph.withUpdatedVertices(vertices)
            graph = updatedGraph
            updateDiagram(updatedGraph)
        }

        updateGraph()
    }

    /**
     * Creates the editor panel.
     *
     * @return The editor panel
     */
    private fun createEditorPanel(): JPanel {
        val panel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        val lineNumbers = TextLineNumber(inputTextArea).apply {
            background = DarculaTheme.editorBackgroundColor
            foreground = java.awt.Color(100, 100, 100)
            font = Font(Font.MONOSPACED, Font.PLAIN, 13)
            border = MatteBorder(0, 0, 0, 1, DarculaTheme.borderColor)
        }

        val editorScrollPane = JScrollPane(inputTextArea).apply {
            border = BorderFactory.createLineBorder(DarculaTheme.borderColor)
            setRowHeaderView(lineNumbers)
        }

        inputTextArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = updateGraph()
            override fun removeUpdate(e: DocumentEvent) = updateGraph()
            override fun changedUpdate(e: DocumentEvent) = updateGraph()
        })

        val structurePanel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DarculaTheme.borderColor),
                "Graph Structure",
                0,
                0,
                Font("Segoe UI", Font.BOLD, 12),
                DarculaTheme.foregroundColor
            )
            add(vertexListPanel, BorderLayout.CENTER)
        }

        panel.add(editorScrollPane, BorderLayout.CENTER)
        panel.add(structurePanel, BorderLayout.SOUTH)

        return panel
    }

    /**
     * Creates the diagram panel.
     *
     * @return The diagram panel
     */
    private fun createDiagramPanel(): JPanel {
        val panel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        diagramTabs = JTabbedPane().apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
            border = BorderFactory.createEmptyBorder()

            addTab("Interactive", null, interactiveGraphPanel, "Interactive graph view with draggable nodes")
            addTab("Static", null, diagramPanel, "Static graph image")
            setBackgroundAt(0, DarculaTheme.activeTabColor)

            addChangeListener { e ->
                val source = e.source as JTabbedPane
                if (source.selectedIndex == 0) {
                    // Interactive view selected
                    interactiveGraphPanel.setGraph(graph)
                }
            }
        }

        panel.add(diagramTabs, BorderLayout.CENTER)
        return panel
    }


    /**
     * Creates a new graph.
     */
    private fun createNewGraph() {
        val confirm = JOptionPane.showConfirmDialog(
            this,
            "Create a new graph? Any unsaved changes will be lost.",
            "New Graph",
            JOptionPane.YES_NO_OPTION
        )

        if (confirm == JOptionPane.YES_OPTION) {
            inputTextArea.text = "A -> B\nB -> C\nC -> D\nD -> A"
            updateGraph()
        }
    }

    /**
     * Opens a graph from a file.
     */
    private fun openGraph() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Text Files", "txt")

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            try {
                val text = file.readText()
                inputTextArea.text = text
                updateGraph()
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error opening file: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    /**
     * Saves the current graph to a file.
     */
    private fun saveGraph() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("Text Files", "txt")

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.selectedFile

            if (!file.name.lowercase().endsWith(".txt")) {
                file = File(file.absolutePath + ".txt")
            }

            try {
                file.writeText(inputTextArea.text)
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error saving file: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    /**
     * Exports the current diagram as an image.
     */
    private fun exportDiagram() {
        val fileChooser = JFileChooser()
        fileChooser.fileFilter = FileNameExtensionFilter("PNG Images", "png")

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.selectedFile

            if (!file.name.lowercase().endsWith(".png")) {
                file = File(file.absolutePath + ".png")
            }

            try {
                val diagramTabs = SwingUtilities.getAncestorOfClass(JTabbedPane::class.java, interactiveGraphPanel)

                if (diagramTabs is JTabbedPane) {
                    val selectedIndex = diagramTabs.selectedIndex

                    if (selectedIndex == 0) {
                        val width = interactiveGraphPanel.width
                        val height = interactiveGraphPanel.height
                        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                        val g = image.createGraphics()
                        interactiveGraphPanel.paint(g)
                        g.dispose()

                        javax.imageio.ImageIO.write(image, "png", file)

                        JOptionPane.showMessageDialog(
                            this,
                            "Interactive diagram exported successfully.",
                            "Export Complete",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    } else {
                        val diagram = diagramPanel.diagram
                        if (diagram != null) {
                            javax.imageio.ImageIO.write(diagram, "png", file)

                            JOptionPane.showMessageDialog(
                                this,
                                "Static diagram exported successfully.",
                                "Export Complete",
                                JOptionPane.INFORMATION_MESSAGE
                            )
                        } else {
                            JOptionPane.showMessageDialog(
                                this,
                                "No diagram to export",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                            )
                        }
                    }
                } else {
                    val diagram = diagramPanel.diagram
                    if (diagram != null) {
                        javax.imageio.ImageIO.write(diagram, "png", file)
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "No diagram to export",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        )
                    }
                }
            } catch (e: Exception) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error exporting diagram: ${e.message}",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    private fun updateGraph() {
        try {
            graph = parser.parse(inputTextArea.text)

            vertexListPanel.updateVertices(graph.vertices)

            updateDiagram(graph)
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(
                this,
                "Error parsing graph: ${e.message}",
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun updateDiagram(graph: Graph) {
        loadingLabel.isVisible = true
        diagramPanel.updateDiagram(null)

        interactiveGraphPanel.setGraph(graph)
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

        coroutineScope.launch {
            val diagram = activeRenderer.renderToImage(graph)
            withContext(Dispatchers.Main) {
                loadingLabel.isVisible = false
                diagramPanel.updateDiagram(diagram)
            }
        }
    }
}