package com.example.androiderestaurant

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
                        sharedPrefManager = sharedPrefManager
                    ) {
                        // Define what happens when the order is placed here
                        // For example, you can show a Toast message
                        Toast.makeText(this, "Order placed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PanierScreen(
        selectedItems: MutableState<List<Items>>,
        selectedItemNames: List<String>, // Add this parameter
        category: String,
        sharedPrefManager: SharedPrefManager,
        onOrderPlaced: () -> Unit
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally, // Align horizontally
            verticalArrangement = Arrangement.Center // Align vertically
        ) {
            Text(
                text = "Panier",
                fontSize = 24.sp,
                color = Color.Black
            )

            var totalPrice = 0.0

            // Iterate over selectedItems to display each item's price, quantity and a button to remove it
            selectedItems.value.forEach { item ->
                val price = item.prices.getOrNull(0)?.price?.toDouble() ?: 0.0
                val quantity = sharedPrefManager.getItemQuantity(item).toDouble()
                totalPrice += price * quantity
                Text(
                    text = "Item: ${item.nameFr}, Price: $price €, Quantity: $quantity",
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Button(onClick = {
                    selectedItems.value = selectedItems.value.filter { it != item }
                }) {
                    Text("Supprimer")
                }
            }

            // Display the total price
            Text(
                text = "Total Price: $totalPrice €",
                fontSize = 18.sp,
                color = Color.Black
            )

            Button(
                onClick = onOrderPlaced,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Passer la commande")
            }
        }
    }
}
