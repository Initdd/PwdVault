package com.example.passmanager.scripts.lists

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.passmanager.isLocked


@Composable
fun PwdItem(platform: String, email: String, password: String = "", showMasterPasswordPopup: MutableState<Boolean>) {
    // Constants
    // Dimensions
    val itemPadding = 8.dp
    val cardPadding = 4.dp
    val itemHeight = 40.dp
    // Colors
    val itemColor = MaterialTheme.colorScheme.surface
    val lockedCardColor = MaterialTheme.colorScheme.outline
    val unlockedCardColor = MaterialTheme.colorScheme.secondary

    // Visible/Hidden password state variable
    val isPasswordVisible = remember { mutableStateOf(false) }
    // Clipboard Manager
    val clipboardManager = LocalClipboardManager.current
    
    val isItemLocked = remember { mutableStateOf(true) }

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
                            if (isLocked.value || isItemLocked.value)
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
                                //isLocked.value = !isLocked.value
                                showMasterPasswordPopup.value = true
                            } else {
                                if (isItemLocked.value)
                                    isItemLocked.value = false
                                else
                                    clipboardManager.setText(AnnotatedString(password))
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
}
