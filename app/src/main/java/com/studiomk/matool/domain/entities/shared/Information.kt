package com.studiomk.matool.domain.entities.shared

import com.studiomk.matool.core.others.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Information(
    override val id: String,
    val name: String = "",
    val description: String? = null
): Identifiable<String>