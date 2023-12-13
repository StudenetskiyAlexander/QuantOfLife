package com.skyfolk.quantoflife.ui.goals.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.entity.GoalPresentation
import com.skyfolk.quantoflife.ui.goals.view.preview.GOAL_COMPLETED_DISPLAYABLE
import com.skyfolk.quantoflife.ui.goals.view.preview.GOAL_DISPLAYABLE
import com.skyfolk.quantoflife.ui.theme.Colors

@Composable
fun GoalView(
    goal: GoalPresentation,
    onItemClick: (GoalPresentation) -> Unit
) {
    Card(
        backgroundColor = Colors.LightGray,
        modifier = Modifier
            .clickable {
                onItemClick(goal)
            }
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 5.dp)
    ) {
        Column(modifier = Modifier) {
            val color = when (goal.progress >= 100) {
                true -> Color.Green
                false -> Color.Yellow
            }
            Text(
                text = goal.targetText,
                modifier = Modifier.align(CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = Colors.LightFontGray
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
            ) {
                LinearProgressIndicator(
                    progress = (goal.progress * 0.01).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .height(25.dp)
                        .align(TopCenter),
                    color = color,
                    backgroundColor = Colors.MidGray
                )
                Text(
                    text = goal.progressText,
                    fontSize = 14.sp,
                    color = Color.White,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f
                        )
                    ),
                    modifier = Modifier.align(Center)
                )
            }
            Text(
                text = goal.additionText,
                fontSize = 12.sp,
                color = Colors.LightFontGray,
                modifier = Modifier.align(CenterHorizontally)
            )
        }
    }
}

@Preview
@Composable
fun GoalPreview() {
    GoalView(goal = GOAL_DISPLAYABLE, onItemClick = {})
}

@Preview
@Composable
fun GoalCompletedPreview() {
    GoalView(goal = GOAL_COMPLETED_DISPLAYABLE, onItemClick = {})
}