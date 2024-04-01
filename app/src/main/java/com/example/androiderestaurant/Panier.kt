package com.example.androiderestaurant

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiderestaurant.ui.theme.AndroidERestaurantTheme
import com.example.androiderestaurant.ui.theme.SharedPrefManager

class Panier : ComponentActivity() {

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedItems = intent.getSerializableExtra("selectedItems") as? ArrayList<Items>
        val sharedPrefManager = SharedPrefManager(this)
        val selectedItemNames = intent.getStringArrayListExtra("selectedItemNames")
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PanierScreen(
                        selectedItems = mutableStateOf(selectedItems ?: emptyList()),
                        selectedItemNames = selectedItemNames ?: emptyList(), // Pass the item names
                        category = "",
                        sharedPrefManager = sharedPrefManager,
                        onOrderPlaced = {
                            // Place the order
                            placeOrder(selectedItems, sharedPrefManager)
                        },
                        onItemRemoved = { item, selectedItems, sharedPrefManager ->
                            onItemRemoved(item, selectedItems, sharedPrefManager)
                        }
                    )
                }
            }
        }
    }

    private fun placeOrder(selectedItems: ArrayList<Items>?, sharedPrefManager: SharedPrefManager) {
        // Place the order
        // Clear the cart
        sharedPrefManager.saveCartItemCount(0)
        selectedItems?.forEach { item ->
            sharedPrefManager.saveItemInfo(item, 0)
        }
    }

    fun onItemRemoved(
        item: Items,
        selectedItems: MutableState<List<Items>>,
        sharedPrefManager: SharedPrefManager
    ) {
        // Remove the item from the list of selected items
        selectedItems.value = selectedItems.value.filter { it != item }

        // Update the number of items in the cart
        sharedPrefManager.saveCartItemCount(selectedItems.value.size)

        // Remove the item info from shared preferences
        sharedPrefManager.saveItemInfo(item, 0)

        // Display a toast to inform the user that the item has been removed from the cart
        val message = "Item retiré du panier"
        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show() // Uncomment this if you have a context
    }
}

@Composable
fun PanierScreen(
    selectedItems: MutableState<List<Items>>,
    selectedItemNames: List<String>,
    category: String,
    sharedPrefManager: SharedPrefManager,
    onOrderPlaced: () -> Unit,
    onItemRemoved: (Items, MutableState<List<Items>>, SharedPrefManager) -> Unit
) {
    var totalPrice by remember { mutableStateOf(0.0) }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Panier",
                fontSize = 24.sp,
                color = Color.Black
            )
        }

        items(selectedItems.value) { item ->
            val quantity = sharedPrefManager.getItemQuantity(item).toDouble()
            if (quantity > 0) {
                val price = item.prices.getOrNull(0)?.price?.toDouble() ?: 0.0
                totalPrice += price * quantity
                Text(
                    text = "Item: ${item.nameFr}, Price: $price €, Quantity: $quantity",
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Button(onClick = { onItemRemoved(item, selectedItems, sharedPrefManager) }) {
                    Text("Supprimer")
                }
            }
        }

        item {
            Text(
                text = "Total Price: $totalPrice €",
                fontSize = 18.sp,
                color = Color.Black
            )
        }

        item {
            Button(
    onClick = onOrderPlaced,
    modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(Alignment.CenterHorizontally)
) {
    Text("Passer la commande")
}
        }
    }
}