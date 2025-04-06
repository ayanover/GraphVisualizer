package com.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VertexTest {

    @Test
    fun `vertex should have correct name and default enabled status`() {
        val vertex = Vertex("A")
        assertEquals("A", vertex.name)
        assertTrue(vertex.enabled)
    }

    @Test
    fun `vertex should have correct name and specified enabled status`() {
        val vertex = Vertex("B", false)
        assertEquals("B", vertex.name)
        assertFalse(vertex.enabled)
    }
}