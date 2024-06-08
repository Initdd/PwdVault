package com.example.passmanager.scripts.popups

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.passmanager.dal.domain.MasterPasswordDO
import com.example.passmanager.isLocked
import com.example.passmanager.masterPasswordManager
import com.example.passmanager.scripts.buttons.MyElevatedButton
import com.example.passmanager.scripts.cards.MyElevatedCard

@SuppressLint("UnrememberedMutableState")
@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun Show_preview() {
    EnterMasterPwdPopup(mutableStateOf(MasterPasswordDO("empty")), mutableStateOf(true))
}

@Composable
fun EnterMasterPwdPopup(masterPwd: MutableState<MasterPasswordDO?>, showEnterMasterPwdPopup: MutableState<Boolean>) {

    val masterPwdStr = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = {
            showEnterMasterPwdPopup.value = false
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        MyElevatedCard(
            modifier = Modifier
                .padding(16.dp, 128.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter Master Password",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = masterPwdStr.value,
                    onValueChange = { masterPwdStr.value = it },
                    label = { Text("Master Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    MyElevatedButton(
                        onClick = {
                            showEnterMasterPwdPopup.value = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                    ) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    MyElevatedButton(
                        onClick = {
                            showEnterMasterPwdPopup.value = false
                            masterPwd.value = MasterPasswordDO(masterPwdStr.value)
                            if (masterPasswordManager.check(masterPwd.value!!)) {
                                isLocked.value = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                    ) {
                        Text(text = "Submit")
                    }
                }
            }
        }
    }
}
