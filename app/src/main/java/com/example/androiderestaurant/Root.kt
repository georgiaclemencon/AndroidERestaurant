package com.example.androiderestaurant

import com.google.gson.annotations.SerializedName


data class Root (

  @SerializedName("data" ) var data : ArrayList<Data> = arrayListOf()

)