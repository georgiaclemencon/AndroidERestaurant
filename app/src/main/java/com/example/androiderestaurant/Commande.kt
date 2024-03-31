package com.example.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.androiderestaurant.ui.theme.AndroidERestaurantTheme
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter


class Commande : ComponentActivity() {
    private val selectedItems = mutableListOf<Items>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        val name = bundle?.getSerializable("dish") as Items
        selectedItems.add(name) // Add the selected item to the list
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayCommande(
                        category = "${name.nameFr}",
                        item = name,
                        startActivity = { dish -> changeActivity(dish) },
                        onItemSelected = { item -> onItemSelected(item) },
                        goToPanier = { goToPanier() }
                    )
                }
            }
        }
    }

    fun saveData(context: Context, fileName: String, data: Any) {
        val gson = Gson()
        val dataJson = gson.toJson(data)
        val outputStreamWriter =
            OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
        outputStreamWriter.use { it.write(dataJson) }
    }

    fun saveItemCount(context: Context, count: Int) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("item_count", count)
        editor.apply()
    }

    fun onItemSelected(item: Items) {
        selectedItems.add(item)
        saveData(this, "user_cart.json", selectedItems)
        saveItemCount(this, selectedItems.size) // Save the item count
    }

    fun goToPanier() {
        val intent = Intent(this, Panier::class.java)
        intent.putExtra("selectedItems", ArrayList(selectedItems))
        startActivity(intent)
    }

    fun getItemCount(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("item_count", 0)
    }


    private fun changeActivity(message: Items) {
        val intent = Intent(this, Panier::class.java)
        intent.putExtra(
            "Item_panier",
            selectedItems.toTypedArray()
        ) // Pass the selected items to the new activity
        this.startActivity(intent)
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayCommande(
    category: String?, item: Items,
    startActivity: (Items) -> Unit,
    onItemSelected: (Items) -> Unit,
    goToPanier: () -> Unit
) {

    val selectedItemIndex = remember {
        mutableStateOf<Int?>(null)
    }
    val quantity = remember { mutableIntStateOf(1) } // Default quantity is 1
    var ite = remember { mutableIntStateOf(1) }
    var qty = remember { mutableIntStateOf(1) }


    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }




    Scaffold(

        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        ) { innerPadding ->
        Card(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                item {
                    Text(
                        text = "Category: $category",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
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


                        Column {
                            Text(
                                text = item.nameFr ?: "No name",
                                fontSize = 24.sp,
                                color = Color.Black
                            )

                            val pagerState = rememberPagerState(pageCount = {
                                item.images.size
                            })
                            HorizontalPager(state = pagerState) { index ->
                                SubcomposeAsyncImage(
                                    model = item.images[index],
                                    loading = {
                                        CircularProgressIndicator()
                                    },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(200.dp) // Set image size to be 200dp
//                                    .clip(RectangleShape) // Clip image to be square
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {


                                Text(
                                    text = "Ingredients: ",
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.arrow),
                                    contentDescription = null
                                )

                                if (selectedItemIndex.value == index) {
                                    Text(
                                        text = item.ingredients.joinToString(", ") {
                                            it.nameFr ?: "No name"
                                        },
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )


                                }
                                Text(
                                    text = "Price: ${item.prices.getOrNull(0)?.price ?: "No price"}€",
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }


                    Row {
                        Button(onClick = {
                            onItemSelected(item)
                            goToPanier()

                        }) {

                            Text("Voir mon panier")
                        }


                        Box {
                            Button(onClick = {
                                scope.launch {
                                    onItemSelected(item)
                                    snackbarHostState.showSnackbar("Item ajouté au panier")
                                }
                            }) {
                                val itemPrice = item.prices.getOrNull(0)?.price?.toIntOrNull() ?: 0
                                Text("Commander: ${itemPrice * quantity.intValue}€")
                            }
                        }

                    }
                    QuantitySelector(item, quantity)

                }


            }
        }

    }
}


@Composable
fun QuantitySelector(item: Items, quantity: MutableState<Int>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.purple_200),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { if (quantity.value > 1) quantity.value-- }) {
                Icon(Icons.Filled.Remove, contentDescription = "Decrease")
            }

            Text(
                text = quantity.value.toString(),
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(onClick = { quantity.value++ }) {
                Icon(painterResource(id = R.drawable.add), contentDescription = "Add")
            }
        }
    }
}