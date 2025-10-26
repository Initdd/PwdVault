package com.example.pwdvault.view.popups

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pwdvault.dal.domain.CredentialDO
import com.example.pwdvault.ui.theme.PassManagerTheme
import com.example.pwdvault.view.buttons.MyElevatedButton
import com.example.pwdvault.view.text.ClickableText
import com.example.pwdvault.view.text.MyTitleText

// Preview function for the Composable
@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun CopyCredentialsDataPopupPreview() {
    PassManagerTheme {
        CopyCredentialsDataPopup(
            credential = CredentialDO(
                platform = "Facebook",
                emailUsername = "test@test.com",
                password = "password",
                otherInfo = listOf()
            ),
            onCancel = {},
            isLocked = false
        )
    }
}

@Composable
fun CopyCredentialsDataPopup(
    credential: CredentialDO,
    onCancel: () -> Unit,
    isLocked: Boolean
) {

    // Constants
    // Dimensions
    val dialogHorizontalPadding = 32.dp
    val dialogVerticalPadding = dialogHorizontalPadding*4
    val itemPadding = 16.dp

    // Setup the clipboard manager
    val clipboardManager = LocalClipboardManager.current

    // visible password state
    val isPwdVisible = remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {
            onCancel()
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ){
        Box (
            modifier = Modifier
                .padding(dialogHorizontalPadding, dialogVerticalPadding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.CenterStart
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(itemPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    MyTitleText(text = "Copy Credentials")
                    Spacer(modifier = Modifier.padding(16.dp))
                    ClickableText(
                        text = credential.platform,
                        onClick = {
                            clipboardManager.setText(AnnotatedString(credential.platform))
                        }
                    )
                    if (credential.emailUsername.isNotEmpty()) {
                        Spacer(modifier = Modifier.padding(16.dp))
                        ClickableText(
                            text = credential.emailUsername,
                            onClick = {
                                clipboardManager.setText(AnnotatedString(credential.emailUsername))
                            }
                        )
                    }
                    if (!isLocked) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        Row (
                            modifier = Modifier.fillMaxWidth()
                        ){
                            ClickableText(
                                text = if (isPwdVisible.value) credential.password else credential.password.replace(
                                    Regex("."),
                                    "*"
                                ),
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(credential.password))
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                            IconButton(
                                onClick = {
                                    isPwdVisible.value = !isPwdVisible.value
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.RemoveRedEye,
                                    contentDescription = "Copy password"
                                )
                            }
                        }
                    }
                }
                Column (
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    MyElevatedButton(
                        onClick = {
                            onCancel()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}
