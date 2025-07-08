package com.studiomk.matool.core.binding

import androidx.compose.runtime.MutableState
import com.studiomk.matool.domain.entities.shared.SimpleDate
import com.studiomk.matool.domain.entities.shared.SimpleTime
import com.studiomk.matool.domain.entities.shared.fromLocalDate
import com.studiomk.matool.domain.entities.shared.fromLocalTime
import com.studiomk.matool.domain.entities.shared.toLocalDate
import com.studiomk.matool.domain.entities.shared.toLocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalTime

class Binding<T>(
    private val getter: () -> T,
    private val setter: (T) -> Unit
) {
    var value: T
        get() = getter()
        set(newValue) = setter(newValue)

    fun get(): T = getter()
    fun set(newValue: T) = setter(newValue)
}

// MutableState<T>からBinding<T>を作成
fun <T> MutableState<T>.asBinding(): Binding<T> =
    Binding(getter = { value }, setter = { value = it })

// MutableStateFlow<T>からBinding<T>を作成
fun <T> MutableStateFlow<T>.asBinding(): Binding<T> =
    Binding(getter = { value }, setter = { value = it })

// StateFlow<T>（immutable）はgetterのみ
fun <T> StateFlow<T>.asReadOnlyBinding(): Binding<T> =
    Binding(getter = { value }, setter = { _ -> /* no-op */ })

//変換拡張
val Binding<SimpleTime>.localTime: Binding<LocalTime>
    get() = Binding(
        getter = { this.get().toLocalTime() },
        setter = { this.set(SimpleTime.fromLocalTime(it)) }
    )

val Binding<SimpleDate>.localDate: Binding<LocalDate>
    get() = Binding(
        getter = { this.get().toLocalDate() },
        setter = { this.set(SimpleDate.fromLocalDate(it)) }
    )