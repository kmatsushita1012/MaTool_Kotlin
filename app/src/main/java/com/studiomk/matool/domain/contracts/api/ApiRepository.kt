package com.studiomk.matool.domain.contracts.api

import com.studiomk.matool.domain.entities.districts.*
import com.studiomk.matool.domain.entities.regions.*
import com.studiomk.matool.domain.entities.routes.*
import com.studiomk.matool.domain.entities.shared.*
import com.studiomk.matool.domain.entities.locations.*

interface ApiRepository {
    suspend fun getRegions(): Result<List<Region>, ApiError>
    suspend fun getRegion(regionId: String): Result<Region, ApiError>
    suspend fun putRegion(region: Region, accessToken: String): Result<String, ApiError>
    suspend fun getDistricts(regionId: String): Result<List<PublicDistrict>, ApiError>
    suspend fun getDistrict(districtId: String): Result<PublicDistrict, ApiError>
    suspend fun postDistrict(regionId: String, districtName: String, email: String, accessToken: String): Result<String, ApiError>
    suspend fun putDistrict(district: District, accessToken: String): Result<String, ApiError>
    suspend fun getTool(districtId: String, accessToken: String?): Result<DistrictTool, ApiError>
    suspend fun getRoutes(districtId: String, accessToken: String?): Result<List<RouteSummary>, ApiError>
    suspend fun getRoute(id: String, accessToken: String?): Result<PublicRoute, ApiError>
    suspend fun getCurrentRoute(districtId: String, accessToken: String?): Result<PublicRoute, ApiError>
    suspend fun postRoute(route: Route, accessToken: String): Result<String, ApiError>
    suspend fun putRoute(route: Route, accessToken: String): Result<String, ApiError>
    suspend fun deleteRoute(id: String, accessToken: String): Result<String, ApiError>
    suspend fun getLocation(districtId: String, accessToken: String?): Result<PublicLocation?, ApiError>
    suspend fun getLocations(regionId: String, accessToken: String?): Result<List<PublicLocation>, ApiError>
    suspend fun putLocation(location: Location, accessToken: String): Result<String, ApiError>
    suspend fun deleteLocation(districtId: String, accessToken: String): Result<String, ApiError>
    suspend fun getSegmentCoordinate(start: Coordinate, end: Coordinate): Result<List<Coordinate>, ApiError>
}

