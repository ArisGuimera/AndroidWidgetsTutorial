package com.aristidevs.widgetstutorial.quotewidget

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aristidevs.widgetstutorial.quotewidget.data.QuoteRepository

object QuotePrefs {
    val quote = stringPreferencesKey("quote")
}

class QuoteWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<Preferences>()
            val currentQuote = state[QuotePrefs.quote]
            Box(
                modifier = GlanceModifier.clickable(actionRunCallback<GetQuoteCallback>())

                    .padding(4.dp)
            ) {
                Text(currentQuote ?: "La felicidad es SUSCRIBIRSE", style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ))
            }
        }
    }
}

class GetQuoteCallback : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val repository = QuoteRepository()
            val quote = repository.getRandomQuote()

            Log.i("AristiError", "quote: ${quote}")

            updateAppWidgetState(context, glanceId) { pref: MutablePreferences ->
                pref[QuotePrefs.quote] = quote
            }

//        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId){ a: Preferences ->
//            val pref = a.toMutablePreferences()
//            pref[QuotePrefs.quote] = quote
//            //guardamos
//            pref
//        }

            QuoteWidget().update(context, glanceId)
        } catch (e: Exception) {
            Log.i("AristiError", "error: ${e.message}")
        }

    }

}

