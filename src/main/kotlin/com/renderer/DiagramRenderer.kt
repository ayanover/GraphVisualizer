package com.renderer

import com.model.Graph
import java.awt.image.BufferedImage

interface DiagramRenderer {
    suspend fun renderToImage(graph: Graph): BufferedImage?
}