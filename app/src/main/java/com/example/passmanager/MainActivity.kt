package com.example.passmanager

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.passmanager.control.CredentialsManager
import com.example.passmanager.control.MasterPasswordManager
import com.example.passmanager.control.ThemeModeManager
import com.example.passmanager.dal.StorageJSON
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.domain.MasterPasswordDO
import com.example.passmanager.dal.domain.ThemeModeDO
import com.example.passmanager.dal.dto.CredentialDT
import com.example.passmanager.dal.dto.MasterPasswordDT
import com.example.passmanager.dal.dto.ThemeModeDT
import com.example.passmanager.dal.dto.compareCredentialDT
import com.example.passmanager.dal.dto.compareMasterPasswordDT
import com.example.passmanager.dal.dto.compareThemeModeDT
import com.example.passmanager.dal.loadFromFile
import com.example.passmanager.ui.theme.PassManagerTheme
import com.example.passmanager.view.bars.TopBar
import com.example.passmanager.view.buttons.AddPwdButton
import com.example.passmanager.view.lists.ItemList
import com.example.passmanager.view.popups.EnterMasterPwdPopup
import com.example.passmanager.view.popups.InputCredentialsPopup
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

        // Files
        val credentialFile = applicationContext.getFileStreamPath(CREDENTIALS_FILE_PATH)
        val themeFile = applicationContext.getFileStreamPath(THEME_FILE_PATH)
        val masterPasswordFile = applicationContext.getFileStreamPath(MASTER_PASSWORD_FILE_PATH)
        // Check if files exist and create them if they don't
        if (!credentialFile.exists()) credentialFile.createNewFile()
        if (!themeFile.exists()) themeFile.createNewFile()
        if (!masterPasswordFile.exists()) masterPasswordFile.createNewFile()

        // Storages
        val credentialStorage = StorageJSON<CredentialDT> { a, b -> compareCredentialDT(a, b) }
        val themeStorage = StorageJSON<ThemeModeDT> { a, b -> compareThemeModeDT(a, b) }
        val masterPasswordStorage =
            StorageJSON<MasterPasswordDT> { a, b -> compareMasterPasswordDT(a, b) }

        // Load data from files
        credentialStorage.load(loadFromFile<CredentialDT>(credentialFile))
        themeStorage.load(loadFromFile<ThemeModeDT>(themeFile))
        masterPasswordStorage.load(loadFromFile<MasterPasswordDT>(masterPasswordFile))

        // Managers
        themeModeManager = ThemeModeManager(themeStorage, themeFile)
        masterPasswordManager = MasterPasswordManager(masterPasswordStorage, masterPasswordFile)
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
        themeModeManager.saveTMToFile()
        masterPasswordManager.saveMPToFile()
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
    var toEdit: CredentialDO? = null
    val ctx = LocalContext.current
    val scope = CoroutineScope(Dispatchers.IO)


    PassManagerTheme(
        darkTheme = themeMode.value == ThemeModeDO.DARK
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top
            ) {
                TopBar(
                    onClick = {
                        // Start settings activity
                        val intent = Intent(ctx, SettingsActivity::class.java)
                        ContextCompat.startActivity(ctx, intent, null)
                    },
                    search = { keyword ->
                        credentialsList.value = credentialsManager.getAll(masterPassword.value)
                            .filter {
                                it.platform.contains(keyword, ignoreCase = true) ||
                                it.emailUsername.contains(keyword, ignoreCase = true)
                            }
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
                AddPwdButton {
                    if (isLocked.value) {
                        showEnterMasterPwdPopup.value = true
                    } else {
                        showAddPassPopup.value = true
                    }
                }

                // Popups
                if (showAddPassPopup.value) {
                    InputCredentialsPopup(
                        onSubmit = { credential ->
                            credentialsManager.add(
                                credential,
                                masterPassword.value!!
                            )
                            credentialsList.value = credentialsManager.getAll(masterPassword.value)
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
                                credentialsList.value =
                                    credentialsManager.getAll(masterPassword.value)
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
                                    credentialsList.value = credentialsManager.getAll(masterPassword.value)
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
