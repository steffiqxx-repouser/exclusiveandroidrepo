package com.example.aiassistantwithandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aiassistantwithandroid.ui.theme.AIAssistantWithAndroidTheme


//package com.example.naturalcommand

//import android.os.Bundle
//import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import com.example.aiassistantwithandroid.execution.ActionExecutor
import com.example.aiassistantwithandroid.ui.theme.CommandScreen
import com.example.aiassistantwithandroid.ui.theme.CommandViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CommandViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val state by viewModel.state.collectAsState()

            LaunchedEffect(Unit) {

                viewModel.events.collect { response ->

                    val messages = mutableListOf<String>()

                    response.answer?.let {
                        messages.add(it)
                    }

                    response.actions.forEach { action ->

                        val result = ActionExecutor.execute(
                            activity = this@MainActivity,
                            action = action,
                            onThemeChanged = viewModel::setTheme,
                            onTextSizeChanged = viewModel::setTextSize
                        )

                        messages.add(result)
                    }

                    viewModel.finishExecution(
                        messages.joinToString("\n\n")
                    )
                }
            }

            MaterialTheme(
                colorScheme = if (state.isDarkTheme) {
                    darkColorScheme()
                } else {
                    lightColorScheme()
                }
            ) {

                CommandScreen(
                    state = state,

                    onInputChanged = viewModel::updateInput,

                    onSubmit = viewModel::submitCommand,

                    onThemeChanged = viewModel::setTheme,

                    onTextSizeChanged = viewModel::setTextSize,

                    onExecutionComplete =
                        viewModel::finishExecution
                )
            }
        }
    }
}
