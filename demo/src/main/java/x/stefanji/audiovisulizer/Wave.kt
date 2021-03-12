package x.stefanji.audiovisulizer

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

class Wave @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private val PAINT_W = 4f.dp2px
    }

    private var values: List<Float> = emptyList()

    fun setValues(sampleValues: List<Float>) {
        values = sampleValues
        requestLayout()
    }

    private val paint = Paint().apply {
        strokeWidth = PAINT_W
        style = Paint.Style.STROKE
        color = Color.RED
        isAntiAlias = true
    }

    private val centerPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = PAINT_W
    }

    override fun draw(canvas: Canvas) {
        Log.d("JJJJ", "start draw")
        super.draw(canvas)
        if (values.isEmpty()) return
        var startX = 0f
        canvas.save()
        canvas.translate(0f, height / 2f)
        canvas.drawPoint(0f, 0f, centerPaint)
        values.forEach {
            val h = it * height
            Log.d("JJJ", "$it, $h")
            val sy = -h / 2
            val ey = h / 2
            canvas.drawLine(startX, sy, startX, ey, paint)
            startX += PAINT_W + PAINT_W
        }
        canvas.restore()
        Log.d("JJJJ", "draw finish")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = values.size * PAINT_W * 2
        val h = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        Log.d("JJJJ", "onMeasure: $w $h")
        setMeasuredDimension(w.toInt(), h)
    }
}

// Function that Calculate Root
// Mean Square
fun rmsValue(arr: IntArray, start: Int, n: Int): Float {
    var square = 0L
    var mean = 0.0
    var root = 0.0

    // Calculate square.
    for (i in start until n) {
        square += arr[i].toDouble().pow(2.0).toLong()
    }
    // Calculate Mean.
//    mean = (square / (n - start)).toDouble()
    // Calculate Root.
    root = sqrt(square.toDouble())
    return root.toFloat()
}

val Float.sp2px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics
    )

val Float.dp2px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

/** Computes the RMS volume of a group of signal sizes ranging from -1 to 1.  */
fun volumeRMS(raw: FloatArray): Double {
    var sum = 0.0
    if (raw.isEmpty()) {
        return sum
    } else {
        for (ii in raw.indices) {
            sum += raw[ii]
        }
    }
    val average = sum / raw.size
    var sumMeanSquare = 0.0
    for (ii in raw.indices) {
        sumMeanSquare += (raw[ii] - average).pow(2.0)
    }
    val averageMeanSquare = sumMeanSquare / raw.size
    return sqrt(averageMeanSquare)
}

fun calcDecibelLevel(buffer: IntArray): Double {

    return buffer.average()

    var sum = 0.0

    for (rawSample in buffer) {
        val sample = rawSample / 32768.0
        sum += sample * sample
    }

    val rms = sqrt(sum / buffer.size)

    return 20 * log10(rms)
}