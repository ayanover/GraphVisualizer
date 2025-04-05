import com.Edge
import com.Graph
import com.Vertex
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GraphTest {

    @Test
    fun `graph should have correct vertices and edges`() {
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
        assertEquals(vertices, graph.vertices)
        assertEquals(edges, graph.edges)
    }

    @Test
    fun `withUpdatedVertices should return new graph with updated vertices`() {
        val originalVertices = listOf(
            Vertex("A"),
            Vertex("B", true),
            Vertex("C")
        )

        val edges = listOf(
            Edge("A", "B"),
            Edge("B", "C")
        )

        val graph = Graph(originalVertices, edges)

        val updatedVertices = listOf(
            Vertex("A", false),
            Vertex("B", false),
            Vertex("C", true)
        )

        val updatedGraph = graph.withUpdatedVertices(updatedVertices)

        // Original graph should be unchanged
        assertEquals(originalVertices, graph.vertices)

        // New graph should have updated vertices but same edges
        assertEquals(updatedVertices, updatedGraph.vertices)
        assertEquals(edges, updatedGraph.edges)
    }

    @Test
    fun `getFilteredGraph should return graph with only enabled vertices and corresponding edges`() {
        val vertices = listOf(
            Vertex("A", true),
            Vertex("B", false),
            Vertex("C", true),
            Vertex("D", false)
        )

        val edges = listOf(
            Edge("A", "B"),
            Edge("A", "C"),
            Edge("B", "C"),
            Edge("C", "D"),
            Edge("D", "A")
        )

        val graph = Graph(vertices, edges)
        val filteredGraph = graph.getFilteredGraph()

        // Should only contain enabled vertices
        assertEquals(2, filteredGraph.vertices.size)
        assertTrue(filteredGraph.vertices.all { it.enabled })
        assertTrue(filteredGraph.vertices.any { it.name == "A" })
        assertTrue(filteredGraph.vertices.any { it.name == "C" })

        // Should only contain edges between enabled vertices
        assertEquals(1, filteredGraph.edges.size)
        assertEquals(Edge("A", "C"), filteredGraph.edges.first())
    }

    @Test
    fun `getVertex should return correct vertex by name`() {
        val vertices = listOf(
            Vertex("A"),
            Vertex("B", false),
            Vertex("C")
        )

        val graph = Graph(vertices, emptyList())

        val vertexA = graph.getVertex("A")
        assertNotNull(vertexA)
        assertEquals("A", vertexA?.name)
        assertTrue(vertexA?.enabled ?: false)

        val vertexB = graph.getVertex("B")
        assertNotNull(vertexB)
        assertEquals("B", vertexB?.name)
        assertFalse(vertexB?.enabled ?: true)

        val vertexD = graph.getVertex("D")
        assertNull(vertexD)
    }
}