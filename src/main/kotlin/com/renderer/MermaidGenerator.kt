package com.renderer

import com.model.Graph
import javax.inject.Inject

class MermaidGenerator @Inject constructor() : IMermaidGenerator {
    override fun generateMermaid(graph: Graph): String {
        val filteredGraph = graph.getFilteredGraph()

        val builder = StringBuilder("graph TD\n")
        filteredGraph.edges.forEach { edge ->
            builder.append("  ${edge.source} --> ${edge.target}\n")
        }

        return builder.toString()
    }
}