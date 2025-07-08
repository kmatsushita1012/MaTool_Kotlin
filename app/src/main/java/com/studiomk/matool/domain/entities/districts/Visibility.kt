package com.studiomk.matool.domain.entities.districts

import com.studiomk.matool.core.serialize.LowerCaseEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(VisibilitySerializer::class)
enum class Visibility {
    Admin,
    Route,
    All
}

object VisibilitySerializer : LowerCaseEnumSerializer<Visibility>(Visibility.entries.toTypedArray())


// idプロパティ
val Visibility.id: Visibility
    get() = this

// ラベルプロパティ
val Visibility.label: String
    get() = when (this) {
        Visibility.Admin -> "非公開"
        Visibility.Route -> "経路のみ公開"
        Visibility.All -> "全て公開"
    }