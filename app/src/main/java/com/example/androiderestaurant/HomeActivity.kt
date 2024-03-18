package com.example.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    MessageCard(Message("Georgia", "Hello bitches!"))
                }

            }
        }
    }


@Composable
fun MessageCard(name: String) {
    Text(text = "Hello $name!")
}


@Composable
fun MessageCard(msg: Message) {
    // Add padding around our message
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.image_fond),
            contentDescription = "image jolie",
            modifier = Modifier
                // Set image size to 40 dp
                .size(40.dp)
                // Clip image to be shaped as a circle
                .clip(CircleShape)
        )
        // Add a horizontal space between the image and the column
        Spacer(modifier = Modifier.width(8.dp))
        Column {//La fonction Column vous permet d'organiser les éléments verticalement. Ajoutez Column à la fonction MessageCard. Vous pouvez utiliser Row pour organiser les éléments horizontalement et Box pour empiler des éléments.


            Text(text = msg.author)
            // Add a vertical space between the author and message texts
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = msg.body)
        }
    }

}


// contenu
// accolodae enfant contenu




data class Message(val author: String, val body: String)

