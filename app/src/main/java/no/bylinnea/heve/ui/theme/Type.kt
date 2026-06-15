package no.bylinnea.heve.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.R

val Bricolage = FontFamily(                                                             // brand + every number
    Font(R.font.bricolage_grotesque, FontWeight.Bold),         // 700 — titles, slider values
    Font(R.font.bricolage_grotesque, FontWeight.ExtraBold),             // 800 — gram weights, the timer
)

val Hanken = FontFamily(                                                        // anything you read
    Font(R.font.hanken_grotesk, FontWeight.Normal),    // 400 — body
    Font(R.font.hanken_grotesk, FontWeight.Medium),             // 500 — meta
    Font(R.font.hanken_grotesk, FontWeight.SemiBold),           // 600 — field labels
    Font(R.font.hanken_grotesk, FontWeight.Bold),               // 700 — row titles, section headers
)
val Typography = Typography(
    titleLarge = TextStyle(fontFamily = Bricolage, fontWeight = FontWeight.Bold,    fontSize = 22.sp),
    bodyLarge  = TextStyle(fontFamily = Hanken,    fontWeight = FontWeight.Normal,  fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Hanken,    fontWeight = FontWeight.Normal,  fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = Hanken,    fontWeight = FontWeight.SemiBold,fontSize = 15.sp),
)