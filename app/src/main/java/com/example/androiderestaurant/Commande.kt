package com.example.androiderestaurant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStreamWriter
import java.net.MalformedURLException
import java.net.URL


class Commande : ComponentActivity() {
    private val selectedItems = mutableListOf<Items>()
    private lateinit var sharedPrefManager: SharedPrefManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        val name = bundle?.getSerializable("dish") as Items

        selectedItems.add(name) // Add the selected item to the list
        readFromJSONFile(selectedItems)

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
                        sharedPrefManager = sharedPrefManager,
                        TopBar = { goToPanier, sharedPrefManager ->
                            TopBar(
                                goToPanier,
                                sharedPrefManager
                            )
                        }
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
        // Vérifiez si l'article est déjà dans le panier
        if (!selectedItems.contains(item)) {
            // Si l'article n'est pas dans le panier, augmentez le nombre d'articles dans le panier
            sharedPrefManager.incrementCartItemCount()

            // Ajoutez l'article sélectionné à la liste des articles sélectionnés
            selectedItems.add(item)

            // Ajoutez l'élément au fichier JSON
            addToJSONFile(item)

            // Mettez à jour le nombre d'articles dans le panier
            sharedPrefManager.saveCartItemCount(selectedItems.size)

            // Sauvegardez les informations de l'article
            sharedPrefManager.saveItemInfo(item, quantity)

            // Affichez un Toast pour informer l'utilisateur que l'article a été ajouté au panier
            Toast.makeText(this, "Item ajouté au panier", Toast.LENGTH_SHORT).show()
        } else {
            // Si l'article est déjà dans le panier, augmentez la quantité de cet article dans le panier.
            val existingItem = selectedItems.find { it == item }
            existingItem?.quantity = existingItem?.quantity?.plus(1)!!

            // Mettez à jour le nombre d'articles dans le panier
            sharedPrefManager.saveCartItemCount(selectedItems.size)

            // Sauvegardez les informations de l'article
            sharedPrefManager.saveItemInfo(item, existingItem.quantity ?: 0)

            // Affichez un Toast pour informer l'utilisateur que la quantité de l'article a été augmentée
            Toast.makeText(this, "La quantité de l'article a été augmentée", Toast.LENGTH_SHORT)
                .show()
        }
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


    private fun addToJSONFile(item: Items) {
        // Créez une instance de la classe Gson
        val gson = Gson()

        // Obtenez le répertoire de l'application
        val directory = this.filesDir

        // Créez un fichier dans ce répertoire
        val file = File(directory, "someFileName.json")

        // Créez une liste pour stocker les éléments
        val itemsList: MutableList<Items>

        // Vérifiez si le fichier existe
        if (file.exists()) {
            // Si le fichier existe, lisez son contenu
            val jsonString = file.readText()

            // Convertissez la chaîne JSON en une liste d'éléments
            val itemType = object : TypeToken<List<Items>>() {}.type
            itemsList = gson.fromJson(jsonString, itemType)

            // Ajoutez le nouvel élément à la liste
            itemsList.add(item)
        } else {
            // Si le fichier n'existe pas, créez une nouvelle liste contenant uniquement le nouvel élément
            itemsList = mutableListOf(item)
        }

        // Convertissez la liste mise à jour en une chaîne JSON
        val updatedJsonString = gson.toJson(itemsList)

        // Écrivez la chaîne JSON dans le fichier
        file.writeText(updatedJsonString)
    }

