import com.model.Edge
import com.model.Graph
import com.model.Vertex
import com.renderer.MermaidGenerator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MermaidGeneratorTest {

    private val generator = MermaidGenerator()

    @Test
    fun `generateMermaid should create correct mermaid syntax for graph`() {
        val vertices = listOf(
            Vertex("A"),
            Vertex("B"),
            Vertex("C"),
            Vertex("D", false)
        )

        val edges = listOf(
            Edge("A", "B"),
            Edge("B", "C"),
            Edge("C", "A"),
            Edge("A", "D"),
            Edge("D", "B")
        )

        val graph = Graph(vertices, edges)

        val mermaidCode = generator.generateMermaid(graph)

        assertTrue(mermaidCode.startsWith("graph TD"))

        assertTrue(mermaidCode.contains("A --> B"))
        assertTrue(mermaidCode.contains("B --> C"))
        assertTrue(mermaidCode.contains("C --> A"))

        assertFalse(mermaidCode.contains("A --> D"))
        assertFalse(mermaidCode.contains("D --> B"))
    }

    @Test
    fun `generateMermaid should handle empty graph`() {
        val graph = Graph(emptyList(), emptyList())
        val mermaidCode = generator.generateMermaid(graph)
        assertEquals("graph TD\n", mermaidCode)
    }
}