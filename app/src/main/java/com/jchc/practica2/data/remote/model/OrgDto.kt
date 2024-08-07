package com.jchc.practica2.data.remote.model

import com.google.gson.annotations.SerializedName

data class OrgDto(
    @SerializedName("id")
    //@SerializedName("uuid")
    val id: String? = null,
    @SerializedName("thumbnail")
    //@SerializedName("splash")
    val thumbnail: String? = null,
    @SerializedName("title")
    //@SerializedName("displayName")
    val title: String? = null
)
