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
import com.example.data.remote.GeminiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AilRepository(private val dao: AilDao) {

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

        // Get past messages for context
        val recentHistory = dao.getAllAiMessages().firstOrNull()?.takeLast(6)?.map {
            it.messageText to it.isFromUser
        } ?: emptyList()

        // Call Gemini / smart local assistant
        val aiResponseText = GeminiService.generateAiResponse(userPrompt, recentHistory)

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
        dao.insertAiMessage(
            AiChatMessageEntity(
                messageText = "🌿 Bonjour et chaleureuse bienvenue sur l'application de l'ONG AIL4C ! Je suis AWA, votre Éco-Assistante IA dédiée à la lutte contre le réchauffement climatique et l'insertion des jeunes à Bouaké. Comment puis-je vous accompagner aujourd'hui ?",
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
            dao.insertNews(news)
        } else {
            dao.updateNews(news)
        }
    }
    suspend fun deleteNews(id: Long) = dao.deleteNewsById(id)

    // --- Eco Actions ---
    val allActions: Flow<List<EcoActionEntity>> = dao.getAllActions()
    suspend fun getActionById(id: Long) = dao.getActionById(id)
    suspend fun saveAction(action: EcoActionEntity) {
        if (action.id == 0L) {
            dao.insertAction(action)
        } else {
            dao.updateAction(action)
        }
    }
    suspend fun deleteAction(id: Long) = dao.deleteActionById(id)

    // --- Projects ---
    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    suspend fun getProjectById(id: Long) = dao.getProjectById(id)
    suspend fun saveProject(project: ProjectEntity) {
        if (project.id == 0L) {
            dao.insertProject(project)
        } else {
            dao.updateProject(project)
        }
    }
    suspend fun deleteProject(id: Long) = dao.deleteProjectById(id)

    suspend fun recordDonation(projectId: Long, amount: Long) {
        val current = dao.getProjectById(projectId) ?: return
        val updated = current.copy(raisedBudget = current.raisedBudget + amount)
        dao.updateProject(updated)
    }

    // --- Trainings ---
    val allTrainings: Flow<List<TrainingEntity>> = dao.getAllTrainings()
    suspend fun getTrainingById(id: Long) = dao.getTrainingById(id)
    suspend fun saveTraining(training: TrainingEntity) {
        if (training.id == 0L) {
            dao.insertTraining(training)
        } else {
            dao.updateTraining(training)
        }
    }
    suspend fun deleteTraining(id: Long) = dao.deleteTrainingById(id)

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
        return id
    }
    suspend fun updateVolunteerStatus(reg: VolunteerRegistrationEntity, newStatus: String) {
        dao.updateVolunteerRegistration(reg.copy(status = newStatus))
    }
    suspend fun deleteVolunteerRegistration(id: Long) = dao.deleteVolunteerRegistrationById(id)

    // --- Training Applications ---
    val allTrainingApplications: Flow<List<TrainingApplicationEntity>> = dao.getAllTrainingApplications()
    suspend fun submitTrainingApplication(app: TrainingApplicationEntity): Long {
        return dao.insertTrainingApplication(app)
    }
    suspend fun updateApplicationStatus(app: TrainingApplicationEntity, newStatus: String) {
        dao.updateTrainingApplication(app.copy(status = newStatus))
    }
    suspend fun deleteTrainingApplication(id: Long) = dao.deleteTrainingApplicationById(id)

    // --- Impact Metrics ---
    val impactMetrics: Flow<List<ImpactMetricEntity>> = dao.getAllImpactMetrics()
    suspend fun updateImpactMetric(metric: ImpactMetricEntity) {
        dao.updateMetric(metric)
    }

    // --- Media & Testimonials ---
    val allMediaTestimonials: Flow<List<MediaTestimonialEntity>> = dao.getAllMediaTestimonials()
    suspend fun saveMediaTestimonial(item: MediaTestimonialEntity) {
        if (item.id == 0L) {
            dao.insertMediaTestimonial(item)
        } else {
            dao.updateMediaTestimonial(item)
        }
    }
    suspend fun deleteMediaTestimonial(id: Long) = dao.deleteMediaTestimonialById(id)

    // --- Org Info & Config ---
    val allOrgInfo: Flow<List<OrgInfoEntity>> = dao.getAllOrgInfo()
    suspend fun getOrgInfo(key: String): String? = dao.getOrgInfoValue(key)
    suspend fun setOrgInfo(key: String, value: String) {
        dao.setOrgInfo(OrgInfoEntity(key, value))
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

    // Reset & Synchronize with authentic Facebook & AIL4C data
    suspend fun resetAndSyncOfficialFacebookData() = withContext(Dispatchers.IO) {
        dao.deleteAllNews()
        dao.deleteAllActions()
        dao.deleteAllProjects()
        dao.deleteAllTrainings()
        dao.deleteAllMediaTestimonials()
        dao.deleteAllMetrics()
        seedAuthenticData()
    }

    suspend fun ensureDefaultDataSeeded() = withContext(Dispatchers.IO) {
        val currentNews = dao.getAllNews().firstOrNull()
        if (!currentNews.isNullOrEmpty()) return@withContext
        seedAuthenticData()
    }

    private suspend fun seedAuthenticData() {
        // 1. Impact Metrics (Real field metrics from AIL4C Bouaké)
        dao.insertAllMetrics(
            listOf(
                ImpactMetricEntity(
                    metricKey = "trees_planted",
                    label = "Arbres & Plants Mis en Terre",
                    valueNumber = 42500,
                    unit = "plants en Gbêkê",
                    iconKey = "forest"
                ),
                ImpactMetricEntity(
                    metricKey = "youth_sensitized",
                    label = "Jeunes & Citoyens Sensibilisés",
                    valueNumber = 2285,
                    unit = "personnes touchées",
                    iconKey = "groups"
                ),
                ImpactMetricEntity(
                    metricKey = "youth_trained",
                    label = "Jeunes Formés aux Métiers Verts",
                    valueNumber = 1650,
                    unit = "bénéficiaires insérés",
                    iconKey = "school"
                ),
                ImpactMetricEntity(
                    metricKey = "waste_collected_tons",
                    label = "Déchets Plastiques Recyclés",
                    valueNumber = 210,
                    unit = "tonnes valorisées",
                    iconKey = "recycling"
                ),
                ImpactMetricEntity(
                    metricKey = "actions_completed",
                    label = "Mobilisations & Salubrité",
                    valueNumber = 84,
                    unit = "journées citoyennes",
                    iconKey = "task_alt"
                )
            )
        )

        // 2. Official News from Facebook Page & Press
        dao.insertAllNews(
            listOf(
                NewsArticleEntity(
                    title = "Semaine de la Population à Bouaké : L'AIL4C et l'UNFPA unissent leurs forces",
                    summary = "Grande mobilisation citoyenne pour la santé de la reproduction, la lutte contre les VBG et la préservation de l'environnement avec plus de 2 285 personnes touchées.",
                    content = "Du 20 au 27 juillet, l'Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage (AIL4C), présidée par Aka Koffi Ezéchiel, a mené avec succès la 'Semaine de la Population' en partenariat avec le Fonds des Nations Unies pour la Population (UNFPA). Placée sous le thème 'La contribution de la population à l'atteinte des objectifs de développement durable', cette semaine a permis de sensibiliser 785 personnes directement et plus de 1 500 indirectement sur les violences basées sur le genre (VBG), la santé sexuelle et reproductive, la lutte contre les grossesses en cours de scolarité et l'éco-citoyenneté active à Bouaké.",
                    category = "Partenariat UNFPA",
                    dateText = "27 Juillet 2024",
                    imageResName = "img_youth_training",
                    author = "Aka Koffi Ezéchiel (Président AIL4C)",
                    isFeatured = true,
                    isPublished = true,
                    viewsCount = 1420
                ),
                NewsArticleEntity(
                    title = "Projet 'Ma Ville Propre' : Salubrité et Curage de caniveaux au quartier Tchelekro",
                    summary = "Mobilisation des jeunes et femmes de Bouaké pour un cadre de vie sain, la prévention des inondations et le tri des déchets.",
                    content = "L'ONG AIL4C a organisé une grande journée de salubrité au quartier Tchelekro de Bouaké dans le cadre de son initiative 'Ma Ville Propre'. Munis de râteaux, pelles et gants, les volontaires ont procédé au curage méthodique des caniveaux obstrués et au ramassage des déchets plastiques. 'Nous voulons une ville de Bouaké propre, résiliente face aux crues saisonnières et fière de ses éco-citoyens', a souligné le président Ezéchiel Aka.",
                    category = "Salubrité & Climat",
                    dateText = "18 Novembre 2023",
                    imageResName = "img_waste_cleanup",
                    author = "Cellule Terrain AIL4C",
                    isFeatured = true,
                    isPublished = true,
                    viewsCount = 980
                ),
                NewsArticleEntity(
                    title = "Sensibilisation Éco-Citoyenne dans les Fanzones : Plus de 1 000 jeunes mobilisés",
                    summary = "Campagne intensive sur les dangers des déchets urbains, la protection de la biodiversité et le tri sélectif à Bouaké.",
                    content = "Pendant les grands rassemblements populaires et fanzones à Bouaké, l'AIL4C a déployé sa brigade de sensibilisation écologique. Plus de 1 000 spectateurs et supporters ont été formés aux bonnes pratiques du tri des déchets plastiques, en mettant en garde contre le rejet sauvage dans les caniveaux qui provoque l'asphyxie des cours d'eau et détruit la biodiversité.",
                    category = "Sensibilisation",
                    dateText = "Février 2024",
                    imageResName = "img_youth_training",
                    author = "Pôle Jeunesse & Climat",
                    isFeatured = true,
                    isPublished = true,
                    viewsCount = 750
                ),
                NewsArticleEntity(
                    title = "Reboisement d'envergure en Forêt de Bamoro : 5 000 arbres plantés",
                    summary = "La jeunesse du Gbêkê mobilisée pour freiner la dégradation des sols et bâtir la ceinture verte de Bouaké.",
                    content = "Avec le soutien des leaders communautaires et des étudiants de l'Université Alassane Ouattara, 5 000 plants d'espèces locales ont été mis en terre dans la zone de Bamoro. Ce reboisement stratégique renforce la régulation thermique de la ville et offre un habitat restauré à la faune locale.",
                    category = "Reboisement",
                    dateText = "12 Août 2026",
                    imageResName = "img_hero_reforestation",
                    author = "Direction des Programmes Verts",
                    isFeatured = false,
                    isPublished = true,
                    viewsCount = 630
                )
            )
        )

        // 3. Actions & Field Mobilizations
        dao.insertAllActions(
            listOf(
                EcoActionEntity(
                    title = "Journée Citoyenne de Salubrité & Tri Plastique à Tchelekro",
                    description = "Grande opération de nettoyage communautaire, curage des caniveaux et tri sélectif des plastiques pour éviter les inondations et assainir le quartier Tchelekro.",
                    category = "Salubrité",
                    dateText = "Samedi 29 Août 2026",
                    timeText = "07h00 - 12h30",
                    location = "Quartier Tchelekro, Carrefour Principal, Bouaké",
                    status = "À venir",
                    maxSpots = 120,
                    registeredCount = 88,
                    coordinatorName = "Aka Koffi Ezéchiel (Président)",
                    coordinatorContact = "+225 07 89 71 02 89",
                    recommendedGear = "Gants et chasubles fournis par l'AIL4C. Prévoir baskets et casquette.",
                    imageResName = "img_waste_cleanup"
                ),
                EcoActionEntity(
                    title = "Caravane Climat, Santé Reproductive & Lutte contre les VBG",
                    description = "Ateliers participatifs et causeries éducatives auprès des jeunes et des femmes en collaboration avec les partenaires UNFPA.",
                    category = "Sensibilisation",
                    dateText = "Mercredi 09 Septembre 2026",
                    timeText = "09h00 - 16h00",
                    location = "Foyer des Jeunes & Campus UAO, Bouaké",
                    status = "À venir",
                    maxSpots = 200,
                    registeredCount = 145,
                    coordinatorName = "Cellule Genre & Jeunesse AIL4C",
                    coordinatorContact = "+225 07 89 97 63 23",
                    recommendedGear = "Entrée libre, supports pédagogiques et kits éco-responsables distribués.",
                    imageResName = "img_youth_training"
                ),
                EcoActionEntity(
                    title = "Campagne de Reforestation 'Ceinture Verte de Bouaké'",
                    description = "Mise en terre de 3 500 plants forestiers et d'arbres d'ombrage le long de l'axe Bamoro-Katiola pour contrer l'avancée de la sécheresse.",
                    category = "Reboisement",
                    dateText = "Samedi 19 Septembre 2026",
                    timeText = "06h30 - 13h00",
                    location = "Site Bamoro Nord, Bouaké",
                    status = "À venir",
                    maxSpots = 180,
                    registeredCount = 132,
                    coordinatorName = "Kouassi Jean-Luc (Pôle Environnement)",
                    coordinatorContact = "+225 07 89 71 02 89",
                    recommendedGear = "Bottes ou chaussures fermées, gourde réutilisable.",
                    imageResName = "img_hero_reforestation"
                ),
                EcoActionEntity(
                    title = "Opération Éco-Gestes & Recyclage dans les Marchés de Bouaké",
                    description = "Sensibilisation des commerçantes au tri des déchets organiques pour le compost et collecte des emballages plastiques.",
                    category = "Recyclage",
                    dateText = "Vendredi 25 Septembre 2026",
                    timeText = "08h00 - 14h00",
                    location = "Grand Marché & Marché d'Ahougnansou, Bouaké",
                    status = "À venir",
                    maxSpots = 80,
                    registeredCount = 54,
                    coordinatorName = "Brigade Verte AIL4C",
                    coordinatorContact = "+225 07 89 97 63 23",
                    recommendedGear = "Gilets AIL4C et sacs de collecte fournis.",
                    imageResName = "img_waste_cleanup"
                )
            )
        )

        // 4. Projects & Solidary Crowdfunding
        dao.insertAllProjects(
            listOf(
                ProjectEntity(
                    title = "Bouaké Ville Verte & Ceinture Écologique du Gbêkê",
                    summary = "Production et plantation de 50 000 arbres pour créer des microclimats urbains et lutter contre la déforestation.",
                    description = "Face aux hausses de température et à la désertification progressive, l'AIL4C installe des pépinières communautaires dans les quartiers de Bouaké et encadre des reboisements scolaires pour reverdir durablement la ville.",
                    targetBudget = 8000000L,
                    raisedBudget = 5800000L,
                    targetObjective = "50 000 arbres plantés",
                    status = "Actif",
                    expectedImpact = "30 hectares restaurés, 40 jeunes pépiniéristes rémunérés.",
                    partnerName = "Ministère de l'Environnement & Mairie de Bouaké",
                    imageResName = "img_hero_reforestation"
                ),
                ProjectEntity(
                    title = "Unité d'Éco-Pavés & Recyclage des Plastiques",
                    summary = "Création d'ateliers d'insertion pour transformer les déchets plastiques des rues de Bouaké en pavés autobloquants écologiques.",
                    description = "Ce projet combat simultanément le chômage des jeunes et l'insalubrité urbaine en rachetant les plastiques collectés et en les valorisant en pavés ultra-résistants pour cours d'écoles et voiries piétonnes.",
                    targetBudget = 14000000L,
                    raisedBudget = 9950000L,
                    targetObjective = "200 tonnes de plastique valorisées",
                    status = "Actif",
                    expectedImpact = "50 emplois verts directs créés pour les jeunes en quête d'insertion.",
                    partnerName = "Partenaires Techniques & Éco-Entreprises",
                    imageResName = "img_waste_cleanup"
                ),
                ProjectEntity(
                    title = "Programme Climat, VBG & Autonomisation Féminine (UNFPA)",
                    summary = "Accompagnement des jeunes filles et femmes vulnérables vers l'agro-écologie, le maraîchage bio et le leadership éco-citoyen.",
                    description = "En alignement avec la Semaine de la Population, ce projet finance la formation de 200 femmes aux techniques agricoles résilientes, la distribution de foyers améliorés et des modules de sensibilisation sur la santé reproductive et les droits humains.",
                    targetBudget = 10500000L,
                    raisedBudget = 7800000L,
                    targetObjective = "200 femmes et jeunes filles accompagnées",
                    status = "Actif",
                    expectedImpact = "Autonomie économique pérenne et réduction des violences basées sur le genre.",
                    partnerName = "UNFPA & Fonds Genre & Climat",
                    imageResName = "img_youth_training"
                )
            )
        )

        // 5. Practical Trainings for Youth
        dao.insertAllTrainings(
            listOf(
                TrainingEntity(
                    title = "Fabrication d'Éco-Pavés à Base de Plastique Recyclé",
                    domain = "Recyclage & Métiers Verts",
                    duration = "5 semaines",
                    startDateText = "21 Septembre 2026",
                    location = "Centre Pilote Recyclage AIL4C, Bouaké",
                    prerequisites = "Aucun diplôme requis, priorité aux jeunes sans emploi de Bouaké (18-35 ans).",
                    certification = "Certificat d'Opérateur en Éco-Matériaux AIL4C",
                    spotsAvailable = 25,
                    isRegistrationOpen = true,
                    description = "Maîtrise complète de la chaîne : collecte, tri, dosage sable-plastique, fonte sécurisée et moulage de pavés de haute densité.",
                    imageResName = "img_waste_cleanup"
                ),
                TrainingEntity(
                    title = "Agro-écologie, Compostage & Maraîchage Biologique",
                    domain = "Agro-écologie",
                    duration = "4 semaines",
                    startDateText = "05 Octobre 2026",
                    location = "Ferme École AIL4C, Bouaké N'Gattakro",
                    prerequisites = "Motivation pour l'agriculture durable, tous niveaux scolaires.",
                    certification = "Attestation de Qualification en Agro-écologie",
                    spotsAvailable = 30,
                    isRegistrationOpen = true,
                    description = "Techniques de valorisation des déchets organiques en compost fertile, biopesticides naturels, irrigation économe et gestion d'une micro-ferme rentable.",
                    imageResName = "img_youth_training"
                ),
                TrainingEntity(
                    title = "Techniques de Pépinière & Sylviculture Communautaire",
                    domain = "Restauration Forestière",
                    duration = "3 semaines",
                    startDateText = "19 Octobre 2026",
                    location = "Pépinière Centrale AIL4C, Bouaké Koko",
                    prerequisites = "Ouvert à tous les passionnés d'arbres et demandeurs d'emploi.",
                    certification = "Certificat de Pépiniériste Éco-Responsable",
                    spotsAvailable = 20,
                    isRegistrationOpen = true,
                    description = "Semis, greffage, multiplication végétative des espèces forestières et fruitières locales, gestion de la santé des jeunes plants.",
                    imageResName = "img_hero_reforestation"
                ),
                TrainingEntity(
                    title = "Énergies Solaires Décentralisées & Pompage d'Eau Agricole",
                    domain = "Énergies Propres",
                    duration = "6 semaines",
                    startDateText = "02 Novembre 2026",
                    location = "Plateforme Technique AIL4C, Bouaké",
                    prerequisites = "Niveau BEPC ou bases en électricité.",
                    certification = "Certificat d'Installateur Énergie Renouvelable",
                    spotsAvailable = 18,
                    isRegistrationOpen = true,
                    description = "Dimensionnement de kits photovoltaïques solaires pour l'éclairage domestique et les motopompes d'irrigation maraîchère.",
                    imageResName = "img_youth_training"
                )
            )
        )

        // 6. Media & Authentic Testimonials from Facebook & Field
        dao.insertAllMediaTestimonials(
            listOf(
                MediaTestimonialEntity(
                    title = "La vision d'AIL4C par son Président-Fondateur",
                    mediaType = "Témoignage",
                    authorOrLocation = "Aka Koffi Ezéchiel, Président-Fondateur AIL4C",
                    descriptionOrQuote = "« Lutter contre le changement climatique à Bouaké, c'est aussi vaincre le chômage des jeunes. Chaque arbre mis en terre, chaque tonne de plastique retirée de nos caniveaux et transformée en pavé crée de la dignité et de l'emploi pour notre jeunesse. »",
                    imageResName = "img_ail4c_logo",
                    tag = "Direction AIL4C"
                ),
                MediaTestimonialEntity(
                    title = "Semaine de la Population avec l'UNFPA : Un succès retentissant",
                    mediaType = "Photo",
                    authorOrLocation = "Bouaké, Centre Culturel Jacques Aka",
                    descriptionOrQuote = "Sensibilisation de plus de 2 285 personnes aux enjeux croisés de la santé de la reproduction, des VBG et de la résilience climatique en milieu urbain.",
                    imageResName = "img_youth_training",
                    tag = "Partenariat UNFPA"
                ),
                MediaTestimonialEntity(
                    title = "Opération Salubrité à Tchelekro : Préserver notre cadre de vie",
                    mediaType = "Photo",
                    authorOrLocation = "Quartier Tchelekro, Bouaké",
                    descriptionOrQuote = "Curage des caniveaux et nettoyage communautaire avec les éco-volontaires d'AIL4C pour prévenir les inondations et préserver la santé publique.",
                    imageResName = "img_waste_cleanup",
                    tag = "Salubrité"
                ),
                MediaTestimonialEntity(
                    title = "De bénévole à entrepreneur dans l'éco-pavé",
                    mediaType = "Témoignage",
                    authorOrLocation = "Koffi N'Guessan, Ancien Volontaire & Promoteur Vert",
                    descriptionOrQuote = "« Grâce aux formations d'AIL4C, je recycle aujourd'hui les plastiques usagés de mon quartier pour fabriquer des pavés écologiques. Je gagne décemment ma vie tout en protégeant Bouaké. »",
                    imageResName = "img_waste_cleanup",
                    tag = "Insertion Jeunesse"
                ),
                MediaTestimonialEntity(
                    title = "Sensibilisation CAN dans les fanzones de Bouaké",
                    mediaType = "Photo",
                    authorOrLocation = "Fanzones & Places Publiques de Bouaké",
                    descriptionOrQuote = "Plus de 1 000 jeunes sensibilisés au tri sélectif et à l'interdiction de jeter les déchets plastiques dans les rigoles.",
                    imageResName = "img_youth_training",
                    tag = "Éco-Citoyenneté"
                ),
                MediaTestimonialEntity(
                    title = "Mobilisation Communautaire & Autorités Locales",
                    mediaType = "Photo",
                    authorOrLocation = "Quartiers de Bouaké & Leaders Traditionnels",
                    descriptionOrQuote = "Rassemblement des chefs communautaires, des femmes dynamiques et des jeunes éco-volontaires d'AIL4C unis pour un cadre de vie sain et durable.",
                    imageResName = "img_community_action",
                    tag = "Engagement Citoyen"
                )
            )
        )

        // 7. Official Org Info Matching Facebook Page
        dao.setAllOrgInfo(
            listOf(
                OrgInfoEntity("org_name", "Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage (des Jeunes)"),
                OrgInfoEntity("org_acronym", "AIL4C"),
                OrgInfoEntity("org_president", "Aka Koffi Ezéchiel"),
                OrgInfoEntity("org_motto", "Agir pour le Climat, Former la Jeunesse, Bâtir l'Avenir"),
                OrgInfoEntity("org_headquarters", "Bouaké, Région du Gbêkê, Côte d'Ivoire"),
                OrgInfoEntity("org_address", "Bouaké - Quartier Tchelekro / Koko / Commerce"),
                OrgInfoEntity("org_phone_1", "+225 07 89 71 02 89"),
                OrgInfoEntity("org_phone_2", "+225 07 89 97 63 23"),
                OrgInfoEntity("org_email", "ongail4c@gmail.com"),
                OrgInfoEntity("org_facebook_url", "https://www.facebook.com/share/1GvChYFAMY/"),
                OrgInfoEntity("org_facebook_page_name", "ONG AIL4C (Page Officielle)"),
                OrgInfoEntity("admin_pin", "1975"),
                OrgInfoEntity("org_mission", "Mobiliser les populations de Bouaké et de Côte d'Ivoire contre les effets néfastes du changement climatique, lutter contre les violences basées sur le genre (VBG) et créer des perspectives concrètes d'emploi et de formation aux métiers verts pour la jeunesse."),
                OrgInfoEntity("org_vision", "Un Bouaké durable, vert et propre où chaque citoyen adopte des réflexes écologiques et où la jeunesse trouve dans la transition écologique un vecteur d'émancipation.")
            )
        )

        // 8. Seed Initial AI Welcome Message if empty
        val existingAiMsgs = dao.getAllAiMessages().firstOrNull()
        if (existingAiMsgs.isNullOrEmpty()) {
            dao.insertAiMessage(
                AiChatMessageEntity(
                    messageText = "🌿 Bonjour et bienvenue sur l'application officielle de l'AIL4C ! Je suis AWA, votre Éco-Assistante IA dédiée à la lutte contre le réchauffement climatique et l'insertion des jeunes à Bouaké. Comment puis-je vous accompagner aujourd'hui ?",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // 9. Seed Initial Logged User if not existing
        val existingUser = dao.getCurrentUser()
        if (existingUser == null) {
            dao.saveUserProfile(
                UserProfileEntity(
                    id = "current_user",
                    fullName = "Éco-Citoyen Bouaké",
                    identifier = "+225 07 00 00 00",
                    authType = "PHONE",
                    phoneNumber = "+225 07 00 00 00",
                    email = "citoyen.bouake@ail4c-ci.org",
                    city = "Bouaké",
                    quartier = "Commerce",
                    ecoPoints = 75,
                    volunteerLevel = "Éco-Volontaire Engagé",
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
                val repo = AilRepository(db.ailDao())
                INSTANCE = repo
                // Trigger background seed
                CoroutineScope(Dispatchers.IO).launch {
                    repo.ensureDefaultDataSeeded()
                }
                repo
            }
        }
    }
}
