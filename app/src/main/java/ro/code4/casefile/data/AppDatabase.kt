package ro.code4.casefile.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ro.code4.casefile.data.dao.*
import ro.code4.casefile.data.helper.DateConverter
import ro.code4.casefile.data.helper.EnumConverters
import ro.code4.casefile.data.model.*
import ro.code4.casefile.data.model.answers.AnsweredQuestion
import ro.code4.casefile.data.model.answers.SelectedAnswer

@Database(
    entities = [Patient::class, FormDetails::class, Section::class, Question::class, Answer::class,
        AnsweredQuestion::class, SelectedAnswer::class, Note::class, County::class, City::class, PatientDetails::class,
        PatientForm::class, PatientDetailsFamilyMember::class],
    version = 1
)
@TypeConverters(DateConverter::class, EnumConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientsDao(): PatientsDao
    abstract fun patientDetailsDao(): PatientDetailsDao
    abstract fun formDetailsDao(): FormsDao
    abstract fun cleanupDao(): CleanupDao
    abstract fun countiesDao(): CountiesDao
    abstract fun citiesDao(): CitiesDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "database"
            ).addMigrations(*Migrations.ALL)
                .build()
    }
}
