package com.example.data.api

import com.example.data.model.ActiveTracking
import com.example.data.model.AppealViolationRequest
import com.example.data.model.ApplyPermitRequest
import com.example.data.model.AuthResponse
import com.example.data.model.CampusDashboardStats
import com.example.data.model.CreateVehicleRequest
import com.example.data.model.CreateViolationRequest
import com.example.data.model.GateEntryRequest
import com.example.data.model.GateExitRequest
import com.example.data.model.LoginRequest
import com.example.data.model.ParkingLot
import com.example.data.model.ParkingPermit
import com.example.data.model.ParkingSlot
import com.example.data.model.RegisterRequest
import com.example.data.model.SimpleMessageResponse
import com.example.data.model.TrackingLog
import com.example.data.model.UpdatePermitStatusRequest
import com.example.data.model.User
import com.example.data.model.Vehicle
import com.example.data.model.ViolationCitation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CpvtsApiService {

    // Authentication Endpoints
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getCurrentUser(): Response<User>

    // Parking Lots & Slots
    @GET("api/lots")
    suspend fun getLots(@Query("zone") zone: String? = null): Response<List<ParkingLot>>

    @GET("api/lots/{id}")
    suspend fun getLotById(@Path("id") id: String): Response<ParkingLot>

    @GET("api/lots/{id}/slots")
    suspend fun getLotSlots(@Path("id") id: String): Response<List<ParkingSlot>>

    // Vehicles
    @GET("api/vehicles")
    suspend fun getVehicles(): Response<List<Vehicle>>

    @POST("api/vehicles")
    suspend fun registerVehicle(@Body request: CreateVehicleRequest): Response<Vehicle>

    @GET("api/vehicles/{id}")
    suspend fun getVehicleById(@Path("id") id: String): Response<Vehicle>

    @DELETE("api/vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: String): Response<SimpleMessageResponse>

    // Permits
    @GET("api/permits")
    suspend fun getPermits(): Response<List<ParkingPermit>>

    @POST("api/permits/apply")
    suspend fun applyPermit(@Body request: ApplyPermitRequest): Response<ParkingPermit>

    @PATCH("api/permits/{id}/status")
    suspend fun updatePermitStatus(
        @Path("id") id: String,
        @Body request: UpdatePermitStatusRequest
    ): Response<ParkingPermit>

    // Gate Entry / Exit & Tracking
    @GET("api/tracking/active")
    suspend fun getActiveTracking(): Response<List<ActiveTracking>>

    @POST("api/tracking/entry")
    suspend fun logGateEntry(@Body request: GateEntryRequest): Response<ActiveTracking>

    @POST("api/tracking/exit")
    suspend fun logGateExit(@Body request: GateExitRequest): Response<SimpleMessageResponse>

    @GET("api/tracking/history")
    suspend fun getTrackingHistory(
        @Query("limit") limit: Int = 50,
        @Query("gate") gate: String? = null
    ): Response<List<TrackingLog>>

    // Violations & Citations
    @GET("api/violations")
    suspend fun getViolations(@Query("plate") plate: String? = null): Response<List<ViolationCitation>>

    @POST("api/violations")
    suspend fun issueViolation(@Body request: CreateViolationRequest): Response<ViolationCitation>

    @PATCH("api/violations/{id}/pay")
    suspend fun payViolation(@Path("id") id: String): Response<ViolationCitation>

    @PATCH("api/violations/{id}/appeal")
    suspend fun appealViolation(
        @Path("id") id: String,
        @Body request: AppealViolationRequest
    ): Response<ViolationCitation>

    // System Dashboard Stats
    @GET("api/stats/dashboard")
    suspend fun getDashboardStats(): Response<CampusDashboardStats>
}
