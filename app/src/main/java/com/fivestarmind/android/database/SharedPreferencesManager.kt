package com.fivestarmind.android.database

import android.content.Context
import android.content.SharedPreferences
import com.fivestarmind.android.interfaces.SharedPrefConst

/**
 * This is the SharedPreferences' Singleton class used in this project.
 */
internal class SharedPreferencesManager private constructor(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(SharedPrefConst.PREF.LOGIN_CREDENTIAL, Context.MODE_PRIVATE)

    companion object {
        private var singleton: SharedPreferencesManager? = null

        /**
         * The global default [SharedPreferencesManager] instance.
         */
        fun with(context: Context): SharedPreferencesManager? {
            if (singleton == null) {
                synchronized(SharedPreferencesManager::class.java) {
                    if (singleton == null) {
                        singleton = Builder(context).build()
                    }
                }
            }

            return singleton
        }
    }

    /**
     * Retrieve all values from the preferences.
     *
     * Note that you *must not* modify the collection returned
     * by this method, or alter any of its contents.  The consistency of your
     * stored data is not guaranteed if you do.

     * @return Returns a map containing a list of pairs key/value representing the preferences.
     * *
     * @throws NullPointerException
     */
    val all: Map<String, *>
        get() = preferences.all

    /**
     * Retrieve a String value from the preferences.

     * @param key The name of the preference to retrieve.
     * *
     * @param defValue Value to return if this preference does not exist.
     * *
     * @return Returns the preference value if it exists, or defValue.  Throws
     * * ClassCastException if there is a preference with this name that is not a String.
     * *
     * @throws ClassCastException
     */
    fun getString(key: String, defValue: String): String? = preferences.getString(key, defValue)

    /**
     * Retrieve a set of String values from the preferences.
     *
     * Note that you *must not* modify the set instance returned
     * by this call.  The consistency of the stored data is not guaranteed
     * if you do, nor is your ability to modify the instance at all.

     * @param key       The name of the preference to retrieve.
     * *
     * @param defValues Values to return if this preference does not exist.
     * *
     * @return Returns the preference values if they exist, or defValues.
     * * Throws ClassCastException if there is a preference with this name that is not a Set.
     * *
     * @throws ClassCastException
     */
    fun getStringSet(key: String, defValues: Set<String>): Set<String>? =
        preferences.getStringSet(key, defValues)

    /**
     * Retrieve an int value from the preferences.

     * @param key      The name of the preference to retrieve.
     * *
     * @param defValue Value to return if this preference does not exist.
     * *
     * @return Returns the preference value if it exists, or defValue.  Throws
     * * ClassCastException if there is a preference with this name that is not an int.
     * *
     * @throws ClassCastException
     */
    fun getInt(key: String, defValue: Int): Int = preferences.getInt(key, defValue)

    /**
     * Retrieve a long value from the preferences.

     * @param key      The name of the preference to retrieve.
     * *
     * @param defValue Value to return if this preference does not exist.
     * *
     * @return Returns the preference value if it exists, or defValue.  Throws
     * * ClassCastException if there is a preference with this name that is not a long.
     * *
     * @throws ClassCastException
     */
    fun getLong(key: String, defValue: Long): Long = preferences.getLong(key, defValue)

    /**
     * Retrieve a float value from the preferences.

     * @param key      The name of the preference to retrieve.
     * *
     * @param defValue Value to return if this preference does not exist.
     * *
     * @return Returns the preference value if it exists, or defValue.  Throws
     * * ClassCastException if there is a preference with this name that is not a float.
     * *
     * @throws ClassCastException
     */
    fun getFloat(key: String, defValue: Float): Float = preferences.getFloat(key, defValue)

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     *
     * @param defValue Value to return if this preference does not exist.
     *
     * @return Returns the preference value if it exists, or defValue.  Throws
     *  ClassCastException if there is a preference with this name that is not a boolean.
     *
     * @throws ClassCastException
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean = preferences.getBoolean(key, defValue)

    /**
     * Checks whether the preferences contains a preference.
     *
     * @param key The name of the preference to a_check.
     *
     * @return Returns true if the preference exists in the preferences,
     * otherwise false.
     */
    operator fun contains(key: String): Boolean = preferences.contains(key)

    /**
     * Create a new Editor for these preferences, through which you can make
     * modifications to the data in the preferences and atomically commit those
     * changes back to the SharedPreferences object.
     *
     * Note that you *must* call [SharedPreferences.Editor.apply] to have any
     * changes you perform in the Editor actually show up in the SharedPreferences.
     *
     * @return Returns a new instance of the [SharedPreferences.Editor] interface, allowing
     * you to modify the values in this SharedPreferences object.
     */
    fun edit(): SharedPreferences.Editor = preferences.edit()

    /**
     * Registers a callback to be invoked when a change happens to a preference.
     *
     * @param listener The callback that will run.
     *
     * @see .unregisterOnSharedPreferenceChangeListener
     */
    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        preferences.registerOnSharedPreferenceChangeListener(listener)

    /**
     * Unregisters a previous callback.

     * @param listener The callback that should be unregistered.
     * *
     * @see .registerOnSharedPreferenceChangeListener
     */
    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        preferences.unregisterOnSharedPreferenceChangeListener(listener)

    /**
     * Fluent API for creating [SharedPreferencesManager] instances.
     */
    private class Builder(context: Context?) {
        private val context: Context

        init {
            requireNotNull(context) { "Context must not be null." }

            this.context = context.applicationContext
        }

        /**
         * Create the [SharedPreferencesManager] instance.
         */
        fun build(): SharedPreferencesManager = SharedPreferencesManager(context)
    }
}
