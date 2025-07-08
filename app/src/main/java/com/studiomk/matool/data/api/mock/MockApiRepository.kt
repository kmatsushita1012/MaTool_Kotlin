package com.studiomk.matool.data.api.mock

import com.studiomk.matool.domain.entities.districts.*
import com.studiomk.matool.domain.entities.locations.*
import com.studiomk.matool.domain.entities.regions.*
import com.studiomk.matool.domain.entities.routes.*
import com.studiomk.matool.domain.entities.shared.*
import com.studiomk.matool.domain.contracts.api.*
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.domain.entities.shared.Result.*

class MockApiRepository : ApiRepository {
    private var routes: MutableList<Route> = mutableListOf(Route.sample)

    override suspend fun getRegions(): Result<List<Region>, ApiError> =
        Success(listOf(Region.sample))

    override suspend fun getRegion(regionId: String): Result<Region, ApiError> =
        Success(Region.sample)

    override suspend fun putRegion(region: Region, accessToken: String): Result<String, ApiError> =
        Success("Success")

    override suspend fun getDistricts(regionId: String): Result<List<PublicDistrict>, ApiError> =
        Success(listOf(PublicDistrict.sample))

    override suspend fun getDistrict(districtId: String): Result<PublicDistrict, ApiError> =
        Success(PublicDistrict.sample)

    override suspend fun postDistrict(regionId: String, districtName: String, email: String, accessToken: String): Result<String, ApiError> =
        Success("Success")

    override suspend fun putDistrict(district: District, accessToken: String): Result<String, ApiError> =
        Success("Success")

    override suspend fun getTool(districtId: String, accessToken: String?): Result<DistrictTool, ApiError> =
        Success(DistrictTool.sample)

    override suspend fun getRoutes(districtId: String, accessToken: String?): Result<List<RouteSummary>, ApiError> {
        val summaries = routes.map { RouteSummary.from(PublicRoute.fromRoute(it, "城北町")) }
        return Success(summaries)
    }

    override suspend fun getRoute(id: String, accessToken: String?): Result<PublicRoute, ApiError> {
        val route = routes.firstOrNull { it.id == id } ?: Route.sample
        return Success(PublicRoute.fromRoute(route, "城北町"))
    }

    override suspend fun getCurrentRoute(districtId: String, accessToken: String?): Result<PublicRoute, ApiError> {
        val route = routes.firstOrNull() ?: Route.sample
        return Success(PublicRoute.fromRoute(route, "城北町"))
    }

    override suspend fun postRoute(route: Route, accessToken: String): Result<String, ApiError> {
        routes.add(route)
        return Success("Success")
    }

    override suspend fun putRoute(route: Route, accessToken: String): Result<String, ApiError> {
        val index = routes.indexOfFirst { it.id == route.id }
        if (index != -1) {
            routes[index] = route
        }
        return Success("Success")
    }

    override suspend fun deleteRoute(id: String, accessToken: String): Result<String, ApiError> {
        val index = routes.indexOfFirst { it.id == id }
        if (index != -1) {
            routes.removeAt(index)
        }
        return Success("Success")
    }

    override suspend fun getLocation(districtId: String, accessToken: String?): Result<PublicLocation?, ApiError> =
        Success(PublicLocation.sample)

    override suspend fun getLocations(regionId: String, accessToken: String?): Result<List<PublicLocation>, ApiError> =
        Success(listOf(PublicLocation.sample))

    override suspend fun putLocation(location: Location, accessToken: String): Result<String, ApiError> =
        Success("Success")

    override suspend fun deleteLocation(districtId: String, accessToken: String): Result<String, ApiError> =
        Success("Success")

    override suspend fun getSegmentCoordinate(start: Coordinate, end: Coordinate): Result<List<Coordinate>, ApiError> {
        val mid = Coordinate(
            latitude = (start.latitude + end.latitude) / 2,
            longitude = (start.longitude + end.longitude) / 2
        )
        return Success(listOf(start, mid, end))
    }
}