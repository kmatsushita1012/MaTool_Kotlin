package com.studiomk.matool.data.api.live


import com.studiomk.matool.core.http.Http
import com.studiomk.matool.domain.entities.districts.*
import com.studiomk.matool.domain.entities.locations.PublicLocation
import com.studiomk.matool.domain.entities.regions.Region
import com.studiomk.matool.domain.entities.routes.*
import com.studiomk.matool.domain.entities.shared.*
import com.studiomk.matool.domain.contracts.api.*
import com.studiomk.matool.domain.entities.shared.Result
import com.studiomk.matool.domain.entities.shared.Result.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LiveApiRepository(
    private val base: String = "https://eqp8rvam4h.execute-api.ap-northeast-1.amazonaws.com"
) : ApiRepository {

    private val json = Json {
        encodeDefaults = true // ← これがポイント
        prettyPrint = false
        isLenient = true
        ignoreUnknownKeys = true
    }

    override suspend fun getRegions(): Result<List<Region>, ApiError> {
        val response = Http.get(base, "/regions")
        return decodeResponse(response)
    }

    override suspend fun getRegion(regionId: String): Result<Region, ApiError> {
        val response = Http.get(base, "/regions/$regionId")
        return decodeResponse(response)
    }

    override suspend fun putRegion(region: Region, accessToken: String): Result<String, ApiError> {
        val body = encodeRequest(region)
        return when (body) {
            is Success -> {
                val response = Http.put(base, "/regions/${region.id}", body = body.value, accessToken = accessToken)
                decodeResponse(response)
            }
            is Failure -> Failure(ApiError.Encoding(body.error.localizedMessage ?: "Encoding Error"))
        }
    }

    override suspend fun getDistricts(regionId: String): Result<List<PublicDistrict>, ApiError> {
        val response = Http.get(base, "/regions/$regionId/districts")
        return decodeResponse(response)
    }

    override suspend fun getDistrict(districtId: String): Result<PublicDistrict, ApiError> {
        val response = Http.get(base, "/districts/$districtId")
        return decodeResponse(response)
    }

    override suspend fun postDistrict(regionId: String, districtName: String, email: String, accessToken: String): Result<String, ApiError> {
        val body = encodeRequest(mapOf("name" to districtName, "email" to email))
        return when (body) {
            is Success -> {
                val response = Http.post(base, "/regions/$regionId/districts", body = body.value, accessToken = accessToken)
                decodeResponse(response)
            }
            is Failure -> Failure(ApiError.Encoding(body.error.localizedMessage ?: "Encoding Error"))
        }
    }

    override suspend fun putDistrict(district: District, accessToken: String): Result<String, ApiError> {
        val body = encodeRequest(district)
        return when (body) {
            is Success -> {
                val response = Http.put(base, "/districts/${district.id}", body = body.value, accessToken = accessToken)
                decodeResponse(response)
            }
            is Failure -> Failure(ApiError.Encoding(body.error.localizedMessage ?: "Encoding Error"))
        }
    }

    override suspend fun getTool(districtId: String, accessToken: String?): Result<DistrictTool, ApiError> {
        val response = Http.get(base, "/districts/$districtId/tools", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun getRoutes(districtId: String, accessToken: String?): Result<List<RouteSummary>, ApiError> {
        val response = Http.get(base, "/districts/$districtId/routes", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun getRoute(id: String, accessToken: String?): Result<PublicRoute, ApiError> {
        val response = Http.get(base, "/routes/$id", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun getCurrentRoute(districtId: String, accessToken: String?): Result<PublicRoute, ApiError> {
        val response = Http.get(base, "/districts/$districtId/routes/current", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun postRoute(route: Route, accessToken: String): Result<String, ApiError> {
        val body = encodeRequest(route)
        return when (body) {
            is Success -> {
                val response = Http.post(base, "/districts/${route.districtId}/routes", body = body.value, accessToken = accessToken)
                decodeResponse(response)
            }
            is Failure -> Failure(ApiError.Encoding(body.error.localizedMessage ?: "Encoding Error"))
        }
    }

    override suspend fun putRoute(route: Route, accessToken: String): Result<String, ApiError> {
        val body = encodeRequest(route)
        return when (body) {
            is Success -> {
                val response = Http.put(base, "/routes/${route.id}", body = body.value, accessToken = accessToken)
                decodeResponse(response)
            }
            is Failure -> Failure(ApiError.Encoding(body.error.localizedMessage ?: "Encoding Error"))
        }
    }

    override suspend fun deleteRoute(id: String, accessToken: String): Result<String, ApiError> {
        val response = Http.delete(base, "/routes/$id", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun getLocation(districtId: String, accessToken: String?): Result<PublicLocation?, ApiError> {
        val response = Http.get(base, "/districts/$districtId/locations", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun getLocations(regionId: String, accessToken: String?): Result<List<PublicLocation>, ApiError> {
        val response = Http.get(base, "/regions/$regionId/locations", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun putLocation(location: com.studiomk.matool.domain.entities.locations.Location, accessToken: String): Result<String, ApiError> {
        val body = encodeRequest(location)
        return when (body) {
            is Success -> {
                val response = Http.put(base, "/districts/${location.districtId}/locations", body = body.value, accessToken = accessToken)
                decodeResponse(response)
            }
            is Failure -> Failure(ApiError.Encoding(body.error.localizedMessage ?: "Encoding Error"))
        }
    }

    override suspend fun deleteLocation(districtId: String, accessToken: String): Result<String, ApiError> {
        val response = Http.delete(base, "/districts/$districtId/locations", accessToken = accessToken)
        return decodeResponse(response)
    }

    override suspend fun getSegmentCoordinate(start: Coordinate, end: Coordinate): Result<List<Coordinate>, ApiError> {
        val response = Http.get(
            base,
            "/route/detail",
            query = mapOf(
                "lat1" to start.latitude,
                "lon1" to start.longitude,
                "lat2" to end.latitude,
                "lon2" to end.longitude
            )
        )
        return decodeResponse(response)
    }

    // --- private helpers ---

    private inline fun <reified T> decodeResponse(response: Result<ByteArray, Exception>): Result<T, ApiError> {
        return when (response) {
            is Success -> {
                try {
                    val decoded = json.decodeFromString<T>(response.value.toString(Charsets.UTF_8))
                    Success(decoded)
                } catch (e: Exception) {
                    Failure(ApiError.Decoding("レスポンスの解析に失敗しました ${e.toString()}"))
                }
            }
            is Failure -> Failure(ApiError.Network(response.error.localizedMessage ?: "Network Error"))
        }
    }

    private inline fun <reified T> encodeRequest(obj: T): Result<ByteArray, Exception> {
        return try {
            val data = json.encodeToString(obj).toByteArray(Charsets.UTF_8)
            Log.d("encodeRequest", json.encodeToString(obj))
            Success(data)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}