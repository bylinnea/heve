package no.bylinnea.heve.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object StepAlarmScheduler {
    private const val REQUEST_CODE = 7001

    fun schedule(context: Context, stepLabel: String, minutes: Int) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = buildIntent(context, stepLabel)
        val triggerAt = System.currentTimeMillis() + minutes * 60_000L

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, intent)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, intent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, intent)
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(buildIntent(context, ""))
    }

    private fun buildIntent(context: Context, stepLabel: String): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, StepAlarmReceiver::class.java).apply {
                putExtra(StepAlarmReceiver.EXTRA_STEP_LABEL, stepLabel)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
