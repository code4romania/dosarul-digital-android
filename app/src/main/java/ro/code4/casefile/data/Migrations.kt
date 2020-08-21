package ro.code4.casefile.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    /*
    According to https://developer.android.com/reference/android/database/sqlite/package-summary Android has old SQLite.
    For example, you can't rename column without recreating table
     */

    val ALL: Array<Migration> = arrayOf()
}
