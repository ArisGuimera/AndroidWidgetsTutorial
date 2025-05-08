package com.aristidevs.widgetstutorial.basic

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class BasicWidgetReceiver :GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BasicWidget()
}