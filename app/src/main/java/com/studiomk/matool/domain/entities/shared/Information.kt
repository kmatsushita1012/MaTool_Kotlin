package com.studiomk.matool.domain.entities.shared

import kotlinx.serialization.Serializable

@Serializable
data class Information(
    val id: String,
    val name: String,
    val description: String?
)