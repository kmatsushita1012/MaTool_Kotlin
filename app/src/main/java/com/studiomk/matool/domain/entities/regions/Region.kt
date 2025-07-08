package com.studiomk.matool.domain.entities.regions

import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val id: String,
    var name: String,
    var subname: String,
    var description: String? = null,
    var prefecture: String,
    var city: String,
    var base: Coordinate,
    var spans: List<Span> = listOf(),
    var imagePath: String? = null
) {
    companion object
}

// サンプル
val Region.Companion.sample: Region
    get() = Region(
        id = "掛川祭_年番本部",
        name = "掛川祭",
        subname = "年番本部",
        description = "省略",
        prefecture = "静岡県",
        city = "掛川市",
        base = Coordinate.sample,
        spans = listOf(Span.sample)
    )