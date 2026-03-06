package com.enterprise.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.enterprise.app.testui.GoalRingScreen
import com.enterprise.app.theme.EnterpriseTheme

class TestUiActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EnterpriseTheme {
                GoalRingScreen()
            }
        }
    }
}