package com.droidcon.mocktest.support

import android.content.Context
import androidx.core.content.edit
import com.droidcon.mocktest.R

/**
 * Provides access to SharedPreferences for location to Activities and Services.
 */
object SharedPreferenceUtil {

    private const val LAST_SPEED_KEY = "last_speed"
    private const val LAST_ACCURACY_KEY = "last_accuracy"

    /**
     * Returns the last speed selected.
     *
     * @param context The [Context].
     */
    fun getLastSpeed(context: Context): Int =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        ).getInt(LAST_SPEED_KEY, 50)

    /**
     * Stores the last speed selected state in SharedPreferences.
     *
     * @param context The [Context].
     * @param speed The last speed selected.
     */
    fun saveLastSpeed(context: Context, speed: Int) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        ).edit {
            putInt(LAST_SPEED_KEY, speed)
        }

    /**
     * Returns the last accuracy selected.
     *
     * @param context The [Context].
     */
    fun getLastAccuracy(context: Context): Int =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        ).getInt(LAST_ACCURACY_KEY, 3)

    /**
     * Stores the last accuracy selected state in SharedPreferences.
     *
     * @param context The [Context].
     * @param accuracy The last accuracy selected.
     */
    fun saveLastAccuracy(context: Context, accuracy: Int) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        ).edit {
            putInt(LAST_ACCURACY_KEY, accuracy)
        }
}
