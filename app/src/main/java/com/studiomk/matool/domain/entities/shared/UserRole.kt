package com.studiomk.matool.domain.entities.shared

import kotlinx.serialization.Serializable

@Serializable
sealed class UserRole {
    data class Region(val id: String) : UserRole()
    data class District(val id: String) : UserRole()
    object Guest : UserRole()
}
