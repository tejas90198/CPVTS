package com.example.data.demo

import com.example.data.model.ActiveTracking
import com.example.data.model.CampusDashboardStats
import com.example.data.model.LotStatus
import com.example.data.model.ParkingLot
import com.example.data.model.ParkingPermit
import com.example.data.model.ParkingSlot
import com.example.data.model.PermitStatus
import com.example.data.model.PermitType
import com.example.data.model.SlotType
import com.example.data.model.TrackingLog
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.Vehicle
import com.example.data.model.VehicleType
import com.example.data.model.ViolationCitation
import com.example.data.model.ViolationStatus
import java.util.UUID

/**
 * Isolated demo data provider used EXCLUSIVELY in DEMO / PREVIEW mode.
 * Clearly separated from live API operations per system guidelines.
 */
object DemoDataProvider {

    val demoUser = User(
        id = "usr_demo_101",
        name = "Alex Chen",
        email = "alex.chen@campus.edu",
        role = UserRole.STUDENT,
        department = "Computer Science & Engineering",
        campusId = "STU-884920"
    )

    private val mutableLots = mutableListOf(
        ParkingLot(
            id = "lot_north_a",
            name = "North Campus Deck",
            code = "LOT-A",
            zone = "Zone A (North)",
            totalCapacity = 350,
            occupiedCount = 287,
            evSlots = 24,
            evOccupied = 18,
            accessibleSlots = 14,
            accessibleOccupied = 7,
            ratePerHour = 25.0,
            description = "Multi-level garage adjacent to Engineering Quad & Student Union.",
            status = LotStatus.AVAILABLE,
            latitude = 37.7749,
            longitude = -122.4194
        ),
        ParkingLot(
            id = "lot_library_b",
            name = "Central Library Core",
            code = "LOT-B",
            zone = "Zone B (Central)",
            totalCapacity = 180,
            occupiedCount = 173,
            evSlots = 8,
            evOccupied = 8,
            accessibleSlots = 8,
            accessibleOccupied = 5,
            ratePerHour = 30.0,
            description = "Convenient surface parking next to Memorial Library and Registrar.",
            status = LotStatus.LIMITED,
            latitude = 37.7758,
            longitude = -122.4182
        ),
        ParkingLot(
            id = "lot_science_c",
            name = "Science & Tech Complex",
            code = "LOT-C",
            zone = "Zone C (West)",
            totalCapacity = 420,
            occupiedCount = 230,
            evSlots = 36,
            evOccupied = 20,
            accessibleSlots = 18,
            accessibleOccupied = 9,
            ratePerHour = 20.0,
            description = "Large open lot with fast Level-2 EV charging hubs.",
            status = LotStatus.AVAILABLE,
            latitude = 37.7735,
            longitude = -122.4215
        ),
        ParkingLot(
            id = "lot_arena_d",
            name = "South Athletics & Arena Lot",
            code = "LOT-D",
            zone = "Zone D (South)",
            totalCapacity = 500,
            occupiedCount = 160,
            evSlots = 16,
            evOccupied = 4,
            accessibleSlots = 24,
            accessibleOccupied = 6,
            ratePerHour = 15.0,
            description = "Spacious perimeter lot with continuous campus shuttle loop.",
            status = LotStatus.AVAILABLE,
            latitude = 37.7712,
            longitude = -122.4200
        ),
        ParkingLot(
            id = "lot_visitor_v",
            name = "Visitor & Health Pavilion",
            code = "LOT-V",
            zone = "Zone V (Visitor)",
            totalCapacity = 120,
            occupiedCount = 115,
            evSlots = 12,
            evOccupied = 11,
            accessibleSlots = 16,
            accessibleOccupied = 12,
            ratePerHour = 40.0,
            description = "Short-term visitor stalls and clinic appointments. Permit required after 5 PM.",
            status = LotStatus.LIMITED,
            latitude = 37.7765,
            longitude = -122.4150
        )
    )

    private val mutableVehicles = mutableListOf(
        Vehicle(
            id = "veh_1",
            licensePlate = "7XYZ890",
            make = "Toyota",
            model = "RAV4 Hybrid",
            color = "Silver",
            year = 2023,
            vehicleType = VehicleType.EV,
            ownerId = demoUser.id,
            ownerName = demoUser.name,
            activePermitId = "perm_001",
            isCurrentlyOnCampus = true
        ),
        Vehicle(
            id = "veh_2",
            licensePlate = "6ABC321",
            make = "Honda",
            model = "Civic",
            color = "Aegean Blue",
            year = 2021,
            vehicleType = VehicleType.FOUR_WHEELER,
            ownerId = demoUser.id,
            ownerName = demoUser.name,
            activePermitId = null,
            isCurrentlyOnCampus = false
        ),
        Vehicle(
            id = "veh_3",
            licensePlate = "5MNO789",
            make = "Tesla",
            model = "Model 3",
            color = "Midnight Pearl",
            year = 2024,
            vehicleType = VehicleType.EV,
            ownerId = "usr_faculty_404",
            ownerName = "Dr. Marcus Vance",
            activePermitId = "perm_fac_99",
            isCurrentlyOnCampus = true
        ),
        Vehicle(
            id = "veh_4",
            licensePlate = "9QRS456",
            make = "Kawasaki",
            model = "Ninja 400",
            color = "Lime Green",
            year = 2022,
            vehicleType = VehicleType.TWO_WHEELER,
            ownerId = "usr_stu_552",
            ownerName = "Jordan Bell",
            activePermitId = "perm_moto_12",
            isCurrentlyOnCampus = true
        )
    )

