package com.example.lab1

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ItemColumn(item: ItemColumnModel) {
    val openDialog = remember { mutableStateOf(false) }
    Button(
        onClick = {
            openDialog.value = true
        },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            containerColor = Color.White
        ),
        modifier = Modifier.padding(5.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
        ) {
            Text(item.title, fontSize = 24.sp)
        }
    }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = {
                Text(item.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("SSID: ${item.ssid}", fontSize = 20.sp)
                    Text("BSSID: ${item.bssid}", fontSize = 20.sp)
                    Text("Signal Level: ${item.signalLevel}", fontSize = 20.sp)
                    Text("Frequency: ${item.freq}", fontSize = 20.sp)
                    Text("Encrypt type: ${item.capabilities}", fontSize = 20.sp)
                }
            },
            confirmButton = {
                Button({
                        openDialog.value = false
                   },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.Black)
                    ) {
                    Text("OK", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
}