package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.Navigation
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Surface(modifier = Modifier.fillMaxSize()) {
            val application = (LocalContext.current.applicationContext as MarbleApplication)
            val viewModel: MainViewModel = viewModel(
              factory = MainViewModelFactory(application.repository)
            )
            Navigation(viewModel = viewModel)
          }
        }
      }
    }
  }
}

