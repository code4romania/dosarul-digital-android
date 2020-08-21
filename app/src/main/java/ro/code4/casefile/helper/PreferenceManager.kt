package ro.code4.casefile.helper

import android.content.SharedPreferences
import ro.code4.casefile.BuildConfig

const val PREFS_TOKEN = "PREFS_TOKEN"
const val PREFS_VERIFIED_AUTH = "PREFS_VERIFIED_AUTH"
const val ONBOARDING_COMPLETED = "ONBOARDING_COMPLETED"
const val CHANGED_PASSWORD = "CHANGED_PASSWORD"
const val PREFS_LANGUAGE_CODE = "PREFS_LANGUAGE_CODE"

fun SharedPreferences.getString(key: String): String? = getString(key, null)
fun SharedPreferences.getInt(key: String): Int = getInt(key, 0)

fun SharedPreferences.putString(key: String, value: String?) {
    val editor = edit()
    editor.putString(key, value)
    editor.apply()
}

fun SharedPreferences.putInt(key: String, value: Int) {
    val editor = edit()
    editor.putInt(key, value)
    editor.apply()
}

fun SharedPreferences.putBoolean(key: String, value: Boolean = true) {
    val editor = edit()
    editor.putBoolean(key, value)
    editor.apply()
}

fun SharedPreferences.getToken(): String? = getString(PREFS_TOKEN)
fun SharedPreferences.saveToken(token: String) = putString(PREFS_TOKEN, token)
fun SharedPreferences.deleteToken() = putString(PREFS_TOKEN, null)

fun SharedPreferences.isAuthenticationVerified() = getBoolean(PREFS_VERIFIED_AUTH, false)
fun SharedPreferences.verifiedAuthentication() = putBoolean(PREFS_VERIFIED_AUTH, true)
fun SharedPreferences.resetAuthenticationVerification() = putBoolean(PREFS_VERIFIED_AUTH, false)

fun SharedPreferences.hasCompletedOnboarding() = getBoolean(ONBOARDING_COMPLETED, false)
fun SharedPreferences.completedOnboarding() = putBoolean(ONBOARDING_COMPLETED)
fun SharedPreferences.resetHasCompletedOnboarding() = putBoolean(ONBOARDING_COMPLETED, false)

fun SharedPreferences.hasChangedPassword() = getBoolean(CHANGED_PASSWORD, false)
fun SharedPreferences.changedPassword() = putBoolean(CHANGED_PASSWORD)
fun SharedPreferences.resetHasChangedPassword() = putBoolean(CHANGED_PASSWORD, false)

fun SharedPreferences.getLocaleCode(): String =
    getString(PREFS_LANGUAGE_CODE, BuildConfig.PREFERRED_LOCALE) ?: BuildConfig.PREFERRED_LOCALE

fun SharedPreferences.setLocaleCode(code: String) = putString(PREFS_LANGUAGE_CODE, code)
