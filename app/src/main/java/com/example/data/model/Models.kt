package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class UserRole {
    @Json(name = "STUDENT") STUDENT,
    @Json(name = "FACULTY") FACULTY,
    @Json(name = "OFFICER") OFFICER,
    @Json(name = "ADMIN") ADMIN
}

enum class VehicleType {
    @Json(name = "FOUR_WHEELER") FOUR_WHEELER,
    @Json(name = "TWO_WHEELER") TWO_WHEELER,
    @Json(name = "EV") EV,
    @Json(name = "ACCESSIBLE") ACCESSIBLE
}

enum class PermitStatus {
    @Json(name = "ACTIVE") ACTIVE,
    @Json(name = "PENDING") PENDING,
    @Json(name = "EXPIRED") EXPIRED,
    @Json(name = "REJECTED") REJECTED
}

enum class PermitType {
    @Json(name = "SEMESTER") SEMESTER,
    @Json(name = "ANNUAL") ANNUAL,
    @Json(name = "DAILY") DAILY,
    @Json(name = "EV_CHARGING") EV_CHARGING,
    @Json(name = "FACULTY_RESERVED") FACULTY_RESERVED,
    @Json(name = "VISITOR") VISITOR
}

enum class LotStatus {
    @Json(name = "AVAILABLE") AVAILABLE,
    @Json(name = "LIMITED") LIMITED,
    @Json(name = "FULL") FULL,
    @Json(name = "MAINTENANCE") MAINTENANCE
}

enum class SlotType {
    @Json(name = "STANDARD") STANDARD,
    @Json(name = "EV") EV,
    @Json(name = "ACCESSIBLE") ACCESSIBLE,
    @Json(name = "RESERVED") RESERVED
}

enum class ViolationStatus {
    @Json(name = "UNPAID") UNPAID,
    @Json(name = "PAID") PAID,
    @Json(name = "APPEALED") APPEALED,
    @Json(name = "DISMISSED") DISMISSED
}

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val department: String? = null,
    val campusId: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val token: String,
    val user: User
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val department: String? = null,
    val campusId: String? = null
)

@JsonClass(generateAdapter = true)
data class ParkingLot(
    val id: String,
    val name: String,
    val code: String,
    val zone: String,
    val totalCapacity: Int,
    val occupiedCount: Int,
    val evSlots: Int = 0,
    val evOccupied: Int = 0,
    val accessibleSlots: Int = 0,
    val accessibleOccupied: Int = 0,
    val ratePerHour: Double = 0.0,
    val description: String = "",
    val status: LotStatus = LotStatus.AVAILABLE,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    val availableCount: Int
        get() = (totalCapacity - occupiedCount).coerceAtLeast(0)

    val occupancyPercentage: Int
        get() = if (totalCapacity > 0) ((occupiedCount.toDouble() / totalCapacity) * 100).toInt() else 0

    val calculatedStatus: LotStatus
        get() = when {
            status == LotStatus.MAINTENANCE -> LotStatus.MAINTENANCE
            occupancyPercentage >= 95 -> LotStatus.FULL
            occupancyPercentage >= 75 -> LotStatus.LIMITED
            else -> LotStatus.AVAILABLE
        }
}

@JsonClass(generateAdapter = true)
data class ParkingSlot(
    val id: String,
    val lotId: String,
    val slotNumber: String,
    val type: SlotType = SlotType.STANDARD,
    val isOccupied: Boolean = false,
    val occupiedByPlate: String? = null,
    val occupiedSince: String? = null
)

@JsonClass(generateAdapter = true)
data class Vehicle(
    val id: String,
    val licensePlate: String,
    val make: String,
    val model: String,
    val color: String,
    val year: Int? = null,
    val vehicleType: VehicleType = VehicleType.FOUR_WHEELER,
    val ownerId: String? = null,
    val ownerName: String? = null,
    val activePermitId: String? = null,
    val isCurrentlyOnCampus: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CreateVehicleRequest(
    val licensePlate: String,
    val make: String,
    val model: String,
    val color: String,
    val year: Int? = null,
    val vehicleType: VehicleType = VehicleType.FOUR_WHEELER
)

@JsonClass(generateAdapter = true)
data class ParkingPermit(
    val id: String,
    val permitNumber: String,
    val vehiclePlate: String,
    val vehicleModel: String? = null,
    val userId: String,
    val userName: String? = null,
    val permitType: PermitType,
    val allowedZone: String,
    val validFrom: String,
    val validTo: String,
    val status: PermitStatus = PermitStatus.ACTIVE,
    val qrCodePayload: String? = null
)

@JsonClass(generateAdapter = true)
data class ApplyPermitRequest(
    val vehiclePlate: String,
    val permitType: PermitType,
    val allowedZone: String,
    val validMonths: Int = 4
)

@JsonClass(generateAdapter = true)
data class UpdatePermitStatusRequest(
    val status: PermitStatus,
    val reviewNotes: String? = null
)

@JsonClass(generateAdapter = true)
data class ActiveTracking(
    val id: String,
    val licensePlate: String,
    val vehicleDescription: String,
    val entryGate: String,
    val entryTime: String,
    val parkLocationLot: String? = null,
    val driverName: String? = null,
    val driverRole: String? = null,
    val hasValidPermit: Boolean = true
)

@JsonClass(generateAdapter = true)
data class GateEntryRequest(
    val licensePlate: String,
    val gateName: String,
    val assignedLot: String? = null
)

@JsonClass(generateAdapter = true)
data class GateExitRequest(
    val licensePlate: String,
    val exitGate: String
)

@JsonClass(generateAdapter = true)
data class TrackingLog(
    val id: String,
    val licensePlate: String,
    val gate: String,
    val action: String, // "ENTRY" or "EXIT"
    val timestamp: String,
    val permitChecked: Boolean = true,
    val officerId: String? = null
)

@JsonClass(generateAdapter = true)
data class ViolationCitation(
    val id: String,
    val citationNumber: String,
    val licensePlate: String,
    val lotName: String,
    val violationType: String,
    val fineAmount: Double,
    val issuedAt: String,
    val officerNotes: String? = null,
    val status: ViolationStatus = ViolationStatus.UNPAID,
    val appealReason: String? = null
)

@JsonClass(generateAdapter = true)
data class CreateViolationRequest(
    val licensePlate: String,
    val lotName: String,
    val violationType: String,
    val fineAmount: Double,
    val officerNotes: String? = null
)

@JsonClass(generateAdapter = true)
data class AppealViolationRequest(
    val reason: String
)

@JsonClass(generateAdapter = true)
data class CampusDashboardStats(
    val totalLots: Int,
    val totalCapacity: Int,
    val totalOccupied: Int,
    val activeOnCampusVehicles: Int,
    val totalPermitsActive: Int,
    val pendingPermitApplications: Int,
    val todayViolationsCount: Int,
    val todayGateEntries: Int,
    val todayGateExits: Int
) {
    val overallOccupancyPercentage: Int
        get() = if (totalCapacity > 0) ((totalOccupied.toDouble() / totalCapacity) * 100).toInt() else 0
}

@JsonClass(generateAdapter = true)
data class SimpleMessageResponse(
    val success: Boolean,
    val message: String
)
