package com.example.aiassistantwithandroid.execution

//package com.example.naturalcommand.execution

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.provider.Settings
import com.example.aiassistantwithandroid.data.CommandAction
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

object ActionExecutor {

    fun execute(
        activity: Activity,
        action: CommandAction,
        onThemeChanged: (Boolean) -> Unit,
        onTextSizeChanged: (Float) -> Unit
    ): String {

        return try {

            when (action.type) {

                "SET_ORIENTATION" -> {

                    val orientation =
                        action.parameters["orientation"]
                            ?.jsonPrimitive
                            ?.contentOrNull

                    when (orientation?.uppercase()) {

                        "PORTRAIT" -> {
                            activity.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                            "Screen changed to portrait"
                        }

                        "LANDSCAPE" -> {
                            activity.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                            "Screen changed to landscape"
                        }

                        "AUTO" -> {
                            activity.requestedOrientation =
                                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                            "Automatic orientation enabled"
                        }

                        else -> "Invalid orientation"
                    }
                }

                "SET_THEME" -> {

                    val theme =
                        action.parameters["theme"]
                            ?.jsonPrimitive
                            ?.contentOrNull
                            ?.uppercase()

                    when (theme) {

                        "DARK" -> {
                            onThemeChanged(true)
                            "Dark theme enabled"
                        }

                        "LIGHT" -> {
                            onThemeChanged(false)
                            "Light theme enabled"
                        }

                        "SYSTEM" -> {
                            onThemeChanged(
                                false
                            )
                            "Light theme selected. System theme support can be added separately."
                        }

                        else -> "Invalid theme"
                    }
                }

                "SET_TEXT_SIZE" -> {

                    val size =
                        action.parameters["sizeSp"]
                            ?.jsonPrimitive
                            ?.intOrNull

                    if (size == null || size !in 12..32) {
                        "Text size must be between 12 and 32"
                    } else {
                        onTextSizeChanged(size.toFloat())
                        "Text size changed to $size"
                    }
                }

                "OPEN_CALCULATOR" -> {

                    val intent = Intent(Intent.ACTION_MAIN)
                        .addCategory(
                            "android.intent.category.APP_CALCULATOR"
                        )

                    try {
                        activity.startActivity(intent)
                        "Opening calculator"
                    } catch (e: ActivityNotFoundException) {
                        "No calculator application was found"
                    }
                }

                "OPEN_SETTINGS" -> {

                    activity.startActivity(
                        Intent(Settings.ACTION_SETTINGS)
                    )

                    "Opening Android settings"
                }

                "SHOW_DATE" -> {

                    java.time.LocalDate.now()
                        .toString()
                }

                else -> {
                    "Unsupported action: ${action.type}"
                }
            }

        } catch (e: Exception) {

            "Action failed: ${e.message ?: "Unknown error"}"
        }
    }
}
