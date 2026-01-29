package com.example.pwdvault

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.pwdvault.control.CredentialsManager
import com.example.pwdvault.control.MasterPasswordManager
import com.example.pwdvault.control.ThemeModeManager
import com.example.pwdvault.dal.StorageSP
import com.example.pwdvault.dal.domain.CredentialDO
import com.example.pwdvault.dal.domain.MasterPasswordDO
import com.example.pwdvault.dal.domain.ThemeModeDO
import com.example.pwdvault.dal.dto.CredentialDT
import com.example.pwdvault.dal.dto.MasterPasswordDT
import com.example.pwdvault.dal.dto.ThemeModeDT
import com.example.pwdvault.dal.dto.compareCredentialDT
import com.example.pwdvault.dal.dto.compareMasterPasswordDT
import com.example.pwdvault.dal.dto.compareThemeModeDT
import com.example.pwdvault.dal.loadFromFile
import com.example.pwdvault.ui.theme.PassManagerTheme
import kotlinx.serialization.builtins.ListSerializer
import com.example.pwdvault.view.bars.TopBar
import com.example.pwdvault.view.buttons.AddPwdButton
import com.example.pwdvault.view.buttons.UnlockButton
import com.example.pwdvault.view.lists.ItemList
import com.example.pwdvault.view.popups.EnterMasterPwdPopup
import com.example.pwdvault.view.popups.InputCredentialsPopup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Constants
// File paths
private const val CREDENTIALS_FILE_PATH = "credentials.json"
private const val THEME_FILE_PATH = "theme.json"
private const val MASTER_PASSWORD_FILE_PATH = "master_password.json"

// Managers
lateinit var themeModeManager: ThemeModeManager
lateinit var masterPasswordManager: MasterPasswordManager
lateinit var credentialsManager: CredentialsManager

// Global
lateinit var credentialsList: MutableState<List<CredentialDO>>
lateinit var themeMode: MutableState<ThemeModeDO>
lateinit var isLocked: MutableState<Boolean>

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Files for migration and export/import
        val credentialFile = applicationContext.getFileStreamPath(CREDENTIALS_FILE_PATH)
        val themeFile = applicationContext.getFileStreamPath(THEME_FILE_PATH)
        val masterPasswordFile = applicationContext.getFileStreamPath(MASTER_PASSWORD_FILE_PATH)

        // Storages (Shared Preferences)
        val credentialStorage = StorageSP<CredentialDT>(applicationContext, "credentials_prefs", ListSerializer(CredentialDT.serializer())) { a, b -> compareCredentialDT(a, b) }
        val themeStorage = StorageSP<ThemeModeDT>(applicationContext, "theme_prefs", ListSerializer(ThemeModeDT.serializer())) { a, b -> compareThemeModeDT(a, b) }
        val masterPasswordStorage = StorageSP<MasterPasswordDT>(applicationContext, "master_password_prefs", ListSerializer(MasterPasswordDT.serializer())) { a, b -> compareMasterPasswordDT(a, b) }

        // Migration logic
        val sharedPrefs = getSharedPreferences("migration_prefs", MODE_PRIVATE)
        val isMigrated = sharedPrefs.getBoolean("is_migrated", false)

        if (!isMigrated) {
            // Load from files if they exist and migration hasn't happened
            if (credentialFile.exists()) {
                val data = loadFromFile<CredentialDT>(credentialFile)
                if (data.isNotEmpty()) credentialStorage.load(data)
            }
            if (themeFile.exists()) {
                val data = loadFromFile<ThemeModeDT>(themeFile)
                if (data.isNotEmpty()) themeStorage.load(data)
            }
            if (masterPasswordFile.exists()) {
                val data = loadFromFile<MasterPasswordDT>(masterPasswordFile)
                if (data.isNotEmpty()) masterPasswordStorage.load(data)
            }
            // Mark as migrated
            sharedPrefs.edit().putBoolean("is_migrated", true).apply()
        }

        // Managers
        themeModeManager = ThemeModeManager(themeStorage)
        masterPasswordManager = MasterPasswordManager(masterPasswordStorage)
        credentialsManager = CredentialsManager(credentialStorage, credentialFile)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassManagerApp()
        }
    }

    // execute on exit
    private fun exit() {
        credentialsManager.saveCredToFile()
    }

    override fun onDestroy() {
        exit(); super.onDestroy()
    }

    override fun onPause() {
        exit(); super.onPause()
    }

    override fun onStop() {
        exit(); super.onStop()
    }
}

