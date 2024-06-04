package com.example.passmanager.scripts.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.passmanager.dal.domain.CredentialDO


@Composable
fun ItemList(list: List<CredentialDO> = emptyList()) {
    val boxPadding = 16.dp
    Box(
        modifier = Modifier
            .fillMaxHeight(0.85f)
            .padding(boxPadding),
        contentAlignment = Alignment.TopStart
    ) {
        // For now some example items
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            list.forEach {
                PwdItem(it.platform, it.email, it.password)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
