package com.example.kotlin_apk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kotlin_apk.ui.theme.KotlinapkTheme
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinapkTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ResponsiveProfile(
                        fullname = "Alshanqiti Mohammed",
                        description = "Étudiant",
                        email = "mohammed.alshanqiti2@gmail.com",
                        linkedin = "https://www.linkedin.com/in/alshanqiti-mohammed2/"
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveProfile(fullname: String, description: String, email: String, linkedin: String) {
    val configuration = LocalConfiguration.current
    if (configuration.screenWidthDp < 600) {
        // petit
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProfileImage()
            ProfileInfo(fullname, description)
            ContactInfo(email, linkedin)
            StartButton()
        }
    } else {
        // grand
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage()
            Spacer(modifier = Modifier.width(80.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ProfileInfo(fullname, description)
                ContactInfo(email, linkedin)
                StartButton()
            }
        }
    }
}

@Composable
fun ProfileImage() {
    AsyncImage(
        model = "https://th.bing.com/th/id/OIP.4yQU3p-Cx4P_mLF7QcesFwAAAA?w=269&h=269&rs=1&pid=ImgDetMain",
        contentDescription = "PP",
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .border(3.dp, Color.Gray, CircleShape)
    )
}

@Composable
fun ProfileInfo(fullname: String, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = fullname,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 30.sp,
            modifier = Modifier.padding(12.dp)
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ContactInfo(email: String, linkedin: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.baseline_email_24),
                contentDescription = "Email Icon",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.linkedin_icon_svg),
                contentDescription = "LinkedIn Icon",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = linkedin,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StartButton() {
    val context = LocalContext.current

    Button(
        onClick = { navigateToMainScreen(context) },
        shape = MaterialTheme.shapes.medium
    ) {
        Text("Démarrer")
    }
}

fun navigateToMainScreen(context: Context) {
    val intent = Intent(context, MainScreenActivity::class.java)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun PreviewResponsiveProfile() {
    KotlinapkTheme {
        ResponsiveProfile(
            fullname = "Alshanqiti Mohammed",
            description = "Étudiant",
            email = "mohammed.alshanqiti2@gmail.com",
            linkedin = "https://www.linkedin.com/in/alshanqiti-mohammed2/"
        )
    }
}

fun Modifier.negativePadding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp) = this.then(
    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = -horizontal.roundToPx(), y = -vertical.roundToPx())
        }
    }
)