    private fun readFromJSONFile(basketItems: MutableList<Items>) {
        // Créez une instance de la classe Gson
        val gson = Gson()

        // Obtenez le répertoire de l'application
        val directory = this.filesDir


        // Créez un fichier dans ce répertoire
        val file = File(directory, "someFileName.json")

        // Vérifiez si le fichier existe
        if (file.exists()) {
            // Si le fichier existe, lisez son contenu
            val jsonString = file.readText()

            // Convertissez la chaîne JSON en une liste d'éléments
            val itemType = object : TypeToken<List<Items>>() {}.type
            basketItems.clear()
            basketItems.addAll(gson.fromJson(jsonString, itemType))
        }

    }


}

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(goToPanier: () -> Unit, sharedPrefManager: SharedPrefManager) {
    TopAppBar(
        title = { Text(text = "Commander", style = MyTypography.h1) },
        actions = {
            IconButton(onClick = { goToPanier() }) {
//                CartIconWithCount(sharedPrefManager)
                TabBarIconView(
                    isSelected = true, // Set this based on your logic
                    title = "Cart",
                    badgeAmount = sharedPrefManager.getCartItemCount(),
                    sharedPrefManager = sharedPrefManager
                )
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DisplayCommande(
    category: String?, item: Items,
    onItemSelected: (Items) -> Unit,
    goToPanier: () -> Unit,
    sharedPrefManager: SharedPrefManager,
    TopBar: @Composable (goToPanier: () -> Unit, sharedPrefManager: SharedPrefManager) -> Unit
) {

    val selectedItemIndex = remember {
        mutableStateOf<Int?>(null)
    }
    val quantity = remember { mutableIntStateOf(1) }


    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = { TopBar(goToPanier, sharedPrefManager) }


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
                        sharedPrefManager,
                        addToJSONFile = { item ->
                            scope.launch {
                                sharedPrefManager.saveItemInfo(item, quantity.value)
                            }
                        }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    title: String,
    badgeAmount: Int, // Change this to Int
    sharedPrefManager: SharedPrefManager // Add this parameter
) {
    val shoppingIcon = painterResource(R.drawable.shopping) // Replace this with your "shopping" icon

    val cartItemCount = sharedPrefManager.getCartItemCount() // Get the cart item count

    BadgedBox(
        badge = { TabBarBadgeView(cartItemCount) }, // Use cartItemCount here
        modifier = Modifier.offset(x = -10.dp, y = 10.dp) // Add an offset here
    ) {
        Icon(
            painter = shoppingIcon,
            contentDescription = title
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

//@Composable
//fun CartIconWithCount(sharedPrefManager: SharedPrefManager) {
//    val cartItemCount = remember { mutableStateOf(0) }
//
//    LaunchedEffect(key1 = sharedPrefManager) {
//        cartItemCount.value = sharedPrefManager.getCartItemCount()
//    }
//
//    Box {
//        Icon(
//            painter = painterResource(id = R.drawable.shopping),
//            contentDescription = "Cart"
//        )
//        if (cartItemCount.value > 0) {
//            Text(
//                text = cartItemCount.value.toString(),
//                color = Color.Red,
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .offset(x = 6.dp, y = -6.dp)
//                    .background(Color.White, CircleShape)
//                    .padding(horizontal = 4.dp)
//            )
//        }
//    }
//}

fun onItemAdded(
    item: Items,
    selectedItems: MutableState<List<Items>>,
    sharedPrefManager: SharedPrefManager
) {
    // Check if the item is already in the cart
    if (!selectedItems.value.contains(item)) {
        // If the item is not in the cart, add it
        selectedItems.value = selectedItems.value + item
    } else {
        // If the item is already in the cart, increase its quantity
        // You need to implement this part based on how you're storing the quantity of each item
    }

    // Update the number of items in the cart
    sharedPrefManager.saveCartItemCount(selectedItems.value.size)
}

fun IsValidUrl(urlString: String?): Boolean {

    try {
        val url = URL(urlString)
        return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches()
    } catch (ignored: MalformedURLException) {
    }
    return false
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
    item: Items,
    selectedItemIndex: MutableState<Int?>,
    index: Int,
    onItemSelected: (Items) -> Unit,
    quantity: MutableState<Int>,
    goToPanier: () -> Unit = { /* default implementation here */ },
    sharedPrefManager: SharedPrefManager,
    addToJSONFile: (Items) -> Unit
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
                val imageUrl = item.images.firstOrNull()
                if (imageUrl != null) {
                    if (IsValidUrl(imageUrl)) {
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            loading = {
                                CircularProgressIndicator()
                            },
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp) // Set image size to be 200dp
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.image_not_available),
                            contentDescription = "Image par défaut",
                            modifier = Modifier
                                .size(200.dp) // Set image size to be 200dp
                        )
                    }
                }
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
                    addToJSONFile(item)

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



