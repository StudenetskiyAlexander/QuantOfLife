package com.skyfolk.quantoflife.ui.feeds.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyfolk.quantoflife.R
import com.skyfolk.quantoflife.ui.theme.QuantOfLifeMainTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun TextSearchField(placeholder: String, initialValue: String = "", onEnter: (String) -> Unit) {
    var value by rememberSaveable { mutableStateOf(initialValue) }
    val relocationRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .bringIntoViewRequester(relocationRequester)
                .onFocusChanged {
                    if ("$it" == "Active") {
                        scope.launch {
                            delay(200)
                            relocationRequester.bringIntoView()
                        }
                    }
                },
            value = value,
            textStyle = TextStyle(
                color = Color(0xFFFFFFFF)
            ),
            onValueChange = {
                value = it
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onEnter(value)
                }
            ),
            decorationBox = { innerTextField ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            maxLines = 1,
                            color = Color.Gray,
                        )
                    }
                }
                innerTextField()
            }
        )

        Image(
            painter = painterResource(R.drawable.ic_clear_search),
            contentDescription = "",
            Modifier
                .size(size = 20.dp)
                .clickable {
                    value = ""
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onEnter(value)
                }
        )
    }
}

@Preview
@Composable
fun TextSearchFieldPreview() {
    QuantOfLifeMainTheme {
        TextSearchField(placeholder = "Введите текст для поиска", onEnter = {})
    }
}
