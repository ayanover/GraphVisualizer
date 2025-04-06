package com.renderer

import com.model.Graph
import java.awt.image.BufferedImage

interface IGraphRenderer {
    suspend fun renderToImage(graph: Graph): BufferedImage?
}
