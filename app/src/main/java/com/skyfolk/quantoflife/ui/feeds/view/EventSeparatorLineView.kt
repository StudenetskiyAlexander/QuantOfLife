package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skyfolk.quantoflife.entity.EventListItem
import com.skyfolk.quantoflife.ui.theme.Colors

@Composable
fun EventSeparatorLine(
    separatorLine: EventListItem.SeparatorLine
) {
    Card(
        backgroundColor = Colors.Orange,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            separatorLine.text, modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic
        )
    }
}

@Preview
@Composable
fun EventSeparatorLinePreview() {
    EventSeparatorLine(separatorLine = EventListItem.SeparatorLine("new day"))
}
