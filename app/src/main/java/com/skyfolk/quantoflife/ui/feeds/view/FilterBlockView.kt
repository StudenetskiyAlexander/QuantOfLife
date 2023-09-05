package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.skyfolk.quantoflife.timeInterval.TimeInterval
import com.skyfolk.quantoflife.ui.entity.QuantFilterMode
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme
import com.skyfolk.quantoflife.utils.timeInMillis
import java.util.Calendar

@Composable
fun FilterBlock(
    listOfQuantFilterModes: List<QuantFilterMode>,
    selectedQuantFilterMode: QuantFilterMode,
    onSelectQuantFilterMode: (QuantFilterMode) -> Unit,
    listOfTimeInterval: List<TimeInterval>,
    selectedTimeInterval: TimeInterval,
    selectedTextFilter: String,
    onSelectTimeInterval: (TimeInterval) -> Unit,
    onTextSearchEnter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SmallSubtitle(text = "Фильтры.")

            TextSearchField(
                initialValue = selectedTextFilter,
                placeholder = "Поиск по словам в названии или примечании",
                onEnter = onTextSearchEnter
            )

            DropdownSpinnerView(
                contentMapper = QuantFilterToContentMapper(),
                content = listOfQuantFilterModes,
                selectedItem = selectedQuantFilterMode
            ) {
                onSelectQuantFilterMode(it)
            }
            DropdownSpinnerView(
                contentMapper = TimeIntervalToContentMapper(),
                content = listOfTimeInterval,
                selectedItem = selectedTimeInterval
            ) {
                onSelectTimeInterval(it)
            }
            SeparatorLine()

            if (selectedTimeInterval is TimeInterval.Selected) {
                SelectedTimeInterval(
                    LocalContext.current,
                    Calendar.getInstance().timeInMillis(selectedTimeInterval.start),
                    Calendar.getInstance().timeInMillis(selectedTimeInterval.end)
                ) { onSelectTimeInterval(it) }
            }
        }
    }
}

@Preview
@Composable
private fun FilterBlockPreview() {
    QuantOfLifeMainTheme {
        FilterBlock(
            listOfQuantFilterModes = listOf(),
            selectedQuantFilterMode = QuantFilterMode.All,
            onSelectQuantFilterMode = {},
            listOfTimeInterval = listOf(),
            selectedTimeInterval = TimeInterval.All,
            selectedTextFilter = "",
            onSelectTimeInterval = {},
            onTextSearchEnter = {}
        )
    }
}
