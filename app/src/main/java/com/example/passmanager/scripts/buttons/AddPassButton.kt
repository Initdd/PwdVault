package com.example.passmanager.scripts.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun AddPassButton(showAddPassPopup: MutableState<Boolean>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp)
            .fillMaxHeight(0.4f),
        contentAlignment = Alignment.BottomCenter
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Row {
                    IconButton(
                        onClick = {
                            showAddPassPopup.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                    }
                    Text(
                        text = "Add Password",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(0.dp, 8.dp)
                    )
                }
            }
        }
    }
}
