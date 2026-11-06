package com.example.aiassistantwithandroid.data

import retrofit2.http.Body
import retrofit2.http.POST

interface CommandApi {

    @POST("v1/command")
    suspend fun interpret(
        @Body request: CommandRequest
    ): CommandResponse
}