package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AiChatMessageEntity
import com.example.data.model.EcoActionEntity
import com.example.data.model.ImpactMetricEntity
import com.example.data.model.MediaTestimonialEntity
import com.example.data.model.NewsArticleEntity
import com.example.data.model.OrgInfoEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TrainingApplicationEntity
import com.example.data.model.TrainingEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.VolunteerRegistrationEntity

@Database(
    entities = [
        NewsArticleEntity::class,
        EcoActionEntity::class,
        ProjectEntity::class,
        TrainingEntity::class,
        VolunteerRegistrationEntity::class,
        TrainingApplicationEntity::class,
        ImpactMetricEntity::class,
        MediaTestimonialEntity::class,
        OrgInfoEntity::class,
        UserProfileEntity::class,
        AiChatMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AilDatabase : RoomDatabase() {
    abstract fun ailDao(): AilDao

    companion object {
        @Volatile
        private var INSTANCE: AilDatabase? = null

        fun getDatabase(context: Context): AilDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AilDatabase::class.java,
                    "ail4c_app_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
