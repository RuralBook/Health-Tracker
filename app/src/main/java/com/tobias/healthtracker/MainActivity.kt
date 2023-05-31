package com.tobias.healthtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobias.healthtracker.ui.theme.HealthTrackerTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class MainActivity : ComponentActivity() {

    var i = 0
    private lateinit var USER_NAME: String
    private var UserDataGoals = UserGoals(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    private var waterPercentage by Delegates.notNull<Float>()
    private var kalorienPercentage by Delegates.notNull<Float>()
    private var workoutPercentage by Delegates.notNull<Float>()

    private lateinit var dbHelper: UserDataDBHelper
    lateinit var lastLogin: String
    private val timeFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY)
    private val currentDay: String = timeFormat.format(Date())
    var checkDay by Delegates.notNull<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDataDBHelper(this)
        lastLogin = dbHelper.getDataById(9).toString()
        checkDay = lastLogin == currentDay

        //dbHelper.updateData(8, "5")
        USER_NAME = dbHelper.getDataById(1).toString()
        getDasboardData()
        System.gc()
        setupView(i)
    }

    override fun onResume() {
        super.onResume()
        i++
        dbHelper = UserDataDBHelper(this)
        if(checkDay){
            dbHelper.insertData(currentDay)
            dbHelper.clearTable("kalorien_this_day")
            dbHelper.clearTable("water_this_day")
        }
       // dbHelper.updateData(8, "5")
        USER_NAME = dbHelper.getDataById(1).toString()
        getDasboardData()
        System.gc()
        setupView(i)
    }


    fun getDasboardData() {
        //Get Water Data
        UserDataGoals.waterGoal = dbHelper.getDataById(3)?.toDouble() ?: 1.0
        UserDataGoals.waterUser = dbHelper.getDataById(4)?.toDouble() ?: 0.0
        waterPercentage = (UserDataGoals.waterUser / UserDataGoals.waterGoal).toFloat()

        //Get Kalorien
        UserDataGoals.kalorienGoal = dbHelper.getDataById(5)?.toDouble() ?: 1.0
        UserDataGoals.kalorienUser = dbHelper.getDataById(6)?.toDouble() ?: 0.0
        kalorienPercentage = (UserDataGoals.kalorienUser / UserDataGoals.kalorienGoal).toFloat()

        //get Workouts
        UserDataGoals.workoutGoal = dbHelper.getDataById(7)?.toDouble() ?: 1.0
        UserDataGoals.workoutUser = dbHelper.getDataById(8)?.toDouble() ?: 0.0
        workoutPercentage = ((UserDataGoals.workoutUser / UserDataGoals.workoutGoal)).toFloat()
    }

    private fun setupView(trigger: Int) {
        setContent {
            val showDialog = remember { mutableStateOf(false) }


            val refreshTrigger by remember { mutableStateOf(trigger) }

            // Call this function to refresh the data and trigger recomposition
            fun refreshData() {
                getDasboardData()
            }
            LaunchedEffect(refreshTrigger) {
                refreshData()
            }

            HealthTrackerTheme() {
                if(dbHelper.getDataById(1) == null){
                    showDialog.value = true
                }
                // Main Circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    Text(
                        text = "Hi $USER_NAME",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.offset(y = 25.dp)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .offset(y = 25.dp)
                            .height(175.dp)
                    ) {
                        ProgressCircle(
                            percentage = workoutPercentage,
                            number = UserDataGoals.workoutGoal,
                            color = "#FF0000".color,
                            colorTrans = "#80FF0000".color,
                            75.dp,
                            textColor = Color.White,
                            description = "Workouts",
                            onClick = {
                                Toast.makeText(
                                    this@MainActivity,
                                    UserDataGoals.workoutGoal.toString() + " " + UserDataGoals.workoutUser,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(this@MainActivity, Camera::class.java)
                                startActivity(intent)
                            })
                    }

                    // 2.Row
                    Row(modifier = Modifier.fillMaxWidth(1f)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(190.dp)
                                .padding(start = 25.dp)
                                .wrapContentWidth(Alignment.Start)
                        ) {
                            ProgressCircle(
                                percentage = kalorienPercentage,
                                number = UserDataGoals.kalorienGoal,
                                color = "#FFA500".color,
                                colorTrans = "#80FFA500".color,
                                55.dp,
                                textColor = Color.Black,
                                description = "Kalorien",
                                onClick = {
                                    val intent =
                                        Intent(this@MainActivity, KalorienActivity::class.java)
                                    intent.putExtra("i", i)
                                    startActivity(intent)
                                })
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(190.dp)
                                .background(Color.Transparent)
                                .padding(end = 25.dp)
                                .wrapContentWidth(Alignment.End)
                        ) {
                            ProgressCircle(
                                percentage = waterPercentage,
                                number = UserDataGoals.waterGoal,
                                color = "#FF1AA7EC".color,
                                colorTrans = "#801AA7EC".color,
                                radius = 55.dp,
                                textColor = Color.Black,
                                description = "Wasser (L)",
                                onClick = {
                                    val intent =
                                        Intent(this@MainActivity,WaterActivity::class.java)
                                    intent.putExtra("i", i)
                                    startActivity(intent)

                                })
                        }
                    }
                    Button(
                        onClick = { dbHelper.debugTable() }) {
                        i++
                        Text(currentDay)
                    }

                }
                InsertName(showDialog = showDialog.value,
                    onDismiss = {
                        showDialog.value = false
                        USER_NAME = dbHelper.getDataById(1).toString()
                                },
                    context = this@MainActivity)
            }
        }

    }
}