package com.example.passmanager.view.bars

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.passmanager.SettingsActivity
import com.example.passmanager.credentialsList
import com.example.passmanager.credentialsManager

@Composable
fun TopBar(ctx: Context) {

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
                onValueChange = {textValue ->
                    text.value = textValue
                    credentialsList.value = credentialsManager.getAll().filter {
                        it.platform.contains(text.value, ignoreCase = true) ||
                                it.email.contains(text.value, ignoreCase = true)
                    }
                },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth(0.87f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                maxLines = 1,
            )
            IconButton(
                onClick = {
                    // Start settings activity
                    val intent = Intent(ctx, SettingsActivity::class.java)
                    ContextCompat.startActivity(ctx, intent, null)
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    }
}
