package com.example.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this
        val fontFamily = FontFamily(
            Font(R.font.font, FontWeight.Thin),

            )
        setContent {

            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    //     MessageCard(nom = "Georgia", msg = "Hello bitches!")
//                    ThreeButtons(activity, ::showToast) { category ->
//                        changeActivity(category)
                    ScaffoldExample(
                        showToast = { message -> showToast(message) },
                        startActivity = { message ->
                            changeActivity(message)
                        }


                    )


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
    fun showToast(message: String) {
        // Affichage du toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample(showToast: (String) -> Unit, startActivity: (String) -> Unit) {
    var presses by remember { mutableIntStateOf(0) }
    val restoItalic = FontFamily(Font(R.font.restoitalic))

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Droid Restaurant",
                        fontFamily = restoItalic
                    )


                }
            )
        },
        bottomBar = {
            BottomAppBar(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Removed click handler */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.insta),
                    contentDescription = "Instagram Logo",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(text = "@Erestaurant", fontFamily = restoItalic)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Removed click handler */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.mail),
                    contentDescription = "Mail Logo",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(text = "Erestaurant@gmail.com", fontFamily = restoItalic)
        }
    }
}
        },



        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MessageCard()
            ThreeButtons(showToast = showToast, startActivity = startActivity)
        }
    }
}


@Composable
fun MessageCard() {

    // Add padding around our message
    Column(modifier = Modifier.padding(all = 8.dp)) {
        Box {
            Image(
                painter = painterResource(R.drawable.image_fond),
                contentDescription = "image jolie",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .alpha(0.8f) // rend l'image semi-transparente
            )
            Text(
                text = "Bienvenu au resto de gg",
                fontSize = 34.sp, // Taille du texte
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.regular)),

                modifier = Modifier.align(Alignment.Center)

            )
        }

    }
}

@Composable
fun ThreeButtons(showToast: (String) -> Unit, startActivity: (String) -> Unit) {
    val restoItalic = FontFamily(Font(R.font.restoitalic))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       Text(
    text = "Menu",
    fontSize = 34.sp, // Taille du texte
    color = Color.Black,
    fontFamily = restoItalic,
    modifier = Modifier.align(Alignment.CenterHorizontally)
)
Spacer(modifier = Modifier.padding(16.dp))


        Button(onClick = {
            showToast("Vous avez cliqué sur Entrée")
            Log.i("HomeActivity", "L'activité HomeActivity est détruite.")
            startActivity("Entrées")
        }, modifier = Modifier.padding(9.dp)) {
            Text(
                text = "Entrée",
                fontFamily = FontFamily(Font(R.font.regular)),

                )
        }

        Button(onClick = {

            showToast("Vous avez cliqué sur Plat")
            Log.i("HomeActivity", "L'activité HomeActivity est détruite.")
            startActivity("Plats")

        }, modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Plat",
                fontFamily = FontFamily(Font(R.font.regular)),
            )
        }

        Button(onClick = {

            showToast("Vous avez cliqué sur Dessert")
            Log.i("HomeActivity", "L'activité HomeActivity est détruite.")
            startActivity("Desserts")
        }, modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Desserts",
                fontFamily = FontFamily(Font(R.font.regular)),
            )
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



