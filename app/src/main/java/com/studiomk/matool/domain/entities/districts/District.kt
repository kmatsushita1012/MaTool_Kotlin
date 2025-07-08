package com.studiomk.matool.domain.entities.districts

import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable

@Serializable
data class District(
    val id: String,
    var name: String,
    val regionId: String,
    var description: String? = null,
    var base: Coordinate? = null,
    var area: List<Coordinate> = listOf(),
    var imagePath: String? = null,
    var performances: List<Performance> = listOf(),
    var visibility: Visibility
) {
    companion object
}

// サンプル
val District.Companion.sample: District
    get() = District(
        id = "掛川祭_城北町",
        name = "城北町",
        regionId = "掛川祭_年番本部",
        description = "省略",
        performances = listOf(Performance.sample),
        visibility = Visibility.All
    )