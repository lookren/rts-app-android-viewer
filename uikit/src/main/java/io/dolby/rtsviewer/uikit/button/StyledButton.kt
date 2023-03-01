/*
 *
 *  This program is protected under international and U.S. copyright laws as
 *  an unpublished work. This program is confidential and proprietary to the
 *  copyright owners. Reproduction or disclosure, in whole or in part, or the
 *  production of derivative works therefrom without the express permission of
 *  the copyright owners is prohibited.
 *
 *                   Copyright (C) 2022 by Dolby Laboratories.
 *                              All rights reserved.
 *
 */

package io.dolby.rtsviewer.uikit.button

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dolby.uicomponents.ui.theme.backgroundColor
import com.dolby.uicomponents.ui.theme.borderColor
import com.dolby.uicomponents.ui.theme.fontColor
import io.dolby.rtsviewer.uikit.utils.listItemHeight
import io.dolby.uikit.utils.ViewState

/**
 * This is a sample component, please do not use this in any real world use case.
 * As more components are built, this can be deleted
 */
@Composable
@Preview(showBackground = false)
fun StyledButton(
    modifier: Modifier = Modifier,
    buttonText: String = "test",
    onClickAction: ((Context) -> Unit)? = null,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    isPrimary: Boolean = false,
    isLarge: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val context = LocalContext.current
    val viewState = ViewState.from(isPressed, isSelected, isEnabled)

    val fontColor = fontColor(viewState)
    val backgroundColor = backgroundColor(state = viewState, isPrimary = isPrimary)
    val borderColor = borderColor(viewState, isPrimary)

    Button(
        modifier = modifier
            .padding(5.dp)
            .listItemHeight()
            .fillMaxWidth()
            .widthIn(min = if (isLarge) 180.dp else 80.dp),
        interactionSource = interactionSource,
        onClick = { onClickAction?.invoke(context) },
        content = {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = fontColor
            )
        },
        colors = ButtonDefaults.textButtonColors(
            contentColor = fontColor,
            backgroundColor = backgroundColor
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, borderColor),
        enabled = isEnabled
    )
}
