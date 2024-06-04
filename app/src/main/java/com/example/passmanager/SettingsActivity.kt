package com.example.passmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passmanager.dal.domain.ThemeModeDO
import com.example.passmanager.scripts.buttons.MyElevatedButton
import com.example.passmanager.scripts.buttons.MySwitch
import com.example.passmanager.ui.theme.PassManagerTheme


class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsPage()
        }
    }
}

@Composable
fun SettingsPage() {

    PassManagerTheme(
        darkTheme = themeMode.value == ThemeModeDO.DARK
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 32.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            MyElevatedButton {
                Text(text = "Change password")
            }
            MyElevatedButton {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                ) {
                    Text(text = "Theme")
                    MySwitch(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface,
                        gapBetweenThumbAndTrackEdge = 4.dp
                    ) {
                        themeMode.value = if (themeMode.value == ThemeModeDO.LIGHT) {
                            ThemeModeDO.DARK
                        } else {
                            ThemeModeDO.LIGHT
                        }
                        themeModeManager.setTheme(themeMode.value)
                        themeModeManager.saveTMToFile()
                    }
                }
            }
            MyElevatedButton {
                Text(text = "Delete all credentials")
            }
        }
    }
}