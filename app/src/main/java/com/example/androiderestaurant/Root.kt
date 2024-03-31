package com.example.androiderestaurant

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Root (

  @SerializedName("data" ) var data : ArrayList<Data> = arrayListOf()

): Serializable