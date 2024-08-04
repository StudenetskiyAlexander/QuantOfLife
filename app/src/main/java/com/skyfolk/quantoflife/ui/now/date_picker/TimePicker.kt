import android.content.res.Configuration
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.ui.now.date_picker.DefaultDatePickerConfig.Companion.timePickerHeight
import com.skyfolk.quantoflife.ui.now.date_picker.black
import com.skyfolk.quantoflife.ui.now.date_picker.preview.EVENTS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    events: List<EventOnPicker>,
    padding: Float = 20f,
    initialTimeInMinutes: Int = 0,
    onProgressChanged: (timeInMinutes: Int) -> Unit,
) {

    val eventsToDraw = mutableListOf<EventToDraw>()

    val spaceBeetweenCyrcle = 120f
    val iconsSize = 130
    val stroke = 10f
    val spiralDelta = 13f
    val stepAngle = 30f

    val coroutineScope = rememberCoroutineScope()
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var radius by remember { mutableStateOf(0f) }
    var bigRadius by remember { mutableStateOf(0f) }
    var center by remember { mutableStateOf(Offset.Zero) }
    var tipEvent by remember { mutableStateOf<EventToDraw?>(null) }

    var appliedAngle by remember { mutableStateOf(initialTimeInMinutes.toFloat() / 2) }
    var lastAngle by remember { mutableStateOf(initialTimeInMinutes.toFloat() / 2) }
    var isSelectInternalCircle by remember { mutableStateOf(initialTimeInMinutes > 60 * 12) }

    val textMeasurer = rememberTextMeasurer()
    val tipMeasurer = rememberTextMeasurer()
    val style = TextStyle(
        fontSize = 25.sp,
        fontWeight = FontWeight.W700,
        color = black()
    )
    val textLayoutResult = remember(getTextFromAngle(lastAngle, isSelectInternalCircle), style) {
        textMeasurer.measure(getTextFromAngle(lastAngle, isSelectInternalCircle), style)
    }

    eventsToDraw.addAll(events.thinOut(60*60*1000).map {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.timeInMillis = it.time
        val totalMinutes = tmpCalendar[Calendar.HOUR_OF_DAY] * 60 + tmpCalendar[Calendar.MINUTE]
        val isInternalCircle = totalMinutes > 60 * 12
        val offset = getSpiralOffsetByAngle(
            angle = (getAngleForCircleAndIcons(
                totalMinutes.toFloat() / 2, isInternalCircle
            ) / 180 * PI - PI / 2).toFloat(),
            radius = bigRadius,
            delta = spiralDelta,
            center = center
        )
        EventToDraw(
            offset = offset.copy(
                x = offset.x - iconsSize / 2,
                y = offset.y - iconsSize / 2
            ),
            comment = it.comment,
            icon = getBitmapFromResourceName(resourceName = it.iconName),
            rectangle = Rect(
                left = offset.x - iconsSize / 2,
                top = offset.y - iconsSize / 2,
                right = offset.x + iconsSize / 2,
                bottom = offset.y + iconsSize / 2
            )
        )
    })

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(timePickerHeight)
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
                    eventsToDraw.forEach { event ->
                        if (event.rectangle.contains(Offset(it.x, it.y))) {
                            tipEvent = event
                            coroutineScope.launch {
                                delay(3000)
                                tipEvent = null
                            }
                        }
                    }
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
        val points = mutableListOf<Offset>()

        val endAngle = 630f - stepAngle
        var currentAngle = -90f - stepAngle
        while (currentAngle <= endAngle) {
            currentAngle += stepAngle
            val currentAngleInGradus = currentAngle * PI / 180f
            points.add(
                getSpiralOffsetByAngle(
                    angle = currentAngleInGradus.toFloat(),
                    radius = bigRadius,
                    delta = spiralDelta,
                    center = center
                )
            )
        }
        drawPoints(
            points = points,
            pointMode = PointMode.Polygon,
            color = style.color,
            strokeWidth = stroke
        )

        drawText(
            textMeasurer = textMeasurer,
            text = getTextFromAngle(lastAngle, isSelectInternalCircle),
            style = style,
            topLeft = Offset(
                x = center.x - textLayoutResult.size.width / 2,
                y = center.y - textLayoutResult.size.height / 2,
            )
        )
        drawLine(
            color = style.color,
            start = getSpiralOffsetByAngle(
                angle = (getAngleForCircleAndIcons(
                    appliedAngle,
                    isSelectInternalCircle
                ) / 180 * PI - PI / 2).toFloat(),
                radius = bigRadius + 30,
                delta = spiralDelta,
                center = center
            ),
            end = getSpiralOffsetByAngle(
                angle = (getAngleForCircleAndIcons(
                    appliedAngle,
                    isSelectInternalCircle
                ) / 180 * PI - PI / 2).toFloat(),
                radius = bigRadius - 30,
                delta = spiralDelta,
                center = center
            ),
            strokeWidth = stroke
        )
        drawEvents(
            eventsToDraw,
            iconsSize
        )

        tipEvent?.let {
            drawText(
                textMeasurer = tipMeasurer,
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = style.color,
                            background = Color.Black.copy(alpha = 0.4f),
                            fontSize = 18.sp,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(it.comment ?: "")
                    }
                },
                style = style.copy(fontSize = 12.sp),
                topLeft = it.offset
            )
        }
    }
}

