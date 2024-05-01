package helpers

import com.chebuso.chargetimer.shared.helpers.trimNonPrintable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TrimNonPrintableTest(
    private val value: String,
    private val expected: String
) {
    @Test fun should_return_expected_result(){
        val actual = value.trimNonPrintable()

        Assert.assertEquals(expected, actual)
    }

    companion object{
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf ("\ta", "a"),
            arrayOf ("a\t", "a"),
            arrayOf (" a ", "a"),
            arrayOf (" ", ""),
            arrayOf ("", ""),
        )
    }
}