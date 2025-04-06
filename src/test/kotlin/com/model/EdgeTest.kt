import com.model.Edge
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EdgeTest {

    @Test
    fun `edge should have correct source and target`() {
        val edge = Edge("A", "B")
        assertEquals("A", edge.source)
        assertEquals("B", edge.target)
    }
}