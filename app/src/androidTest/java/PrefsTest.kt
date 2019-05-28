import androidx.test.platform.app.InstrumentationRegistry
import com.stankurdziel.scoredisplay.Display
import com.stankurdziel.scoredisplay.Prefs
import org.junit.Assert
import org.junit.Test

class PrefsTest {
    @Test
    fun testPreferencesPersist() {
        val p = Prefs(InstrumentationRegistry.getInstrumentation().context)
        Assert.assertEquals("0", p.display("left").id)
        p.saveDisplay(Display("left", "abc"))
        Assert.assertEquals("abc", p.display("left").id)
    }
}
