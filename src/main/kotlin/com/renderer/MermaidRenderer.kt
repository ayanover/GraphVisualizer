package com.renderer

import com.model.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

class MermaidRenderer(private val mermaidGenerator: MermaidGenerator) : DiagramRenderer {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Renders a graph to an image using the Mermaid web service.
     *
     * @param graph The graph to render
     * @return A BufferedImage representation of the graph, or null if rendering failed
     */
    override suspend fun renderToImage(graph: Graph): BufferedImage? = withContext(Dispatchers.IO) {
        try {
            val mermaidCode = mermaidGenerator.generateMermaid(graph)
            println("Generating diagram for code: $mermaidCode")

            val base64Mermaid = Base64.getUrlEncoder().encodeToString(mermaidCode.toByteArray())

            val url = "https://mermaid.ink/img/svg/$base64Mermaid"
            println("Requesting diagram from: $url")

            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Error response: ${response.code} - ${response.message}")
                    return@withContext null
                }

                val contentType = response.body?.contentType()
                println("Response content type: $contentType")

                val responseBytes = response.body?.bytes() ?: return@withContext null
                println("Response size: ${responseBytes.size} bytes")

                if (responseBytes.isEmpty()) {
                    println("Empty response received")
                    return@withContext null
                }

                try {
                    val inputStream = ByteArrayInputStream(responseBytes)
                    val image = ImageIO.read(inputStream)

                    if (image == null) {
                        println("Failed to read image from response")
                    } else {
                        println("Successfully loaded image: ${image.width}x${image.height}")
                    }

                    return@withContext image
                } catch (e: Exception) {
                    println("Error reading image: ${e.message}")
                    e.printStackTrace()
                    return@withContext null
                }
            }
        } catch (e: Exception) {
            println("Exception in renderToImage: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }
}