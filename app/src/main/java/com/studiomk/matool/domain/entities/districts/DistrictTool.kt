package com.studiomk.matool.domain.entities.districts

import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable

@Serializable
data class DistrictTool(
    val districtId: String,
    val districtName: String,
    val regionId: String,
    val regionName: String,
    val milestones: List<Information>,
    val base: Coordinate,
    val spans: List<Span>
) {
    companion object
}

// サンプル
val DistrictTool.Companion.sample: DistrictTool
    get() = DistrictTool(
        districtId = "掛川祭_城北町",
        districtName = "城北町",
        regionId = "掛川祭_年番本部",
        regionName = "年番本部",
        milestones = listOf(),
        base = Coordinate.sample,
        spans = listOf(Span.sample)
    )