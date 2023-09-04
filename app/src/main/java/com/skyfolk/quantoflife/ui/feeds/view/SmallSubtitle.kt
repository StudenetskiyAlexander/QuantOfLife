package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme
import com.skyfolk.quantoflife.ui.theme.Typography

@Composable
fun SmallSubtitle(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        text = text,
        style = Typography.subtitle1,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}

@Preview
@Composable
fun SmallSubtitlePreview() {
    QuantOfLifeMainTheme {
        SmallSubtitle(text = "Текст подзаголовка")
    }
}