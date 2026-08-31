package com.example.data.repository

import android.content.Context
import com.example.data.local.AilDao
import com.example.data.local.AilDatabase
import com.example.data.model.AiChatMessageEntity
import com.example.data.model.EcoActionEntity
import com.example.data.model.EcoActivityRecordEntity
import com.example.data.model.ImpactMetricEntity
import com.example.data.model.MediaTestimonialEntity
import com.example.data.model.MentorTrainerEntity
import com.example.data.model.NewsArticleEntity
import com.example.data.model.OrgInfoEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.QuizBank
import com.example.data.model.QuizQuestion
import com.example.data.model.TrainingApplicationEntity
import com.example.data.model.TrainingEntity
import com.example.data.model.UserAccountEntity
import com.example.data.model.UserBadgeEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.VolunteerRegistrationEntity
import com.example.data.remote.CloudSyncEngine
import com.example.data.remote.CloudSyncStatus
import com.example.data.remote.GeminiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AilRepository(
    private val dao: AilDao,
    context: Context? = null
) {
    private val appContext: Context? = context?.applicationContext
    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val syncEngine: CloudSyncEngine? = appContext?.let { CloudSyncEngine(it, dao, repoScope) }

    val cloudSyncStatus: StateFlow<CloudSyncStatus>? = syncEngine?.syncStatus

    fun triggerCloudSync(onComplete: (Boolean) -> Unit = {}) {
        syncEngine?.triggerImmediateSync(onComplete)
    }

    // --- User Profile & Auth ---
    val currentUserProfile: Flow<UserProfileEntity?> = dao.getCurrentUserProfile()
    val allUserAccounts: Flow<List<UserAccountEntity>> = dao.getAllUserAccounts()

    suspend fun registerUser(
        fullName: String,
        identifier: String,
        authType: String,
        password: String,
        city: String = "Bouaké",
        quartier: String = "Commerce"
    ): AuthResult {
        val cleanIdentifier = identifier.trim()
        val cleanName = fullName.trim()
        if (cleanName.isBlank()) {
            return AuthResult.Error("Veuillez renseigner votre nom et prénoms complets.")
        }
        if (cleanIdentifier.isBlank()) {
            return AuthResult.Error("Veuillez renseigner votre identifiant (téléphone ou email).")
        }
        if (password.length < 4) {
            return AuthResult.Error("Le mot de passe doit contenir au moins 4 caractères.")
        }

        val normalizedId = cleanIdentifier.lowercase()
        val existing = dao.getUserAccountById(normalizedId)
            ?: dao.getUserAccountByPhone(cleanIdentifier)
            ?: dao.getUserAccountByEmail(cleanIdentifier)
            
        if (existing != null) {
            return AuthResult.Error("Un compte existe déjà avec cet identifiant. Veuillez vous connecter.")
        }

        val phone = if (authType == "PHONE") cleanIdentifier else ""
        val email = if (authType == "EMAIL") cleanIdentifier else ""

        val account = UserAccountEntity(
            id = normalizedId,
            fullName = cleanName,
            identifier = cleanIdentifier,
            authType = authType,
            phoneNumber = phone,
            email = email,
            password = password,
            city = city.ifBlank { "Bouaké" },
            quartier = quartier.ifBlank { "Commerce" },
            ecoPoints = 50,
            volunteerLevel = "Éco-Volontaire Engagé",
            registeredDate = "Août 2026",
            registeredTimestamp = System.currentTimeMillis()
        )
        dao.insertUserAccount(account)

        val profile = UserProfileEntity(
            id = "current_user",
            fullName = cleanName,
            identifier = cleanIdentifier,
            authType = authType,
            phoneNumber = phone,
            email = email,
            city = city.ifBlank { "Bouaké" },
            quartier = quartier.ifBlank { "Commerce" },
            ecoPoints = 50,
            volunteerLevel = "Éco-Volontaire Engagé",
            avatarResName = "avatar_user",
            isLoggedIn = true,
            joinedDate = "Août 2026"
        )
        dao.saveUserProfile(profile)
        syncEngine?.notifyCloudItemCreated("UserAccount", normalizedId)
        return AuthResult.Success(profile)
    }

    suspend fun loginUser(
        identifier: String,
        authType: String,
        password: String
    ): AuthResult {
        val cleanIdentifier = identifier.trim()
        if (cleanIdentifier.isBlank()) {
            return AuthResult.Error("Veuillez renseigner votre numéro de téléphone ou votre adresse email.")
        }
        val normalizedId = cleanIdentifier.lowercase()

        val isPredefinedAdmin = normalizedId in listOf(
            "atchouyaosylvain59@gmail.com",
            "ail4c03@gmail.com"
        )

        if (isPredefinedAdmin) {
            if (!password.trim().equals("AIL4CCI", ignoreCase = true)) {
                return AuthResult.Error("Mot de passe incorrect. Veuillez réessayer.")
            }
        }

        val account = dao.getUserAccountById(normalizedId)
            ?: dao.getUserAccountByPhone(cleanIdentifier)
            ?: dao.getUserAccountByEmail(cleanIdentifier)

        if (account == null) {
            if (isPredefinedAdmin) {
                val adminProfile = UserProfileEntity(
                    id = "current_user",
                    fullName = if (normalizedId.contains("sylvain")) "Sylvain Atchouyao (Admin)" else "Administrateur AIL4C",
                    identifier = cleanIdentifier,
                    authType = "EMAIL",
                    phoneNumber = "+225 07 89 71 02 89",
                    email = cleanIdentifier,
                    city = "Bouaké",
                    quartier = "Gbêkê",
                    ecoPoints = 500,
                    volunteerLevel = "Administrateur National",
                    avatarResName = "avatar_user",
                    isLoggedIn = true,
                    joinedDate = "Août 2026"
                )
                dao.saveUserProfile(adminProfile)
                return AuthResult.Success(adminProfile)
            }
            return AuthResult.Error("Aucun compte trouvé avec cet identifiant. Veuillez créer votre compte d'abord.")
        }

        if (!isPredefinedAdmin && account.password.isNotBlank() && password.isNotBlank() && account.password != password.trim()) {
            return AuthResult.Error("Mot de passe incorrect. Veuillez réessayer.")
        }

        val profile = UserProfileEntity(
            id = "current_user",
            fullName = account.fullName,
            identifier = account.identifier,
            authType = account.authType,
            phoneNumber = account.phoneNumber,
            email = account.email,
            city = account.city,
            quartier = account.quartier,
            ecoPoints = account.ecoPoints,
            volunteerLevel = account.volunteerLevel,
            avatarResName = "avatar_user",
            isLoggedIn = true,
            joinedDate = account.registeredDate
        )
        dao.saveUserProfile(profile)
        return AuthResult.Success(profile)
    }

    suspend fun loginWithPhone(phoneNumber: String, fullName: String, city: String = "Bouaké", quartier: String = "Commerce"): UserProfileEntity {
        val cleanPhone = phoneNumber.trim()
        val displayName = if (fullName.isNotBlank()) fullName.trim() else "Bénévole ($cleanPhone)"
        val profile = UserProfileEntity(
            id = "current_user",
            fullName = displayName,
            identifier = cleanPhone,
            authType = "PHONE",
            phoneNumber = cleanPhone,
            email = "",
            city = city.ifBlank { "Bouaké" },
            quartier = quartier.ifBlank { "Commerce" },
            ecoPoints = 50,
            volunteerLevel = "Éco-Volontaire Engagé",
            avatarResName = "avatar_user",
            isLoggedIn = true,
            joinedDate = "Août 2026"
        )
        dao.saveUserProfile(profile)
        return profile
    }

    suspend fun loginWithEmail(email: String, fullName: String, city: String = "Bouaké", quartier: String = "Commerce"): UserProfileEntity {
        val cleanEmail = email.trim()
        val displayName = if (fullName.isNotBlank()) fullName.trim() else cleanEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        val profile = UserProfileEntity(
            id = "current_user",
            fullName = displayName,
            identifier = cleanEmail,
            authType = "EMAIL",
            phoneNumber = "",
            email = cleanEmail,
            city = city.ifBlank { "Bouaké" },
            quartier = quartier.ifBlank { "Commerce" },
            ecoPoints = 50,
            volunteerLevel = "Éco-Volontaire Engagé",
            avatarResName = "avatar_user",
            isLoggedIn = true,
            joinedDate = "Août 2026"
        )
        dao.saveUserProfile(profile)
        return profile
    }

    suspend fun loginAsGuest(): UserProfileEntity {
        val profile = UserProfileEntity(
            id = "current_user",
            fullName = "Visiteur Découverte",
            identifier = "invite@ail4c.org",
            authType = "GUEST",
            phoneNumber = "",
            email = "",
            city = "Bouaké",
            quartier = "Bouaké Centre",
            ecoPoints = 10,
            volunteerLevel = "Visiteur Éco-Curieux",
            avatarResName = "avatar_user",
            isLoggedIn = true,
            joinedDate = "Août 2026"
        )
        dao.saveUserProfile(profile)
        return profile
    }

    suspend fun updateUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun addEcoPoints(points: Int, reason: String = ""): UserBadgeEntity? {
        val current = dao.getCurrentUser() ?: return null
        val newPoints = (current.ecoPoints + points).coerceAtLeast(0)

        // Dynamic volunteer level based on points
        val newLevel = when {
            newPoints >= 1000 -> "Héros Vert de Côte d'Ivoire"
            newPoints >= 750 -> "Ambassadeur Climat AIL4C"
            newPoints >= 500 -> "Expert Éco-Formé"
            newPoints >= 350 -> "Pionnier Énergies Vertes"
            newPoints >= 200 -> "Champion du Recyclage"
            newPoints >= 100 -> "Planteur Gbêkê"
            newPoints >= 50 -> "Éco-Gardien Actif"
            else -> "Visiteur Éco-Curieux"
        }

        dao.saveUserProfile(current.copy(ecoPoints = newPoints, volunteerLevel = newLevel))

        // Check for badge unlocks
        val allBadges = dao.getAllBadges().firstOrNull() ?: emptyList()
        var newlyUnlockedBadge: UserBadgeEntity? = null

        for (badge in allBadges) {
            if (!badge.isUnlocked && newPoints >= badge.requiredPoints) {
                val updated = badge.copy(
                    isUnlocked = true,
                    unlockedTimestamp = System.currentTimeMillis(),
                    isCelebrationSeen = false
                )
                dao.updateBadge(updated)
                if (newlyUnlockedBadge == null) {
                    newlyUnlockedBadge = updated
                }
            }
        }
        return newlyUnlockedBadge
    }

    suspend fun recordEcoActivity(
        activityKey: String,
        title: String,
        category: String,
        pointsAwarded: Int,
        description: String,
        iconKey: String = "Eco"
    ): UserBadgeEntity? {
        dao.insertEcoActivity(
            EcoActivityRecordEntity(
                activityKey = activityKey,
                title = title,
                category = category,
                pointsAwarded = pointsAwarded,
                description = description,
                completedTimestamp = System.currentTimeMillis(),
                iconKey = iconKey
            )
        )
        return addEcoPoints(pointsAwarded, title)
    }

    suspend fun checkAndAwardDailyLogin(): Pair<Boolean, UserBadgeEntity?> {
        val todayKey = QuizBank.getTodayDateKey()
        val existingCheckin = dao.getEcoActivityByKeyPattern("DAILY_CHECKIN_${todayKey}%")
        if (existingCheckin != null) {
            return false to null
        }
        val newlyUnlockedBadge = recordEcoActivity(
            activityKey = "DAILY_CHECKIN_${todayKey}",
            title = "Connexion Quotidienne Éco-Citoyenne",
            category = "Présence & Assiduité",
            pointsAwarded = 5,
            description = "Connexion quotidienne validée pour le $todayKey (+5 pts éco-citoyens).",
            iconKey = "Calendar"
        )
        return true to newlyUnlockedBadge
    }

    suspend fun isDailyQuizAnsweredToday(): Boolean {
        val todayKey = QuizBank.getTodayDateKey()
        return dao.getEcoActivityByKeyPattern("DAILY_QUIZ_${todayKey}%") != null
    }

    suspend fun recordDailyQuizAnswer(question: QuizQuestion, isCorrect: Boolean): UserBadgeEntity? {
        val todayKey = QuizBank.getTodayDateKey()
        val points = if (isCorrect) 10 else 0
        return recordEcoActivity(
            activityKey = "DAILY_QUIZ_${todayKey}",
            title = if (isCorrect) "Quiz Climat Quotidien Validé 🎯" else "Quiz Climat Quotidien Participé 🌱",
            category = "Quiz Climat",
            pointsAwarded = points,
            description = if (isCorrect) {
                "Bonne réponse à la question du jour (+10 pts) : '${question.question.take(45)}...'"
            } else {
                "Participation à la question du jour : '${question.question.take(45)}...'"
            },
            iconKey = "Scholar"
        )
    }

    val allEcoActivities: Flow<List<EcoActivityRecordEntity>> = dao.getAllEcoActivities()
    val allBadges: Flow<List<UserBadgeEntity>> = dao.getAllBadges()
    val unlockedBadges: Flow<List<UserBadgeEntity>> = dao.getUnlockedBadges()

    suspend fun markBadgeCelebrationSeen(badgeId: String) {
        val badge = dao.getBadgeById(badgeId) ?: return
        dao.updateBadge(badge.copy(isCelebrationSeen = true))
    }

    suspend fun logoutUser() {
        dao.clearUserProfile()
    }

    // --- AI Assistant Chat ---
    val allAiMessages: Flow<List<AiChatMessageEntity>> = dao.getAllAiMessages()

    suspend fun askAiAssistant(userPrompt: String): Pair<String, UserBadgeEntity?> {
        val userMessagesCount = dao.getUserMessageCount()
        val isFirstInteraction = userMessagesCount == 0

        // Save user message to database
        val userMsg = AiChatMessageEntity(
            messageText = userPrompt,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )
        dao.insertAiMessage(userMsg)

        // Get current user's name if logged in
        val currentUser = dao.getCurrentUser()
        val userName = currentUser?.fullName ?: ""

        // Get past messages for context
        val recentHistory = dao.getAllAiMessagesList().takeLast(10).map {
            it.messageText to it.isFromUser
        }

        // Call Gemini connected AI engine personalized with user name and continuity
        val aiResponseText = GeminiService.generateAiResponse(
            userPrompt = userPrompt,
            userName = userName,
            recentHistory = recentHistory,
            isFirstInteraction = isFirstInteraction,
            context = appContext
        )

        // Save AI response to database
        val aiMsg = AiChatMessageEntity(
            messageText = aiResponseText,
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertAiMessage(aiMsg)

        // Award 5 eco points for continuous environmental learning with the bot
        val badgeUnlocked = addEcoPoints(5, "Apprentissage avec ÉcoBot IA")

        return Pair(aiResponseText, badgeUnlocked)
    }

    suspend fun clearAiChatHistory() {
        dao.clearAiMessages()
        // Re-insert initial welcome message
        val currentUser = dao.getCurrentUser()
        val nameGreeting = if (!currentUser?.fullName.isNullOrBlank()) " ${currentUser?.fullName}" else ""
        dao.insertAiMessage(
            AiChatMessageEntity(
                messageText = "🌿 Bonjour$nameGreeting et bienvenue sur l'application de l'ONG AIL4C ! Je suis ÉcoBot IA, votre assistant intelligent et connecté à Internet. Je construis des réponses précises et sur-mesure à toutes vos questions sur le climat, l'agroforesterie, les formations et l'ONG AIL4C. Comment puis-je vous aider aujourd'hui ?",
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            )
        )
    }


    // --- News ---
    val allNews: Flow<List<NewsArticleEntity>> = dao.getAllNews()
    val publishedNews: Flow<List<NewsArticleEntity>> = dao.getPublishedNews()
    val featuredNews: Flow<List<NewsArticleEntity>> = dao.getFeaturedNews()

    suspend fun getNewsById(id: Long) = dao.getNewsById(id)
    suspend fun saveNews(news: NewsArticleEntity) {
        if (news.id == 0L) {
            val newId = dao.insertNews(news)
            syncEngine?.notifyCloudItemCreated("Actualité", newId.toString())
        } else {
            dao.updateNews(news)
            syncEngine?.notifyCloudItemUpdated("Actualité", news.id.toString())
        }
    }
    suspend fun deleteNews(id: Long) {
        dao.deleteNewsById(id)
        syncEngine?.notifyCloudItemDeleted("Actualité", id.toString())
    }

    // --- Eco Actions ---
    val allActions: Flow<List<EcoActionEntity>> = dao.getAllActions()
    suspend fun getActionById(id: Long) = dao.getActionById(id)
    suspend fun saveAction(action: EcoActionEntity) {
        if (action.id == 0L) {
            val newId = dao.insertAction(action)
            syncEngine?.notifyCloudItemCreated("Action", newId.toString())
        } else {
            dao.updateAction(action)
            syncEngine?.notifyCloudItemUpdated("Action", action.id.toString())
        }
    }
    suspend fun deleteAction(id: Long) {
        dao.deleteActionById(id)
        syncEngine?.notifyCloudItemDeleted("Action", id.toString())
    }

    // --- Projects ---
    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    suspend fun getProjectById(id: Long) = dao.getProjectById(id)
    suspend fun saveProject(project: ProjectEntity) {
        if (project.id == 0L) {
            val newId = dao.insertProject(project)
            syncEngine?.notifyCloudItemCreated("Projet", newId.toString())
        } else {
            dao.updateProject(project)
            syncEngine?.notifyCloudItemUpdated("Projet", project.id.toString())
        }
    }
    suspend fun deleteProject(id: Long) {
        dao.deleteProjectById(id)
        syncEngine?.notifyCloudItemDeleted("Projet", id.toString())
    }

    suspend fun recordDonation(projectId: Long, amount: Long) {
        val current = dao.getProjectById(projectId) ?: return
        val updated = current.copy(raisedBudget = current.raisedBudget + amount)
        dao.updateProject(updated)
        syncEngine?.notifyCloudItemUpdated("Donation Projet", projectId.toString())
    }

    // --- Trainings ---
    val allTrainings: Flow<List<TrainingEntity>> = dao.getAllTrainings()
    suspend fun getTrainingById(id: Long) = dao.getTrainingById(id)
    suspend fun saveTraining(training: TrainingEntity) {
        if (training.id == 0L) {
            val newId = dao.insertTraining(training)
            syncEngine?.notifyCloudItemCreated("Formation", newId.toString())
        } else {
            dao.updateTraining(training)
            syncEngine?.notifyCloudItemUpdated("Formation", training.id.toString())
        }
    }
    suspend fun deleteTraining(id: Long) {
        dao.deleteTrainingById(id)
        syncEngine?.notifyCloudItemDeleted("Formation", id.toString())
    }

    // --- Mentors & Formateurs ---
    val allMentorsTrainers: Flow<List<MentorTrainerEntity>> = dao.getAllMentorsTrainers()
    val availableMentors: Flow<List<MentorTrainerEntity>> = dao.getAvailableMentors()
    suspend fun getMentorTrainerById(id: Long) = dao.getMentorTrainerById(id)
    suspend fun saveMentorTrainer(mentor: MentorTrainerEntity) {
        if (mentor.id == 0L) {
            val newId = dao.insertMentorTrainer(mentor)
            syncEngine?.notifyCloudItemCreated("Formateur/Mentor", newId.toString())
        } else {
            dao.updateMentorTrainer(mentor)
            syncEngine?.notifyCloudItemUpdated("Formateur/Mentor", mentor.id.toString())
        }
    }
    suspend fun deleteMentorTrainer(id: Long) {
        dao.deleteMentorTrainerById(id)
        syncEngine?.notifyCloudItemDeleted("Formateur/Mentor", id.toString())
    }

    // --- Volunteer Registrations ---
    val allVolunteerRegistrations: Flow<List<VolunteerRegistrationEntity>> = dao.getAllVolunteerRegistrations()
    suspend fun registerVolunteer(reg: VolunteerRegistrationEntity): Pair<Long, UserBadgeEntity?> {
        val id = dao.insertVolunteerRegistration(reg)
        // If tied to an action, increment registered count
        if (reg.actionId != null && reg.actionId > 0L) {
            val action = dao.getActionById(reg.actionId)
            if (action != null) {
                dao.updateAction(action.copy(registeredCount = action.registeredCount + 1))
            }
        }
        syncEngine?.notifyCloudItemCreated("Bénévolat", id.toString())

        // Automatically award 10 eco-points for field action participation
        val todayKey = QuizBank.getTodayDateKey()
        val actionTitle = if (reg.actionTitle.isNotBlank()) reg.actionTitle else "Action Citoyenne AIL4C"
        val newlyUnlockedBadge = recordEcoActivity(
            activityKey = "FIELD_ACTION_${reg.actionId ?: id}_$todayKey",
            title = "Participation Action Terrain : $actionTitle",
            category = "Action Terrain",
            pointsAwarded = 10,
            description = "Inscription et participation bénévole validées pour l'événement sur le terrain '$actionTitle' (+10 pts).",
            iconKey = "Volunteer"
        )

        return id to newlyUnlockedBadge
    }
    suspend fun updateVolunteerStatus(reg: VolunteerRegistrationEntity, newStatus: String) {
        dao.updateVolunteerRegistration(reg.copy(status = newStatus))
        syncEngine?.notifyCloudItemUpdated("Bénévolat", reg.id.toString())
    }
    suspend fun deleteVolunteerRegistration(id: Long) {
        dao.deleteVolunteerRegistrationById(id)
        syncEngine?.notifyCloudItemDeleted("Bénévolat", id.toString())
    }

    // --- Training Applications ---
    val allTrainingApplications: Flow<List<TrainingApplicationEntity>> = dao.getAllTrainingApplications()
    suspend fun submitTrainingApplication(app: TrainingApplicationEntity): Long {
        val id = dao.insertTrainingApplication(app)
        syncEngine?.notifyCloudItemCreated("Candidature Formation", id.toString())
        return id
    }
    suspend fun updateApplicationStatus(app: TrainingApplicationEntity, newStatus: String) {
        dao.updateTrainingApplication(app.copy(status = newStatus))
        syncEngine?.notifyCloudItemUpdated("Candidature Formation", app.id.toString())
    }
    suspend fun deleteTrainingApplication(id: Long) {
        dao.deleteTrainingApplicationById(id)
        syncEngine?.notifyCloudItemDeleted("Candidature Formation", id.toString())
    }

    // --- Impact Metrics ---
    val impactMetrics: Flow<List<ImpactMetricEntity>> = dao.getAllImpactMetrics()
    suspend fun updateImpactMetric(metric: ImpactMetricEntity) {
        dao.updateMetric(metric)
        syncEngine?.notifyCloudItemUpdated("Métrique Impact", metric.metricKey)
    }

    // --- Media & Testimonials ---
    val allMediaTestimonials: Flow<List<MediaTestimonialEntity>> = dao.getAllMediaTestimonials()
    suspend fun saveMediaTestimonial(item: MediaTestimonialEntity) {
        if (item.id == 0L) {
            val newId = dao.insertMediaTestimonial(item)
            syncEngine?.notifyCloudItemCreated("Média", newId.toString())
        } else {
            dao.updateMediaTestimonial(item)
            syncEngine?.notifyCloudItemUpdated("Média", item.id.toString())
        }
    }
    suspend fun deleteMediaTestimonial(id: Long) {
        dao.deleteMediaTestimonialById(id)
        syncEngine?.notifyCloudItemDeleted("Média", id.toString())
    }

    // --- Org Info & Config ---
    val allOrgInfo: Flow<List<OrgInfoEntity>> = dao.getAllOrgInfo()
    suspend fun getOrgInfo(key: String): String? = dao.getOrgInfoValue(key)
    suspend fun setOrgInfo(key: String, value: String) {
        dao.setOrgInfo(OrgInfoEntity(key, value))
        syncEngine?.notifyCloudItemUpdated("Infos ONG", key)
    }
    suspend fun setAllOrgInfo(infoList: List<OrgInfoEntity>) {
        dao.setAllOrgInfo(infoList)
        syncEngine?.notifyCloudItemUpdated("Infos ONG", "Batch")
    }

    // Export registrations as CSV string
    suspend fun exportRegistrationsCsv(): String = withContext(Dispatchers.Default) {
        val list = dao.getAllVolunteerRegistrations().firstOrNull() ?: emptyList()
        val sb = java.lang.StringBuilder()
        sb.append("ID;Nom Complet;Telephone;Email;Ville;Action;Disponibilite;Statut;Date\n")
        list.forEach { r ->
            sb.append("${r.id};\"${r.fullName}\";\"${r.phone}\";\"${r.email}\";\"${r.city}\";\"${r.actionTitle}\";\"${r.availability}\";\"${r.status}\";\"${r.dateSubmitted}\"\n")
        }
        sb.toString()
    }

    suspend fun exportApplicationsCsv(): String = withContext(Dispatchers.Default) {
        val list = dao.getAllTrainingApplications().firstOrNull() ?: emptyList()
        val sb = java.lang.StringBuilder()
        sb.append("ID;Formation;Nom Complet;Telephone;Email;Niveau;Statut;Date\n")
        list.forEach { a ->
            sb.append("${a.id};\"${a.trainingTitle}\";\"${a.fullName}\";\"${a.phone}\";\"${a.email}\";\"${a.educationLevel}\";\"${a.status}\";\"${a.dateSubmitted}\"\n")
        }
        sb.toString()
    }

    // Clear all content in the database to start completely fresh
    suspend fun clearAllContent() = withContext(Dispatchers.IO) {
        dao.deleteAllNews()
        dao.deleteAllActions()
        dao.deleteAllProjects()
        dao.deleteAllTrainings()
        dao.deleteAllMentorsTrainers()
        dao.deleteAllMediaTestimonials()
        dao.deleteAllMetrics()
    }

    // Reset & Synchronize with authentic Facebook & AIL4C data
    suspend fun resetAndSyncOfficialFacebookData() = withContext(Dispatchers.IO) {
        dao.deleteAllNews()
        dao.deleteAllActions()
        dao.deleteAllProjects()
        dao.deleteAllTrainings()
        dao.deleteAllMentorsTrainers()
        dao.deleteAllMediaTestimonials()
        dao.deleteAllMetrics()
    }

    suspend fun deleteAllMentorsTrainers() = withContext(Dispatchers.IO) {
        dao.deleteAllMentorsTrainers()
    }

    suspend fun ensureDefaultDataSeeded() = withContext(Dispatchers.IO) {
        // Clear all mock/sample data so all tabs start completely empty as requested by user
        val cleanedFlag = dao.getOrgInfoValue("sample_content_cleared_v1")
        if (cleanedFlag == null) {
            dao.deleteAllNews()
            dao.deleteAllActions()
            dao.deleteAllProjects()
            dao.deleteAllTrainings()
            dao.deleteAllMentorsTrainers()
            dao.deleteAllMediaTestimonials()
            dao.deleteAllMetrics()
            dao.setOrgInfo(OrgInfoEntity("sample_content_cleared_v1", "true"))
        }

        // Clean hardcoded mentors as specifically requested by the user
        val cleanedMentorsFlag = dao.getOrgInfoValue("mentors_cleared_user_request_v3")
        if (cleanedMentorsFlag == null) {
            dao.deleteAllMentorsTrainers()
            dao.setOrgInfo(OrgInfoEntity("mentors_cleared_user_request_v3", "true"))
        }

        // 1. Official Org Info Matching Official Records
        val currentWeb = dao.getOrgInfoValue("org_website_url")
        if (currentWeb == null || currentWeb.contains("ongail4c.com") || currentWeb.contains("facebook.com")) {
            dao.setOrgInfo(OrgInfoEntity("org_website_url", "https://ongail4csiteweb.netlify.app/"))
            dao.setOrgInfo(OrgInfoEntity("org_website_domain", "ongail4csiteweb.netlify.app"))
        }

        val existingOrg = dao.getOrgInfoValue("org_name")
        if (existingOrg == null) {
            dao.setAllOrgInfo(
                listOf(
                    OrgInfoEntity("org_name", "Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage (des Jeunes)"),
                    OrgInfoEntity("org_acronym", "AIL4C"),
                    OrgInfoEntity("org_president", "SENIN Tchoumou Esdras Gemiel"),
                    OrgInfoEntity("org_founder", "Aka Koffi Ezéchiel"),
                    OrgInfoEntity("org_motto", "Agir pour le Climat, Former la Jeunesse, Bâtir l'Avenir"),
                    OrgInfoEntity("org_about_history", "Créée en Côte d'Ivoire par des jeunes engagés pour la cause environnementale sous l'impulsion de son Président-Fondateur Aka Koffi Ezéchiel et présidée par SENIN Tchoumou Esdras Gemiel, l'Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage (AIL4C) œuvre activement pour la justice climatique, l'autonomisation de la jeunesse et le développement durable. Basée à Bouaké, l'ONG déploie des actions concrètes de reboisement massif, de salubrité urbaine, d'agroforesterie, de lutte contre les VBG et d'insertion professionnelle aux métiers verts."),
                    OrgInfoEntity("org_headquarters", "Bouaké, Région du Gbêkê, Côte d'Ivoire (Siège National)"),
                    OrgInfoEntity("org_address", "Siège National : Bouaké - Quartier Tchelekro / Koko / Commerce"),
                    OrgInfoEntity("org_phone_1", "+225 07 89 71 02 89"),
                    OrgInfoEntity("org_phone_2", "+225 07 89 97 63 23"),
                    OrgInfoEntity("org_email", "ongail4c@gmail.com"),
                    OrgInfoEntity("org_website_url", "https://ongail4csiteweb.netlify.app/"),
                    OrgInfoEntity("org_website_domain", "ongail4csiteweb.netlify.app"),
                    OrgInfoEntity("org_facebook_url", "https://www.facebook.com/share/1GvChYFAMY/"),
                    OrgInfoEntity("org_facebook_page_name", "ONG AIL4C (Page Officielle)"),
                    OrgInfoEntity("admin_pin", "AIL4CCI"),
                    OrgInfoEntity("org_mission", "Mobiliser toutes les populations contre les effets néfastes du changement climatique, lutter contre les violences basées sur le genre (VBG) et créer des perspectives concrètes d'emploi et de formation aux métiers verts pour toute la jeunesse sans exception."),
                    OrgInfoEntity("org_vision", "Un environnement durable, vert et propre où chaque citoyen adopte des réflexes écologiques et où la jeunesse trouve dans la transition écologique un vecteur d'émancipation et d'épanouissement socio-économique."),
                    OrgInfoEntity("org_objectives", "1. Reboisement massif & Création de pépinières communautaires durables.\n2. Formation certifiante aux métiers verts (agro-écologie, recyclage, compostage).\n3. Salubrité urbaine, curage citoyen et prévention des inondations.\n4. Sensibilisation de masse en milieu scolaire et santé reproductive (UNFPA).\n5. Insertion professionnelle et accompagnement des jeunes porteurs d'éco-projets."),
                    OrgInfoEntity("org_creation_year", "2023"),
                    OrgInfoEntity("org_legal_status", "Organisation Non Gouvernementale (ONG) à but non lucratif enregistrée en Côte d'Ivoire")
                )
            )
        } else {
            // Ensure website URL is updated to the official Netlify site
            dao.setOrgInfo(OrgInfoEntity("org_website_url", "https://ongail4csiteweb.netlify.app/"))
            dao.setOrgInfo(OrgInfoEntity("org_website_domain", "ongail4csiteweb.netlify.app"))
        }

        // 2. Seed Initial AI Welcome Message if empty
        val existingAiMsgs = dao.getAllAiMessages().firstOrNull()
        if (existingAiMsgs.isNullOrEmpty()) {
            dao.insertAiMessage(
                AiChatMessageEntity(
                    messageText = "🌿 Bonjour et bienvenue sur l'application officielle de l'AIL4C ! Je suis ÉcoBot IA, votre assistant intelligent et connecté à Internet. Je construis des réponses précises et sur-mesure à toutes vos questions sur le climat, l'agroforesterie, les formations et l'ONG AIL4C. Comment puis-je vous aider aujourd'hui ?",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // 3. Mentors & Formateurs are created strictly by the Admin

        // 4. Do NOT auto-login user - strict authentication is mandatory at startup
        val existingBadges = dao.getAllBadges().firstOrNull() ?: emptyList()
        val currentPoints = 50
        if (existingBadges.isEmpty()) {
            val defaultBadges = listOf(
                UserBadgeEntity(
                    badgeId = "BADGE_SEED",
                    title = "Graine Citoyenne",
                    description = "Première interaction citoyenne ou premier Quiz éco-climat validé",
                    requiredPoints = 10,
                    iconKey = "Seed",
                    tierLevel = "Bronze",
                    isUnlocked = currentPoints >= 10,
                    unlockedTimestamp = if (currentPoints >= 10) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                ),
                UserBadgeEntity(
                    badgeId = "BADGE_GUARDIAN",
                    title = "Éco-Gardien",
                    description = "50 points d'éco-citoyenneté : Engagement actif pour l'environnement",
                    requiredPoints = 50,
                    iconKey = "Guardian",
                    tierLevel = "Bronze",
                    isUnlocked = currentPoints >= 50,
                    unlockedTimestamp = if (currentPoints >= 50) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                ),
                UserBadgeEntity(
                    badgeId = "BADGE_PLANTER",
                    title = "Planteur Gbêkê",
                    description = "100 points : Participation active au reboisement et aux pépinières",
                    requiredPoints = 100,
                    iconKey = "Planter",
                    tierLevel = "Argent",
                    isUnlocked = currentPoints >= 100,
                    unlockedTimestamp = if (currentPoints >= 100) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                ),
                UserBadgeEntity(
                    badgeId = "BADGE_RECYCLER",
                    title = "Champion du Recyclage",
                    description = "200 points : Actions concrètes de tri et valorisation plastique en éco-pavés",
                    requiredPoints = 200,
                    iconKey = "Recycle",
                    tierLevel = "Argent",
                    isUnlocked = currentPoints >= 200,
                    unlockedTimestamp = if (currentPoints >= 200) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                ),
                UserBadgeEntity(
                    badgeId = "BADGE_SOLAR",
                    title = "Pionnier Énergies Vertes",
                    description = "350 points : Promotion du solaire et de l'efficacité énergétique",
                    requiredPoints = 350,
                    iconKey = "Solar",
                    tierLevel = "Or",
                    isUnlocked = currentPoints >= 350,
                    unlockedTimestamp = if (currentPoints >= 350) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                ),
                UserBadgeEntity(
                    badgeId = "BADGE_SCHOLAR",
                    title = "Expert Éco-Formé",
                    description = "500 points : Candidat aux formations & maîtrise des métiers verts",
                    requiredPoints = 500,
                    iconKey = "Scholar",
                    tierLevel = "Or",
                    isUnlocked = currentPoints >= 500,
                    unlockedTimestamp = if (currentPoints >= 500) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                ),
                UserBadgeEntity(
                    badgeId = "BADGE_AMBASSADOR",
                    title = "Ambassadeur Climat AIL4C",
                    description = "750 points : Sensibilisation communautaire et leadership inspirant",
                    requiredPoints = 750,
                    iconKey = "Ambassador",
                    tierLevel = "Platine",
                    isUnlocked = currentPoints >= 750,
                    unlockedTimestamp = if (currentPoints >= 750) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                ),
                UserBadgeEntity(
                    badgeId = "BADGE_HERO",
                    title = "Héros Vert de Côte d'Ivoire",
                    description = "1000 points : Niveau d'excellence et dévouement citoyen exceptionnel",
                    requiredPoints = 1000,
                    iconKey = "Hero",
                    tierLevel = "Diamant",
                    isUnlocked = currentPoints >= 1000,
                    unlockedTimestamp = if (currentPoints >= 1000) System.currentTimeMillis() else null,
                    isCelebrationSeen = true
                )
            )
            dao.insertAllBadges(defaultBadges)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AilRepository? = null

        fun getInstance(context: Context): AilRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AilDatabase.getDatabase(context)
                val repo = AilRepository(db.ailDao(), context.applicationContext)
                INSTANCE = repo
                // Trigger background seed
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        repo.ensureDefaultDataSeeded()
                    } catch (e: Exception) {
                        android.util.Log.e("AilRepository", "Error seeding database: ${e.localizedMessage}", e)
                    }
                }
                repo
            }
        }
    }
}

sealed class AuthResult {
    data class Success(val profile: UserProfileEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

