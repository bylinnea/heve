package no.bylinnea.heve

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import no.bylinnea.heve.ui.screens.RecipeScreen
import no.bylinnea.heve.ui.theme.HeveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeveTheme {
                RecipeScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "heve",
        style = MaterialTheme.typography.titleLarge
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HeveTheme {
        Greeting("Android")
    }
}