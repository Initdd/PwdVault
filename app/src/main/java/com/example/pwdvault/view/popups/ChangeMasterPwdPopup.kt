package com.example.pwdvault.view.popups

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pwdvault.dal.domain.MasterPasswordDO
import com.example.pwdvault.view.buttons.MyElevatedButton


// Preview function for the Composable
@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun ChangeMasterPwdPopupPreview() {
    ChangeMasterPwdPopup(
        onConfirm = { _, _ -> },
        onCancel = {}
    )
}

@Composable
fun ChangeMasterPwdPopup(
    onConfirm: (oldMasterPwd: MasterPasswordDO, newMasterPwd: MasterPasswordDO) -> Unit,
    onCancel: () -> Unit
) {

    // Constants
    // Dimensions
    val dialogHorizontalPadding = 32.dp
    val dialogVerticalPadding = dialogHorizontalPadding*4
    val itemPadding = 16.dp

    val oldPwdInput: MutableState<String> = remember { mutableStateOf("") }
    val newPwdInput: MutableState<String> = remember { mutableStateOf("") }
    val confirmPwdInput: MutableState<String> = remember { mutableStateOf("") }

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
                    OutlinedTextField(
                        value = oldPwdInput.value,
                        onValueChange = {
                            oldPwdInput.value = it
                        },
                        label = { Text("Old Password") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = newPwdInput.value,
                        onValueChange = {
                            newPwdInput.value = it
                        },
                        label = { Text("New Password") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = confirmPwdInput.value,
                        onValueChange = {
                            confirmPwdInput.value = it
                        },
                        label = { Text("Confirm Password") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Column (
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Spacer(modifier = Modifier.padding(16.dp))
                    MyElevatedButton(
                        onClick = {
                            if (newPwdInput.value.isEmpty() || confirmPwdInput.value.isEmpty() || oldPwdInput.value.isEmpty()) {
                                return@MyElevatedButton
                            }
                            if (newPwdInput.value != confirmPwdInput.value) {
                                return@MyElevatedButton
                            }
                            onConfirm(
                                MasterPasswordDO(oldPwdInput.value),
                                MasterPasswordDO(newPwdInput.value)
                            )
                        },
                        primaryColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.background
                    ) {
                        Text("Change")
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    MyElevatedButton(
                        onClick = {
                            onCancel()
                        },
                        primaryColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.background
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}