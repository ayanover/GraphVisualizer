package com.renderer

import com.model.Graph
import com.model.Vertex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * Renders a Graph directly as a BufferedImage without using external services.
 * This renderer is used for exporting static images of the graph.
 */
class SimpleGraphRenderer : DiagramRenderer {
    private val backgroundColor = Color(43, 43, 43)
    private val edgeColor = Color(180, 180, 180)
    private val vertexColor = Color(75, 110, 175)
    private val vertexBorderColor = Color(90, 130, 200)
    private val textColor = Color.WHITE  // White text

    companion object {
        val vertexPositions = mutableMapOf<String, Pair<Int, Int>>()
    }

    /**
     * Renders a graph directly as a BufferedImage.
     *
     * @param graph The graph to render
     * @return A BufferedImage representation of the graph
     */
    override suspend fun renderToImage(graph: Graph): BufferedImage? = withContext(Dispatchers.Default) {
        val width = 800
        val height = 600
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        try {
            renderGraphToGraphics(graph, g2d, width, height)
            return@withContext image
        } finally {
            g2d.dispose()
        }
    }

    /**
     * Renders a graph to a Graphics2D context.
     *
     * @param graph The graph to render
     * @param g2d The Graphics2D context to render to
     * @param width The width of the image
     * @param height The height of the image
     */
    private fun renderGraphToGraphics(graph: Graph, g2d: Graphics2D, width: Int, height: Int) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g2d.color = backgroundColor
        g2d.fillRect(0, 0, width, height)

        val filteredGraph = graph.getFilteredGraph()

        val positions = calculateVertexPositions(filteredGraph.vertices, width, height)

        g2d.color = edgeColor
        g2d.stroke = BasicStroke(1.5f)

        for (edge in filteredGraph.edges) {
            val sourcePos = positions[edge.source] ?: continue
            val targetPos = positions[edge.target] ?: continue

            g2d.drawLine(sourcePos.first, sourcePos.second, targetPos.first, targetPos.second)

            drawArrow(g2d, sourcePos.first, sourcePos.second, targetPos.first, targetPos.second)
        }

        g2d.font = Font(Font.MONOSPACED, Font.PLAIN, 13)
        val fontMetrics = g2d.fontMetrics

        for ((name, position) in positions) {
            g2d.color = vertexColor
            val nodeSize = 40
            g2d.fillOval(position.first - nodeSize/2, position.second - nodeSize/2, nodeSize, nodeSize)

            g2d.color = vertexBorderColor
            g2d.drawOval(position.first - nodeSize/2, position.second - nodeSize/2, nodeSize, nodeSize)

            g2d.color = textColor
            val textWidth = fontMetrics.stringWidth(name)
            val textHeight = fontMetrics.height
            g2d.drawString(name, position.first - textWidth/2, position.second + textHeight/4)
        }
    }

    /**
     * Calculate positions for vertices in a circular layout.
     * The positions are stored in a shared map that can be accessed by the interactive panel.
     *
     * @param vertices The vertices to position
     * @param width The width of the image
     * @param height The height of the image
     * @return A map of vertex names to positions (x, y)
     */
    private fun calculateVertexPositions(
        vertices: List<Vertex>,
        width: Int,
        height: Int
    ): Map<String, Pair<Int, Int>> {
        val centerX = width / 2
        val centerY = height / 2
        val radius = minOf(width, height) / 3

        vertexPositions.keys.toList().forEach { name ->
            if (vertices.none { it.name == name }) {
                vertexPositions.remove(name)
            }
        }

        if (vertices.isEmpty() || (vertices.isNotEmpty() && vertexPositions.isEmpty())) {
            vertices.forEachIndexed { index, vertex ->
                val angle = 2.0 * Math.PI * index / vertices.size
                val x = centerX + (radius * Math.cos(angle)).toInt()
                val y = centerY + (radius * Math.sin(angle)).toInt()
                vertexPositions[vertex.name] = Pair(x, y)
            }
        } else {
            vertices.forEachIndexed { index, vertex ->
                if (!vertexPositions.containsKey(vertex.name)) {
                    val angle = 2.0 * Math.PI * index / vertices.size
                    val x = centerX + (radius * Math.cos(angle)).toInt()
                    val y = centerY + (radius * Math.sin(angle)).toInt()
                    vertexPositions[vertex.name] = Pair(x, y)
                }
            }
        }

        println("Vertex positions in SimpleGraphRenderer:")
        vertexPositions.forEach { (name, pos) ->
            println("  $name: (${pos.first}, ${pos.second})")
        }

        return vertexPositions
    }

    /**
     * Draw an arrow at the end of a line.
     *
     * @param g2d The Graphics2D context
     * @param x1 Starting X coordinate
     * @param y1 Starting Y coordinate
     * @param x2 Ending X coordinate
     * @param y2 Ending Y coordinate
     */
    private fun drawArrow(g2d: Graphics2D, x1: Int, y1: Int, x2: Int, y2: Int) {
        val dx = x2 - x1
        val dy = y2 - y1
        val length = Math.sqrt((dx * dx + dy * dy).toDouble())

        val dirX = dx / length
        val dirY = dy / length

        val arrowPositionX = x2 - dirX * 20
        val arrowPositionY = y2 - dirY * 20

        val perpX = -dirY
        val perpY = dirX

        val arrowSize = 10

        val arrowPoint1X = (arrowPositionX + arrowSize * (-dirX + perpX * 0.5)).toInt()
        val arrowPoint1Y = (arrowPositionY + arrowSize * (-dirY + perpY * 0.5)).toInt()

        val arrowPoint2X = (arrowPositionX + arrowSize * (-dirX - perpX * 0.5)).toInt()
        val arrowPoint2Y = (arrowPositionY + arrowSize * (-dirY - perpY * 0.5)).toInt()

        val xPoints = intArrayOf(x2, arrowPoint1X, arrowPoint2X)
        val yPoints = intArrayOf(y2, arrowPoint1Y, arrowPoint2Y)

        g2d.fillPolygon(xPoints, yPoints, 3)
    }
}