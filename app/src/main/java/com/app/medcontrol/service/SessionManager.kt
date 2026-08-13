package com.app.medcontrol.service

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.medcontrol.model.TipoUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class UserSession(
    val userId: Int?,
    val userType: TipoUsuario?
)

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_TYPE = stringPreferencesKey("user_type")
    }

    val userSession: Flow<UserSession> = dataStore.data.map { preferences ->
        val userId = preferences[USER_ID]
        val userTypeStr = preferences[USER_TYPE]
        val userType = userTypeStr?.let {
            try { TipoUsuario.valueOf(it) } catch (e: Exception) { null }
        }
        UserSession(userId, userType)
    }

    suspend fun saveSession(userId: Int, userType: TipoUsuario) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_TYPE] = userType.name
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_TYPE)
        }
    }

    suspend fun getSessionOnce(): UserSession {
        return userSession.first()
    }
}