@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun PassManagerApp() {

    // show popups boolean
    val showAddPassPopup = remember { mutableStateOf(false) }
    val showEnterMasterPwdPopup = remember { mutableStateOf(false) }
    val showEditCredentialsPopup = remember { mutableStateOf(false) }

    // Global variables initialization
    isLocked = remember { mutableStateOf(true) }
    credentialsList = remember { mutableStateOf(credentialsManager.getAll(null)) }
    themeMode = remember { mutableStateOf(themeModeManager.getTheme()) }

    val masterPassword = remember { mutableStateOf<MasterPasswordDO?>(null) }
    val searchQuery = remember { mutableStateOf("") }

    fun refreshCredentialsList() {
        credentialsList.value = credentialsManager.getAll(masterPassword.value)
            .filter {
                it.platform.contains(searchQuery.value, ignoreCase = true) ||
                it.emailUsername.contains(searchQuery.value, ignoreCase = true)
            }
    }

    var toEdit: CredentialDO? = null
    val ctx = LocalContext.current
    val scope = CoroutineScope(Dispatchers.IO)


    PassManagerTheme(
        darkTheme = themeMode.value == ThemeModeDO.DARK
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top
            ) {
                TopBar(
                    onClick = {
                        // Start settings activity
                        val intent = Intent(ctx, SettingsActivity::class.java)
                        ContextCompat.startActivity(ctx, intent, null)
                    },
                    searchQuery = searchQuery.value,
                    onSearchQueryChange = { query ->
                        searchQuery.value = query
                        refreshCredentialsList()
                    }
                )
                ItemList(
                    list = credentialsList,
                    unlock = { showEnterMasterPwdPopup.value = true },
                    delete = { platform: String, email: String ->
                        if (isLocked.value) {
                            showEnterMasterPwdPopup.value = true
                        } else {
                            credentialsList.value = credentialsList.value.filter {
                                it.platform != platform ||
                                it.emailUsername != email
                            }
                            credentialsManager.remove(platform, email)
                        }
                    },
                    edit = { credential: CredentialDO ->
                        if (isLocked.value) {
                            showEnterMasterPwdPopup.value = true
                        } else {
                            toEdit = credentialsList.value.find {
                                it.platform == credential.platform &&
                                        it.emailUsername == credential.emailUsername
                            }
                            showEditCredentialsPopup.value = true
                        }
                    }
                )
                
                // Button Row - Add Password and Unlock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    AddPwdButton(
                        modifier = Modifier.weight(1f),
                        onAddPwd = {
                            if (isLocked.value) {
                                showEnterMasterPwdPopup.value = true
                            } else {
                                showAddPassPopup.value = true
                            }
                        }
                    )
                    UnlockButton(
                        isLocked = isLocked.value,
                        onUnlock = {
                            if (isLocked.value) {
                                showEnterMasterPwdPopup.value = true
                            } else {
                                isLocked.value = true
                                masterPassword.value = null
                                refreshCredentialsList()
                            }
                        }
                    )
                }

                // Popups
                if (showAddPassPopup.value) {
                    InputCredentialsPopup(
                        onSubmit = { credential ->
                            credentialsManager.add(
                                credential,
                                masterPassword.value!!
                            )
                            refreshCredentialsList()
                            showAddPassPopup.value = false
                            scope.launch { credentialsManager.saveCredToFile() }
                        },
                        onCancel = { showAddPassPopup.value = false }
                    )
                }
                if (showEditCredentialsPopup.value) {
                    if (toEdit != null) {
                        InputCredentialsPopup(
                            initialValues = toEdit!!,
                            onSubmit = { credential ->
                                credentialsManager.update(
                                    toEdit!!.platform,
                                    toEdit!!.emailUsername,
                                    credential.copy( // if empty, keep the old value
                                        platform = credential.platform.ifEmpty { toEdit!!.platform },
                                        emailUsername = credential.emailUsername.ifEmpty { toEdit!!.emailUsername },
                                        password = credential.password.ifEmpty { toEdit!!.password },
                                        otherInfo = credential.otherInfo.ifEmpty { toEdit!!.otherInfo }
                                    ),
                                    masterPassword.value!!
                                )
                                toEdit = null
                                refreshCredentialsList()
                                showEditCredentialsPopup.value = false
                                scope.launch { credentialsManager.saveCredToFile() }
                            },
                            onCancel = {
                                showEditCredentialsPopup.value = false
                            },
                        )
                    }
                }
                if (showEnterMasterPwdPopup.value) {
                    EnterMasterPwdPopup(
                        onSubmit = {
                            if (masterPasswordManager.check(it)) {
                                try {
                                    isLocked.value = false
                                    masterPassword.value = it
                                    showEnterMasterPwdPopup.value = false
                                    refreshCredentialsList()
                                } catch (e: SecurityException) {
                                    showEnterMasterPwdPopup.value = false
                                }
                            }
                        },
                        onCancel = { showEnterMasterPwdPopup.value = false }
                    )
                }
            }
        }
    }
}
