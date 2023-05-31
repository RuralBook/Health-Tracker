package com.tobias.healthtracker

import android.content.Context
import android.widget.Toast
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
fun PopupDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    goalId: Int,
    context: Context
) {
    val dbHelper: UserDataDBHelper = UserDataDBHelper(context)
    val goal = dbHelper.getDataById(goalId)

    if (showDialog) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Change your Goal") },
            text = {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    placeholder = { Text(goal.toString()) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )},
            confirmButton = {
                Button(
                    onClick = {
                        if(textState.value.text != ""){
                            dbHelper.updateData(goalId, textState.value.text)
                        }
                        onDismiss()},
                    content = { Text("UPDATE") }
                )
            }
        )
    }
}