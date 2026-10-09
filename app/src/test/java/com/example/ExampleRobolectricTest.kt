package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.ApiConfig
import com.example.data.api.DataSourceMode
import com.example.data.model.CreateVehicleRequest
import com.example.data.model.VehicleType
import com.example.data.repository.CpvtsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext<Context>()
    ApiConfig.init(context)
  }

  @Test
  fun `read app name string from context matches CPVTS`() {
    val appName = context.getString(R.string.app_name)
    assertEquals("CPVTS", appName)
  }

  @Test
  fun `api config defaults and url normalization`() {
    assertEquals(DataSourceMode.DEMO, ApiConfig.modeState.value)
    assertTrue(ApiConfig.baseUrlState.value.endsWith("/"))
    
    ApiConfig.setBaseUrl("https://example-cpvts.edu/api")
    assertEquals("https://example-cpvts.edu/api/", ApiConfig.baseUrlState.value)
  }

  @Test
  fun `repository loads lots and vehicles in demo mode`() = runBlocking {
    val repository = CpvtsRepository()
    val lotsResult = repository.getLots()
    assertTrue(lotsResult.isSuccess)
    val lots = lotsResult.getOrThrow()
    assertTrue(lots.isNotEmpty())

    val vehResult = repository.getVehicles()
    assertTrue(vehResult.isSuccess)
    val vehicles = vehResult.getOrThrow()
    assertTrue(vehicles.isNotEmpty())
  }

  @Test
  fun `vehicle registration adds vehicle to repository in demo mode`() = runBlocking {
    val repository = CpvtsRepository()
    val request = CreateVehicleRequest(
      licensePlate = "TEST-999",
      make = "Tesla",
      model = "Model Y",
      color = "White",
      year = 2024,
      vehicleType = VehicleType.EV
    )
    val registerResult = repository.registerVehicle(request)
    assertTrue(registerResult.isSuccess)
    val added = registerResult.getOrThrow()
    assertEquals("TEST-999", added.licensePlate)

    val lookup = repository.lookupVehicle("TEST-999")
    assertTrue(lookup.isSuccess)
    assertNotNull(lookup.getOrNull())
  }
}
