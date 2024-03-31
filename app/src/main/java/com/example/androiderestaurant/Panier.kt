package com.example.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.androiderestaurant.ui.theme.AndroidERestaurantTheme

class Panier : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedItems = intent.getSerializableExtra("selectedItems") as? ArrayList<Items>

        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PanierScreen(selectedItems ?: arrayListOf(), "Panier")
                }
            }
        }
    }
}

@Composable
fun PanierScreen(selectedItems: ArrayList<Items>, category: String) {
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
        itemsIndexed(selectedItems) { index, item ->
            Text(
                text = "Item: ${item.nameFr ?: "No name"}",
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = "Price: ${item.prices.getOrNull(0)?.price ?: "No price"}€",
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}


