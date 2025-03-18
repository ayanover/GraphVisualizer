package com.parser

import com.model.Edge
import com.model.Graph
import com.model.Vertex

class GraphParser {
    /**
     * Parses the input text into a Graph object.
     *
     * @param input Text representation of the graph
     * @return A Graph object with vertices and edges
     */
    fun parse(input: String): Graph {
        val edges = mutableListOf<Edge>()
        val vertexSet = mutableSetOf<String>()

        input.trim().lines().filter { it.isNotEmpty() }.forEach { line ->
            parseLine(line)?.let { (source, target) ->
                edges.add(Edge(source, target))
                vertexSet.add(source)
                vertexSet.add(target)
            }
        }

        val vertices = vertexSet.map { Vertex(it, true) }.toList()
        return Graph(vertices, edges)
    }

    /**
     * Parses a single line in the format "A -> B" and returns the source and target vertices.
     *
     * @param line The line to parse
     * @return A Pair of source and target vertices, or null if the line doesn't match the expected format
     */
    private fun parseLine(line: String): Pair<String, String>? {
        val parts = line.trim().split("->").map { it.trim() }
        if (parts.size == 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()) {
            return Pair(parts[0], parts[1])
        }
        return null
    }
}