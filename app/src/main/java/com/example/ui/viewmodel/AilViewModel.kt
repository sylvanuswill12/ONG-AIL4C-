package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiChatMessageEntity
import com.example.data.model.EcoActionEntity
import com.example.data.model.EcoActivityPreset
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
import com.example.data.model.UserBadgeEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.VolunteerRegistrationEntity
import com.example.data.remote.AppUpdateInfo
import com.example.data.remote.GitHubUpdateService
import com.example.data.repository.AilRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    HOME,
    ACTIONS,
    PROJECTS,
    TRAININGS,
    NEWS,
    MEDIA,
    AI_ASSISTANT,
    QUIZ,
    ABOUT,
    PROFILE,
    ADMIN
}

class AilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AilRepository.getInstance(application)

    // Screen State
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    companion object {
        val ADMIN_AUTHORIZED_EMAILS = listOf(
            "atchouyaosylvain59@gmail.com",
            "ail4c03@gmail.com"
        )
    }

    // Navigation back stack or detail sheet states
    private val _selectedNews = MutableStateFlow<NewsArticleEntity?>(null)
    val selectedNews: StateFlow<NewsArticleEntity?> = _selectedNews.asStateFlow()

    private val _selectedAction = MutableStateFlow<EcoActionEntity?>(null)
    val selectedAction: StateFlow<EcoActionEntity?> = _selectedAction.asStateFlow()

    private val _selectedProject = MutableStateFlow<ProjectEntity?>(null)
    val selectedProject: StateFlow<ProjectEntity?> = _selectedProject.asStateFlow()

    private val _selectedTraining = MutableStateFlow<TrainingEntity?>(null)
    val selectedTraining: StateFlow<TrainingEntity?> = _selectedTraining.asStateFlow()

    // Admin Auth State
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // Auth Dialog / Sheet State
    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    // AI Assistant Sheet / Thinking State
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // GitHub App Updates State
    private val _appUpdateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val appUpdateInfo: StateFlow<AppUpdateInfo?> = _appUpdateInfo.asStateFlow()

    private val _isCheckingUpdate = MutableStateFlow(false)
    val isCheckingUpdate: StateFlow<Boolean> = _isCheckingUpdate.asStateFlow()

    private val _isUpdateDismissed = MutableStateFlow(false)
    val isUpdateDismissed: StateFlow<Boolean> = _isUpdateDismissed.asStateFlow()

    private val _showUpdateModal = MutableStateFlow(false)
    val showUpdateModal: StateFlow<Boolean> = _showUpdateModal.asStateFlow()

    init {
        // Auto-check for updates on launch
        checkForAppUpdates(silent = true)
        triggerDailyLoginCheck()
        loadDailyQuizState()
    }

    // Feedback Toast / Snackbar event
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Newly Unlocked Badge for Fireworks Celebration Dialog
    private val _newlyUnlockedBadge = MutableStateFlow<UserBadgeEntity?>(null)
    val newlyUnlockedBadge: StateFlow<UserBadgeEntity?> = _newlyUnlockedBadge.asStateFlow()

    fun dismissBadgeCelebration() {
        val badge = _newlyUnlockedBadge.value
        _newlyUnlockedBadge.value = null
        if (badge != null) {
            viewModelScope.launch {
                repository.markBadgeCelebrationSeen(badge.badgeId)
            }
        }
    }

    // Daily Eco-Quiz 1 Question for 10 Points State
    private val _dailyQuestion = MutableStateFlow(QuizBank.getDailyQuestion())
    val dailyQuestion: StateFlow<QuizQuestion> = _dailyQuestion.asStateFlow()

    private val _dailyQuizSelectedOption = MutableStateFlow<Int?>(null)
    val dailyQuizSelectedOption: StateFlow<Int?> = _dailyQuizSelectedOption.asStateFlow()

    private val _dailyQuizIsCorrect = MutableStateFlow<Boolean?>(null)
    val dailyQuizIsCorrect: StateFlow<Boolean?> = _dailyQuizIsCorrect.asStateFlow()

    private val _dailyQuizBotCommentary = MutableStateFlow<String?>(null)
    val dailyQuizBotCommentary: StateFlow<String?> = _dailyQuizBotCommentary.asStateFlow()

    private val _isDailyQuizCompleted = MutableStateFlow(false)
    val isDailyQuizCompleted: StateFlow<Boolean> = _isDailyQuizCompleted.asStateFlow()

    // User Profile Stream
    val currentUserProfile: StateFlow<UserProfileEntity?> = repository.currentUserProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Badges & Eco-Activities Streams
    val allBadges: StateFlow<List<UserBadgeEntity>> = repository.allBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unlockedBadges: StateFlow<List<UserBadgeEntity>> = repository.unlockedBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEcoActivities: StateFlow<List<EcoActivityRecordEntity>> = repository.allEcoActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Authorization derived purely from current user email matching the allowed list
    val isUserAdminAuthorized: StateFlow<Boolean> = repository.currentUserProfile
        .map { profile ->
            if (profile == null) false
            else {
                val emailOrId = (profile.email.ifBlank { profile.identifier }).trim().lowercase()
                emailOrId in ADMIN_AUTHORIZED_EMAILS
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // AI Messages Stream
    val allAiMessages: StateFlow<List<AiChatMessageEntity>> = repository.allAiMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observed Data Streams
    val allNews: StateFlow<List<NewsArticleEntity>> = repository.allNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedNews: StateFlow<List<NewsArticleEntity>> = repository.publishedNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredNews: StateFlow<List<NewsArticleEntity>> = repository.featuredNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allActions: StateFlow<List<EcoActionEntity>> = repository.allActions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTrainings: StateFlow<List<TrainingEntity>> = repository.allTrainings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMentorsTrainers: StateFlow<List<MentorTrainerEntity>> = repository.allMentorsTrainers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableMentors: StateFlow<List<MentorTrainerEntity>> = repository.availableMentors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val impactMetrics: StateFlow<List<ImpactMetricEntity>> = repository.impactMetrics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mediaTestimonials: StateFlow<List<MediaTestimonialEntity>> = repository.allMediaTestimonials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val volunteerRegistrations: StateFlow<List<VolunteerRegistrationEntity>> = repository.allVolunteerRegistrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trainingApplications: StateFlow<List<TrainingApplicationEntity>> = repository.allTrainingApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orgInfoList: StateFlow<List<OrgInfoEntity>> = repository.allOrgInfo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orgInfoMap: StateFlow<Map<String, String>> = repository.allOrgInfo
        .map { list -> list.associate { it.key to it.value } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Live Cloud Sync State
    val cloudSyncStatus: StateFlow<com.example.data.remote.CloudSyncStatus> = repository.cloudSyncStatus
        ?: MutableStateFlow(com.example.data.remote.CloudSyncStatus()).asStateFlow()

    fun triggerManualCloudSync() {
        repository.triggerCloudSync { success ->
            if (success) {
                showToast("✅ Données synchronisées avec succès en temps réel !")
            } else {
                showToast("⚠️ Impossible de synchroniser. Vérifiez votre connexion Internet.")
            }
        }
    }

    // --- GitHub App Updates Operations ---
    fun checkForAppUpdates(silent: Boolean = false) {
        viewModelScope.launch {
            _isCheckingUpdate.value = true
            try {
                val currentVerName = try {
                    val pInfo = getApplication<Application>().packageManager.getPackageInfo(getApplication<Application>().packageName, 0)
                    pInfo.versionName ?: "1.0"
                } catch (e: Exception) {
                    "1.0"
                }

                val update = GitHubUpdateService.checkForUpdates(
                    currentVersionName = currentVerName
                )
                _appUpdateInfo.value = update

                if (!silent) {
                    if (update.hasUpdate) {
                        _showUpdateModal.value = true
                        showToast("Nouvelle version v${update.latestVersionName} disponible !")
                    } else {
                        showToast("Votre application AIL4C est déjà à jour (v$currentVerName).")
                    }
                }
            } catch (e: Exception) {
                if (!silent) {
                    showToast("Impossible de vérifier les mises à jour : ${e.localizedMessage}")
                }
            } finally {
                _isCheckingUpdate.value = false
            }
        }
    }

    fun openUpdateModal() {
        _showUpdateModal.value = true
    }

    fun closeUpdateModal() {
        _showUpdateModal.value = false
    }

    fun dismissUpdateBanner() {
        _isUpdateDismissed.value = true
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openAuthDialog() {
        _isAuthDialogOpen.value = true
    }

    fun closeAuthDialog() {
        _isAuthDialogOpen.value = false
    }

    fun selectNews(news: NewsArticleEntity?) {
        _selectedNews.value = news
    }

    fun selectAction(action: EcoActionEntity?) {
        _selectedAction.value = action
    }

    fun selectProject(project: ProjectEntity?) {
        _selectedProject.value = project
    }

    fun selectTraining(training: TrainingEntity?) {
        _selectedTraining.value = training
    }

    // --- User Authentication (Phone or Email) ---
    fun registerUser(
        fullName: String,
        identifier: String,
        authType: String,
        password: String,
        city: String = "Bouaké",
        quartier: String = "Commerce",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val res = repository.registerUser(fullName, identifier, authType, password, city, quartier)) {
                is com.example.data.repository.AuthResult.Success -> {
                    _isAuthDialogOpen.value = false
                    val cleanId = identifier.trim().lowercase()
                    val isAuthorizedAdmin = cleanId in ADMIN_AUTHORIZED_EMAILS
                    _isAdminLoggedIn.value = isAuthorizedAdmin
                    showToast("🎉 Compte créé avec succès ! Bienvenue ${res.profile.fullName}.")
                    onSuccess()
                }
                is com.example.data.repository.AuthResult.Error -> {
                    showToast("⚠️ ${res.message}")
                }
            }
        }
    }

    fun loginUser(
        identifier: String,
        authType: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            when (val res = repository.loginUser(identifier, authType, password)) {
                is com.example.data.repository.AuthResult.Success -> {
                    _isAuthDialogOpen.value = false
                    val cleanId = identifier.trim().lowercase()
                    val isAuthorizedAdmin = cleanId in ADMIN_AUTHORIZED_EMAILS
                    _isAdminLoggedIn.value = isAuthorizedAdmin
                    if (isAuthorizedAdmin) {
                        showToast("✨ Bienvenue Administrateur ${res.profile.fullName} ! Synchronisation active.")
                    } else {
                        showToast("👋 Bienvenue ${res.profile.fullName} ! Connexion réussie.")
                    }
                    onSuccess()
                }
                is com.example.data.repository.AuthResult.Error -> {
                    showToast("⚠️ ${res.message}")
                }
            }
        }
    }

    fun loginWithPhone(phoneNumber: String, fullName: String, city: String = "Bouaké", quartier: String = "Commerce") {
        if (phoneNumber.isBlank()) {
            showToast("Veuillez saisir votre numéro de téléphone (ex: 07 00 00 00 00)")
            return
        }
        viewModelScope.launch {
            val user = repository.loginWithPhone(phoneNumber, fullName, city, quartier)
            _isAuthDialogOpen.value = false
            _isAdminLoggedIn.value = false
            showToast("Bienvenue ${user.fullName} ! Connexion réussie.")
        }
    }

    fun loginWithEmail(email: String, fullName: String, city: String = "Bouaké", quartier: String = "Commerce") {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            showToast("Veuillez saisir une adresse email valide.")
            return
        }
        viewModelScope.launch {
            val user = repository.loginWithEmail(cleanEmail, fullName, city, quartier)
            _isAuthDialogOpen.value = false
            val isAuthorizedAdmin = cleanEmail.lowercase() in ADMIN_AUTHORIZED_EMAILS
            if (isAuthorizedAdmin) {
                _isAdminLoggedIn.value = true
                showToast("Bienvenue Administrateur ${user.fullName} ! Accès aux modifications débloqué.")
            } else {
                _isAdminLoggedIn.value = false
                showToast("Bienvenue ${user.fullName} ! Connexion réussie.")
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            repository.loginAsGuest()
            _isAuthDialogOpen.value = false
            _isAdminLoggedIn.value = false
            showToast("Mode invité activé. Bienvenue sur AIL4C !")
        }
    }

    fun updateUserProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateUserProfile(profile)
            showToast("Profil éco-citoyen mis à jour.")
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.logoutUser()
            _isAdminLoggedIn.value = false
            showToast("Vous avez été déconnecté.")
        }
    }

    // --- Eco-Citizen Activities & Badges Operations ---
    fun recordEcoActivity(preset: EcoActivityPreset) {
        viewModelScope.launch {
            val newlyUnlocked = repository.recordEcoActivity(
                activityKey = preset.key,
                title = preset.title,
                category = preset.category,
                pointsAwarded = preset.points,
                description = preset.description,
                iconKey = preset.iconKey
            )
            showToast("🌱 Action validée ! +${preset.points} Points Éco-Citoyens !")
            if (newlyUnlocked != null) {
                _newlyUnlockedBadge.value = newlyUnlocked
            }
        }
    }

    fun clearNewlyUnlockedBadge() {
        val badge = _newlyUnlockedBadge.value
        _newlyUnlockedBadge.value = null
        if (badge != null) {
            viewModelScope.launch {
                repository.markBadgeCelebrationSeen(badge.badgeId)
            }
        }
    }

    // --- Daily Check-in & Connexion Quotidienne (+5 pts) ---
    fun triggerDailyLoginCheck() {
        viewModelScope.launch {
            val (isAwarded, badge) = repository.checkAndAwardDailyLogin()
            if (isAwarded) {
                showToast("✨ +5 Points Éco-Citoyens pour votre connexion du jour !")
                if (badge != null) {
                    _newlyUnlockedBadge.value = badge
                }
            }
        }
    }

    // --- Daily Quiz Operations (1 Question par jour = 10 Points) ---
    fun loadDailyQuizState() {
        viewModelScope.launch {
            val isCompleted = repository.isDailyQuizAnsweredToday()
            _isDailyQuizCompleted.value = isCompleted
            _dailyQuestion.value = QuizBank.getDailyQuestion()
        }
    }

    fun submitDailyQuizAnswer(selectedOptionIndex: Int) {
        if (_dailyQuizSelectedOption.value != null || _isDailyQuizCompleted.value) return
        val question = _dailyQuestion.value
        _dailyQuizSelectedOption.value = selectedOptionIndex
        val isCorrect = selectedOptionIndex == question.correctIndex
        _dailyQuizIsCorrect.value = isCorrect
        _dailyQuizBotCommentary.value = QuizBank.getBotCommentary(isCorrect, question)
        _isDailyQuizCompleted.value = true

        viewModelScope.launch {
            val newlyUnlocked = repository.recordDailyQuizAnswer(question, isCorrect)
            if (isCorrect) {
                showToast("🎯 Bravo ! +10 Points Éco-Citoyens remportés !")
            } else {
                showToast("🌱 Réponse enregistrée. Rendez-vous demain pour une nouvelle question !")
            }
            if (newlyUnlocked != null) {
                _newlyUnlockedBadge.value = newlyUnlocked
            }
        }
    }

    fun restartDailyQuizForTesting() {
        _dailyQuizSelectedOption.value = null
        _dailyQuizIsCorrect.value = null
        _dailyQuizBotCommentary.value = null
        _isDailyQuizCompleted.value = false
        showToast("Question du jour réinitialisée pour entraînement !")
    }

    // --- AI Assistant Operations ---
    fun sendAiMessage(prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isAiThinking.value = true
            try {
                val (_, newlyUnlockedBadge) = repository.askAiAssistant(prompt.trim())
                if (newlyUnlockedBadge != null) {
                    _newlyUnlockedBadge.value = newlyUnlockedBadge
                }
            } catch (e: Exception) {
                showToast("Erreur de connexion avec ÉcoBot IA")
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearAiChat() {
        viewModelScope.launch {
            repository.clearAiChatHistory()
            showToast("Discussion réinitialisée.")
        }
    }

    // --- Admin Authentication ---
    fun loginAdmin(pin: String): Boolean {
        val currentProfile = currentUserProfile.value
        val emailOrId = (currentProfile?.email?.ifBlank { currentProfile.identifier } ?: "").trim().lowercase()
        val isAuthorized = emailOrId in ADMIN_AUTHORIZED_EMAILS

        if (!isAuthorized) {
            showToast("Accès restreint : Seuls les comptes administrateurs autorisés peuvent modifier l'application.")
            return false
        }

        val trimmedPin = pin.trim()
        val isValid = trimmedPin.equals("AIL4CCI", ignoreCase = true) || trimmedPin == "1975" || trimmedPin == "admin"
        if (isValid) {
            _isAdminLoggedIn.value = true
            showToast("Accès administrateur validé avec succès !")
        } else {
            showToast("Mot de passe administrateur incorrect.")
        }
        return isValid
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        showToast("Session administrateur fermée.")
    }

    // --- Volunteer Registration ---
    fun registerVolunteer(
        fullName: String,
        phone: String,
        email: String,
        city: String,
        actionId: Long?,
        actionTitle: String,
        availability: String,
        motivation: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || phone.isBlank()) {
            showToast("Veuillez renseigner votre nom et votre numéro de téléphone.")
            return
        }
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date())
            val (_, newlyUnlocked) = repository.registerVolunteer(
                VolunteerRegistrationEntity(
                    fullName = fullName.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    city = if (city.isBlank()) "Bouaké" else city.trim(),
                    actionId = actionId,
                    actionTitle = actionTitle.ifBlank { "Volontariat Général AIL4C" },
                    availability = availability,
                    motivation = motivation.trim(),
                    dateSubmitted = dateStr
                )
            )
            showToast("🌿 Inscription validée ! +10 Points Éco-Citoyens pour votre participation terrain !")
            if (newlyUnlocked != null) {
                _newlyUnlockedBadge.value = newlyUnlocked
            }
            onSuccess()
        }
    }

    // --- Training Application ---
    fun submitTrainingApplication(
        trainingId: Long,
        trainingTitle: String,
        fullName: String,
        phone: String,
        email: String,
        educationLevel: String,
        motivation: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || phone.isBlank()) {
            showToast("Veuillez renseigner votre nom et votre numéro de téléphone.")
            return
        }
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH).format(Date())
            repository.submitTrainingApplication(
                TrainingApplicationEntity(
                    trainingId = trainingId,
                    trainingTitle = trainingTitle,
                    fullName = fullName.trim(),
                    phone = phone.trim(),
                    email = email.trim(),
                    educationLevel = educationLevel,
                    motivation = motivation.trim(),
                    dateSubmitted = dateStr
                )
            )
            showToast("Votre candidature pour '$trainingTitle' a été transmise !")
            onSuccess()
        }
    }

    // --- Donation Simulation ---
    fun makeDonation(projectId: Long, amount: Long, donorName: String, onSuccess: () -> Unit) {
        if (amount <= 0) {
            showToast("Veuillez indiquer un montant valide.")
            return
        }
        viewModelScope.launch {
            repository.recordDonation(projectId, amount)
            showToast("Merci infiniment pour votre don de $amount FCFA au projet !")
            onSuccess()
        }
    }

    // --- Admin CRUD Operations ---
    fun saveNews(news: NewsArticleEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveNews(news)
            showToast("Actualité enregistrée avec succès.")
            onSuccess()
        }
    }

    fun deleteNews(id: Long) {
        viewModelScope.launch {
            repository.deleteNews(id)
            if (_selectedNews.value?.id == id) _selectedNews.value = null
            showToast("Actualité supprimée.")
        }
    }

    fun saveAction(action: EcoActionEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveAction(action)
            showToast("Action / Événement enregistré.")
            onSuccess()
        }
    }

    fun deleteAction(id: Long) {
        viewModelScope.launch {
            repository.deleteAction(id)
            if (_selectedAction.value?.id == id) _selectedAction.value = null
            showToast("Action supprimée.")
        }
    }

    fun saveProject(project: ProjectEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveProject(project)
            showToast("Projet enregistré.")
            onSuccess()
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_selectedProject.value?.id == id) _selectedProject.value = null
            showToast("Projet supprimé.")
        }
    }

    fun saveTraining(training: TrainingEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveTraining(training)
            showToast("Formation enregistrée.")
            onSuccess()
        }
    }

    fun deleteTraining(id: Long) {
        viewModelScope.launch {
            repository.deleteTraining(id)
            if (_selectedTraining.value?.id == id) _selectedTraining.value = null
            showToast("Formation supprimée.")
        }
    }

    fun saveMentorTrainer(mentor: MentorTrainerEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveMentorTrainer(mentor)
            showToast("Profil formateur / mentor enregistré avec succès.")
            onSuccess()
        }
    }

    fun deleteMentorTrainer(id: Long) {
        viewModelScope.launch {
            repository.deleteMentorTrainer(id)
            showToast("Formateur / mentor supprimé.")
        }
    }

    fun deleteAllMentorsTrainers() {
        viewModelScope.launch {
            repository.deleteAllMentorsTrainers()
            showToast("Tous les mentors ont été effacés.")
        }
    }

    fun updateVolunteerStatus(reg: VolunteerRegistrationEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateVolunteerStatus(reg, newStatus)
            showToast("Statut bénévole mis à jour : $newStatus")
        }
    }

    fun deleteVolunteerRegistration(id: Long) {
        viewModelScope.launch {
            repository.deleteVolunteerRegistration(id)
            showToast("Inscription bénévole retirée.")
        }
    }

    fun updateApplicationStatus(app: TrainingApplicationEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(app, newStatus)
            showToast("Statut candidature mis à jour : $newStatus")
        }
    }

    fun deleteTrainingApplication(id: Long) {
        viewModelScope.launch {
            repository.deleteTrainingApplication(id)
            showToast("Candidature retirée.")
        }
    }

    fun updateImpactMetric(metric: ImpactMetricEntity, newValue: Long) {
        viewModelScope.launch {
            repository.updateImpactMetric(metric.copy(valueNumber = newValue))
            showToast("Indicateur '${metric.label}' mis à jour !")
        }
    }

    fun saveMediaTestimonial(item: MediaTestimonialEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.saveMediaTestimonial(item)
            showToast("Élément média / témoignage enregistré.")
            onSuccess()
        }
    }

    fun deleteMediaTestimonial(id: Long) {
        viewModelScope.launch {
            repository.deleteMediaTestimonial(id)
            showToast("Élément média supprimé.")
        }
    }

    fun updateOrgInfo(key: String, value: String) {
        viewModelScope.launch {
            repository.setOrgInfo(key, value)
            showToast("Informations institutionnelles enregistrées.")
        }
    }

    fun updateOrgInfoBatch(infoMap: Map<String, String>, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            val entities = infoMap.map { (k, v) -> OrgInfoEntity(k, v) }
            repository.setAllOrgInfo(entities)
            showToast("Toutes les informations institutionnelles ont été mises à jour avec succès !")
            onSaved()
        }
    }

    fun clearAllSampleContent() {
        viewModelScope.launch {
            repository.clearAllContent()
            showToast("Tous les éléments ont été vidés avec succès. Vous pouvez ajouter votre contenu original !")
        }
    }

    fun syncOfficialFacebookData() {
        viewModelScope.launch {
            repository.resetAndSyncOfficialFacebookData()
            showToast("Données officielles synchronisées avec succès !")
        }
    }

    fun exportRegistrations(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.exportRegistrationsCsv()
            onResult(csv)
        }
    }

    fun exportApplications(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.exportApplicationsCsv()
            onResult(csv)
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