private fun List<EventOnPicker>.thinOut(timeout: Long): List<EventOnPicker> {
    val result = mutableListOf<EventOnPicker>()

    this.sortedByDescending { it.time }.forEachIndexed { index, event ->
        val nextItem = this.getOrNull(index + 1)
        when (nextItem == null) {
            true -> result.add(event)
            false -> when (abs(event.time - nextItem.time) < timeout) {
                true -> result.add(event.copy(time = nextItem.time - timeout))
                false -> result.add(event)
            }
        }
    }
    return result
}

private fun getAngleForCircleAndIcons(appliedAngle: Float, isInternalCircle: Boolean): Float {
    val appliedAngleWithoutKnownAboutCircle = when (appliedAngle >= 360) {
        true -> appliedAngle - 360
        false -> appliedAngle
    }
    return when (isInternalCircle) {
        true -> appliedAngleWithoutKnownAboutCircle + 360
        false -> appliedAngleWithoutKnownAboutCircle
    }
}

private fun getSpiralOffsetByAngle(
    angle: Float,
    radius: Float,
    delta: Float,
    center: Offset
): Offset {
    return Offset(
        (radius - angle * delta) * cos(angle) + center.x,
        (radius - angle * delta) * sin(angle) + center.y + delta
    )
}

private fun deltaAngle(x: Float, y: Float): Float {
    return Math.toDegrees(atan2(y, x).toDouble()).toFloat()
}

private fun calculateTimeFromAngle(angle: Float, isInternalCircle: Boolean): Calendar {
    val calendar = Calendar.getInstance()
    val currentAngle = angle % 360
    calendar[Calendar.HOUR_OF_DAY] = (currentAngle / 30).toInt() + when (isInternalCircle) {
        true -> 12
        false -> 0
    }
    calendar[Calendar.MINUTE] = ((currentAngle % 30) * 2).toInt()
    return calendar
}

private fun getTextFromAngle(angle: Float, isInternalCircle: Boolean): String {
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

private fun DrawScope.drawEvents(
    images: List<EventToDraw>,
    size: Int
) = this.drawIntoCanvas { canvas: Canvas ->
    images.forEach {
        drawImage(
            image = it.icon,
            dstSize = IntSize(size, size),
            dstOffset = IntOffset(
                it.offset.x.toInt(),
                it.offset.y.toInt()
            )
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
    val iconName: String,
    val comment: String? = null
)

data class EventToDraw(
    val offset: Offset,
    val rectangle: Rect,
    val icon: ImageBitmap,
    val comment: String? = null
)

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TimePickerPreview() {
    Box(modifier = Modifier.background(Color.Black)) {
        TimePicker(
            initialTimeInMinutes = 1200,
            events = EVENTS
        ) {

        }
    }
}