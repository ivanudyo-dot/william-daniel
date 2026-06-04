package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GamingViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Instantiate GamingViewModel
    val viewModel = ViewModelProvider(this)[GamingViewModel::class.java]
    
    setContent {
      MyApplicationTheme {
        MainAppScreen(viewModel = viewModel)
      }
    }
  }
}