    private val mutablePermits = mutableListOf(
        ParkingPermit(
            id = "perm_001",
            permitNumber = "CPVTS-2026-F-8812",
            vehiclePlate = "7XYZ890",
            vehicleModel = "Toyota RAV4 Hybrid",
            userId = demoUser.id,
            userName = demoUser.name,
            permitType = PermitType.SEMESTER,
            allowedZone = "Zone A (North), Zone C (West)",
            validFrom = "2026-08-15",
            validTo = "2026-12-20",
            status = PermitStatus.ACTIVE,
            qrCodePayload = "CPVTS|PERMIT:8812|PLATE:7XYZ890|VALID:20261220"
        ),
        ParkingPermit(
            id = "perm_002",
            permitNumber = "CPVTS-2026-EV-9401",
            vehiclePlate = "7XYZ890",
            vehicleModel = "Toyota RAV4 Hybrid",
            userId = demoUser.id,
            userName = demoUser.name,
            permitType = PermitType.EV_CHARGING,
            allowedZone = "All Campus EV Hubs",
            validFrom = "2026-09-01",
            validTo = "2026-12-31",
            status = PermitStatus.PENDING,
            qrCodePayload = "CPVTS|EV:9401|PLATE:7XYZ890|STATUS:PENDING"
        ),
        ParkingPermit(
            id = "perm_003",
            permitNumber = "CPVTS-2026-SUM-1200",
            vehiclePlate = "6ABC321",
            vehicleModel = "Honda Civic",
            userId = demoUser.id,
            userName = demoUser.name,
            permitType = PermitType.DAILY,
            allowedZone = "Zone D (South)",
            validFrom = "2026-06-01",
            validTo = "2026-08-10",
            status = PermitStatus.EXPIRED,
            qrCodePayload = "CPVTS|DAILY:1200|PLATE:6ABC321|EXPIRED"
        )
    )

    private val mutableActiveTracking = mutableListOf(
        ActiveTracking(
            id = "track_101",
            licensePlate = "7XYZ890",
            vehicleDescription = "Toyota RAV4 (Silver)",
            entryGate = "North Gate (Main Entry)",
            entryTime = "08:14 AM Today",
            parkLocationLot = "North Campus Deck (Level 2, A-24)",
            driverName = "Alex Chen",
            driverRole = "Student",
            hasValidPermit = true
        ),
        ActiveTracking(
            id = "track_102",
            licensePlate = "5MNO789",
            vehicleDescription = "Tesla Model 3 (Midnight Pearl)",
            entryGate = "West Gate (Engineering)",
            entryTime = "08:42 AM Today",
            parkLocationLot = "Science & Tech Complex (EV-04)",
            driverName = "Dr. Marcus Vance",
            driverRole = "Faculty",
            hasValidPermit = true
        ),
        ActiveTracking(
            id = "track_103",
            licensePlate = "4KLM999",
            vehicleDescription = "Ford F-150 (Black)",
            entryGate = "South Gate (Arena)",
            entryTime = "09:05 AM Today",
            parkLocationLot = "South Athletics & Arena Lot",
            driverName = "Visitor - Vendor",
            driverRole = "Visitor",
            hasValidPermit = false
        ),
        ActiveTracking(
            id = "track_104",
            licensePlate = "9QRS456",
            vehicleDescription = "Kawasaki Ninja 400 (Green)",
            entryGate = "East Gate (Medical)",
            entryTime = "09:30 AM Today",
            parkLocationLot = "Central Library Core (M-02)",
            driverName = "Jordan Bell",
            driverRole = "Student",
            hasValidPermit = true
        )
    )

