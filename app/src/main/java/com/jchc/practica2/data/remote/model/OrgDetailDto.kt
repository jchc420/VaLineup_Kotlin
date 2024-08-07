package com.jchc.practica2.data.remote.model

import com.google.gson.annotations.SerializedName


data class OrgDetailDto(

    @SerializedName("image")
    //@SerializedName("displayIcon")
    val image: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("sovaLineup")
    val sovaLnp: String? = null,
    @SerializedName("kayoLineup")
    val kayoLnp: String? = null,
    @SerializedName("gekkoLineup")
    val gekkoLnp: String? = null,
    @SerializedName("viperLineup")
    val viperLnp: String? = null,
    @SerializedName("kjLineup")
    val kjLnp: String? = null,
    @SerializedName("fadeLineup")
    val fadeLnp: String? = null,
    @SerializedName("brimLineup")
    val brimLnp: String? = null,
)
