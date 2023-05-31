package com.tobias.healthtracker

import android.content.Context
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertName(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    context: Context
) {
    val dbHelper: UserDataDBHelper = UserDataDBHelper(context)

    if (showDialog) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Pls insert your name") },
            text = {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    placeholder = { Text("name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        dbHelper.updateData(1, textState.value.text)
                        onDismiss()},
                    content = { Text("OK") }
                )
            }
        )
    }
}