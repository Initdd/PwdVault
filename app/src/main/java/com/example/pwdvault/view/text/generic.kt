package com.example.pwdvault.view.text

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(
    device = "spec:width=2280px,height=1080px,orientation=portrait",
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun TextPreview() {
    Column {
        MyTitleText("My Title")
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            ClickableText(text = "Clickable text") {}
        }
    }
}

@Composable
fun MyTitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.displaySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ClickableText(
    text: String,
    modifier: Modifier = Modifier,
    onClick: ()-> Unit,
) {
    Text(
        text = text,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.clickable { onClick() },
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal,
        style = MaterialTheme.typography.bodyLarge
    )
}