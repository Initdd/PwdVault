package com.example.passmanager

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.passmanager.dal.domain.ThemeModeDO
import com.example.passmanager.ui.theme.PassManagerTheme
import com.example.passmanager.view.buttons.MyElevatedButton
import com.example.passmanager.view.buttons.MySwitch
import com.example.passmanager.view.cards.MyElevatedCard


class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsPage()
        }
    }
}

@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SettingsPage() {
    PassManagerTheme(
        darkTheme = themeMode.value == ThemeModeDO.DARK
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp, 32.dp)
            ) {
                Text( // Settings title
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                )
                MyElevatedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Change Master password")
                }
                MyElevatedCard (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Theme",
                        )
                        Spacer(modifier = Modifier.padding(16.dp, 0.dp))
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
                MyElevatedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Delete all credentials")
                }
            }
        }
    }
}