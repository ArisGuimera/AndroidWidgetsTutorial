package com.aristidevs.widgetstutorial.quotewidget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class QuoteWidgetReceiver :GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuoteWidget()
}