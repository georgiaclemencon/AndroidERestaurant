package com.example.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this
        setContent {

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    MessageCard(nom = "Georgia", msg = "Hello bitches!")
                    ThreeButtons(activity,::showToast) { category ->
                        changeActivity(category)
                    }
                }

            }
        }
    }

    private fun changeActivity(message: String) {
        val intent = Intent(this, Activity2::class.java)
        intent.putExtra("category", message)
        this.startActivity(intent)
    }

    // Fonction pour afficher un toast
    fun showToast(context: Context, message: String) {
        // Affichage du toast
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun MessageCard(nom: String, msg: String) {
    // Add padding around our message
    Column(modifier = Modifier.padding(all = 8.dp)) {
        Box {
            Image(
                painter = painterResource(R.drawable.image_fond),
                contentDescription = "image jolie",
                modifier = Modifier
                    // Set image size to 40 dp
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
            Text(
                text = "Bienvenu au resto de gg",
                fontSize = 34.sp, // Taille du texte
                color = Color.White,

                modifier = Modifier.align(Alignment.Center)

            )

            // Add a horizontal space between the image and the column
            //Spacer(modifier = Modifier.width(8.dp))
            Column {//La fonction Column vous permet d'organiser les éléments verticalement. Ajoutez Column à la fonction MessageCard. Vous pouvez utiliser Row pour organiser les éléments horizontalement et Box pour empiler des éléments.


                // Add a vertical space between the author and message texts
                //Spacer(modifier = Modifier.height(4.dp))
                Text(text = nom)
                Text(text = msg)
                //Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Welcome", color = Color.Red)


            }
        }

    }
}

@Composable
fun ThreeButtons(context: Context, showToast: (Context, String) -> Unit, startActivity: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {

            //val intent = Intent(context, Activity2::class.java)
            //intent.putExtra("category", "Entrée")
            //context.startActivity(intent)
            showToast(context, "Vous avez cliqué sur Entrée")
            Log.i("HomeActivity", "L'activité HomeActivity est détruite.")
            startActivity("Entrée")
        }, modifier = Modifier.padding(9.dp)) {
            Text(text = "Entrée")
        }

        Button(onClick = {
            // val intent = Intent(context, Activity2::class.java)
            // intent.putExtra("category", "Plat")
            // context.startActivity(intent)
            showToast(context, "Vous avez cliqué sur Plat")
            Log.i("HomeActivity", "L'activité HomeActivity est détruite.")
            startActivity("Plat")

        }, modifier = Modifier.padding(8.dp)) {
            Text(text = "Plat")
        }

        Button(onClick = {
            // val intent = Intent(context, Activity2::class.java)
            // intent.putExtra("category", "Dessert")
            // context.startActivity(intent)
            showToast(context, "Vous avez cliqué sur Dessert")
            Log.i("HomeActivity", "L'activité HomeActivity est détruite.")
            startActivity("Dessert")
        }, modifier = Modifier.padding(8.dp)) {
            Text(text = "Dessert")
        }
    }
}




// contenu
// accolodae enfant contenu
/*
    @Preview
    @Composable
    fun DefaultPreview() {
        Column(modifier = Modifier.background(Color.White)) {
            MessageCard(nom = "Georgia", msg = "Hello bitches!")
            //ThreeButtons()
        }

    }

 */



