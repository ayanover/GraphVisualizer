package com.renderer

import com.model.Graph

interface IMermaidGenerator {
    fun generateMermaid(graph: Graph): String
}