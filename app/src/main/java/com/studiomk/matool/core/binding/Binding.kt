package com.studiomk.matool.core.binding

import com.studiomk.ktca.core.util.Binding
import com.studiomk.matool.domain.entities.shared.SimpleDate
import com.studiomk.matool.domain.entities.shared.SimpleTime
import com.studiomk.matool.domain.entities.shared.fromLocalDate
import com.studiomk.matool.domain.entities.shared.fromLocalTime
import com.studiomk.matool.domain.entities.shared.toLocalDate
import com.studiomk.matool.domain.entities.shared.toLocalTime
import java.time.LocalDate
import java.time.LocalTime

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