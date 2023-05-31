package com.tobias.healthtracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobias.healthtracker.data.UserDataDBHelper
import com.tobias.healthtracker.data.UserWorkoutGoals
import com.tobias.healthtracker.data.UserWorkouts
import com.tobias.healthtracker.gui.PopupDialog
import com.tobias.healthtracker.gui.ProgressCircle
import com.tobias.healthtracker.gui.color
import com.tobias.healthtracker.ui.theme.HealthTrackerTheme
import java.util.Date
import kotlin.properties.Delegates

class WorkoutActivity : ComponentActivity() {
    lateinit var UserDataGoals: UserWorkoutGoals
    lateinit var AllDrunken: List<UserWorkouts>
    var workoutPercentage by Delegates.notNull<Float>()


    private lateinit var dbHelper: UserDataDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDataDBHelper(this@WorkoutActivity)
        getWorkoutData()
        Toast.makeText(this,dbHelper.getDataById(8).toString() , Toast.LENGTH_LONG).show()
        setContent {
            SetupView(
            )
        }
        Runtime.getRuntime().gc()

    }

    private fun getWorkoutData() {
        //Get Kalorien
        UserDataGoals = UserWorkoutGoals(dbHelper.getDataById(7)?.toDouble() ?: 1.0, dbHelper.getDataById(8)?.toDouble() ?: 0.0)
        workoutPercentage = (UserDataGoals.workoutUser / UserDataGoals.workoutGoal).toFloat()

        //get Food:
        AllDrunken = dbHelper.getAllWorkoutData()
    }


    override fun onResume() {
        super.onResume()
        getWorkoutData()
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
                        text = "Workouts",
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
                        percentage = workoutPercentage,
                        number = UserDataGoals.workoutUser,
                        color = "#FF1AA7EC".color,
                        colorTrans = "#801AA7EC".color,
                        radius = 85.dp,
                        textColor = Color.Black,
                        description = "",
                        onClick = {
                            Toast.makeText(
                                this@WorkoutActivity,
                                UserDataGoals.workoutUser.toString(),
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
                        WorkoutItem(AllDrunken[index].id,AllDrunken[index].time)
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
                context = this@WorkoutActivity
            )

            PopupAddDialogWorkout(
                showAddFoodDialog = showAddFoodDialog.value,
                onDismiss = {
                    showAddFoodDialog.value = false
                    reloadUI.value = true
                },
                context = this@WorkoutActivity,
            )
            if (reloadUI.value) {
                getWorkoutData()
                reloadUI.value = false
            }
        }
    }


}

@Composable
fun WorkoutItem(id: Int, time: String) {
    Row(
        Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(1f)) {
        Text(text = "$id. @ $time")
    }
}
@SuppressLint("SimpleDateFormat", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupAddDialogWorkout(
    showAddFoodDialog: Boolean,
    onDismiss: () -> Unit,
    context: Context,
) {
    val dbHelper: UserDataDBHelper = UserDataDBHelper(context)

    if (showAddFoodDialog) {
        val Date = Date()
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("new drink") },
            text = {
                Column {
                    Text(text = Date.toString())
                }
            },
            confirmButton = {
                Row() {
                    Button(
                        onClick = {
                           dbHelper.insertWorkoutData(Date.toString())
                            dbHelper.updateData(8, (1 + (dbHelper.getDataById(8)
                                ?.toDouble() ?: 0.0)).toString())
                            Toast.makeText(
                                context,
                                dbHelper.getDataById(8).toString(),
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