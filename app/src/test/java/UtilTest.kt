import com.stankurdziel.scoredisplay.Display
import com.stankurdziel.scoredisplay.Prefs
import org.junit.Assert
import org.junit.Test

class UtilTest {
    @Test
    fun testCreateDisplay() {
        val d:Display = Display("left", "abc")
        Assert.assertEquals("left:abc", d.serialized())
    }
}
