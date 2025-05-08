package com.aristidevs.widgetstutorial.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PomodoroAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val glanceManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceManager.getGlanceIds(PomodoroWidget::class.java)
            glanceIds.forEach { glanceId ->
                val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                val mutablePrefs = prefs.toMutablePreferences()
                val secondsLeft = (prefs[PomodoroStateKeys.secondsLeft] ?: 0) - 5

                //delay
                val now = System.currentTimeMillis()
                val last = prefs[PomodoroStateKeys.lastScheduledTime] ?: now
                val delay = now-last
                Log.i("POMODORO", "Delay: $delay")

                if (secondsLeft <= 0) {
                    mutablePrefs[PomodoroStateKeys.secondsLeft] = 0
                    mutablePrefs[PomodoroStateKeys.isRunning] = false
                    mutablePrefs[PomodoroStateKeys.isCompleted] = true
                    cancelPomodoroAlarm(context)
                } else {
                    mutablePrefs[PomodoroStateKeys.secondsLeft] = secondsLeft
                    startPomodoroAlarm(context)
                }

                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
                    mutablePrefs
                }

                PomodoroWidget().update(context, glanceId)
            }
        }
    }
}