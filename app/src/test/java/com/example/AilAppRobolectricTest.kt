package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AilDatabase
import com.example.data.model.EcoActionEntity
import com.example.data.model.ImpactMetricEntity
import com.example.data.model.NewsArticleEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TrainingApplicationEntity
import com.example.data.model.TrainingEntity
import com.example.data.model.VolunteerRegistrationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AilAppRobolectricTest {

    private lateinit var database: AilDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AilDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testMainActivityLaunch() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
    }

    @Test
    fun testInsertAndRetrieveNews() = runBlocking {
        val dao = database.ailDao()
        val news = NewsArticleEntity(
            title = "Grande Opération de Reboisement à Bouaké",
            summary = "Plus de 500 jeunes mobilisés pour restaurer la forêt.",
            content = "L'ONG AIL4C a réuni des bénévoles et partenaires pour une journée écologique...",
            category = "Reboisement",
            dateText = "24 Août 2026",
            author = "Cellule Communication AIL4C",
            imageResName = "img_hero_reforestation",
            isFeatured = true
        )
        dao.insertNews(news)

        val newsList = dao.getAllNews().first()
        assertEquals(1, newsList.size)
        assertEquals("Grande Opération de Reboisement à Bouaké", newsList[0].title)
        assertTrue(newsList[0].isFeatured)
    }

    @Test
    fun testVolunteerRegistrationFlow() = runBlocking {
        val dao = database.ailDao()
        val reg = VolunteerRegistrationEntity(
            fullName = "Konan Yao",
            phone = "+225 07 12 34 56 78",
            email = "konan.yao@example.ci",
            city = "Bouaké",
            actionId = 1L,
            actionTitle = "Reboisement Forêt de Bamoro",
            availability = "Weekends",
            motivation = "Je souhaite agir activement pour l'environnement de ma région.",
            dateSubmitted = "24/08/2026 10:00"
        )
        dao.insertVolunteerRegistration(reg)

        val list = dao.getAllVolunteerRegistrations().first()
        assertEquals(1, list.size)
        assertEquals("Konan Yao", list[0].fullName)
        assertEquals("Reçue", list[0].status)

        // Update status to Validée
        dao.updateVolunteerRegistration(list[0].copy(status = "Validée"))
        val updatedList = dao.getAllVolunteerRegistrations().first()
        assertEquals("Validée", updatedList[0].status)
    }

    @Test
    fun testTrainingApplicationFlow() = runBlocking {
        val dao = database.ailDao()
        val training = TrainingEntity(
            title = "Technicien en Agroforesterie et Pépinières",
            domain = "Agro-écologie",
            duration = "4 semaines",
            startDateText = "01 Octobre 2026",
            location = "Centre Pilote AIL4C Bouaké",
            prerequisites = "Motivation, niveau 3ème minimum",
            certification = "Certificat de Qualification AIL4C",
            spotsAvailable = 25,
            description = "Apprentissage des techniques de multiplication végétale...",
            imageResName = "img_youth_training",
            isRegistrationOpen = true
        )
        val trainingId = dao.insertTraining(training)

        val app = TrainingApplicationEntity(
            trainingId = trainingId,
            trainingTitle = "Technicien en Agroforesterie et Pépinières",
            fullName = "Ahou Estelle",
            phone = "+225 05 98 76 54 32",
            email = "estelle.ahou@example.ci",
            educationLevel = "BAC D",
            motivation = "Je veux créer une ferme agro-écologique à Bouaké.",
            dateSubmitted = "24/08/2026 11:30"
        )
        dao.insertTrainingApplication(app)

        val applications = dao.getAllTrainingApplications().first()
        assertEquals(1, applications.size)
        assertEquals("Ahou Estelle", applications[0].fullName)
    }

    @Test
    fun testImpactMetricsUpdate() = runBlocking {
        val dao = database.ailDao()
        val metric = ImpactMetricEntity(
            metricKey = "trees_planted",
            label = "Arbres plantés",
            valueNumber = 12500L,
            unit = "arbres",
            iconKey = "forest"
        )
        dao.insertOrUpdateMetric(metric)

        val metrics = dao.getAllImpactMetrics().first()
        assertEquals(1, metrics.size)
        assertEquals(12500L, metrics[0].valueNumber)

        // Update metric
        dao.updateMetric(metrics[0].copy(valueNumber = 15000L))
        val updatedMetrics = dao.getAllImpactMetrics().first()
        assertEquals(15000L, updatedMetrics[0].valueNumber)
    }

    @Test
    fun testUserProfileAndAiChat() = runBlocking {
        val dao = database.ailDao()
        val user = com.example.data.model.UserProfileEntity(
            fullName = "Koffi Jean",
            identifier = "+225 07 11 22 33",
            authType = "PHONE",
            city = "Bouaké",
            quartier = "Koko",
            ecoPoints = 120
        )
        dao.saveUserProfile(user)

        val retrievedUser = dao.getCurrentUser()
        assertEquals("Koffi Jean", retrievedUser?.fullName)
        assertEquals(120, retrievedUser?.ecoPoints)

        // AI Chat message insertion
        val aiMsg = com.example.data.model.AiChatMessageEntity(
            messageText = "🌿 Bienvenue sur AIL4C !",
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertAiMessage(aiMsg)

        val msgs = dao.getAllAiMessages().first()
        assertEquals(1, msgs.size)
        assertEquals("🌿 Bienvenue sur AIL4C !", msgs[0].messageText)
    }

    @Test
    fun testProjectDonationUpdate() = runBlocking {
        val dao = database.ailDao()
        val project = ProjectEntity(
            title = "Ceinture Verte de Bouaké - 50 000 Arbres",
            summary = "Projet majeur de reboisement périurbain.",
            description = "Création d'un corridor biologique pour lutter contre les îlots de chaleur.",
            targetBudget = 15000000L,
            raisedBudget = 6250000L,
            targetObjective = "50 000 arbres",
            status = "En cours",
            expectedImpact = "25 hectares restaurés",
            partnerName = "Ministère de l'Environnement",
            imageResName = "img_hero_reforestation"
        )
        val projectId = dao.insertProject(project)

        val retrieved = dao.getProjectById(projectId)
        assertNotNull(retrieved)
        assertEquals(6250000L, retrieved?.raisedBudget)

        // Make donation of 50 000 FCFA
        dao.updateProject(retrieved!!.copy(raisedBudget = retrieved.raisedBudget + 50000L))
        val updated = dao.getProjectById(projectId)
        assertEquals(6300000L, updated?.raisedBudget)
    }

    @Test
    fun testAdminEmailAuthorizationRules() = runBlocking {
        val repo = com.example.data.repository.AilRepository(database.ailDao())

        // Test 1: Phone user -> not admin
        val phoneUser = repo.loginWithPhone("+225 07 00 00 00", "Bénévole Simple")
        val isPhoneAdmin = phoneUser.identifier.lowercase() in com.example.ui.viewmodel.AilViewModel.ADMIN_AUTHORIZED_EMAILS
        org.junit.Assert.assertFalse(isPhoneAdmin)

        // Test 2: Normal email -> not admin
        val regularUser = repo.loginWithEmail("autre_utilisateur@gmail.com", "Autre Utilisateur")
        val isRegularAdmin = regularUser.identifier.lowercase() in com.example.ui.viewmodel.AilViewModel.ADMIN_AUTHORIZED_EMAILS
        org.junit.Assert.assertFalse(isRegularAdmin)

        // Test 3: Authorized email 1 -> is admin
        val admin1 = repo.loginWithEmail("atchouyaosylvain59@gmail.com", "Sylvain Atchou")
        val isAdmin1 = admin1.identifier.lowercase() in com.example.ui.viewmodel.AilViewModel.ADMIN_AUTHORIZED_EMAILS
        assertTrue(isAdmin1)

        // Test 4: Authorized email 2 -> is admin
        val admin2 = repo.loginWithEmail("ail4c03@gmail.com", "AIL4C Officiel")
        val isAdmin2 = admin2.identifier.lowercase() in com.example.ui.viewmodel.AilViewModel.ADMIN_AUTHORIZED_EMAILS
        assertTrue(isAdmin2)
    }
}