    private val mutableTrackingLogs = mutableListOf(
        TrackingLog(
            id = "log_1",
            licensePlate = "7XYZ890",
            gate = "North Gate (Main Entry)",
            action = "ENTRY",
            timestamp = "Today 08:14:22",
            permitChecked = true,
            officerId = "OFF-402"
        ),
        TrackingLog(
            id = "log_2",
            licensePlate = "3WER123",
            gate = "South Gate",
            action = "EXIT",
            timestamp = "Today 08:35:10",
            permitChecked = true,
            officerId = "OFF-109"
        ),
        TrackingLog(
            id = "log_3",
            licensePlate = "5MNO789",
            gate = "West Gate (Engineering)",
            action = "ENTRY",
            timestamp = "Today 08:42:04",
            permitChecked = true,
            officerId = "OFF-312"
        ),
        TrackingLog(
            id = "log_4",
            licensePlate = "8KLP771",
            gate = "North Gate",
            action = "EXIT",
            timestamp = "Today 09:12:44",
            permitChecked = true,
            officerId = "OFF-402"
        )
    )

    private val mutableViolations = mutableListOf(
        ViolationCitation(
            id = "viol_001",
            citationNumber = "VIO-2026-0891",
            licensePlate = "4KLM999",
            lotName = "North Campus Deck",
            violationType = "Parked in Fire Lane / Restricted Zone",
            fineAmount = 1000.00,
            issuedAt = "2026-10-08 09:15",
            officerNotes = "Vehicle blocking emergency service egress near stairwell B.",
            status = ViolationStatus.UNPAID
        ),
        ViolationCitation(
            id = "viol_002",
            citationNumber = "VIO-2026-0840",
            licensePlate = "6ABC321",
            lotName = "Central Library Core",
            violationType = "No Active Permit Displayed / Expired Daily Pass",
            fineAmount = 500.00,
            issuedAt = "2026-10-05 14:22",
            officerNotes = "Daily pass expired at 12:00 PM; vehicle parked at 14:22.",
            status = ViolationStatus.APPEALED,
            appealReason = "Attended extended academic thesis review with advisor."
        ),
        ViolationCitation(
            id = "viol_003",
            citationNumber = "VIO-2026-0799",
            licensePlate = "1ZZZ999",
            lotName = "Science & Tech Complex",
            violationType = "EV Spot Abuse (Non-EV or Not Charging)",
            fineAmount = 750.00,
            issuedAt = "2026-10-02 11:00",
            officerNotes = "Internal combustion engine vehicle parked in designated charging stall.",
            status = ViolationStatus.PAID
        )
    )

    // Data Access Methods for Demo Mode
    fun getLots(): List<ParkingLot> = mutableLots.toList()

    fun getLot(id: String): ParkingLot? = mutableLots.find { it.id == id }

    fun getSlotsForLot(lotId: String): List<ParkingSlot> {
        val lot = mutableLots.find { it.id == lotId } ?: return emptyList()
        val slots = mutableListOf<ParkingSlot>()
        val total = lot.totalCapacity.coerceAtMost(30) // Show representative 30 slots for UI
        for (i in 1..total) {
            val type = when {
                i <= lot.accessibleSlots.coerceAtMost(4) -> SlotType.ACCESSIBLE
                i <= (lot.accessibleSlots + lot.evSlots).coerceAtMost(8) -> SlotType.EV
                else -> SlotType.STANDARD
            }
            val isOccupied = i <= (total * lot.occupancyPercentage / 100)
            slots.add(
                ParkingSlot(
                    id = "${lot.code}-$i",
                    lotId = lotId,
                    slotNumber = "${lot.code}-${i.toString().padStart(2, '0')}",
                    type = type,
                    isOccupied = isOccupied,
                    occupiedByPlate = if (isOccupied) "CA-${(100..999).random()}-X" else null
                )
            )
        }
        return slots
    }

    fun getVehicles(): List<Vehicle> = mutableVehicles.toList()

    fun addVehicle(request: com.example.data.model.CreateVehicleRequest): Vehicle {
        val newVehicle = Vehicle(
            id = "veh_${UUID.randomUUID().toString().take(6)}",
            licensePlate = request.licensePlate.uppercase().trim(),
            make = request.make.trim(),
            model = request.model.trim(),
            color = request.color.trim(),
            year = request.year,
            vehicleType = request.vehicleType,
            ownerId = demoUser.id,
            ownerName = demoUser.name,
            activePermitId = null,
            isCurrentlyOnCampus = false
        )
        mutableVehicles.add(0, newVehicle)
        return newVehicle
    }

    fun deleteVehicle(id: String): Boolean {
        return mutableVehicles.removeIf { it.id == id }
    }

    fun getPermits(): List<ParkingPermit> = mutablePermits.toList()

    fun applyPermit(request: com.example.data.model.ApplyPermitRequest): ParkingPermit {
        val num = (1000..9999).random()
        val newPermit = ParkingPermit(
            id = "perm_${UUID.randomUUID().toString().take(6)}",
            permitNumber = "CPVTS-2026-APP-$num",
            vehiclePlate = request.vehiclePlate.uppercase().trim(),
            userId = demoUser.id,
            userName = demoUser.name,
            permitType = request.permitType,
            allowedZone = request.allowedZone,
            validFrom = "2026-10-09",
            validTo = "2027-02-15",
            status = PermitStatus.PENDING,
            qrCodePayload = "CPVTS|APP:$num|PLATE:${request.vehiclePlate}|PENDING"
        )
        mutablePermits.add(0, newPermit)
        return newPermit
    }

