package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AiChatMessageEntity
import com.example.data.model.EcoActionEntity
import com.example.data.model.EcoActivityRecordEntity
import com.example.data.model.ImpactMetricEntity
import com.example.data.model.MediaTestimonialEntity
import com.example.data.model.MentorTrainerEntity
import com.example.data.model.NewsArticleEntity
import com.example.data.model.OrgInfoEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TrainingApplicationEntity
import com.example.data.model.TrainingEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserBadgeEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.VolunteerRegistrationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AilDao {

    // --- News Articles ---
    @Query("SELECT * FROM news_articles ORDER BY id DESC")
    fun getAllNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE isPublished = 1 ORDER BY id DESC")
    fun getPublishedNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE isFeatured = 1 AND isPublished = 1 ORDER BY id DESC LIMIT 5")
    fun getFeaturedNews(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE id = :id")
    suspend fun getNewsById(id: Long): NewsArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsArticleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNews(newsList: List<NewsArticleEntity>)

    @Update
    suspend fun updateNews(news: NewsArticleEntity)

    @Delete
    suspend fun deleteNews(news: NewsArticleEntity)

    @Query("DELETE FROM news_articles WHERE id = :id")
    suspend fun deleteNewsById(id: Long)

    // --- Eco Actions & Events ---
    @Query("SELECT * FROM eco_actions ORDER BY id DESC")
    fun getAllActions(): Flow<List<EcoActionEntity>>

    @Query("SELECT * FROM eco_actions WHERE id = :id")
    suspend fun getActionById(id: Long): EcoActionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: EcoActionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllActions(actions: List<EcoActionEntity>)

    @Update
    suspend fun updateAction(action: EcoActionEntity)

    @Delete
    suspend fun deleteAction(action: EcoActionEntity)

    @Query("DELETE FROM eco_actions WHERE id = :id")
    suspend fun deleteActionById(id: Long)

    // --- Projects & Crowdfunding ---
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProjects(projects: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    // --- Trainings ---
    @Query("SELECT * FROM trainings ORDER BY id DESC")
    fun getAllTrainings(): Flow<List<TrainingEntity>>

    @Query("SELECT * FROM trainings WHERE id = :id")
    suspend fun getTrainingById(id: Long): TrainingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: TrainingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrainings(trainings: List<TrainingEntity>)

    @Update
    suspend fun updateTraining(training: TrainingEntity)

    @Delete
    suspend fun deleteTraining(training: TrainingEntity)

    @Query("DELETE FROM trainings WHERE id = :id")
    suspend fun deleteTrainingById(id: Long)

    // --- Volunteer Registrations ---
    @Query("SELECT * FROM volunteer_registrations ORDER BY id DESC")
    fun getAllVolunteerRegistrations(): Flow<List<VolunteerRegistrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteerRegistration(registration: VolunteerRegistrationEntity): Long

    @Update
    suspend fun updateVolunteerRegistration(registration: VolunteerRegistrationEntity)

    @Delete
    suspend fun deleteVolunteerRegistration(registration: VolunteerRegistrationEntity)

    @Query("DELETE FROM volunteer_registrations WHERE id = :id")
    suspend fun deleteVolunteerRegistrationById(id: Long)

    // --- Training Applications ---
    @Query("SELECT * FROM training_applications ORDER BY id DESC")
    fun getAllTrainingApplications(): Flow<List<TrainingApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingApplication(application: TrainingApplicationEntity): Long

    @Update
    suspend fun updateTrainingApplication(application: TrainingApplicationEntity)

    @Delete
    suspend fun deleteTrainingApplication(application: TrainingApplicationEntity)

    @Query("DELETE FROM training_applications WHERE id = :id")
    suspend fun deleteTrainingApplicationById(id: Long)

    // --- Impact Metrics ---
    @Query("SELECT * FROM impact_metrics")
    fun getAllImpactMetrics(): Flow<List<ImpactMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMetric(metric: ImpactMetricEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMetrics(metrics: List<ImpactMetricEntity>)

    @Update
    suspend fun updateMetric(metric: ImpactMetricEntity)

    // --- Media & Testimonials ---
    @Query("SELECT * FROM media_testimonials ORDER BY id DESC")
    fun getAllMediaTestimonials(): Flow<List<MediaTestimonialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaTestimonial(item: MediaTestimonialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMediaTestimonials(items: List<MediaTestimonialEntity>)

    @Update
    suspend fun updateMediaTestimonial(item: MediaTestimonialEntity)

    @Delete
    suspend fun deleteMediaTestimonial(item: MediaTestimonialEntity)

    @Query("DELETE FROM media_testimonials WHERE id = :id")
    suspend fun deleteMediaTestimonialById(id: Long)

    // --- Mentors & Formateurs ---
    @Query("SELECT * FROM mentors_trainers ORDER BY displayOrder ASC, id ASC")
    fun getAllMentorsTrainers(): Flow<List<MentorTrainerEntity>>

    @Query("SELECT * FROM mentors_trainers WHERE isAvailableForMentoring = 1 ORDER BY displayOrder ASC, id ASC")
    fun getAvailableMentors(): Flow<List<MentorTrainerEntity>>

    @Query("SELECT * FROM mentors_trainers WHERE id = :id")
    suspend fun getMentorTrainerById(id: Long): MentorTrainerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMentorTrainer(mentor: MentorTrainerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMentorsTrainers(mentors: List<MentorTrainerEntity>)

    @Update
    suspend fun updateMentorTrainer(mentor: MentorTrainerEntity)

    @Delete
    suspend fun deleteMentorTrainer(mentor: MentorTrainerEntity)

    @Query("DELETE FROM mentors_trainers WHERE id = :id")
    suspend fun deleteMentorTrainerById(id: Long)

    @Query("DELETE FROM mentors_trainers")
    suspend fun deleteAllMentorsTrainers()

    // --- Bulk Clear Queries for Sync ---
    @Query("DELETE FROM news_articles")
    suspend fun deleteAllNews()

    @Query("DELETE FROM eco_actions")
    suspend fun deleteAllActions()

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects()

    @Query("DELETE FROM trainings")
    suspend fun deleteAllTrainings()

    @Query("DELETE FROM media_testimonials")
    suspend fun deleteAllMediaTestimonials()

    @Query("DELETE FROM impact_metrics")
    suspend fun deleteAllMetrics()

    // --- Org Information & Config ---
    @Query("SELECT * FROM org_info")
    fun getAllOrgInfo(): Flow<List<OrgInfoEntity>>

    @Query("SELECT value FROM org_info WHERE `key` = :key")
    suspend fun getOrgInfoValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setOrgInfo(info: OrgInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAllOrgInfo(infoList: List<OrgInfoEntity>)

    // --- Registered User Accounts ---
    @Query("SELECT * FROM user_accounts ORDER BY registeredTimestamp DESC")
    fun getAllUserAccounts(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserAccountById(id: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserAccountByPhone(phone: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserAccountByEmail(email: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAccount(account: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUserAccounts(accounts: List<UserAccountEntity>)

    // --- User Profile & Auth ---
    @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
    fun getCurrentUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
    suspend fun getCurrentUser(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()

    // --- AI Assistant Chat History ---
    @Query("SELECT * FROM ai_chat_messages ORDER BY id ASC")
    fun getAllAiMessages(): Flow<List<AiChatMessageEntity>>

    @Query("SELECT * FROM ai_chat_messages ORDER BY id ASC")
    suspend fun getAllAiMessagesList(): List<AiChatMessageEntity>

    @Query("SELECT COUNT(*) FROM ai_chat_messages WHERE isFromUser = 1")
    suspend fun getUserMessageCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMessage(message: AiChatMessageEntity): Long

    @Query("DELETE FROM ai_chat_messages")
    suspend fun clearAiMessages()

    // --- Eco Activities Records ---
    @Query("SELECT * FROM eco_activities ORDER BY completedTimestamp DESC")
    fun getAllEcoActivities(): Flow<List<EcoActivityRecordEntity>>

    @Query("SELECT * FROM eco_activities ORDER BY completedTimestamp DESC")
    suspend fun getAllEcoActivitiesList(): List<EcoActivityRecordEntity>

    @Query("SELECT * FROM eco_activities WHERE activityKey LIKE :keyPattern LIMIT 1")
    suspend fun getEcoActivityByKeyPattern(keyPattern: String): EcoActivityRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEcoActivity(activity: EcoActivityRecordEntity): Long

    @Query("DELETE FROM eco_activities")
    suspend fun deleteAllEcoActivities()

    // --- User Badges ---
    @Query("SELECT * FROM user_badges ORDER BY requiredPoints ASC")
    fun getAllBadges(): Flow<List<UserBadgeEntity>>

    @Query("SELECT * FROM user_badges WHERE badgeId = :badgeId LIMIT 1")
    suspend fun getBadgeById(badgeId: String): UserBadgeEntity?

    @Query("SELECT * FROM user_badges WHERE isUnlocked = 1 ORDER BY requiredPoints ASC")
    fun getUnlockedBadges(): Flow<List<UserBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: UserBadgeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBadges(badges: List<UserBadgeEntity>)

    @Update
    suspend fun updateBadge(badge: UserBadgeEntity)
}

