package com.bytebabies.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.bytebabies.app.navigation.ByteBabiesNavGraph
import com.bytebabies.app.ui.theme.ByteBabiesTheme
import com.bytebabies.app.data.Repo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Removed Repo.seed() since we now rely on Firebase
        setContent {
            ByteBabiesTheme {
                ByteBabiesNavGraph()
            }
        }
    }
}
