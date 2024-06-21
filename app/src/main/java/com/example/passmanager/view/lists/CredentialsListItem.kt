package com.example.passmanager.view.lists

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.example.passmanager.dal.domain.CredentialDO
import com.example.passmanager.isLocked
import com.example.passmanager.view.popups.CopyCredentialsDataPopup
import dev.obvionaoe.compose.swipeablecard.SwipeableCard

@SuppressLint("UnrememberedMutableState")
@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun Show_preview() {
    PwdItem(
        credential = CredentialDO(
            platform = "Google",
            emailUsername = "test@test.com",
            password = "password",
            otherInfo = listOf("info1", "info2")
        ),
        tryUnlock = {},
        delete = { _, _ -> },
        edit = {}
    )
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun PwdItem(
    credential: CredentialDO,
    tryUnlock: () -> Unit,
    delete: (platform: String, email: String) -> Unit,
    edit: (credential: CredentialDO) -> Unit
) {
    // Constants
    // Dimensions
    val itemPadding = 8.dp
    val cardPadding = 4.dp
    val itemHeight = 40.dp
    // Colors
    val lockedCardColor = MaterialTheme.colorScheme.outline
    val unlockedCardColor = MaterialTheme.colorScheme.secondary

    // Mutable States
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isItemLocked = remember { mutableStateOf(true) }
    val showCopyCredentialsDataPopup = remember { mutableStateOf(false) }

    // Clipboard Manager
    val clipboardManager = LocalClipboardManager.current


    SwipeableCard (
        modifier = Modifier
            .fillMaxWidth()
            .padding(itemPadding),
        endAction = {
            Row {
                IconButton(onClick = {
                    edit(credential)
                }) {
                    Icon(
                        // edit button
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .scale(1.3f)
                    )
                }
                IconButton(onClick = {
                    delete(credential.platform, credential.emailUsername)
                }) {
                    Icon(
                        // delete button
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .scale(1.3f)
                    )
                }
            }
        },
        onClick = {
            showCopyCredentialsDataPopup.value = true
        }
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
                            if (isLocked.value || isItemLocked.value)
                                lockedCardColor
                            else
                                unlockedCardColor
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = credential.platform,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(8.dp),
                        color = if (isLocked.value || isItemLocked.value)
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
                            if (isLocked.value || isItemLocked.value)
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
                            text = if (isLocked.value || isItemLocked.value)
                                credential.emailUsername
                            else
                                if (isPasswordVisible.value)
                                    credential.password
                                else
                                    "*".repeat(credential.password.length),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            color = if (isLocked.value || isItemLocked.value)
                                Color.Black
                            else
                                Color.White
                        )
                        if (!isLocked.value && !isItemLocked.value)
                            IconButton(
                                onClick = {
                                    isPasswordVisible.value = !isPasswordVisible.value
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = if (isLocked.value || isItemLocked.value)
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
                            if (isLocked.value || isItemLocked.value)
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
                                tryUnlock()
                            } else {
                                if (isItemLocked.value)
                                    isItemLocked.value = false
                                else
                                    clipboardManager.setText(AnnotatedString(credential.password))
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (isLocked.value || isItemLocked.value)
                                Color.Black
                            else
                                Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isLocked.value || isItemLocked.value)
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
    if (showCopyCredentialsDataPopup.value) {
        CopyCredentialsDataPopup(
            credential = credential,
            onCancel = { showCopyCredentialsDataPopup.value = false },
            isLocked = isLocked.value
        )
    }
}
