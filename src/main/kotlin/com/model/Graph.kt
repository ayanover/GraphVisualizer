package com.model


data class Graph(val vertices: List<Vertex>, val edges: List<Edge>) {
    fun withUpdatedVertices(newVertices: List<Vertex>): Graph {
        return Graph(newVertices, this.edges)
    }

    fun getFilteredGraph(): Graph {
        val enabledVertices = vertices.filter { it.enabled }.map { it.name }.toSet()
        val filteredEdges = edges.filter {
            enabledVertices.contains(it.source) && enabledVertices.contains(it.target)
        }
        return Graph(vertices.filter { it.enabled }, filteredEdges)
    }

    fun getVertex(name: String): Vertex? {
        return vertices.find { it.name == name }
    }
}