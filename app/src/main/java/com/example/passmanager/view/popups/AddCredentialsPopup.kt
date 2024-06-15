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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.passmanager.credentialsManager
import com.example.passmanager.view.buttons.MyElevatedButton

@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun AddCredentialsPopupPreview() {
    AddCredentialsPopup(
        onSubmit = { platform, email, password ->
            println("Platform: $platform, Email: $email, Password: $password")
        },
        onCancel = {
            println("Cancel")
        }
    )
}


@Composable
fun AddCredentialsPopup(
    onSubmit: (platform: String, email: String, password: String) -> Unit,
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
    val platform = remember { mutableStateOf("") }
    // Email
    val email = remember { mutableStateOf("") }
    // Password
    val password = remember { mutableStateOf("") }

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
                .background(itemColor),
            contentAlignment = Alignment.CenterStart
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(itemPadding * 2),
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
                            onSubmit(platform.value, email.value, password.value)
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