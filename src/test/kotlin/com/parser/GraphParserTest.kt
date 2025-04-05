package com.parser

import com.Edge
import com.Graph
import com.Vertex
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class GraphParserTest {

    private val parser = GraphParser()

    @Test
    fun `parse should create correct graph from valid input`() {
        val input = """
            A -> B
            B -> C
            C -> A
        """.trimIndent()

        val graph = parser.parse(input)

        assertEquals(3, graph.vertices.size)
        assertTrue(graph.vertices.any { it.name == "A" && it.enabled })
        assertTrue(graph.vertices.any { it.name == "B" && it.enabled })
        assertTrue(graph.vertices.any { it.name == "C" && it.enabled })

        assertEquals(3, graph.edges.size)
        assertTrue(graph.edges.contains(Edge("A", "B")))
        assertTrue(graph.edges.contains(Edge("B", "C")))
        assertTrue(graph.edges.contains(Edge("C", "A")))
    }

    @Test
    fun `parse should handle empty lines and whitespace`() {
        val input = """
            
            A -> B
            
            B -> C
               
            C -> A
            
        """.trimIndent()

        val graph = parser.parse(input)

        assertEquals(3, graph.edges.size)
        assertTrue(graph.edges.contains(Edge("A", "B")))
        assertTrue(graph.edges.contains(Edge("B", "C")))
        assertTrue(graph.edges.contains(Edge("C", "A")))
    }

    @Test
    fun `parse should handle whitespace around arrow operator`() {
        val input = """
            A->B
            B -> C
            C  ->  D
            D->  E
            E  ->F
        """.trimIndent()

        val graph = parser.parse(input)

        assertEquals(5, graph.edges.size)
        assertTrue(graph.edges.contains(Edge("A", "B")))
        assertTrue(graph.edges.contains(Edge("B", "C")))
        assertTrue(graph.edges.contains(Edge("C", "D")))
        assertTrue(graph.edges.contains(Edge("D", "E")))
        assertTrue(graph.edges.contains(Edge("E", "F")))
    }

    @Test
    fun `parse should handle empty input`() {
        val input = ""

        val graph = parser.parse(input)

        assertTrue(graph.vertices.isEmpty())
        assertTrue(graph.edges.isEmpty())
    }

    @Test
    fun `parse should ignore invalid lines`() {
        val input = """
            A -> B
            This is not a valid edge
            B -> C
            -> D
            E ->
            F - G
            C -> A
        """.trimIndent()

        val graph = parser.parse(input)
        assertEquals(3, graph.edges.size)
        assertTrue(graph.edges.contains(Edge("A", "B")))
        assertTrue(graph.edges.contains(Edge("B", "C")))
        assertTrue(graph.edges.contains(Edge("C", "A")))
    }
}