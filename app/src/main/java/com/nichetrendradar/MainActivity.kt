package com.nichetrendradar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nichetrendradar.ui.navigation.NavGraph
import com.nichetrendradar.ui.theme.NicheTrendRadarTheme
import com.nichetrendradar.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NicheTrendRadarTheme {
                val vm: MainViewModel = viewModel()
                NavGraph(vm)
            }
        }
    }
}