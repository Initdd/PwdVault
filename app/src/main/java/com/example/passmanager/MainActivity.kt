package com.example.passmanager

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.passmanager.control.CredentialsManager
import com.example.passmanager.control.MasterPasswordManager
import com.example.passmanager.control.ThemeModeManager
import com.example.passmanager.dal.StorageJSON
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.dal.dto.CredentialDT
import com.example.passmanager.dal.dto.MasterPasswordDT
import com.example.passmanager.dal.dto.ThemeModeDT
import com.example.passmanager.dal.dto.compareCredentialDT
import com.example.passmanager.dal.dto.compareMasterPasswordDT
import com.example.passmanager.dal.dto.compareThemeModeDT
import com.example.passmanager.dal.loadFromFile
import com.example.passmanager.dal.saveToFile
import com.example.passmanager.ui.theme.PassManagerTheme
import java.io.File

// Constants
// File paths
private const val CREDENTIALS_FILE_PATH = "credentials.json"
private const val THEME_FILE_PATH = "theme.json"
private const val MASTER_PASSWORD_FILE_PATH = "master_password.json"

// Storages
lateinit var credentialStorage: StorageJSON<CredentialDT>
lateinit var themeStorage: StorageJSON<ThemeModeDT>
lateinit var masterPasswordStorage: StorageJSON<MasterPasswordDT>

// Managers
lateinit var themeModeManager: ThemeModeManager
lateinit var masterPasswordManager: MasterPasswordManager
lateinit var credentialsManager: CredentialsManager

class MainActivity : ComponentActivity() {

    private lateinit var credentialFile: File;
    private lateinit var themeFile: File;
    private lateinit var masterPasswordFile: File;

    override fun onCreate(savedInstanceState: Bundle?) {

        // Files
        credentialFile = applicationContext.getFileStreamPath(CREDENTIALS_FILE_PATH)
        themeFile = applicationContext.getFileStreamPath(THEME_FILE_PATH)
        masterPasswordFile = applicationContext.getFileStreamPath(MASTER_PASSWORD_FILE_PATH)

        // Check if files exist and create them if they don't
        if (!credentialFile.exists()) {
            credentialFile.createNewFile()
        }
        if (!themeFile.exists()) {
            themeFile.createNewFile()
        }
        if (!masterPasswordFile.exists()) {
            masterPasswordFile.createNewFile()
        }

        // Storages
        credentialStorage = StorageJSON(
            credentialFile,
        ) { a, b ->
            compareCredentialDT(a, b)
        }
        themeStorage = StorageJSON(
            themeFile,
        ) { a, b ->
            compareThemeModeDT(a, b)
        }
        masterPasswordStorage = StorageJSON(
            masterPasswordFile,
        ) { a, b ->
            compareMasterPasswordDT(a, b)
        }

        // Load data from files
        credentialStorage.storage = loadFromFile<CredentialDT>(credentialFile).toMutableList()
        loadFromFile<ThemeModeDT>(themeFile)
        loadFromFile<MasterPasswordDT>(masterPasswordFile)


        // Theme
        themeModeManager = ThemeModeManager(themeStorage)
        masterPasswordManager = MasterPasswordManager(masterPasswordStorage)
        credentialsManager = CredentialsManager(credentialStorage)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassManagerApp()
        }
    }

    // execute on exit
    private fun exit() {
        saveToFile<CredentialDT>(credentialFile, credentialStorage.retrieveAll())
        saveToFile<ThemeModeDT>(themeFile, themeStorage.retrieveAll())
        saveToFile<MasterPasswordDT>(masterPasswordFile, masterPasswordStorage.retrieveAll())
    }

    // On app closed
    override fun onDestroy() {
        exit()
        super.onDestroy()
    }
    // On app paused
    override fun onPause() {
        exit()
        super.onPause()
    }
    // On app stopped
    override fun onStop() {
        exit()
        super.onStop()
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

    val showAddPassPopup = remember { mutableStateOf(false) }

    PassManagerTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top
            ) {
                TopBar()
                ItemList(credentialsManager.getAll())
                AddButton(showAddPassPopup)
                if (showAddPassPopup.value) {
                    AddPassPopup(showAddPassPopup)
                }
            }
        }
    }
}

