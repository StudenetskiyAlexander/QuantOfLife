package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme
import com.skyfolk.quantoflife.ui.theme.Typography
import com.skyfolk.quantoflife.utils.toDateWithoutHourAndMinutes

@Composable
fun TimeSelectorView(
    modifier: Modifier = Modifier,
    time: Long,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onTimeSelectClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        Text(
            text = time.toDateWithoutHourAndMinutes(),
            style = Typography.body2,
        )
        Image(
            painter = painterResource(R.drawable.quant_date),
            contentDescription = "",
            Modifier
                .size(size = 30.dp)
                .clickable(onClick = { onTimeSelectClick() })
        )
    }
}

@Preview
@Composable
private fun TimeSelectorView() {
    QuantOfLifeMainTheme {
        TimeSelectorView(time = 123456) {}
    }
}