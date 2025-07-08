package com.studiomk.matool.domain.entities.districts

import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable

@Serializable
data class PublicDistrict(
    val id: String,
    val name: String,
    val regionId: String,
    val regionName: String,
    val description: String?,
    val base: Coordinate?,
    val area: List<Coordinate>,
    val imagePath: String?,
    val performances: List<Performance>,
    val visibility: Visibility
) {
    companion object
}

// Districtへの変換
fun PublicDistrict.toModel(): District = District(
    id = id,
    name = name,
    regionId = regionId,
    description = description,
    base = base,
    area = area,
    imagePath = imagePath,
    performances = performances,
    visibility = visibility
)

// サンプル
val PublicDistrict.Companion.sample: PublicDistrict
    get() = PublicDistrict(
        id = "Johoku",
        name = "城北町",
        regionId = "掛川祭_年番本部",
        regionName = "掛川祭",
        description = "省略",
        base = Coordinate.sample,
        area = listOf(),
        imagePath = null,
        performances = listOf(Performance.sample),
        visibility = Visibility.All
    )