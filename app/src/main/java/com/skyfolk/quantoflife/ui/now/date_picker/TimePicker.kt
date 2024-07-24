import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.ui.now.date_picker.black
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

val ColorPrimary = Color(0xFF1c2026)
val LightGreen = Color(0xFF8dc387)
val ProgressBarBg = Color(0xFFFFE9DD)
val ProgressBarProgress = Color(0xFFE08868)
val ProgressBarTint = Color(0xFFE1BAAA)


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    events: List<EventOnPicker>,
    padding: Float = 20f,
    stroke: Float = 15f,
    cap: StrokeCap = StrokeCap.Round,
    initialTimeInMinutes: Int = 0,
    onProgressChanged: (timeInMinutes: Int) -> Unit,
) {

    val eventsToDraw = mutableListOf<EventToDraw>()
    val spaceBeetweenCyrcle = 100f
    val iconsSize = 100f

    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var radius by remember { mutableStateOf(0f) }
    var bigRadius by remember { mutableStateOf(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }

    var appliedAngle by remember { mutableStateOf(initialTimeInMinutes.toDouble() / 2) }
    var lastAngle by remember { mutableStateOf(initialTimeInMinutes.toDouble() / 2) }
    var isSelectInternalCircle by remember { mutableStateOf(initialTimeInMinutes > 60 * 12) }

    val textMeasurer = rememberTextMeasurer()
    val style = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W700,
        color = black()
    )
    val textLayoutResult = remember(getTextFromAngle(lastAngle, isSelectInternalCircle), style) {
        textMeasurer.measure(getTextFromAngle(lastAngle, isSelectInternalCircle), style)
    }

    eventsToDraw.addAll(events.map {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.timeInMillis = it.time
        val forEventRadius = when (tmpCalendar[Calendar.HOUR_OF_DAY] < 12) {
            true -> bigRadius
            false -> radius
        }
        val totalMinutes = tmpCalendar[Calendar.HOUR_OF_DAY] * 60 + tmpCalendar[Calendar.MINUTE]
        val eventAngle = totalMinutes / 2
        EventToDraw(
            offset = center + Offset(
                forEventRadius * cos((-90 + abs(eventAngle)) * PI / 180f).toFloat() - iconsSize / 2,
                forEventRadius * sin((-90 + abs(eventAngle)) * PI / 180f).toFloat() - iconsSize / 2
            ),
            icon = getBitmapFromResourceName(resourceName = it.iconName)
        )
    })

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(250.dp)
        .onGloballyPositioned {
            width = it.size.width
            height = it.size.height
            center = Offset(width / 2f, height / 2f)
            radius = min(
                width.toFloat() - spaceBeetweenCyrcle,
                height.toFloat() - spaceBeetweenCyrcle
            ) / 2f - padding - stroke / 2f
            bigRadius = min(width.toFloat(), height.toFloat()) / 2f - padding - stroke / 2f
        }
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    val xRadiusRange = center.x - radius..center.x + radius
                    val yRadiusRange = center.y - radius..center.y + radius
                    isSelectInternalCircle = (it.x in xRadiusRange && it.y in yRadiusRange)
                    appliedAngle = if (center.x > it.x && center.y > it.y) {
                        270 + deltaAngle(center.x - it.x, center.y - it.y)
                    } else {
                        90 - deltaAngle(it.x - center.x, center.y - it.y)
                    }
                    val addFrom24Hour = when (isSelectInternalCircle) {
                        true -> 60 * 12
                        false -> 0
                    }
                    onProgressChanged((appliedAngle * 2).toInt() + addFrom24Hour)
                    lastAngle = appliedAngle
                }

                MotionEvent.ACTION_MOVE -> {
                    val xRadiusRange = center.x - radius..center.x + radius
                    val yRadiusRange = center.y - radius..center.y + radius
                    isSelectInternalCircle = (it.x in xRadiusRange && it.y in yRadiusRange)
                    appliedAngle = if (center.x > it.x && center.y > it.y) {
                        270 + deltaAngle(center.x - it.x, center.y - it.y)
                    } else {
                        90 - deltaAngle(it.x - center.x, center.y - it.y)
                    }
                    val addFrom24Hour = when (isSelectInternalCircle) {
                        true -> 60 * 12
                        false -> 0
                    }
                    onProgressChanged((appliedAngle * 2).toInt() + addFrom24Hour)
                    lastAngle = appliedAngle

                }

                MotionEvent.ACTION_UP -> {}
                else -> {
                    return@pointerInteropFilter false
                }
            }
            return@pointerInteropFilter true
        }
    ) {
        drawArc(
            color = ProgressBarBg,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = stroke,
                cap = cap
            )
        )
        drawArc(
            color = ProgressBarBg,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = center - Offset(bigRadius, bigRadius),
            size = Size(bigRadius * 2, bigRadius * 2),
            style = Stroke(
                width = stroke,
                cap = cap
            )
        )
        //TODO Draw line

        drawText(
            textMeasurer = textMeasurer,
            text = getTextFromAngle(lastAngle, isSelectInternalCircle),
            style = style,
            topLeft = Offset(
                x = center.x - textLayoutResult.size.width / 2,
                y = center.y - textLayoutResult.size.height / 2,
            )
        )

        val forPointRadius = when (isSelectInternalCircle) {
            true -> radius
            false -> bigRadius
        }
        drawCircle(
            color = LightGreen,
            radius = ((stroke * 5.0) / 3.0).toFloat(),
            center = center + Offset(
                forPointRadius * cos((-90 + abs(appliedAngle)) * PI / 180f).toFloat(),
                forPointRadius * sin((-90 + abs(appliedAngle)) * PI / 180f).toFloat()
            )
        )
        drawEvents(eventsToDraw, Size(iconsSize, iconsSize))
    }
}

