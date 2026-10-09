package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.api.ApiConfig
import com.example.data.api.DataSourceMode
import com.example.data.demo.DemoDataProvider
import com.example.data.model.ActiveTracking
import com.example.data.model.AppealViolationRequest
import com.example.data.model.ApplyPermitRequest
import com.example.data.model.CampusDashboardStats
import com.example.data.model.CreateVehicleRequest
import com.example.data.model.CreateViolationRequest
import com.example.data.model.GateEntryRequest
import com.example.data.model.GateExitRequest
import com.example.data.model.ParkingLot
import com.example.data.model.ParkingPermit
import com.example.data.model.ParkingSlot
import com.example.data.model.PermitStatus
import com.example.data.model.TrackingLog
import com.example.data.model.Vehicle
import com.example.data.model.ViolationCitation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class CpvtsRepository {

    private val api = ApiClient.service

    private inline fun <T> executeApiCall(call: () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Server returned empty response (${response.code()})"))
                }
            } else {
                val errBody = response.errorBody()?.string()?.take(200) ?: ""
                val msg = "Server error ${response.code()}: ${response.message()} $errBody".trim()
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            val errorMsg = when {
                e is java.net.ConnectException -> "Cannot connect to server at ${ApiConfig.baseUrlState.value}. Please verify server is running and accessible."
                e is java.net.SocketTimeoutException -> "Connection timed out. Server at ${ApiConfig.baseUrlState.value} did not respond."
                e is java.net.UnknownHostException -> "Unknown host: ${ApiConfig.baseUrlState.value}. Please check URL."
                else -> "Network error: ${e.localizedMessage ?: e.javaClass.simpleName}"
            }
            Result.failure(Exception(errorMsg, e))
        }
    }

    suspend fun getLots(): Result<List<ParkingLot>> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getLots())
        } else {
            executeApiCall { api.getLots() }
        }
    }

    suspend fun getLot(id: String): Result<ParkingLot> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            val lot = DemoDataProvider.getLot(id)
            if (lot != null) Result.success(lot) else Result.failure(Exception("Lot not found"))
        } else {
            executeApiCall { api.getLotById(id) }
        }
    }

    suspend fun getSlotsForLot(lotId: String): Result<List<ParkingSlot>> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getSlotsForLot(lotId))
        } else {
            executeApiCall { api.getLotSlots(lotId) }
        }
    }

    suspend fun getVehicles(): Result<List<Vehicle>> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getVehicles())
        } else {
            executeApiCall { api.getVehicles() }
        }
    }

    suspend fun registerVehicle(request: CreateVehicleRequest): Result<Vehicle> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.addVehicle(request))
        } else {
            executeApiCall { api.registerVehicle(request) }
        }
    }

    suspend fun deleteVehicle(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.deleteVehicle(id))
        } else {
            val res = executeApiCall { api.deleteVehicle(id) }
            res.map { it.success }
        }
    }

    suspend fun getPermits(): Result<List<ParkingPermit>> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getPermits())
        } else {
            executeApiCall { api.getPermits() }
        }
    }

    suspend fun applyPermit(request: ApplyPermitRequest): Result<ParkingPermit> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.applyPermit(request))
        } else {
            executeApiCall { api.applyPermit(request) }
        }
    }

    suspend fun updatePermitStatus(id: String, status: PermitStatus): Result<ParkingPermit> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            val updated = DemoDataProvider.updatePermitStatus(id, status)
            if (updated != null) Result.success(updated) else Result.failure(Exception("Permit not found"))
        } else {
            executeApiCall {
                api.updatePermitStatus(id, com.example.data.model.UpdatePermitStatusRequest(status = status))
            }
        }
    }

    suspend fun getActiveTracking(): Result<List<ActiveTracking>> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getActiveTracking())
        } else {
            executeApiCall { api.getActiveTracking() }
        }
    }

    suspend fun logGateEntry(request: GateEntryRequest): Result<ActiveTracking> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.logGateEntry(request))
        } else {
            executeApiCall { api.logGateEntry(request) }
        }
    }

    suspend fun logGateExit(request: GateExitRequest): Result<Boolean> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.logGateExit(request.licensePlate, request.exitGate))
        } else {
            val res = executeApiCall { api.logGateExit(request) }
            res.map { it.success }
        }
    }

    suspend fun getTrackingHistory(): Result<List<TrackingLog>> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getTrackingHistory())
        } else {
            executeApiCall { api.getTrackingHistory() }
        }
    }

    suspend fun getViolations(): Result<List<ViolationCitation>> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getViolations())
        } else {
            executeApiCall { api.getViolations() }
        }
    }

    suspend fun issueViolation(request: CreateViolationRequest): Result<ViolationCitation> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.issueViolation(request))
        } else {
            executeApiCall { api.issueViolation(request) }
        }
    }

    suspend fun payViolation(id: String): Result<ViolationCitation> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            val updated = DemoDataProvider.payViolation(id)
            if (updated != null) Result.success(updated) else Result.failure(Exception("Citation not found"))
        } else {
            executeApiCall { api.payViolation(id) }
        }
    }

    suspend fun appealViolation(id: String, reason: String): Result<ViolationCitation> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            val updated = DemoDataProvider.appealViolation(id, reason)
            if (updated != null) Result.success(updated) else Result.failure(Exception("Citation not found"))
        } else {
            executeApiCall { api.appealViolation(id, AppealViolationRequest(reason)) }
        }
    }

    suspend fun getDashboardStats(): Result<CampusDashboardStats> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.getDashboardStats())
        } else {
            executeApiCall { api.getDashboardStats() }
        }
    }

    suspend fun lookupVehicle(plate: String): Result<Vehicle?> = withContext(Dispatchers.IO) {
        if (ApiConfig.modeState.value == DataSourceMode.DEMO) {
            Result.success(DemoDataProvider.findVehicleByPlate(plate))
        } else {
            val allVehicles = executeApiCall { api.getVehicles() }
            allVehicles.map { list ->
                list.find { it.licensePlate.equals(plate.trim(), ignoreCase = true) }
            }
        }
    }
}
