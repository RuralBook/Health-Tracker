package com.tobias.healthtracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobias.healthtracker.data.UserDataDBHelper
import com.tobias.healthtracker.data.UserKalorienEaten
import com.tobias.healthtracker.data.UserKalorienGoals
import com.tobias.healthtracker.gui.PopupDialog
import com.tobias.healthtracker.gui.ProgressCircle
import com.tobias.healthtracker.gui.color
import com.tobias.healthtracker.ui.theme.HealthTrackerTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale.GERMANY
import kotlin.properties.Delegates

class KalorienActivity : ComponentActivity() {
    lateinit var UserDataGoals: UserKalorienGoals
    lateinit var AllEaten: List<UserKalorienEaten>
    var kalorienPercentage by Delegates.notNull<Float>()
    var allKal by Delegates.notNull<Double>()


    private lateinit var dbHelper: UserDataDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDataDBHelper(this@KalorienActivity)
        getKalorienData()
        Toast.makeText(this,dbHelper.getDataById(6).toString() , Toast.LENGTH_LONG).show()
        setContent {
            SetupView(
            )
        }
        Runtime.getRuntime().gc()

    }

    private fun getKalorienData() {
        //Get Kalorien
        UserDataGoals = UserKalorienGoals(dbHelper.getDataById(5)?.toDouble() ?: 1.0, dbHelper.getDataById(6)?.toDouble() ?: 0.0)
        kalorienPercentage = (UserDataGoals.kalorienUser / UserDataGoals.kalorienGoal).toFloat()

        //get Food:
        AllEaten = dbHelper.getAllFoodData()
    }


    override fun onResume() {
        super.onResume()
        getKalorienData()
    }

    @Composable
    fun SetupView() {
        val showDialog = remember { mutableStateOf(false) }
        val showAddFoodDialog = remember { mutableStateOf(false) }
        val reloadUI = remember { mutableStateOf(false) }

        HealthTrackerTheme() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(top = 25.dp)
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Deine Kalorien",
                        fontSize = 25.sp,
                    )
                }
                Box(
                    Modifier
                        .padding(top = 15.dp)
                        .height(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressCircle(
                        percentage = kalorienPercentage,
                        number = UserDataGoals.kalorienGoal,
                        color = "#FFA500".color,
                        colorTrans = "#80FFA500".color,
                        radius = 85.dp,
                        textColor = Color.Black,
                        description = "",
                        onClick = {
                            Toast.makeText(
                                this@KalorienActivity,
                                UserDataGoals.kalorienUser.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
                Row() {
                    OutlinedButton(onClick = {
                        showDialog.value = true
                    }) {
                        Text("Change Goal")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(onClick = {
                        showAddFoodDialog.value = true
                    }) {
                        Text("Add Food")
                    }
                }

                LazyColumn(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(25.dp)
                ){
                    // Add 5 items
                    items(AllEaten.size) { index ->
                        KalorienItem(AllEaten[index].name,AllEaten[index].time ,AllEaten[index].kalorien.toInt().toString())
                    }

                }
            }
            PopupDialog(
                showDialog = showDialog.value,
                onDismiss = {
                    showDialog.value = false
                    reloadUI.value = true
                },
                goalId = 5,
                context = this@KalorienActivity
            )

            PopupAddDialog(
                showAddFoodDialog = showAddFoodDialog.value,
                onDismiss = {
                    showAddFoodDialog.value = false
                    reloadUI.value = true
                },
                context = this@KalorienActivity,
            )
            if (reloadUI.value) {
                getKalorienData()
                reloadUI.value = false
            }
        }
    }


}

@Composable
fun KalorienItem(name: String, time: String, kalorien: String) {
    Row(
        Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(1f)) {
        Text(text = "$name @$time")
        Text(
            text = "$kalorien Kcal",
            Modifier
                .weight(1f)
                .wrapContentWidth(align = Alignment.End)
        )
    }
}
@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupAddDialog(
    showAddFoodDialog: Boolean,
    onDismiss: () -> Unit,
    context: Context,
) {
    val dbHelper: UserDataDBHelper = UserDataDBHelper(context)

    if (showAddFoodDialog) {
        val textStateName = remember { mutableStateOf(TextFieldValue()) }
        val textStateKalorien = remember { mutableStateOf(TextFieldValue()) }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Popup Dialog") },
            text = {
                Column {


                    TextField(
                        value = textStateName.value,
                        onValueChange = { textStateName.value = it },
                        placeholder = { Text("name") },
                        //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    TextField(
                        value = textStateKalorien.value,
                        onValueChange = { textStateKalorien.value = it },
                        placeholder = { Text("Kcal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, GERMANY)
                        val currentTime = timeFormat.format(Date())
                        if(textStateName.value.text != "" && textStateKalorien.value.text != "") {
                            dbHelper.insertFoodData(
                                textStateName.value.text,
                                currentTime,
                                (textStateKalorien.value.text.toString())
                            )
                            dbHelper.updateData(
                                6,
                                (textStateKalorien.value.text.toDouble() + (dbHelper.getDataById(6)
                                    ?.toDouble() ?: 0.0)).toString()
                            )
                            Toast.makeText(
                                context,
                                dbHelper.getDataById(6).toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "PLS enter valid data!", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    },
                    content = { Text("UPDATE") }
                )

            }
        )
    }
}