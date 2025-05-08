package com.aristidevs.widgetstutorial.pomodoro

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aristidevs.widgetstutorial.pomodoro.PomodoroStateKeys.isCompleted
import com.aristidevs.widgetstutorial.pomodoro.PomodoroStateKeys.isRunning
import com.aristidevs.widgetstutorial.pomodoro.PomodoroStateKeys.isWorkTime
import com.aristidevs.widgetstutorial.pomodoro.PomodoroStateKeys.secondsLeft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PomodoroStateKeys {
    val secondsLeft = intPreferencesKey("secondsLeft")
    val isRunning = booleanPreferencesKey("isRunning")
    val isWorkTime = booleanPreferencesKey("isWorkTime")
    val isCompleted = booleanPreferencesKey("isCompleted")
    val lastScheduledTime = longPreferencesKey("lastScheduledTime")
}

class PomodoroWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = currentState<Preferences>()
            val totalSeconds = state[secondsLeft] ?: 1500
            val isRunning = state[isRunning] ?: false
            val isWorkTime = state[isWorkTime] ?: true
            val isComplete = state[isCompleted] ?: false

            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60

            val backgroundColor = if (isComplete) ColorProvider(Color.DarkGray)
            else if (isWorkTime) ColorProvider(Color(0xFFFF5722)) else ColorProvider(Color.Green)

            Row(
                modifier = GlanceModifier.fillMaxSize().background(backgroundColor).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isComplete) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "¡Se acabó el TIEMPO!", style = TextStyle(
                                fontSize = 24.sp,
                                color = ColorProvider(Color.White),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    if (!isRunning) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button("▲", onClick = actionRunCallback<IncreaseMinutesAction>())
                            Spacer(GlanceModifier.height(8.dp))
                            Button("▼", onClick = actionRunCallback<DecreaseMinutesAction>())
                        }
                        Spacer(GlanceModifier.width(16.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            style = TextStyle(fontSize = 32.sp)
                        )
                        Spacer(GlanceModifier.height(16.dp))
                        if (isRunning) {
                            Button("Pausar", onClick = actionRunCallback<PausePomodoroAction>())
                        } else {
                            Button("Iniciar", onClick = actionRunCallback<StartPomodoroAction>())
                        }
                        Spacer(GlanceModifier.height(8.dp))
                        Button("Reiniciar", onClick = actionRunCallback<ResetPomodoroAction>())
                    }


                }
            }
        }
    }
}

class IncreaseMinutesAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentSeconds = prefs[secondsLeft] ?: 60
            prefs[secondsLeft] = currentSeconds + 60
        }
        PomodoroWidget().update(context, glanceId)
    }

}

class DecreaseMinutesAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentSeconds = prefs[secondsLeft] ?: 60
            if (currentSeconds > 60) {
                prefs[secondsLeft] = currentSeconds - 60
            } else {
                prefs[secondsLeft] = 0
            }
        }
        PomodoroWidget().update(context, glanceId)
    }

}

class PausePomodoroAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[isRunning] = false
        }
        PomodoroWidget().update(context, glanceId)
        cancelPomodoroAlarm(context)
    }

}

class ResetPomodoroAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[isRunning] = false
            prefs[isWorkTime] = true
            prefs[isCompleted] = false
            prefs[secondsLeft] = 25 * 60
        }
        PomodoroWidget().update(context, glanceId)
        cancelPomodoroAlarm(context)
    }

}

class StartPomodoroAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[secondsLeft] = (prefs[secondsLeft]?.div(60) ?: 25) * 60
            prefs[isRunning] = true
            prefs[isCompleted] = false
        }
        PomodoroWidget().update(context, glanceId)
        startPomodoroAlarm(context)
    }

}

fun startPomodoroAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return
        }
    }

    val intent = Intent(context, PomodoroAlarmReceiver::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerAtMillis = System.currentTimeMillis() + 5000L
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
    )

    CoroutineScope(Dispatchers.IO).launch {
        val glanceManager = GlanceAppWidgetManager(context)
        val glanceIds = glanceManager.getGlanceIds(PomodoroWidget::class.java)

        glanceIds.forEach {
            updateAppWidgetState(context, it) { prefs ->
                prefs[PomodoroStateKeys.lastScheduledTime] = triggerAtMillis
            }
        }
    }

}

fun cancelPomodoroAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, PomodoroAlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)
}