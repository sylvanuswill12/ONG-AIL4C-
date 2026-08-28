package com.example.data.repository

import android.content.Context
import com.example.data.local.AilDao
import com.example.data.local.AilDatabase
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

    suspend fun addEcoPoints(points: Int) {
        val current = dao.getCurrentUser() ?: return
        dao.saveUserProfile(current.copy(ecoPoints = current.ecoPoints + points))
    }

    suspend fun logoutUser() {
        dao.clearUserProfile()
    }

    // --- AI Assistant Chat ---
    val allAiMessages: Flow<List<AiChatMessageEntity>> = dao.getAllAiMessages()

    suspend fun askAiAssistant(userPrompt: String): String {
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
        val recentHistory = dao.getAllAiMessages().firstOrNull()?.takeLast(6)?.map {
            it.messageText to it.isFromUser
        } ?: emptyList()

        // Call Gemini connected AI engine personalized with user name
        val aiResponseText = GeminiService.generateAiResponse(
            userPrompt = userPrompt,
            userName = userName,
            recentHistory = recentHistory,
            context = appContext
        )

        // Save AI response to database
        val aiMsg = AiChatMessageEntity(
            messageText = aiResponseText,
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertAiMessage(aiMsg)

        return aiResponseText
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

    // --- Volunteer Registrations ---
    val allVolunteerRegistrations: Flow<List<VolunteerRegistrationEntity>> = dao.getAllVolunteerRegistrations()
    suspend fun registerVolunteer(reg: VolunteerRegistrationEntity): Long {
        val id = dao.insertVolunteerRegistration(reg)
        // If tied to an action, increment registered count
        if (reg.actionId != null && reg.actionId > 0L) {
            val action = dao.getActionById(reg.actionId)
            if (action != null) {
                dao.updateAction(action.copy(registeredCount = action.registeredCount + 1))
            }
        }
        syncEngine?.notifyCloudItemCreated("Bénévolat", id.toString())
        return id
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
        dao.deleteAllMediaTestimonials()
        dao.deleteAllMetrics()
    }

    // Reset & Synchronize with authentic Facebook & AIL4C data
    suspend fun resetAndSyncOfficialFacebookData() = withContext(Dispatchers.IO) {
        dao.deleteAllNews()
        dao.deleteAllActions()
        dao.deleteAllProjects()
        dao.deleteAllTrainings()
        dao.deleteAllMediaTestimonials()
        dao.deleteAllMetrics()
    }

    suspend fun ensureDefaultDataSeeded() = withContext(Dispatchers.IO) {
        // Clear all mock/sample data so all tabs start completely empty as requested by user
        val cleanedFlag = dao.getOrgInfoValue("sample_content_cleared_v1")
        if (cleanedFlag == null) {
            dao.deleteAllNews()
            dao.deleteAllActions()
            dao.deleteAllProjects()
            dao.deleteAllTrainings()
            dao.deleteAllMediaTestimonials()
            dao.deleteAllMetrics()
            dao.setOrgInfo(OrgInfoEntity("sample_content_cleared_v1", "true"))
        }

        // 1. Official Org Info Matching Official Records
        val currentWeb = dao.getOrgInfoValue("org_website_url")
        if (currentWeb == null || currentWeb.contains("ongail4c.com")) {
            dao.setOrgInfo(OrgInfoEntity("org_website_url", "https://www.facebook.com/share/1GvChYFAMY/"))
            dao.setOrgInfo(OrgInfoEntity("org_website_domain", "facebook.com/share/1GvChYFAMY"))
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
                    OrgInfoEntity("org_website_url", "https://www.facebook.com/share/1GvChYFAMY/"),
                    OrgInfoEntity("org_website_domain", "facebook.com/share/1GvChYFAMY"),
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

        // 3. Seed Initial Logged User if not existing
        val existingUser = dao.getCurrentUser()
        if (existingUser == null) {
            dao.saveUserProfile(
                UserProfileEntity(
                    id = "current_user",
                    fullName = "Éco-Citoyen",
                    identifier = "+225 07 00 00 00",
                    authType = "PHONE",
                    phoneNumber = "+225 07 00 00 00",
                    email = "citoyen@ongail4c.com",
                    city = "Bouaké",
                    quartier = "Commerce",
                    ecoPoints = 50,
                    volunteerLevel = "Membre Adhérent",
                    avatarResName = "avatar_user",
                    isLoggedIn = true,
                    joinedDate = "Août 2026"
                )
            )
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
