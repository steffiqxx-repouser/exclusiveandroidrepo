
package com.example.aiassistantwithandroid.data

import com.example.aiassistantwithandroid.BuildConfig

import kotlinx.serialization.json.Json

import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType

import retrofit2.Retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

import java.util.concurrent.TimeUnit

class CommandRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = false
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val api: CommandApi = Retrofit.Builder()

        .baseUrl(BuildConfig.API_BASE_URL)

        .client(client)

        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )

        .build()

        .create(CommandApi::class.java)

    suspend fun interpret(
        text: String
    ): CommandResponse {

        require(text.isNotBlank()) {
            "Please enter a command"
        }

        require(text.length <= 2000) {
            "Command is too long"
        }

        return api.interpret(
            CommandRequest(text.trim())
        )
    }
}
