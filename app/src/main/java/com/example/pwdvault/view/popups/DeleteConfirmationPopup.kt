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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pwdvault.view.buttons.MyElevatedButton
import com.example.pwdvault.view.text.MyTitleText

// Preview function for the Composable
@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun DeleteConfirmationPopupPreview() {
    DeleteConfirmationPopup(
        onDelete = {},
        onCancel = {},
        toDeleteStr = "this item"
    )
}

@Composable
fun DeleteConfirmationPopup(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    toDeleteStr: String
) {

    // Constants
    // Dimensions
    val dialogHorizontalPadding = 32.dp
    val dialogVerticalPadding = dialogHorizontalPadding*4
    val itemPadding = 16.dp

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
                    MyTitleText(text = "Confirm deletion")
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = AnnotatedString("Are you sure you want to delete $toDeleteStr?"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column (
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Spacer(modifier = Modifier.padding(16.dp))
                    MyElevatedButton(
                        onClick = {
                            onDelete()
                        },
                        primaryColor = MaterialTheme.colorScheme.error,
                        backgroundColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text("Delete")
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    MyElevatedButton(
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}