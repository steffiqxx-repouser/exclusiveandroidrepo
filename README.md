# Exclusive Android Projects

This repository contains three independent Android projects, plus a small Node.js backend used by the natural-language Android assistant. The Android apps are built with Kotlin and Gradle Kotlin DSL.

## Projects

### `AIAssistantWithAndroid`

A Kotlin/Jetpack Compose app that turns natural-language requests into a limited set of Android actions or information searches. The Android client sends requests to an Express backend, which uses the OpenAI API to interpret them. Supported actions in the backend include changing orientation or theme, adjusting text size, opening Calculator or Settings, and showing the date. Requests for information are handled using web search.

The backend validates action types and parameters before returning them to the app. It does not execute Android operations itself; the Android app handles the supported actions locally.

### `LocationDetectionSinglePage`

A Jetpack Compose location demo that requests fine or coarse location permission, obtains the current location using Google Play services, and displays latitude, longitude, and a reverse-geocoded address. Location access requires device permission and an available location provider.

### `MyApplication3`

A basic Android login-screen example built with AppCompat and XML layouts. It checks that an email and password have been entered and shows a local success toast. It is a UI/demo flow, not a connected authentication system.

## Requirements

- Android Studio with Android SDK Platform 37 installed
- JDK 11 or a compatible JDK supported by the installed Android Gradle Plugin
- Node.js and npm for the assistant backend
- An OpenAI API key to use the assistant backend
- A physical Android device or emulator; Google Play services are needed for location detection

Each Android app is a separate Gradle project. Open the desired project directory in Android Studio and let Gradle sync before building or running it.

## Build and run the Android apps

Open one of these directories in Android Studio:

```text
AIAssistantWithAndroid/
LocationDetectionSinglePage/
MyApplication3/
```

Select the `app` run configuration and launch it on an emulator or device. The projects include Gradle Wrapper scripts; from the project directory, you can also build with:

```bash
./gradlew assembleDebug
```

On Windows, use `gradlew.bat assembleDebug`.

## Run the AI assistant backend

In a terminal, from `AIAssistantWithAndroid/backend/`:

```bash
npm install
```

Create a local `.env` file in that directory (do not commit secrets):

```dotenv
OPENAI_API_KEY=your_api_key
# Optional; defaults to gpt-4.1-mini
OPENAI_MODEL=gpt-4.1-mini
# Optional; defaults to 8080
PORT=8080
```

Start the server:

```bash
npm start
```

For development with Node's watch mode:

```bash
npm run dev
```

The backend exposes `GET /health` and `POST /v1/command`. The Android debug build is configured to connect to `http://10.0.2.2:8080/`, which reaches the host machine from the standard Android emulator. For a physical device or a deployed backend, configure a reachable base URL in `AIAssistantWithAndroid/app/build.gradle.kts` and use HTTPS in production. Keep API keys on the backend; do not embed them in the Android app.

## Notes

- The AI assistant uses a constrained allowlist of Android actions. Requests outside the supported action set are not executed as device actions.
- Location permissions are requested at runtime. The app can only display location data after permission is granted and a location fix is available.
- The repository contains multiple standalone Android projects rather than a single shared Gradle build.
- No repository license is currently specified.
