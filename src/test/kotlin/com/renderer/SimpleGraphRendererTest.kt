import com.model.Edge
import com.model.Graph
import com.model.Vertex
import com.renderer.SimpleGraphRenderer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage

class SimpleGraphRendererTest {

    private val renderer = SimpleGraphRenderer()

    @BeforeEach
    fun setUp() {
        SimpleGraphRenderer.vertexPositions.clear()
    }

    @Test
    fun `renderToImage should generate non-null image for valid graph`() = runBlocking {
        val vertices = listOf(
            Vertex("A"),
            Vertex("B"),
            Vertex("C")
        )

        val edges = listOf(
            Edge("A", "B"),
            Edge("B", "C"),
            Edge("C", "A")
        )

        val graph = Graph(vertices, edges)

        val image = renderer.renderToImage(graph)

        assertNotNull(image)
        assertTrue(image is BufferedImage)
        assertTrue(image!!.width > 0)
        assertTrue(image.height > 0)
    }

    @Test
    fun `renderToImage should generate image for empty graph`() = runBlocking {
        val graph = Graph(emptyList(), emptyList())

        val image = renderer.renderToImage(graph)

        assertNotNull(image)
        assertTrue(image is BufferedImage)
    }

    @Test
    fun `vertex positions should be initialized correctly`() = runBlocking {
        val vertices = listOf(
            Vertex("A"),
            Vertex("B"),
            Vertex("C"),
            Vertex("D")
        )

        val graph = Graph(vertices, emptyList())

        renderer.renderToImage(graph)

        assertEquals(4, SimpleGraphRenderer.vertexPositions.size)
        assertTrue(SimpleGraphRenderer.vertexPositions.containsKey("A"))
        assertTrue(SimpleGraphRenderer.vertexPositions.containsKey("B"))
        assertTrue(SimpleGraphRenderer.vertexPositions.containsKey("C"))
        assertTrue(SimpleGraphRenderer.vertexPositions.containsKey("D"))

        val posA = SimpleGraphRenderer.vertexPositions["A"]!!
        assertTrue(posA.first is Int)
        assertTrue(posA.second is Int)
    }

    @Test
    fun `vertex positions should be reused for same vertices`() = runBlocking {
        val vertices1 = listOf(
            Vertex("A"),
            Vertex("B")
        )
        val graph1 = Graph(vertices1, emptyList())
        renderer.renderToImage(graph1)

        val posA1 = SimpleGraphRenderer.vertexPositions["A"]!!
        val posB1 = SimpleGraphRenderer.vertexPositions["B"]!!

        val vertices2 = listOf(
            Vertex("A"),
            Vertex("B"),
            Vertex("C")
        )
        val graph2 = Graph(vertices2, emptyList())
        renderer.renderToImage(graph2)

        assertEquals(posA1, SimpleGraphRenderer.vertexPositions["A"])
        assertEquals(posB1, SimpleGraphRenderer.vertexPositions["B"])

        assertTrue(SimpleGraphRenderer.vertexPositions.containsKey("C"))
    }
}