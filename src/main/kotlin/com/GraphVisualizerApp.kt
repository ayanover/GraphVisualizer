package com

import com.di.AppComponent
import com.model.Graph
import com.parser.GraphParser
import com.parser.IGraphParser
import com.renderer.IGraphRenderer
import com.renderer.MermaidGenerator
import com.renderer.SimpleGraphRenderer
import com.service.FileService
import com.service.IDialogService
import com.service.IFileService
import com.service.SwingDialogService
import com.ui.*
import com.ui.components.Toolbar
import com.ui.theme.DarculaTheme
import kotlinx.coroutines.*
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.inject.Inject
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.MatteBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicSplitPaneDivider
import javax.swing.plaf.basic.BasicSplitPaneUI

class GraphVisualizerApp : JFrame("Directed Graph Visualizer") {
    @Inject lateinit var parser: IGraphParser
    @Inject lateinit var renderer: IGraphRenderer
    @Inject lateinit var fileService: IFileService
    @Inject lateinit var dialogService: IDialogService
    @Inject lateinit var vertexListPanel: IVertexListPanel
    @Inject lateinit var diagramPanel: IDiagramPanel
    @Inject lateinit var interactiveGraphPanel: IInteractiveGraphPanel

    private val inputTextArea = JTextArea().apply {
        text = "A -> B\nB -> C\nC -> D\nD -> A"
        font = Font(Font.MONOSPACED, Font.PLAIN, 13)
        background = DarculaTheme.editorBackgroundColor
        foreground = DarculaTheme.foregroundColor
        caretColor = DarculaTheme.caretColor
        selectionColor = DarculaTheme.selectionColor
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
    }

    private val loadingLabel = JLabel("Loading diagram...").apply {
        isVisible = false
        foreground = DarculaTheme.foregroundColor
    }

    private var coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var graph = Graph(emptyList(), emptyList())
    private lateinit var diagramTabs: JTabbedPane
    private fun initDependencies() {
        // Manually initialize all dependencies
        parser = GraphParser()
        fileService = FileService()
        dialogService = SwingDialogService(this)
        val mermaidGenerator = MermaidGenerator()
        renderer = SimpleGraphRenderer()
        vertexListPanel = VertexListPanel()
        diagramPanel = DiagramPanel()
        interactiveGraphPanel = InteractiveGraphPanel()
    }
    init {
        // Configure frame
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        size = Dimension(1200, 800)
        setLocationRelativeTo(null)

        initDependencies()
        // Setup UI and event handlers
        setupUI()
        setupEventHandlers()
        updateGraph()
    }

    private fun setupUI() {
        val toolbar = Toolbar()

        toolbar.addButton("New", "Creates a new graph") {
            createNewGraph()
        }

        toolbar.addButton("Open", "Open a graph from file") {
            openGraph()
        }

        toolbar.addButton("Save", "Save current graph to file") {
            saveGraph()
        }

        toolbar.addButton("Export", "Export diagram as image") {
            exportDiagram()
        }

        toolbar.addSeparator()

        toolbar.addButton("Refresh", "Refresh the diagram") {
            updateGraph()
        }

        val splitPane = createSplitPane()
        val statusBar = createStatusBar()

        contentPane = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            add(toolbar, BorderLayout.NORTH)
            add(splitPane, BorderLayout.CENTER)
            add(statusBar, BorderLayout.SOUTH)
        }
    }

    private fun createSplitPane(): JSplitPane {
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

        val rightTabs = JTabbedPane().apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
            border = BorderFactory.createEmptyBorder()

            addTab("Diagram", null, createDiagramPanel(), "View graph diagram")
            setBackgroundAt(0, DarculaTheme.activeTabColor)
        }

        leftPanel.add(editorTabs, BorderLayout.CENTER)
        rightPanel.add(rightTabs, BorderLayout.CENTER)

        splitPane.leftComponent = leftPanel
        splitPane.rightComponent = rightPanel
        splitPane.setDividerLocation(400)

        return splitPane
    }

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
            add(vertexListPanel as Component, BorderLayout.CENTER)
        }

        panel.add(editorScrollPane, BorderLayout.CENTER)
        panel.add(structurePanel, BorderLayout.SOUTH)

        return panel
    }

    private fun createDiagramPanel(): JPanel {
        val panel = JPanel(BorderLayout()).apply {
            background = DarculaTheme.backgroundColor
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }

        diagramTabs = JTabbedPane().apply {
            background = DarculaTheme.toolbarColor
            foreground = DarculaTheme.foregroundColor
            border = BorderFactory.createEmptyBorder()

            addTab("Interactive", null, interactiveGraphPanel as Component, "Interactive graph view")
            addTab("Static", null, diagramPanel as Component, "Static graph image")
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

    private fun createStatusBar(): JPanel {
        return JPanel(BorderLayout()).apply {
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
    }

    private fun setupEventHandlers() {
        inputTextArea.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = updateGraph()
            override fun removeUpdate(e: DocumentEvent) = updateGraph()
            override fun changedUpdate(e: DocumentEvent) = updateGraph()
        })

        vertexListPanel.setOnVertexToggleListener { vertices ->
            val updatedGraph = graph.withUpdatedVertices(vertices)
            graph = updatedGraph
            updateDiagram(updatedGraph)
        }
    }

    private fun createNewGraph() {
        if (dialogService.showConfirmDialog(
                "Create a new graph? Any unsaved changes will be lost.",
                "New Graph"
            )) {
            inputTextArea.text = "A -> B\nB -> C\nC -> D\nD -> A"
            updateGraph()
        }
    }

    private fun openGraph() {
        dialogService.showOpenDialog()?.let { file ->
            try {
                val text = fileService.readFile(file)
                inputTextArea.text = text
                updateGraph()
            } catch (e: Exception) {
                dialogService.showErrorMessage(
                    "Error opening file: ${e.message}",
                    "Error"
                )
            }
        }
    }

    private fun saveGraph() {
        dialogService.showSaveDialog("txt")?.let { file ->
            try {
                fileService.writeFile(file, inputTextArea.text)
            } catch (e: Exception) {
                dialogService.showErrorMessage(
                    "Error saving file: ${e.message}",
                    "Error"
                )
            }
        }
    }

    private fun exportDiagram() {
        dialogService.showSaveDialog("png")?.let { file ->
            try {
                val selectedIndex = diagramTabs.selectedIndex

                if (selectedIndex == 0) {
                    val panel = interactiveGraphPanel as JComponent
                    val image = panel.createImage()
                    fileService.exportImage(file, image)
                    dialogService.showInfoMessage(
                        "Interactive diagram exported successfully.",
                        "Export Complete"
                    )
                } else {
                    // Static panel
                    val diagram = diagramPanel.diagram
                    if (diagram != null) {
                        fileService.exportImage(file, diagram)
                        dialogService.showInfoMessage(
                            "Static diagram exported successfully.",
                            "Export Complete"
                        )
                    } else {
                        dialogService.showErrorMessage(
                            "No diagram to export",
                            "Error"
                        )
                    }
                }
            } catch (e: Exception) {
                dialogService.showErrorMessage(
                    "Error exporting diagram: ${e.message}",
                    "Error"
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
            dialogService.showErrorMessage(
                "Error parsing graph: ${e.message}",
                "Error"
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
            val diagram = renderer.renderToImage(graph)
            withContext(Dispatchers.Main) {
                loadingLabel.isVisible = false
                diagramPanel.updateDiagram(diagram)
            }
        }
    }
}

private fun JComponent.createImage(): java.awt.image.BufferedImage {
    val image = java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()
    paint(g)
    g.dispose()
    return image
}