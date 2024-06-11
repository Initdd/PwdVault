package com.example.passmanager.view.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyElevatedCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier
            .padding(bottom = 16.dp)
            .width(300.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 5.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    ) {
        content()
    }
}