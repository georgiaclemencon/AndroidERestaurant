package com.example.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.androiderestaurant.ui.theme.AndroidERestaurantTheme
import com.example.androiderestaurant.ui.theme.SharedPrefManager
import com.google.gson.Gson
import java.io.OutputStreamWriter


class Commande : ComponentActivity() {
    private val selectedItems = mutableListOf<Items>()
    private lateinit var sharedPrefManager: SharedPrefManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        val name = bundle?.getSerializable("dish") as Items

        selectedItems.add(name) // Add the selected item to the list

        // Initialize sharedPrefManager before setContent
        sharedPrefManager = SharedPrefManager(this)

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
                        onItemSelected = { item -> onItemSelected(item, 1) },
                        goToPanier = { goToPanier() },
                        sharedPrefManager = sharedPrefManager
                    )
                }
            }
        }
    }

    fun saveData(context: Context, fileName: String, data: Any) {
        val gson = Gson()
        val dataJson = gson.toJson(data)
        val outputStreamWriter =
            OutputStreamWriter(context.openFileOutput(fileName, MODE_PRIVATE))
        outputStreamWriter.use { it.write(dataJson) }
    }

    fun saveItemCount(context: Context, count: Int) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("item_count", count)
        editor.apply()
    }

    fun onItemSelected(item: Items, quantity: Int) {
    // Vérifiez si l'article est déjà dans la liste
    val existingItem = selectedItems.find { it.id == item.id }
    if (existingItem == null) {
        // Si l'article n'est pas dans la liste, augmentez le nombre d'articles dans le panier
        sharedPrefManager.incrementCartItemCount()
        // Ajoutez l'article sélectionné à la liste des articles sélectionnés
        selectedItems.add(item)
    } else {
        // Si l'article est déjà dans la liste, mettez à jour la quantité
        // Vous pouvez ajouter une logique ici pour mettre à jour la quantité si nécessaire
    }

    // Convertissez la liste des articles sélectionnés en JSON
    val gson = Gson()
    val selectedItemsJson = gson.toJson(selectedItems)

    // Écrivez le JSON dans un fichier
    val fileName = "user_cart.json"
    val fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
    fileOutputStream.use { it.write(selectedItemsJson.toByteArray()) }

    // Mettez à jour le nombre d'articles dans le panier
    sharedPrefManager.saveCartItemCount(selectedItems.size)

    // Sauvegardez les informations de l'article
    sharedPrefManager.saveItemInfo(item, quantity)

    // Affichez un Toast pour informer l'utilisateur que l'article a été ajouté au panier
    Toast.makeText(this, "Item ajouté au panier", Toast.LENGTH_SHORT).show()
}


    fun goToPanier() {
        val intent = Intent(this, Panier::class.java)
        intent.putExtra("selectedItems", ArrayList(selectedItems))
        intent.putExtra(
            "selectedItemNames",
            ArrayList(selectedItems.map { it.nameFr })
        ) // Pass the item names
        startActivity(intent)
    }

    fun getItemCount(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", MODE_PRIVATE)
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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DisplayCommande(
    category: String?, item: Items,
    onItemSelected: (Items) -> Unit,
    goToPanier: () -> Unit,
    sharedPrefManager: SharedPrefManager // Corrected parameter name
) {

    val selectedItemIndex = remember {
        mutableStateOf<Int?>(null)
    }
    val quantity = remember { mutableIntStateOf(1) } // Default quantity is 1


    val scope = rememberCoroutineScope()



    Scaffold(
        topBar = { TopBar(goToPanier, sharedPrefManager) },


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
                    ItemCard(
                        item,
                        selectedItemIndex,
                        index,
                        onItemSelected,
                        quantity,
                        goToPanier,
                        sharedPrefManager
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
val RestoItalic = FontFamily(Font(resId = R.font.restoitalic))

@RequiresApi(Build.VERSION_CODES.O)
val MyTypography = androidx.compose.material.Typography(
    h1 = androidx.compose.ui.text.TextStyle(
        fontFamily = RestoItalic,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    )

)


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(goToPanier: () -> Unit, sharedPrefManager: SharedPrefManager) {

    TopAppBar(

        title = { Text(text = "Commander", style = MyTypography.h1) },
        actions = {
            IconButton(onClick = { goToPanier() }) {
                CartIconWithCount(sharedPrefManager)
            }
        }
    )
}


@Composable
fun CartIconWithCount(sharedPrefManager: SharedPrefManager) {
    Box {
        Icon(
            painter = painterResource(id = R.drawable.shopping),
            contentDescription = "Cart"
        )
        val cartItemCount = sharedPrefManager.getCartItemCount()
        if (cartItemCount > 0) {
            Text(
                text = cartItemCount.toString(),
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = -6.dp)
                    .background(Color.White, CircleShape)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    item: Items, selectedItemIndex: MutableState<Int?>,
    index: Int, onItemSelected: (Items) -> Unit,
    quantity: MutableState<Int>,
    goToPanier: () -> Unit = { /* default implementation here */ },
    sharedPrefManager: SharedPrefManager
) {
    val scope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (selectedItemIndex.value == index) {
                    selectedItemIndex.value = null // Reset the selected item
                } else {
                    selectedItemIndex.value = index // Open the description if it's closed
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
                        text = item.ingredients.joinToString(", \n") {
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



            Row {
                Button(onClick = {
                    onItemSelected(item)
                    goToPanier()

                }) {

                    Text("Voir mon panier")
                }
                Button(onClick = {

                    onItemSelected(item)

                })

                {
                    Text("Ajouter au panier")
                }

            }

            QuantitySelector(item, quantity)
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
