package com.example.aiassistantwithandroid.data
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CommandRequest(
    val text: String
)

@Serializable
data class CommandAction(
    val type: String,
    val parameters: JsonObject =
        JsonObject(emptyMap())
)

@Serializable
data class CommandResponse(
    val actions: List<CommandAction> = emptyList(),
    val answer: String? = null
)
