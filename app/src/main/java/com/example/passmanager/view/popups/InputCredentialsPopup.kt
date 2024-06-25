package com.example.passmanager.view.popups

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.view.buttons.MyElevatedButton

@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun InputCredentialsPopupPreview() {
    InputCredentialsPopup(
        onSubmit = {},
        onCancel = {}
    )
}


@Composable
fun InputCredentialsPopup(
    initialValues: CredentialDO = CredentialDO("", "", "", emptyList()),
    onSubmit: (CredentialDO) -> Unit,
    onCancel: () -> Unit
) {
    // Constants
    // Dimensions
    val padding = 32.dp
    val itemPadding = 8.dp
    val dialogVerticalPadding = padding*4
    val buttonWidth = 100.dp
    // Colors
    val itemColor = MaterialTheme.colorScheme.surface

    // Platform
    val platform = remember { mutableStateOf(initialValues.platform) }
    // Email/Username
    val emailUsername = remember { mutableStateOf(initialValues.emailUsername) }
    // Password
    val password = remember { mutableStateOf(initialValues.password) }
    // Other Info
    val otherInfo = remember { mutableStateOf(initialValues.otherInfo.joinToString("\n")) }

    Dialog(
        onDismissRequest = {
            onCancel()
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ){
        Box (
            modifier = Modifier
                .padding(padding, dialogVerticalPadding)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(itemColor)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(itemPadding * 2),
                verticalArrangement = Arrangement.Center
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        // Platform
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
                        ),
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(itemPadding))
                    OutlinedTextField(
                        // Email/Username
                        value = emailUsername.value,
                        onValueChange = {
                            emailUsername.value = it
                        },
                        label = { Text("Email/Username") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(itemPadding))
                    OutlinedTextField(
                        // Password
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
                        ),
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(itemPadding))
                    OutlinedTextField(
                        // Other Info
                        value = otherInfo.value,
                        onValueChange = {
                            otherInfo.value = it
                        },
                        label = {
                            if (otherInfo.value.isEmpty()) Text("Other Info (one per line)\n(e.g. username, etc.)")
                            else Text("Other Info")
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        minLines = 3,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(itemPadding),
                    horizontalArrangement = Arrangement.End
                ) {
                    Spacer(modifier = Modifier.height(itemPadding))
                    MyElevatedButton(
                        onClick = {
                            onCancel()
                        },
                        modifier = Modifier
                            //.padding(itemPadding)
                            .width(buttonWidth)
                    ) {
                        Text(
                            "Cancel",
                            modifier = Modifier
                                .width(buttonWidth),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(itemPadding))
                    MyElevatedButton(
                        onClick = {
                            onSubmit(
                                CredentialDO(
                                    platform = platform.value,
                                    emailUsername = emailUsername.value,
                                    password = password.value,
                                    otherInfo = otherInfo.value.split("\n").map { it.trim() }
                                )
                            )
                        },
                        modifier = Modifier
                            //.padding(itemPadding)
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