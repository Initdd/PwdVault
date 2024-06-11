package com.example.passmanager

import android.annotation.SuppressLint
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
import com.example.passmanager.view.popups.AddCredentialsPopup
import com.example.passmanager.view.popups.EnterMasterPwdPopup

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
    val showAddEnterMasterPwdPopup = remember { mutableStateOf(false) }

    // Global variables initialization
    isLocked = remember { mutableStateOf(true) }
    credentialsList = remember { mutableStateOf(credentialsManager.getAll()) }
    themeMode = remember { mutableStateOf(themeModeManager.getTheme()) }

    val masterPassword = remember { mutableStateOf<MasterPasswordDO?>(null) }

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
                TopBar(LocalContext.current)
                ItemList(credentialsList, showAddEnterMasterPwdPopup)
                AddPwdButton(showAddPassPopup)
                if (showAddPassPopup.value) {
                    AddCredentialsPopup(showAddPassPopup)
                }
                if (showAddEnterMasterPwdPopup.value) {
                    EnterMasterPwdPopup(masterPassword, showAddEnterMasterPwdPopup)
                }
            }
        }
    }
}
