package com.example.pwdvault

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pwdvault.dal.domain.ThemeModeDO
import com.example.pwdvault.ui.theme.PassManagerTheme
import com.example.pwdvault.view.buttons.MyElevatedButton
import com.example.pwdvault.view.buttons.MySwitch
import com.example.pwdvault.view.cards.MyElevatedCard
import com.example.pwdvault.view.popups.ChangeMasterPwdPopup
import com.example.pwdvault.view.popups.DeleteConfirmationPopup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    val showDeleteConfirmationPopup = remember { mutableStateOf(false) }
    val showChangeMasterPwdPopup = remember { mutableStateOf(false) }

    val scope = CoroutineScope(Dispatchers.IO)

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
                    onClick = {
                        showChangeMasterPwdPopup.value = true
                    },
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
                            scope.launch { themeModeManager.saveTMToFile() }
                        }
                    }
                }
                MyElevatedButton(
                    onClick = {
                        showDeleteConfirmationPopup.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Delete all credentials")
                }
            }
        }
        if (showDeleteConfirmationPopup.value) {
            DeleteConfirmationPopup(
                onDelete = {
                    credentialsManager.deleteAll()
                    credentialsList.value = credentialsManager.getAll(null)
                    showDeleteConfirmationPopup.value = false
                },
                onCancel = {
                    showDeleteConfirmationPopup.value = false
                },
                toDeleteStr = "all credentials"
            )
        }
        if (showChangeMasterPwdPopup.value) {
            ChangeMasterPwdPopup(
                onConfirm = { oldMasterPassword, newMasterPassword ->
                    if (masterPasswordManager.check(oldMasterPassword)) {
                        // Update the master password
                        masterPasswordManager.set(newMasterPassword)
                        credentialsManager.reencryptAll(oldMasterPassword, newMasterPassword)
                        scope.launch { masterPasswordManager.saveMPToFile() }
                        // reset the decrypted state
                        isLocked.value = true
                        credentialsList.value = credentialsManager.getAll(null)
                        // close the popup
                        showChangeMasterPwdPopup.value = false
                    }
                },
                onCancel = {
                    showChangeMasterPwdPopup.value = false
                }
            )
        }
    }
}