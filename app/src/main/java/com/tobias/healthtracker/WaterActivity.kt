package com.tobias.healthtracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner.current
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
import androidx.core.content.ContextCompat.startActivity
import com.tobias.healthtracker.ui.theme.HealthTrackerTheme
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Locale.GERMANY
import kotlin.properties.Delegates

class WaterActivity : ComponentActivity() {
    lateinit var UserDataGoals: UserWaterGoals
    lateinit var AllDrunken: List<UserWaterDrunken>
    var kalorienPercentage by Delegates.notNull<Float>()
    var allKal by Delegates.notNull<Double>()


    private lateinit var dbHelper: UserDataDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDataDBHelper(this@WaterActivity)
        getWaterData()
        Toast.makeText(this,dbHelper.getDataById(4).toString() , Toast.LENGTH_LONG).show()
        setContent {
            SetupView(
            )
        }
        Runtime.getRuntime().gc()

    }

    private fun getWaterData() {
        //Get Kalorien
        UserDataGoals = UserWaterGoals(dbHelper.getDataById(3)?.toDouble() ?: 1.0, dbHelper.getDataById(4)?.toDouble() ?: 0.0)
        kalorienPercentage = (UserDataGoals.waterUser / UserDataGoals.waterGoal).toFloat()

        //get Food:
        AllDrunken = dbHelper.getAllWaterData()
    }


    override fun onResume() {
        super.onResume()
        getWaterData()
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
                        text = "Wasser",
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
                        number = UserDataGoals.waterGoal,
                        color = "#FF1AA7EC".color,
                        colorTrans = "#801AA7EC".color,
                        radius = 85.dp,
                        textColor = Color.Black,
                        description = "",
                        onClick = {
                            Toast.makeText(
                                this@WaterActivity,
                                UserDataGoals.waterUser.toString(),
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
                        Text("Add Water")
                    }
                }

                LazyColumn(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .padding(25.dp)
                ){
                    // Add 5 items
                    items(AllDrunken.size) { index ->
                        WaterItem(AllDrunken[index].name,AllDrunken[index].time ,AllDrunken[index].liters.toString())
                    }

                }
            }
            PopupDialog(
                showDialog = showDialog.value,
                onDismiss = {
                    showDialog.value = false
                    reloadUI.value = true
                },
                goalId = 3,
                context = this@WaterActivity
            )

            PopupAddDialogWater(
                showAddFoodDialog = showAddFoodDialog.value,
                onDismiss = {
                    showAddFoodDialog.value = false
                    reloadUI.value = true
                },
                context = this@WaterActivity,
            )
            if (reloadUI.value) {
                getWaterData()
                reloadUI.value = false
            }
        }
    }


}

@Composable
fun WaterItem(name: String, time: String, kalorien: String) {
    Row(
        Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(1f)) {
        Text(text = "$name @$time")
        Text(
            text = "$kalorien L",
            Modifier
                .weight(1f)
                .wrapContentWidth(align = Alignment.End)
        )
    }
}
@SuppressLint("SimpleDateFormat", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupAddDialogWater(
    showAddFoodDialog: Boolean,
    onDismiss: () -> Unit,
    context: Context,
) {
    val dbHelper: UserDataDBHelper = UserDataDBHelper(context)

    if (showAddFoodDialog) {
        val textStateName = remember { mutableStateOf(TextFieldValue()) }
        val textStateLiters = remember { mutableStateOf(TextFieldValue()) }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("new drink") },
            text = {
                Column {


                    TextField(
                        value = textStateName.value,
                        onValueChange = { textStateName.value = it },
                        placeholder = { Text("name") },
                        //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    TextField(
                        value = textStateLiters.value,
                        onValueChange = { textStateLiters.value = it },
                        placeholder = { Text("menge (L)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Row() {
                    Button(
                        onClick = {
                            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, GERMANY)
                            val currentTime = timeFormat.format(Date())

                            if (textStateLiters.value.text != "" && textStateLiters.value.text != " " && textStateLiters.value.text != "" && textStateLiters.value.text != " ")
                                dbHelper.insertWaterData(
                                    textStateName.value.text,
                                    currentTime,
                                    (textStateLiters.value.text.toString())
                                )
                            dbHelper.updateData(
                                4, (textStateLiters.value.text.toDouble() + (dbHelper.getDataById(4)
                                    ?.toDouble() ?: 0.0)).toString()
                            )
                            Toast.makeText(
                                context,
                                dbHelper.getDataById(4).toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            onDismiss()
                        },
                        content = { Text("UPDATE") }
                    )

                    Button(onClick = {
                        val intent = Intent(context, Camera::class.java)
                        context.startActivity(intent)
                    }) {

                    }

                }
            }
        )
    }
}