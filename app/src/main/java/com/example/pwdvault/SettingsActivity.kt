package com.example.pwdvault

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.pwdvault.SettingsActivity.Companion.filePickerLauncher
import com.example.pwdvault.SettingsActivity.Companion.gotExternalStoragePermission
import com.example.pwdvault.SettingsActivity.Companion.requestPermissionLauncher
import com.example.pwdvault.control.file_picker.FilePickerUtils
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

    companion object {
        // Permission flags
        var gotExternalStoragePermission = false

        // Launchers
        lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
        lateinit var filePickerLauncher: ActivityResultLauncher<Intent>

        // Whether we're exporting (true) or importing (false)
        var filePickerOperation: Boolean? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for permission depending on Android version
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                gotExternalStoragePermission = true
            } else {
                Toast.makeText(
                    this,
                    "Cannot import or export credentials without permission.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Handle file picker result
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    lifecycleScope.launch {
                        handleFilePickerResult(uri)
                    }
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            SettingsPage()
        }
    }

    private suspend fun handleFilePickerResult(uri: Uri) {
        if (filePickerOperation == true) {
            // Export credentials (write)
            FilePickerUtils.alterDocument(
                this,
                uri,
                credentialsManager.encodeCredentials(null).also { println(it) }
            )
        } else {
            // Import credentials (read)
            try {
                val fileStr = FilePickerUtils.readTextFromUri(this, uri)
                credentialsManager.loadCredFromJsonString(fileStr)
                credentialsList.value = credentialsManager.getAll(null)
                isLocked.value = true
            } catch (e: Exception) {
                Toast.makeText(this, "Unable to import credentials", Toast.LENGTH_SHORT).show()
            }
        }
        filePickerOperation = null
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestStoragePermission()
    }

    private fun checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            // Android 12L and below need this permission
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // Android 13+ — no need for media or storage permission
            gotExternalStoragePermission = true
        }
    }
}


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
                MyElevatedButton(
                    onClick = {
                        // request permission to read and write external storage
                        if (!gotExternalStoragePermission) {
                            requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                                .addCategory(Intent.CATEGORY_OPENABLE)
                                .setType("application/json")
                                .putExtra(Intent.EXTRA_TITLE, "credentials.json")
                            // set the operation to export
                            SettingsActivity.filePickerOperation = true
                            filePickerLauncher.launch(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Export credentials")
                }
                MyElevatedButton(
                    onClick = {
                        // request permission to read and write external storage
                        if (!gotExternalStoragePermission) {
                            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                .addCategory(Intent.CATEGORY_OPENABLE)
                                .setType("application/json")
                            // set the operation to import
                            SettingsActivity.filePickerOperation = false
                            filePickerLauncher.launch(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Import credentials")
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