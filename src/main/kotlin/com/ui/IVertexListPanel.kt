package com.ui

import com.model.Vertex
import java.awt.Color

interface IVertexListPanel {
    fun updateVertices(vertices: List<Vertex>)

    fun setOnVertexToggleListener(listener: (List<Vertex>) -> Unit)
    fun getVertices(): List<Vertex>

    fun setupDarculaColors(bgColor: Color, fgColor: Color, brdColor: Color)
}