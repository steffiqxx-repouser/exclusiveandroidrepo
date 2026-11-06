package com.example.aiassistantwithandroid.ui.theme

//package com.example.naturalcommand.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiassistantwithandroid.execution.ActionExecutor

@Composable
fun CommandScreen(
    state: CommandUiState,
    onInputChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onThemeChanged: (Boolean) -> Unit,
    onTextSizeChanged: (Float) -> Unit,
    onExecutionComplete: (String) -> Unit
) {

    val context = LocalContext.current
    val activity = context as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (state.isDarkTheme) {
                    androidx.compose.ui.graphics.Color.Black
                } else {
                    androidx.compose.ui.graphics.Color.White
                }
            )
            .padding(24.dp)
    ) {

        Text(
            text = "Natural Command",
            fontSize = 22.sp,
            color = if (state.isDarkTheme)
                androidx.compose.ui.graphics.Color.White
            else
                androidx.compose.ui.graphics.Color.Black
        )

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = state.output,
                fontSize = state.textSize.sp,
                color = if (state.isDarkTheme)
                    androidx.compose.ui.graphics.Color.White
                else
                    androidx.compose.ui.graphics.Color.DarkGray
            )
        }

        OutlinedTextField(
            value = state.input,
            onValueChange = onInputChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Tell Android what to do...")
            },
            shape = RoundedCornerShape(16.dp),
            maxLines = 4,
            enabled = !state.loading
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading &&
                    state.input.isNotBlank()
        ) {

            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Execute command")
            }
        }
    }
}
