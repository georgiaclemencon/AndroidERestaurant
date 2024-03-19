package com.example.androiderestaurant

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.androiderestaurant.ui.theme.AndroidERestaurantTheme
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import java.util.ArrayList


class Activity2 : ComponentActivity() {
    private var response = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = intent.getStringExtra("category")

        requestData { response ->
            this.response = response
            setContent {
                AndroidERestaurantTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CategoryComponent(name!!, getData(name))
                    }
                }
            }
        }
    }

    private fun requestData(callback: (String) -> Unit) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/menu"

        val gson = Gson()
        val params = HashMap<String, String>()
        params["id_shop"] = "1"
        val requestBody = gson.toJson(params)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                // Handling Success
                Log.d("Success", "simpleRequest:${it}")
                callback(it)

            },
            Response.ErrorListener {
                // Handling Error
                Log.d("Error", "simpleRequest:${it}")
                callback("Error")
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray(Charsets.UTF_8)
            }
        }
        queue.add(stringRequest)
    }

    private fun getCategoryFromData(data: String, category: String): JsonObject {
        val gson = Gson()
        val jsonObject = gson.fromJson(data, JsonObject::class.java)
        val dataArray = jsonObject.getAsJsonArray("data")

        return when (category) {
            "Entries" -> dataArray.get(0).asJsonObject
            "Plats" -> dataArray.get(1).asJsonObject
            "Desserts" -> dataArray.get(2).asJsonObject
            else -> JsonObject()
        }
    }


    private fun serializeData(data: String): Root { // fonction pour faire connaitre nos data class a ce fichier
        return Gson().fromJson(data, Root::class.java)
    }

    private fun getData(category: String): Data {
        val root = serializeData(this.response)
        return when (category) {
            "Entrée" -> root.data[0]
            "Plat" -> root.data[1]
            "Dessert" -> root.data[2]
            else -> Data()
        }
    }
}



@Composable
fun CategoryComponent(category: String?, data: Data) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Category: $category",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            LazyColumn {
                items(data.items) { item ->
                    Text(
                        text = item.nameFr!!,
                        fontSize = 24.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(8.dp)
                    )
                    for (i in 0 until data.items.size) {

                            Text(
                                text = data.items[i].nameFr.toString(),
                                fontSize = 20.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                            for (j in 0 until data.items[i].images.size) {

                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(data.items[i].images[j])
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null
                                )
       }

                    }
                    Text(
                        text = "Price: ${item.prices[0].price}", // Assurez-vous que l'élément a au moins un prix
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
            }
        }
    }
}

fun rememberImagePainter(items: ArrayList<String>): Painter {
    TODO("Not yet implemented")
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