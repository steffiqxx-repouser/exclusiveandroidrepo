
package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        setContent {
            MyApplicationTheme {
                LocationScreen(fusedLocationClient)
            }
        }
    }
}

@Composable
fun LocationScreen(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var latitude by remember { mutableStateOf("Detecting...") }
    var longitude by remember { mutableStateOf("Detecting...") }
    var address by remember { mutableStateOf("Fetching address...") }
    var status by remember { mutableStateOf("Checking location permission...") }

    var permissionGranted by remember { mutableStateOf(false) }

    fun detectLocation() {
        try {
            status = "Detecting your current location..."
            latitude = "Detecting..."
            longitude = "Detecting..."
            address = "Fetching address..."

            val tokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                tokenSource.token
            ).addOnSuccessListener { location ->

                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    status = "Location detected successfully"

                    Executors.newSingleThreadExecutor().execute {
                        try {
                            @Suppress("DEPRECATION")
                            val geocoder = Geocoder(
                                context,
                                Locale.getDefault()
                            )

                            @Suppress("DEPRECATION")
                            val results = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )

                            val detectedAddress =
                                results?.firstOrNull()?.getAddressLine(0)
                                    ?: "Address not available"

                            (context as? ComponentActivity)?.runOnUiThread {
                                address = detectedAddress
                            }

                        } catch (e: Exception) {
                            (context as? ComponentActivity)?.runOnUiThread {
                                address = "Unable to retrieve address"
                            }
                        }
                    }

                } else {
                    status = "Location unavailable. Please enable GPS."
                    latitude = "Not available"
                    longitude = "Not available"
                    address = "Not available"
                }

            }.addOnFailureListener {
                status = "Unable to detect location"
                latitude = "Not available"
                longitude = "Not available"
                address = "Not available"
            }

        } catch (e: SecurityException) {
            status = "Location permission is required"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        permissionGranted = granted

        if (granted) {
            detectLocation()
        } else {
            status = "Location permission denied"
            latitude = "Not available"
            longitude = "Not available"
            address = "Not available"
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            permissionGranted = true
            detectLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "My Location",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Current Latitude",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = latitude,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Current Longitude",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = longitude,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Current Address",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