private fun deltaAngle(x: Float, y: Float): Double {
    return Math.toDegrees(atan2(y.toDouble(), x.toDouble()))
}

private fun calculateTimeFromAngle(angle: Double, isInternalCircle: Boolean): Calendar {
    val calendar = Calendar.getInstance()
    val currentAngle = angle % 360
    calendar[Calendar.HOUR_OF_DAY] = (currentAngle / 30).toInt() + when (isInternalCircle) {
        true -> 12
        false -> 0
    }
    calendar[Calendar.MINUTE] = ((currentAngle % 30) * 2).toInt()
    return calendar
}

private fun getTextFromAngle(angle: Double, isInternalCircle: Boolean): String {
    val calendar = calculateTimeFromAngle(angle, isInternalCircle)
    return "${getTimeWithZeroText(calendar, Calendar.HOUR_OF_DAY)}:${
        getTimeWithZeroText(
            calendar,
            Calendar.MINUTE
        )
    }"
}

fun getTimeWithZeroText(calendar: Calendar, period: Int): String {
    val tmp = calendar[period]
    return when (tmp < 10) {
        true -> "0${tmp}"
        false -> tmp.toString()
    }
}

private fun DrawScope.drawEvents(images: List<EventToDraw>, size: Size) =
    this.drawIntoCanvas { canvas: Canvas ->
        images.forEach {
            drawImage(
                image = it.icon,
                dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                dstOffset = IntOffset(it.offset.x.toInt(), it.offset.y.toInt())
            )
        }
    }

@Composable
private fun getBitmapFromResourceName(resourceName: String): ImageBitmap {
    val context = LocalContext.current
    val imageResource = context.resources.getIdentifier(
        resourceName,
        "drawable",
        context.packageName
    )
    return ImageBitmap.imageResource(imageResource)
}

data class EventOnPicker(
    val time: Long,
    val iconName: String
)

data class EventToDraw(
    val offset: Offset,
    val icon: ImageBitmap
)