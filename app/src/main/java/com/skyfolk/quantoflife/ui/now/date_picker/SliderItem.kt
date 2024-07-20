package com.skyfolk.quantoflife.ui.now.date_picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp


@Composable
fun SliderItem(
    value: Int,
    selectedIndex: Int,
    items: List<String>,
    onItemClick: (Int) -> Unit,
    alignment: Alignment,
    configuration: DatePickerConfiguration,
    height: Dp
) {
    // this gap variable helps in maintaining list as center focused list
    val gap = configuration.numberOfMonthYearRowsDisplayed / 2
    val isSelected = value == selectedIndex + gap
    val scale by animateFloatAsState(targetValue = if (isSelected) configuration.selectedMonthYearScaleFactor else 1f)
    Box(
        modifier = Modifier
            .height(height / (configuration.numberOfMonthYearRowsDisplayed))
    ) {
        if (value >= gap && value < items.size + gap) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .noRippleClickable {
                        onItemClick(value - gap)
                    },
                contentAlignment = if (alignment == Alignment.CenterEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                configuration.selectedMonthYearTextStyle.fontSize
                Box(
                    modifier = Modifier.width(
                        configuration.selectedMonthYearTextStyle.fontSize.spToDp(LocalDensity.current) * 5
                    )
                ) {
                    Text(
                        text = items[value - gap],
                        modifier = Modifier
                            .align(alignment)
                            .scale(scale),
                        style = if (isSelected) configuration.selectedMonthYearTextStyle
                        else configuration.monthYearTextStyle
                    )
                }
            }
        }
    }
}
