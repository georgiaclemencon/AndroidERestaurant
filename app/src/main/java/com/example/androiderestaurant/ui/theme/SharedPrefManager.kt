package com.example.androiderestaurant.ui.theme

import android.content.Context
import android.content.SharedPreferences
import com.example.androiderestaurant.Items
class SharedPrefManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

    fun saveCartItemCount(count: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("cartItemCount", count)
        editor.apply()
    }

    fun getCartItemCount(): Int {
        return sharedPreferences.getInt("cartItemCount", 0) // Default value is 0
    }

    fun saveItemInfo(item: Items, quantity: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(item.nameFr, quantity)
        editor.apply()
    }

    fun incrementCartItemCount() {
        val currentCount = getCartItemCount()
        saveCartItemCount(currentCount + 1)
    }

    fun getItemQuantity(item: Items): Int {
        return sharedPreferences.getInt(item.nameFr, 0) // Default value is 0
    }
}
