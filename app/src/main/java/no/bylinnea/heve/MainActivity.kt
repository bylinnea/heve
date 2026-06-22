package no.bylinnea.heve

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import no.bylinnea.heve.notification.NotificationHelper
import no.bylinnea.heve.ui.navigation.HeveNavGraph
import no.bylinnea.heve.ui.theme.HeveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        enableEdgeToEdge()
        setContent {
            HeveTheme {
                HeveNavGraph()
            }
        }
    }
}
