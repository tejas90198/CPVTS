package com.example.data.api

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DataSourceMode {
    DEMO,   // Offline / Preview mode with comprehensive sample campus dataset
    LIVE    // Real HTTP REST requests to the configured backend API
}

/**
 * Single source of truth for CPVTS API Base URL, authentication token,
 * and Live vs Demo mode switching.
 */
object ApiConfig {
    // Default Base URL provided in development environment
    const val DEFAULT_BASE_URL = "https://ais-dev-nr3caiopbuhcxqgpwdqkwn-515432023679.asia-east1.run.app/"
    private const val PREFS_NAME = "cpvts_config_prefs"
    private const val KEY_BASE_URL = "api_base_url"
    private const val KEY_DATA_MODE = "data_source_mode"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ROLE = "active_role"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private var prefs: SharedPreferences? = null

    private val _baseUrlState = MutableStateFlow(DEFAULT_BASE_URL)
    val baseUrlState: StateFlow<String> = _baseUrlState.asStateFlow()

    private val _modeState = MutableStateFlow(DataSourceMode.DEMO)
    val modeState: StateFlow<DataSourceMode> = _modeState.asStateFlow()

    private val _authTokenState = MutableStateFlow<String?>(null)
    val authTokenState: StateFlow<String?> = _authTokenState.asStateFlow()

    var activeRole: String = "STUDENT"
        private set

    var currentUserName: String = "Alex Chen"
        private set

    var currentUserEmail: String = "alex.chen@campus.edu"
        private set

    fun init(context: Context) {
        val p = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = p
        val savedUrl = p.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        _baseUrlState.value = normalizeUrl(savedUrl)

        val savedMode = p.getString(KEY_DATA_MODE, DataSourceMode.DEMO.name)
        _modeState.value = try {
            DataSourceMode.valueOf(savedMode ?: DataSourceMode.DEMO.name)
        } catch (_: Exception) {
            DataSourceMode.DEMO
        }

        _authTokenState.value = p.getString(KEY_AUTH_TOKEN, null)
        activeRole = p.getString(KEY_USER_ROLE, "STUDENT") ?: "STUDENT"
        currentUserName = p.getString(KEY_USER_NAME, "Alex Chen") ?: "Alex Chen"
        currentUserEmail = p.getString(KEY_USER_EMAIL, "alex.chen@campus.edu") ?: "alex.chen@campus.edu"
    }

    fun setBaseUrl(url: String) {
        val normalized = normalizeUrl(url)
        _baseUrlState.value = normalized
        prefs?.edit()?.putString(KEY_BASE_URL, normalized)?.apply()
    }

    fun setMode(mode: DataSourceMode) {
        _modeState.value = mode
        prefs?.edit()?.putString(KEY_DATA_MODE, mode.name)?.apply()
    }

    fun setAuthToken(token: String?) {
        _authTokenState.value = token
        prefs?.edit()?.putString(KEY_AUTH_TOKEN, token)?.apply()
    }

    fun setUserProfile(name: String, email: String, role: String) {
        activeRole = role
        currentUserName = name
        currentUserEmail = email
        prefs?.edit()
            ?.putString(KEY_USER_ROLE, role)
            ?.putString(KEY_USER_NAME, name)
            ?.putString(KEY_USER_EMAIL, email)
            ?.apply()
    }

    fun resetToDefaults() {
        setBaseUrl(DEFAULT_BASE_URL)
        setMode(DataSourceMode.DEMO)
        setAuthToken(null)
    }

    private fun normalizeUrl(url: String): String {
        var clean = url.trim()
        if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
            clean = "https://$clean"
        }
        if (!clean.endsWith("/")) {
            clean = "$clean/"
        }
        return clean
    }
}
