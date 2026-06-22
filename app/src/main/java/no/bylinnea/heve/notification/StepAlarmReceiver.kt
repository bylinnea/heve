package no.bylinnea.heve.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StepAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stepLabel = intent.getStringExtra(EXTRA_STEP_LABEL) ?: "step"
        NotificationHelper.postStepDone(context, stepLabel)
    }

    companion object {
        const val EXTRA_STEP_LABEL = "step_label"
    }
}
