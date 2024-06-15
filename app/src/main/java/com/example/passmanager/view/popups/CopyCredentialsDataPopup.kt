package com.example.passmanager.view.popups

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.view.buttons.MyElevatedButton
import com.example.passmanager.view.text.MyTitleText

// Preview function for the Composable
@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun CopyCredentialsDataPopupPreview() {
    CopyCredentialsDataPopup(
        credential = CredentialDO(
            platform = "Facebook",
            email = "test@test.com",
            password = "password"
        ),
        onCancel = {},
        isLocked = false
    )
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
                MyTitleText(text = "Copy Credentials")
                Spacer(modifier = Modifier.padding(16.dp))
                MyElevatedButton(onClick = {
                    clipboardManager.setText(AnnotatedString(credential.platform))
                }) {
                    Text("Copy Platform Name")
                }
                MyElevatedButton(onClick = {
                    clipboardManager.setText(AnnotatedString(credential.email))
                }) {
                    Text("Copy Email")
                }
                if (!isLocked) {
                    MyElevatedButton(onClick = {
                        clipboardManager.setText(AnnotatedString(credential.password))
                    }) {
                        Text("Copy Password")
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                MyElevatedButton(
                    onClick = {
                        onCancel()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                ) {
                    Text("Back")
                }
            }
        }
    }
}