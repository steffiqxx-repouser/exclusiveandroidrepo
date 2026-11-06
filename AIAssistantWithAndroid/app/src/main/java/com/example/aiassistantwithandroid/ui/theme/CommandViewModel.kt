package com.example.aiassistantwithandroid.ui.theme

//package com.example.naturalcommand.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aiassistantwithandroid.data.CommandRepository
import com.example.aiassistantwithandroid.data.CommandResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommandUiState(
    val input: String = "",
    val loading: Boolean = false,
    val output: String = "",
    val isDarkTheme: Boolean = false,
    val textSize: Float = 18f
)

class CommandViewModel : ViewModel() {

    private val repository = CommandRepository()

    private val _state =
        MutableStateFlow(CommandUiState())

    val state = _state.asStateFlow()

    private val eventChannel =
        Channel<CommandResponse>(Channel.BUFFERED)

    val events = eventChannel.receiveAsFlow()

    fun updateInput(value: String) {
        _state.update {
            it.copy(input = value)
        }
    }

    fun submitCommand() {

        val command = state.value.input.trim()

        if (command.isBlank() || state.value.loading) {
            return
        }

        viewModelScope.launch {

            _state.update {
                it.copy(
                    loading = true,
                    output = "Understanding your request..."
                )
            }

            try {

                val response =
                    repository.interpret(command)

                if (response.actions.isEmpty() &&
                    response.answer.isNullOrBlank()
                ) {
                    _state.update {
                        it.copy(
                            output = "No supported action was identified."
                        )
                    }
                } else {
                    eventChannel.send(response)
                }

            } catch (e: Exception) {

                _state.update {
                    it.copy(
                        output = "Unable to process command: " +
                                (e.message ?: "Unknown error")
                    )
                }

            } finally {

                _state.update {
                    it.copy(loading = false)
                }
            }
        }
    }

    fun setTheme(dark: Boolean) {
        _state.update {
            it.copy(isDarkTheme = dark)
        }
    }

    fun setTextSize(size: Float) {
        _state.update {
            it.copy(textSize = size)
        }
    }

    fun finishExecution(message: String) {
        _state.update {
            it.copy(output = message)
        }
    }
}
