package com.example.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.androiderestaurant.ui.theme.AndroidERestaurantTheme
import com.google.gson.Gson
import org.json.JSONObject


class Activity2 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("category") ?: "No category"

        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    val itemsState = remember {
                        mutableStateListOf<Items>()
                    }
                    requestData(name, itemsState)
                    CategoryComponent(
                        category = name,
                        item = itemsState,
                        startActivity = { dish -> changeActivity(dish) }
                    )
                }


            }

        }


    }

    private fun changeActivity(message: Items) {
        val intent = Intent(this, Commande::class.java)
        intent.putExtra("dish", message)
        this.startActivity(intent)

    }

    private fun requestData(category: String, itemsToDisplay: MutableList<Items>) {
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val param = JSONObject()
        param.put("id_shop", "1")

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, param, { response ->
            val result = Gson().fromJson(response.toString(), Root::class.java)
            val itemsFromCategory = result.data.find { it.nameFr == category }?.items ?: emptyList()
            Log.d("Response", "fetchData:${itemsFromCategory}")
            itemsToDisplay.addAll(itemsFromCategory)

        }, { error ->
            Log.d("Error", "fetchData:${error}")
        })
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

}


@Composable
fun CategoryComponent(
    category: String,
    item: List<Items>,

    startActivity: (Items) -> Unit
) {

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Category: $category",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        LazyColumn {

            items(item) { item ->
                Text(
                    text = item.nameFr.toString(),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = {
                    startActivity(item)
                }) {
                    Box(modifier = Modifier.fillMaxWidth()) {
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
                    }
                }
            }
        }
    }
}


@Composable
fun Activity2Content(category: String?, data: String?) {
    Text(text = "Category: $category")
    Text(text = "Data: $data")
}
// Fonction pour effectuer une requête POST JSON à une URL donnée


// faire 2 call back, elle prenne la reponse et l'erreur
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidERestaurantTheme {
    }
}