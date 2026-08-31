package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val summary: String,
    val content: String,
    val category: String, // Climat, Jeunesse, Reboisement, Partenariat, Événement
    val dateText: String,
    val imageResName: String,
    val author: String = "Cellule Communication AIL4C",
    val isFeatured: Boolean = false,
    val isPublished: Boolean = true,
    val viewsCount: Int = 0
)

@Entity(tableName = "eco_actions")
data class EcoActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // Reboisement, Salubrité, Sensibilisation, Climat
    val dateText: String,
    val timeText: String,
    val location: String, // Bouaké, etc.
    val status: String, // "À venir", "En cours", "Terminé"
    val maxSpots: Int,
    val registeredCount: Int = 0,
    val coordinatorName: String,
    val coordinatorContact: String,
    val recommendedGear: String,
    val imageResName: String
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val summary: String,
    val description: String,
    val targetBudget: Long, // in FCFA
    val raisedBudget: Long, // in FCFA
    val targetObjective: String, // e.g. "5 000 plants d'arbres", "200 jeunes formés"
    val status: String, // "Actif", "Financé", "En cours"
    val expectedImpact: String,
    val partnerName: String,
    val imageResName: String
)

@Entity(tableName = "trainings")
data class TrainingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val domain: String, // Agro-écologie, Recyclage & Éco-artisanat, Métiers Verts
    val duration: String,
    val startDateText: String,
    val location: String,
    val prerequisites: String,
    val certification: String,
    val spotsAvailable: Int,
    val isRegistrationOpen: Boolean = true,
    val description: String,
    val imageResName: String
)

@Entity(tableName = "volunteer_registrations")
data class VolunteerRegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phone: String,
    val email: String,
    val city: String,
    val actionId: Long? = null,
    val actionTitle: String,
    val availability: String, // "Weekends", "En semaine", "Temps plein"
    val motivation: String,
    val status: String = "Reçue", // "Reçue", "Validée", "Confirmée"
    val dateSubmitted: String
)

@Entity(tableName = "training_applications")
data class TrainingApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trainingId: Long,
    val trainingTitle: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val educationLevel: String,
    val motivation: String,
    val status: String = "En attente", // "En attente", "Acceptée", "Entretien convoqué"
    val dateSubmitted: String
)

@Entity(tableName = "impact_metrics")
data class ImpactMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val metricKey: String,
    val label: String,
    val valueNumber: Long,
    val unit: String,
    val iconKey: String
)

@Entity(tableName = "media_testimonials")
data class MediaTestimonialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val mediaType: String, // "Témoignage", "Photo", "Vidéo"
    val authorOrLocation: String,
    val descriptionOrQuote: String,
    val imageResName: String,
    val tag: String
)

@Entity(tableName = "org_info")
data class OrgInfoEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val fullName: String,
    val identifier: String, // email or phone number
    val authType: String = "PHONE", // "PHONE", "EMAIL", "GUEST"
    val phoneNumber: String = "",
    val email: String = "",
    val city: String = "Bouaké",
    val quartier: String = "Commerce",
    val ecoPoints: Int = 50,
    val volunteerLevel: String = "Éco-Volontaire Engagé",
    val avatarResName: String = "avatar_user",
    val isLoggedIn: Boolean = true,
    val joinedDate: String = "Août 2026"
)

@Entity(tableName = "ai_chat_messages")
data class AiChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageText: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val quickActionType: String? = null
)

@Entity(tableName = "mentors_trainers")
data class MentorTrainerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val roleTitle: String = "Formateur & Expert Climat",
    val category: String = "Formateur", // "Formateur", "Mentor", "Formatrice & Mentore", "Expert Climat"
    val specialty: String = "",
    val bio: String = "",
    val experienceYears: Int = 5,
    val phone: String = "+225 ",
    val email: String = "",
    val location: String = "Bouaké, Côte d'Ivoire",
    val photoResName: String = "avatar_user",
    val isAvailableForMentoring: Boolean = true,
    val displayOrder: Int = 0
)

@Entity(tableName = "eco_activities")
data class EcoActivityRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityKey: String, // e.g. "TREE_PLANTED", "PLASTIC_RECYCLED", "COMPOST_MADE", "ENERGY_SAVED", "CLEANUP_DONE", "WATER_SAVED", "QUIZ_COMPLETED", "AIL4C_SHARE"
    val title: String,
    val category: String, // "Agroforesterie", "Recyclage", "Énergie & Eau", "Salubrité", "Sensibilisation", "Quiz Climat"
    val pointsAwarded: Int,
    val description: String,
    val completedTimestamp: Long = System.currentTimeMillis(),
    val iconKey: String = "Eco"
)

@Entity(tableName = "user_badges")
data class UserBadgeEntity(
    @PrimaryKey val badgeId: String,
    val title: String,
    val description: String,
    val requiredPoints: Int,
    val iconKey: String, // e.g. "Seed", "Guardian", "Planter", "Recycle", "Solar", "Scholar", "Ambassador", "Hero"
    val tierLevel: String, // "Bronze", "Argent", "Or", "Platine", "Diamant"
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long? = null,
    val isCelebrationSeen: Boolean = false
)

