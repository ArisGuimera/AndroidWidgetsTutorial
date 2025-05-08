package com.aristidevs.widgetstutorial.pomodoro

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class PomodoroReceiver:GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PomodoroWidget()
}