@Composable
fun AddPassPopup(showAddPassPopup: MutableState<Boolean>) {
    // Constants
    // Dimensions
    val padding = 32.dp
    val itemPadding = 8.dp
    val dialogVerticalPadding = padding*5
    val buttonWidth = 70.dp
    // Colors
    val itemColor = MaterialTheme.colorScheme.surface

    // Platform
    val platform = remember { mutableStateOf("") }
    // Email
    val email = remember { mutableStateOf("") }
    // Password
    val password = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {
           showAddPassPopup.value = false
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ){
        Box(
            modifier = Modifier
                .padding(padding, dialogVerticalPadding)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(itemColor),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(itemPadding*2),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = platform.value,
                    onValueChange = {
                        platform.value = it
                    },
                    label = { Text("Platform") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(itemPadding))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                    },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(itemPadding))
                OutlinedTextField(
                    value = password.value,
                    onValueChange = {
                        password.value = it
                    },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(itemPadding))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(itemPadding),
                    horizontalArrangement = Arrangement.End
                ) {
                    ElevatedCard {
                        IconButton(
                            onClick = {
                                showAddPassPopup.value = false
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .padding(itemPadding)
                                .width(buttonWidth)
                        ) {
                            Text(
                                "Cancel",
                                modifier = Modifier
                                    .width(buttonWidth),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(itemPadding))
                    ElevatedCard {
                        IconButton(
                            onClick = {
                                credentialsManager.add(platform.value, email.value, password.value)
                                showAddPassPopup.value = false
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .padding(itemPadding)
                                .width(buttonWidth)
                        ) {
                            Text(
                                "Save",
                                modifier = Modifier
                                    .width(buttonWidth),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddButton(showAddPassPopup: MutableState<Boolean>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .fillMaxHeight(0.4f),
        contentAlignment = Alignment.BottomCenter
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Row {
                    IconButton(
                        onClick = {
                            showAddPassPopup.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                    }
                    Text(
                        text = "Add Password",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(0.dp, 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemList(list: List<CredentialDO> = emptyList()) {
    val boxPadding = 16.dp
    Box(
        modifier = Modifier
            .fillMaxHeight(0.85f)
            .padding(boxPadding),
        contentAlignment = Alignment.TopStart
    ) {
        // For now some example items
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            list.forEach {
                PwdItem(it.platform, it.email, it.password)
            }
//            PwdItem("Facebook", "test@test.com", "password")
//            PwdItem("Facebook", "test@test.com", "password")
//            PwdItem("Facebook", "test@test.com", "password")
//            PwdItem("Facebook", "test@test.com", "password")
//            PwdItem("Facebook", "test@test.com", "password")
//            PwdItem("Facebook", "test@test.com", "password")
//            PwdItem("Facebook", "test@test.com", "password")
        }
    }
}

@Composable
fun PwdItem(platform: String, email: String, password: String = "", ) {
    // Constants
    // Dimensions
    val itemPadding = 8.dp
    val cardPadding = 4.dp
    val itemHeight = 40.dp
    // Colors
    val itemColor = MaterialTheme.colorScheme.surface
    val lockedCardColor = MaterialTheme.colorScheme.outline
    val unlockedCardColor = MaterialTheme.colorScheme.secondary

    // Locked/Unlocked State variable
    val isLocked = remember { mutableStateOf(true) }
    // Visible/Hidden password state variable
    val isPasswordVisible = remember { mutableStateOf(false) }
    // Clipboard Manager
    val clipboardManager = LocalClipboardManager.current

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = itemColor,
        ),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(itemPadding)
        ) {
            Card(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(cardPadding)
                    .height(itemHeight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isLocked.value)
                                lockedCardColor
                            else
                                unlockedCardColor
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = platform,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(8.dp),
                        color = if (isLocked.value)
                                    Color.Black
                                else
                                    Color.White
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(cardPadding)
                    .height(itemHeight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isLocked.value)
                                lockedCardColor
                            else
                                unlockedCardColor
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text(
                            text = if (isLocked.value)
                                        email
                                    else
                                        if (isPasswordVisible.value)
                                            password
                                        else
                                            "*".repeat(password.length),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            color = if (isLocked.value)
                                Color.Black
                            else
                                Color.White
                        )
                        if (!isLocked.value)
                            IconButton(
                                onClick = {
                                    isPasswordVisible.value = !isPasswordVisible.value
                                    },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (isLocked.value)
                                        Color.Black
                                    else
                                        Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RemoveRedEye,
                                    contentDescription = "See Password"
                                )
                            }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(0.1f) // 30% of the row's width
                    .padding(cardPadding)
                    .height(itemHeight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isLocked.value)
                                lockedCardColor
                            else
                                unlockedCardColor
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (isLocked.value){
                                isLocked.value = !isLocked.value
                            } else {
                                clipboardManager.setText(AnnotatedString(password))
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isLocked.value)
                                Color.Black
                            else
                                Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isLocked.value)
                                            Icons.Filled.Lock
                                        else
                                            Icons.Filled.ContentCopy,
                            contentDescription = "Lock"
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TopBar() {
    val text = remember { mutableStateOf("") }
    Box (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = text.value,
                onValueChange = {
                    text.value = it
                },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth(0.87f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            IconButton(
                onClick = { /* Handle menu button click */ },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    }
}
