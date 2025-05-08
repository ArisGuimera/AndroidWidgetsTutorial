package com.aristidevs.widgetstutorial.basic

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class BasicWidget :GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
       provideContent {
           MyContent()
       }
    }

    @Composable
    private fun MyContent(){
        Column {
            Text("AristiDevs", style = TextStyle(color = ColorProvider(Color.Green)))
            Text("Tu dev Favorito")
        }
    }

}