package helpers

import com.chebuso.chargetimer.shared.helpers.toFallbackInt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ToFallbackIntTest(
    private val value: String,
    private val expected: Int
) {
    @Test fun should_return_expected_result(){
        val actual = value.toFallbackInt(-1)

        Assert.assertEquals(expected, actual)
    }

    companion object{
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf ("", -1),
            arrayOf ("0", 0),
            arrayOf ("-2", -2),
            arrayOf ("2", 2),
        )
    }
}