package com.example.passmanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.passmanager.control.CredentialsManager
import com.example.passmanager.control.MasterPasswordManager
import com.example.passmanager.control.ThemeModeManager
import com.example.passmanager.dal.StorageJSON
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.domain.ThemeModeDO
import com.example.passmanager.dal.dto.CredentialDT
import com.example.passmanager.dal.dto.MasterPasswordDT
import com.example.passmanager.dal.dto.ThemeModeDT
import com.example.passmanager.dal.dto.compareCredentialDT
import com.example.passmanager.dal.dto.compareMasterPasswordDT
import com.example.passmanager.dal.dto.compareThemeModeDT
import com.example.passmanager.dal.loadFromFile
import com.example.passmanager.scripts.bars.TopBar
import com.example.passmanager.scripts.buttons.AddPassButton
import com.example.passmanager.scripts.lists.ItemList
import com.example.passmanager.scripts.popups.AddPassPopup
import com.example.passmanager.ui.theme.PassManagerTheme

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
        val credentialStorage = StorageJSON<CredentialDT>{ a, b -> compareCredentialDT(a, b)}
        val themeStorage = StorageJSON<ThemeModeDT>{ a, b ->compareThemeModeDT(a, b)}
        val masterPasswordStorage = StorageJSON<MasterPasswordDT>{ a, b ->compareMasterPasswordDT(a, b)}

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
    override fun onDestroy() {exit(); super.onDestroy()}
    override fun onPause() {exit(); super.onPause()}
    override fun onStop() {exit(); super.onStop()}
}

@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun PassManagerApp() {

    val showAddPassPopup = remember { mutableStateOf(false) }

    credentialsList = remember { mutableStateOf(credentialsManager.getAll()) }

    themeMode = remember { mutableStateOf(themeModeManager.getTheme()) }

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
                ItemList(credentialsList.value)
                AddPassButton(showAddPassPopup)
                if (showAddPassPopup.value) {
                    AddPassPopup(showAddPassPopup)
                }
            }
        }
    }
}
