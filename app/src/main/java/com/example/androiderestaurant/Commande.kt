package com.example.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.androiderestaurant.ui.theme.AndroidERestaurantTheme

class Commande : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getSerializableExtra("dish") as Items
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   // Greeting("${name.nameFr}", item = name)
                    DisplayCommande(category = "${name.nameFr}", item = name)
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String, modifier: Modifier = Modifier, item: Items,
) {
    Column {

    }
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Text(
        text = "Price: ${item.prices[0].price}€", // Assurez-vous que l'élément a au moins un prix
        fontSize = 18.sp,
        color = Color.Black,
        modifier = Modifier.padding(8.dp)
    )
    Text(
        text = "Ingredients: ${item.ingredients.joinToString(", ") { it.nameFr!! }}", // Assurez-vous que chaque ingrédient a un nom en français
        fontSize = 18.sp,
        color = Color.Black,
        modifier = Modifier.padding(8.dp)
    )

}
@Composable
fun DisplayCommande(category: String?, item: Items) {
    val selectedItemIndex = remember { mutableStateOf<Int?>(null)
    }
    val quantity = remember { mutableStateOf(1) } // Default quantity is 1
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Column {
            Text(
                text = "Category: $category",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn {
                itemsIndexed(listOf(item)) { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedItemIndex.value == index) {
                                    selectedItemIndex.value = null // Reset the selected item
                                } else {
                                    selectedItemIndex.value =
                                        index // Open the description if it's closed
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Row {
                            Text(
                            text = "${item.nameFr ?: "No name"} - voir les détails",
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                            Image(
                                painter = painterResource(id = R.drawable.arrow) ,
                                contentDescription = null
                            )
                        }

                            Column {
                                Text(
                                    text = item.nameFr ?: "No name",
                                    fontSize = 24.sp,
                                    color = Color.Black
                                )

                                LazyRow {
                                    items(item.images) { imageUrl ->
                                        SubcomposeAsyncImage(
                                            model = imageUrl,
                                            loading = {
                                                CircularProgressIndicator()
                                            },
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(200.dp) // Set image size to be 200dp
                                                .clip(RectangleShape) // Clip image to be square
                                        )

                                    }

                                }

                                Card(
                                    modifier = Modifier
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Price: ${item.prices.getOrNull(0)?.price ?: "No price"}€",
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                        if (selectedItemIndex.value == index) {
                                        Text(
                                            text = "Ingredients: ${
                                                item.ingredients.joinToString(", ") { it.nameFr ?: "No name" }
                                            }",
                                            fontSize = 18.sp,
                                            color = Color.Black
                                        )
                                    }
                                        Button(onClick = {}) {
                                            Text("Total Price: ${item.prices.getOrNull(0)?.price?.times(quantity.value) ?: "No price"}€")
                                        }

                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview2() {
//    AndroidERestaurantTheme {
//        Greeting("Android")
//    }
//}