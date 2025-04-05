package com.ui

import com.Graph
import com.Vertex
import com.renderer.SimpleGraphRenderer
import com.ui.theme.DarculaTheme
import java.awt.*
import java.awt.event.*
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import javax.swing.JPanel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class InteractiveGraphPanel : JPanel(), MouseListener, MouseMotionListener {
    private var graph: Graph? = null

    private val nodePositions = mutableMapOf<String, Point>()
    private val nodeSize = 40
    private val nodeRadius = nodeSize / 2

    private val backgroundColor = DarculaTheme.backgroundColor
    private val nodeColor = DarculaTheme.selectionColor
    private val nodeBorderColor = Color(90, 130, 200)
    private val edgeColor = Color(180, 180, 180)
    private val textColor = Color.WHITE
    private val selectedNodeColor = Color(200, 100, 100)

    private var draggedNode: String? = null
    private var dragOffsetX = 0
    private var dragOffsetY = 0

    private val labelFont = Font(Font.MONOSPACED, Font.PLAIN, 13)

    init {
        background = backgroundColor
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    fun setGraph(newGraph: Graph) {
        graph = newGraph
        initializeNodePositions()
        repaint()
    }

    private fun initializeNodePositions() {
        val enabledVertices = graph?.vertices?.filter { it.enabled } ?: emptyList()
        if (enabledVertices.isEmpty()) return

        println("Current nodePositions in InteractiveGraphPanel before initialization:")
        nodePositions.forEach { (name, pos) ->
            println("  $name: (${pos.x}, ${pos.y})")
        }

        val sharedPositions = SimpleGraphRenderer.vertexPositions
        println("Shared positions from SimpleGraphRenderer:")
        sharedPositions.forEach { (name, pos) ->
            println("  $name: (${pos.first}, ${pos.second})")
        }

        nodePositions.keys.toList().forEach { name ->
            if (enabledVertices.none { it.name == name }) {
                nodePositions.remove(name)
            }
        }

        var needFullInitialization = nodePositions.isEmpty()

        if (sharedPositions.isNotEmpty()) {
            enabledVertices.forEach { vertex ->
                val sharedPos = sharedPositions[vertex.name]
                if (sharedPos != null) {
                    nodePositions[vertex.name] = Point(sharedPos.first, sharedPos.second)
                }
            }
        } else if (needFullInitialization) {
            val centerX = if (width > 0) width / 2 else 400
            val centerY = if (height > 0) height / 2 else 300
            val radius = if (width > 0 && height > 0) minOf(width, height) / 3 else 200

            enabledVertices.forEachIndexed { index, vertex ->
                val angle = 2.0 * Math.PI * index / enabledVertices.size
                val x = centerX + (radius * Math.cos(angle)).toInt()
                val y = centerY + (radius * Math.sin(angle)).toInt()

                nodePositions[vertex.name] = Point(x, y)

                SimpleGraphRenderer.vertexPositions[vertex.name] = Pair(x, y)
            }
        } else {
            enabledVertices.forEach { vertex ->
                if (!nodePositions.containsKey(vertex.name)) {
                    val sharedPos = sharedPositions[vertex.name]
                    if (sharedPos != null) {
                        nodePositions[vertex.name] = Point(sharedPos.first, sharedPos.second)
                    } else {
                        val centerX = if (width > 0) width / 2 else 400
                        val centerY = if (height > 0) height / 2 else 300
                        val radius = if (width > 0 && height > 0) minOf(width, height) / 3 else 200
                        val angle = Math.random() * 2.0 * Math.PI
                        val x = centerX + (radius * Math.cos(angle)).toInt()
                        val y = centerY + (radius * Math.sin(angle)).toInt()

                        nodePositions[vertex.name] = Point(x, y)
                        SimpleGraphRenderer.vertexPositions[vertex.name] = Pair(x, y)
                    }
                }
            }
        }

        println("Final nodePositions in InteractiveGraphPanel after initialization:")
        nodePositions.forEach { (name, pos) ->
            println("  $name: (${pos.x}, ${pos.y})")
        }
    }

    private fun createCircularLayout(vertices: List<Vertex>) {
        val centerX = width / 2
        val centerY = height / 2
        val radius = minOf(width, height) / 3

        vertices.forEachIndexed { index, vertex ->
            val angle = 2.0 * Math.PI * index / vertices.size
            val x = centerX + (radius * Math.cos(angle)).toInt()
            val y = centerY + (radius * Math.sin(angle)).toInt()

            nodePositions[vertex.name] = Point(x, y)

            SimpleGraphRenderer.vertexPositions[vertex.name] = Pair(x, y)
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g2d.color = backgroundColor
        g2d.fillRect(0, 0, width, height)

        val currentGraph = graph ?: return

        if (nodePositions.isEmpty()) {
            initializeNodePositions()
        }

        val enabledVertices = currentGraph.vertices.filter { it.enabled }
        val enabledVertexNames = enabledVertices.map { it.name }.toSet()

        g2d.stroke = BasicStroke(1.5f)
        g2d.color = edgeColor

        currentGraph.edges.forEach { edge ->
            if (enabledVertexNames.contains(edge.source) && enabledVertexNames.contains(edge.target)) {
                val sourcePos = nodePositions[edge.source] ?: return@forEach
                val targetPos = nodePositions[edge.target] ?: return@forEach

                drawEdgeWithArrow(g2d, sourcePos, targetPos)
            }
        }

        g2d.font = labelFont
        val fontMetrics = g2d.fontMetrics

        enabledVertices.forEach { vertex ->
            val position = nodePositions[vertex.name] ?: return@forEach

            g2d.color = if (vertex.name == draggedNode) selectedNodeColor else nodeColor
            g2d.fillOval(position.x - nodeRadius, position.y - nodeRadius, nodeSize, nodeSize)

            g2d.color = nodeBorderColor
            g2d.drawOval(position.x - nodeRadius, position.y - nodeRadius, nodeSize, nodeSize)

            g2d.color = textColor
            val textWidth = fontMetrics.stringWidth(vertex.name)
            val textHeight = fontMetrics.height
            g2d.drawString(vertex.name, position.x - textWidth / 2, position.y + textHeight / 4)
        }
    }

    private fun drawEdgeWithArrow(g2d: Graphics2D, source: Point, target: Point) {
        val dx = target.x - source.x
        val dy = target.y - source.y
        val length = sqrt((dx * dx + dy * dy).toDouble())

        if (length < 0.0001) return

        val dirX = dx / length
        val dirY = dy / length

        val startX = source.x + (dirX * nodeRadius).toInt()
        val startY = source.y + (dirY * nodeRadius).toInt()
        val endX = target.x - (dirX * nodeRadius).toInt()
        val endY = target.y - (dirY * nodeRadius).toInt()

        g2d.draw(Line2D.Double(startX.toDouble(), startY.toDouble(), endX.toDouble(), endY.toDouble()))

        val arrowSize = 10.0
        val arrowAngle = 0.5

        val lineAngle = atan2(endY - startY.toDouble(), endX - startX.toDouble())

        val arrowPath = Path2D.Double()
        arrowPath.moveTo(endX.toDouble(), endY.toDouble())
        arrowPath.lineTo(
            endX - arrowSize * cos(lineAngle - arrowAngle),
            endY - arrowSize * sin(lineAngle - arrowAngle)
        )
        arrowPath.lineTo(
            endX - arrowSize * cos(lineAngle + arrowAngle),
            endY - arrowSize * sin(lineAngle + arrowAngle)
        )
        arrowPath.closePath()

        g2d.fill(arrowPath)
    }

    private fun findNodeAt(x: Int, y: Int): String? {
        for ((name, position) in nodePositions) {
            val dx = x - position.x
            val dy = y - position.y
            val distanceSquared = dx * dx + dy * dy

            if (distanceSquared <= nodeRadius * nodeRadius) {
                return name
            }
        }
        return null
    }

    override fun mousePressed(e: MouseEvent) {
        val nodeAtPosition = findNodeAt(e.x, e.y)
        if (nodeAtPosition != null) {
            draggedNode = nodeAtPosition
            val nodePosition = nodePositions[nodeAtPosition]!!
            dragOffsetX = nodePosition.x - e.x
            dragOffsetY = nodePosition.y - e.y
            repaint()
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        draggedNode = null
        repaint()
    }


    override fun mouseDragged(e: MouseEvent) {
        draggedNode?.let { nodeName ->
            val newPoint = Point(e.x + dragOffsetX, e.y + dragOffsetY)
            nodePositions[nodeName] = newPoint

            SimpleGraphRenderer.vertexPositions[nodeName] =
                Pair(newPoint.x, newPoint.y)

            repaint()
        }
    }

    override fun mouseClicked(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
    override fun mouseMoved(e: MouseEvent) {}
}