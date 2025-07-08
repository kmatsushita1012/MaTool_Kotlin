package com.studiomk.matool.domain.entities.locations

data class Interval(val label: String, val value: Int) {
    companion object {
        val sample = Interval(label = "1分", value = 60)
        val options = listOf(
            Interval(label = "1秒（確認用）", value = 1),
            Interval(label = "1分", value = 60),
            Interval(label = "2分", value = 120),
            Interval(label = "3分", value = 180),
            Interval(label = "5分", value = 300),
            Interval(label = "10分", value = 600),
            Interval(label = "15分", value = 900)
        )
    }
}