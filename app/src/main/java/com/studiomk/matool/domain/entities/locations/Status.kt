package com.studiomk.matool.domain.entities.locations

import java.time.LocalDateTime

sealed class Status {
    data class Update(val location: Location) : Status()
    data class Delete(val date: LocalDateTime) : Status()
    data class Loading(val date: LocalDateTime) : Status()
    data class LocationError(val date: LocalDateTime) : Status()
    data class ApiError(val date: LocalDateTime) : Status()

    val text: String
        get() = when (this) {
            is Update -> "${location.timestamp.text()} 送信成功"
            is Loading -> "${date.text()} 読み込み中"
            is LocationError -> "${date.text()} 取得失敗"
            is ApiError -> "${date.text()} 送信失敗"
            is Delete -> "${date.text()} 削除済み"
        }

    val id: String
        get() = when (this) {
            is Update -> location.timestamp.text()
            is Loading -> date.text()
            is LocationError -> date.text()
            is ApiError -> date.text()
            is Delete -> date.text()
        }
}

// LocalDateTime拡張: text()（必要に応じてSimpleDateのtext等を流用）
fun LocalDateTime.text(): String =
    this.toString() // 必要に応じて書式を変更してください