    fun updatePermitStatus(id: String, status: PermitStatus): ParkingPermit? {
        val idx = mutablePermits.indexOfFirst { it.id == id }
        if (idx != -1) {
            val updated = mutablePermits[idx].copy(status = status)
            mutablePermits[idx] = updated
            return updated
        }
        return null
    }

    fun getActiveTracking(): List<ActiveTracking> = mutableActiveTracking.toList()

    fun logGateEntry(request: com.example.data.model.GateEntryRequest): ActiveTracking {
        val plate = request.licensePlate.uppercase().trim()
        val tracking = ActiveTracking(
            id = "track_${UUID.randomUUID().toString().take(6)}",
            licensePlate = plate,
            vehicleDescription = "Tracked Vehicle ($plate)",
            entryGate = request.gateName,
            entryTime = "Just Now",
            parkLocationLot = request.assignedLot ?: "General Campus Parking",
            driverName = "Verified Campus Visitor/Member",
            driverRole = "Driver",
            hasValidPermit = true
        )
        mutableActiveTracking.add(0, tracking)
        mutableTrackingLogs.add(
            0,
            TrackingLog(
                id = "log_${UUID.randomUUID().toString().take(6)}",
                licensePlate = plate,
                gate = request.gateName,
                action = "ENTRY",
                timestamp = "Just Now",
                permitChecked = true,
                officerId = "OFF-CAMPUS"
            )
        )
        return tracking
    }

    fun logGateExit(plate: String, gate: String): Boolean {
        val cleanPlate = plate.uppercase().trim()
        val removed = mutableActiveTracking.removeIf { it.licensePlate.equals(cleanPlate, ignoreCase = true) }
        mutableTrackingLogs.add(
            0,
            TrackingLog(
                id = "log_${UUID.randomUUID().toString().take(6)}",
                licensePlate = cleanPlate,
                gate = gate,
                action = "EXIT",
                timestamp = "Just Now",
                permitChecked = true,
                officerId = "OFF-CAMPUS"
            )
        )
        return removed || true
    }

    fun getTrackingHistory(): List<TrackingLog> = mutableTrackingLogs.toList()

    fun getViolations(): List<ViolationCitation> = mutableViolations.toList()

    fun issueViolation(request: com.example.data.model.CreateViolationRequest): ViolationCitation {
        val num = (1000..9999).random()
        val citation = ViolationCitation(
            id = "viol_${UUID.randomUUID().toString().take(6)}",
            citationNumber = "VIO-2026-$num",
            licensePlate = request.licensePlate.uppercase().trim(),
            lotName = request.lotName,
            violationType = request.violationType,
            fineAmount = request.fineAmount,
            issuedAt = "Just Now",
            officerNotes = request.officerNotes,
            status = ViolationStatus.UNPAID
        )
        mutableViolations.add(0, citation)
        return citation
    }

    fun payViolation(id: String): ViolationCitation? {
        val idx = mutableViolations.indexOfFirst { it.id == id }
        if (idx != -1) {
            val updated = mutableViolations[idx].copy(status = ViolationStatus.PAID)
            mutableViolations[idx] = updated
            return updated
        }
        return null
    }

    fun appealViolation(id: String, reason: String): ViolationCitation? {
        val idx = mutableViolations.indexOfFirst { it.id == id }
        if (idx != -1) {
            val updated = mutableViolations[idx].copy(
                status = ViolationStatus.APPEALED,
                appealReason = reason
            )
            mutableViolations[idx] = updated
            return updated
        }
        return null
    }

    fun getDashboardStats(): CampusDashboardStats {
        val lots = mutableLots
        val totalCap = lots.sumOf { it.totalCapacity }
        val totalOcc = lots.sumOf { it.occupiedCount }
        return CampusDashboardStats(
            totalLots = lots.size,
            totalCapacity = totalCap,
            totalOccupied = totalOcc,
            activeOnCampusVehicles = mutableActiveTracking.size,
            totalPermitsActive = mutablePermits.count { it.status == PermitStatus.ACTIVE },
            pendingPermitApplications = mutablePermits.count { it.status == PermitStatus.PENDING },
            todayViolationsCount = mutableViolations.count { it.status == ViolationStatus.UNPAID },
            todayGateEntries = 412,
            todayGateExits = 289
        )
    }

    fun findVehicleByPlate(plate: String): Vehicle? {
        val clean = plate.trim().uppercase()
        return mutableVehicles.find { it.licensePlate.equals(clean, ignoreCase = true) }
    }
}
