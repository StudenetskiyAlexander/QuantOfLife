package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SeparatorLine() {
    Divider(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .padding(top = 5.dp),
        color = Color.Black,
        thickness = 1.dp
    )
}

@Preview
@Composable
fun SeparatorLinePreview() {
    SeparatorLine()
}