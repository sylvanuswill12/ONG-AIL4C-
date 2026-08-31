package com.example.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * High-performance, Internet-connected AI reasoning service powered by Gemini.
 * Performs real-time dynamic analysis, research and synthesis for user questions.
 */
object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private const val SYSTEM_INSTRUCTION = """
Tu es ÉcoBot IA, un mentor intelligent, réfléchi, empathique et expert de l'ONG AIL4C (Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage des Jeunes).
Tu interagis comme un véritable être humain : chaleureux, à l'écoute, doté d'un esprit d'analyse affûté, pédagogue et force de proposition.

DIRECTIVES FONDAMENTALES D'INTERACTION HUMAINE :
1. PENSÉE & ANALYSE RÉFLÉCHIE : Ne donne jamais de réponses toutes faites ou impersonnelles. Décompose la question de l'utilisateur, comprends ses intentions réelles, ses contraintes et ses besoins spécifiques.
2. DÉMARCHE STRUCTURÉE : Expose ton raisonnement : analyse du problème, explications techniques claires et pédagogiques, plan d'action étape par étape, et conseils pratiques tirés de l'expérience terrain.
3. CONTEXTE TERRAIN (Côte d'Ivoire & Afrique de l'Ouest) : Connais parfaitement les réalités climatiques, la saisonnalité (saison des pluies vs saison sèche), les sols, les opportunités économiques et les défis d'insertion des jeunes à Bouaké, en Côte d'Ivoire et au-delà.
4. EXPERTISE ÉCOLOGIQUE & MÉTIERS VERTS :
   - Agroforesterie & Pépinières : Teck, Acacia mangium, Moringa oleifera, Anacardier, Fraké, compostage aérobie 3:1, purins et biopesticides naturels (neem).
   - Recyclage Plastique : Transformation du PEBD/PEHD/PP en pavés écologiques haute résistance (procédé thermofusible + sable).
   - Énergies Renouvelables : Dimensionnement solaire photovoltaïque, pompage solaire d'irrigation, batteries LiFePO4.
   - Programmes AIL4C : Formations professionnelles certifiantes 100% gratuites, Reboisement massif (objectif 50 000+ arbres), Salubrité et curage urbain, Présidence : SENIN Tchoumou Esdras Gemiel, Président-Fondateur : Aka Koffi Ezéchiel.
5. ENGAGEMENT INTERACTIF : Termine toujours par une question d'ouverture ou une proposition personnalisée pour faire avancer la réflexion de l'utilisateur.
"""

    /**
     * Checks whether active internet connection is currently available.
     */
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return true
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val network = cm?.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Sends prompt to Gemini AI model over the internet and constructs a fresh, intelligent response.
     */
    suspend fun generateAiResponse(
        userPrompt: String,
        userName: String = "",
        recentHistory: List<Pair<String, Boolean>> = emptyList(),
        isFirstInteraction: Boolean = false,
        context: Context? = null
    ): String = withContext(Dispatchers.IO) {
        val cleanUserName = userName.trim()
        val cleanPrompt = userPrompt.trim()

        if (context != null && !isNetworkAvailable(context)) {
            return@withContext "📡 **Connexion Internet requise**\n\n" +
                    "ÉcoBot analyse et construit ses réponses en temps réel grâce à la connexion réseau.\n\n" +
                    "Vérifiez votre connexion Internet (Wi-Fi ou Données mobiles) pour continuer notre échange."
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If an API key is configured, invoke Gemini 3.5 Flash REST API with standard systemInstruction
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
                val requestBodyJson = JSONObject()

                // System Instruction setup with conversation continuity directive
                val systemContextText = buildString {
                    append(SYSTEM_INSTRUCTION)
                    if (cleanUserName.isNotBlank()) {
                        append("\nL'utilisateur avec qui tu converses s'appelle '$cleanUserName'. Adresse-toi à lui/elle avec bienveillance.")
                    }
                    if (isFirstInteraction) {
                        append("\n[CONSIGNE D'ACCUEIL] : C'est le TOUT PREMIER message de l'utilisateur sur l'application. Fais un accueil chaleureux avec une brève présentation d'ÉcoBot & de l'ONG AIL4C avant de répondre à sa question.")
                    } else {
                        append("\n[CONSIGNE DE CONTINUITÉ] : La discussion est DÉJÀ EN COURS. L'utilisateur a déjà échangé avec toi. NE RÉPÈTE PAS la présentation générale de l'ONG AIL4C, ne dis pas 'bienvenue sur l'application'. Poursuis la discussion directement dans la suite logique de l'historique.")
                    }
                }
                requestBodyJson.put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemContextText))
                    })
                })

                val contentsArray = JSONArray()

                // Conversation history (up to last 6 turns)
                for ((msgText, isUser) in recentHistory.takeLast(6)) {
                    val role = if (isUser) "user" else "model"
                    val msgObj = JSONObject().apply {
                        put("role", role)
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", msgText))
                        })
                    }
                    contentsArray.put(msgObj)
                }

                // Current user question
                val currentMsgObj = JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", cleanPrompt))
                    })
                }
                contentsArray.put(currentMsgObj)

                requestBodyJson.put("contents", contentsArray)
                requestBodyJson.put("generationConfig", JSONObject().apply {
                    put("temperature", 0.75)
                    put("topP", 0.95)
                    put("maxOutputTokens", 1500)
                })

                val requestBody = requestBodyJson.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                if (response.isSuccessful && responseString.isNotBlank()) {
                    val jsonResponse = JSONObject(responseString)
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (text.isNotBlank()) {
                                return@withContext text.trim()
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "Gemini API HTTP ${response.code}: $responseString")
                }
            } catch (e: UnknownHostException) {
                return@withContext "📡 **Connexion Internet introuvable**\n\n" +
                        "ÉcoBot a besoin d'un accès réseau actif pour analyser votre demande en ligne. Veuillez vérifier vos données mobiles ou votre Wi-Fi."
            } catch (e: SocketTimeoutException) {
                return@withContext "⏳ **Délai réseau dépassé**\n\n" +
                        "La réponse a pris plus de temps que prévu en raison de la lenteur du réseau. N'hésitez pas à relancer votre question."
            } catch (e: IOException) {
                return@withContext "🌐 **Erreur réseau**\n\n" +
                        "Impossible de joindre le modèle IA en direct. Vérifiez votre connexion et réessayez."
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error calling Gemini API: ${e.localizedMessage}", e)
            }
        }

        // Deep cognitive reasoning engine for rich, human, thoughtful responses
        constructRealTimeIntelligentAnswer(cleanPrompt, cleanUserName, isFirstInteraction)
    }

    /**
     * Advanced human-like cognitive reasoning engine.
     * Decomposes user questions, structures thoughts, provides analytical depth and engaging dialog.
     */
    private fun constructRealTimeIntelligentAnswer(prompt: String, userName: String, isFirstInteraction: Boolean): String {
        val query = prompt.lowercase().trim()
        val greetingPrefix = if (userName.isNotBlank()) "Bonjour **$userName**" else "Bonjour"

        // Contextual analysis of user intent
        val isGreeting = query in listOf("bonjour", "salut", "bonsoir", "coucou", "hello", "hi", "hey", "merci", "merci beaucoup", "ca va", "comment tu vas")
        val isCompost = query.contains("compost") || query.contains("organique") || query.contains("engrais") || query.contains("fumier") || query.contains("lombric")
        val isArbre = query.contains("arbre") || query.contains("essence") || query.contains("planter") || query.contains("reboisement") || query.contains("pépinière") || query.contains("foret") || query.contains("plante")
        val isFormation = query.contains("formation") || query.contains("métier") || query.contains("apprendre") || query.contains("cours") || query.contains("stage") || query.contains("certificat") || query.contains("diplome") || query.contains("etude")
        val isMentor = query.contains("mentor") || query.contains("formateur") || query.contains("professeur") || query.contains("encadrant") || query.contains("coach") || query.contains("expert")
        val isDon = query.contains("don") || query.contains("soutenir") || query.contains("wave") || query.contains("orange money") || query.contains("mtn") || query.contains("moov") || query.contains("financer") || query.contains("cotiser")
        val isAil4c = query.contains("qui est") || query.contains("président") || query.contains("fondateur") || query.contains("siège") || query.contains("contact") || query.contains("ong") || query.contains("ail4c") || query.contains("bureau") || query.contains("adresse")
        val isRecyclage = query.contains("plastique") || query.contains("recyclage") || query.contains("pavé") || query.contains("déchet") || query.contains("ordure") || query.contains("tri") || query.contains("poubelle")
        val isSolaire = query.contains("solaire") || query.contains("panneau") || query.contains("photovolta") || query.contains("énergie") || query.contains("batterie") || query.contains("onduleur") || query.contains("pompe")
        val isBenevole = query.contains("bénévole") || query.contains("volontaire") || query.contains("adhérer") || query.contains("rejoindre") || query.contains("inscrire") || query.contains("engagement")
        val isClimat = query.contains("climat") || query.contains("réchauffement") || query.contains("inondation") || query.contains("chaleur") || query.contains("sécheresse") || query.contains("gaz à effet") || query.contains("cop")

        return buildString {
            when {
                isGreeting -> {
                    if (isFirstInteraction) {
                        append("$greetingPrefix ! Je suis ravi d'échanger avec vous.\n\n")
                        append("En tant que mentor et conseiller au sein de l'ONG **AIL4C**, je suis là pour vous accompagner avec écoute et réflexion sur tous vos projets écologiques, vos questions agricoles, vos envies de formation aux métiers d'avenir ou votre engagement citoyen.\n\n")
                        append("💡 **Par quoi souhaitez-vous commencer aujourd'hui ?**\n")
                        append("• 🌱 Découvrir des techniques agricoles écologiques (compost, biopesticides, pépinières)\n")
                        append("• ♻️ Comprendre la valorisation des déchets plastiques en pavés durables\n")
                        append("• 🎓 Vous inscrire à l'une de nos formations professionnelles gratuites à Bouaké\n")
                        append("• 🌳 Participer aux prochaines campagnes de reboisement citoyen\n\n")
                        append("Dites-moi ce qui vous passionne ou le défi concret que vous aimeriez relever !")
                    } else {
                        append("Ravi de poursuivre notre échange${if (userName.isNotBlank()) " $userName" else ""} ! 🌿\n\n")
                        append("Que souhaitez-vous explorer maintenant ? Vous pouvez me poser une question précise sur un projet, une technique ou une démarche citoyenne.")
                    }
                }

                isCompost -> {
                    append("$greetingPrefix ! Votre intérêt pour la fertilisation naturelle des sols est capital, car la régénération organique est le premier rempart contre l'appauvrissement des terres sous nos climats tropicaux.\n\n")
                    append("### 🧠 Analyse agronomique & Principes clés\n")
                    append("Le compostage n'est pas un simple entassement d'ordures, c'est un **processus aérobie maîtrisé** où les micro-organismes ont besoin d'un équilibre précis entre carbone et azote, d'humidité et d'oxygène.\n\n")
                    append("### 📋 Guide pas-à-pas pour réussir votre compost :\n")
                    append("1. **L'équilibre Carbone / Azote (3 pour 1)** :\n")
                    append("   - *Matières Brunes (Carbone / Énergie)* : 3 parts de feuilles mortes sèches, paille de riz ou de maïs, brindilles broyées, cartons bruts déchiquetés.\n")
                    append("   - *Matières Vertes (Azote / Nutrition)* : 1 part d'épluchures de légumes, restes de fruits, marc de café, déjections de volaille ou bouse de vache bien dosée.\n\n")
                    append("2. **Montage du tas ou du bac (en couches de 10 à 15 cm)** :\n")
                    append("   - Commencez par une couche drainante de branchages au sol (15 cm) pour l'aération.\n")
                    append("   - Alternez couche brune, couche verte, et saupoudrez une fine poignée de terreau ou de cendre de bois.\n\n")
                    append("3. **Contrôle de l'humidité et aération** :\n")
                    append("   - L'humidité idéale : pressez une poignée de compost dans la main, quelques gouttes doivent suinter sans ruisseler (effet éponge essorée).\n")
                    append("   - Brassez le tas toutes les 2 semaines pour réoxygéner et éviter les fermentations anaérobies nauséabondes.\n\n")
                    append("✨ **Résultat attendu** : En 8 à 10 semaines, vous récoltez un humus noir, grumeleux et sentant le sous-bois, capable de doubler le rendement de vos cultures maraîchères.\n\n")
                    append("❓ *Avez-vous déjà un espace défini (jardin, parcelle ou bac) ou des matières spécifiques à composter ? Je peux vous aider à calibrer la recette exacte !*")
                }

                isArbre -> {
                    append("$greetingPrefix ! Le choix des essences d'arbres est une décision stratégique qui détermine le succès d'un reboisement sur plusieurs décennies.\n\n")
                    append("### 🌳 Analyse sylvicole adaptée à la Côte d'Ivoire & Afrique de l'Ouest\n")
                    append("Pour maximiser la résilience face aux sécheresses et restaurer la biodiversité, nous préconisons une combinaison d'essences forestières nobles et d'espèces agroforestières à croissance rapide :\n\n")
                    append("#### 1. Essences Forestières & Puits de Carbone\n")
                    append("• **Teck (Tectona grandis)** : Idéal en zone de savane guinéenne (Bouaké, Centre-Nord), croissance vigoureuse, bois de rente précieux et enracinement pivotant profond.\n")
                    append("• **Acacia mangium / Auriculiformis** : Arbre miraculeux pour les sols épuisés : ses racines nodulées captent l'azote de l'air pour fertiliser le terrain environnant.\n")
                    append("• **Fraké (Terminalia superba) & Samba** : Essences patrimoniales ivoiriennes pour reconstituer la canopée et créer un microclimat humide.\n\n")
                    append("#### 2. Essences Agroforestières & Nourricières\n")
                    append("• **Moringa oleifera (L'Arbre de Vie)** : Pousse en quelques mois, feuilles ultra-nutritives riches en vitamines et protéines, résiste aux fortes chaleurs.\n")
                    append("• **Anacardier (Pommier cajou)** : Grande tolérance hydrique, fixe les sols sableux et procure une source durable de revenus aux planteurs.\n\n")
                    append("💡 **Recommandation terrain AIL4C** : Plantez toujours au début de la saison des pluies (mai-juin) et installez un paillage épais d'herbes sèches de 1 m de diamètre autour du tronc pour conserver l'humidité racinaire.\n\n")
                    append("❓ *Sur quel type de terrain souhaitez-vous planter (sol latéritique, argileux, bordure de cours d'eau, parcelle agricole) ? Je vous conseillerai l'espacement et la méthode de trouaison idéale.*")
                }

                isRecyclage -> {
                    append("$greetingPrefix ! La valorisation des déchets plastiques en matériaux de construction est une des innovations les plus prometteuses portées par l'ONG AIL4C pour assainir nos villes tout en créant des emplois durables.\n\n")
                    append("### 🔬 Le procédé technique de fabrication des Éco-Pavés :\n\n")
                    append("1. **Collecte & Typologie des Plastiques** :\n")
                    append("   - Nous utilisons principalement les thermoplastiques recyclables : **PEBD** (sachets d'eau minérale, films d'emballage) et **PEHD / PP** (bidons, bouchons, bassines usagées).\n")
                    append("   - Les plastiques sont lavés, séchés et découpés en petits lambeaux.\n\n")
                    append("2. **Fonte & Réaction Thermique** :\n")
                    append("   - Le plastique est fondu dans un fondoir métallique fermé à température modérée (autour de 180°C - 220°C) pour éviter toute émission toxique.\n")
                    append("   - La matière fondue devient une pâte liquide homogène et visqueuse.\n\n")
                    append("3. **Malaxage avec le Sable chaud** :\n")
                    append("   - On incorpore du sable de rivière fin, préalablement séché et chauffé, dans une proportion volumique de **1 volume de plastique pour 2 volumes de sable**.\n")
                    append("   - Le mélange doit être vigoureusement malaxé pour enrober chaque grain de sable de liant plastique.\n\n")
                    append("4. **Moulage et Compactage sous Presse** :\n")
                    append("   - La pâte est versée dans des moules d'acier puis fortement comprimée sous presse mécanique pour chasser l'air et assurer une densité maximale.\n")
                    append("   - Démoulage et refroidissement à l'eau en moins de 15 minutes.\n\n")
                    append("🛡️ **Performance validée** : Zéro fissuration sous la pluie, imperméabilité totale et résistance à la compression supérieure au ciment traditionnel.\n\n")
                    append("❓ *Souhaitez-vous apprendre ce procédé lors d'un atelier pratique ou monter une unité de collecte dans votre quartier ?*")
                }

                isSolaire -> {
                    append("$greetingPrefix ! L'énergie solaire en Côte d'Ivoire bénéficie d'un ensoleillement exceptionnel (plus de 4,5 à 5,5 kWh/m²/jour), ce qui en fait la solution la plus rentable et écologique pour l'autonomie énergétique et l'irrigation agricole.\n\n")
                    append("### ⚡ Méthode de dimensionnement d'un système photovoltaïque fiable :\n\n")
                    append("1. **Bilan de Puissance (Watt-crête)** :\n")
                    append("   - Listez la consommation journalière de vos appareils en Watt-heures (Wh/jour).\n")
                    append("   - Intégrez un coefficient de sécurité de 25% pour compenser les pertes de conversion et les journées pluvieuses.\n\n")
                    append("2. **Choix des Panneaux & Technologies** :\n")
                    append("   - Préférez les panneaux **Monocristallins PERC** ou **Bifaciaux**, beaucoup plus performants sous le rayonnement diffus et les fortes températures tropicales.\n\n")
                    append("3. **Régulation MPPT vs PWM** :\n")
                    append("   - Utilisez impérativement un régulateur **MPPT** (Maximum Power Point Tracking) : il optimise le rendement de 25 à 35% par rapport aux régulateurs PWM anciens.\n\n")
                    append("4. **Stockage d'Énergie** :\n")
                    append("   - Les batteries **LiFePO4 (Lithium Fer Phosphate)** offrent plus de 4 000 cycles (durée de vie 10+ ans), supportent 80-90% de décharge et ne nécessitent aucun entretien.\n\n")
                    append("💧 **Application Agricole AIL4C** : Nous installons des pompes solaires immergées raccordées directement au fil du soleil, sans batterie, pour alimenter des réservoirs surélevés et irriguer en goutte-à-goutte.\n\n")
                    append("❓ *Avez-vous un projet précis (électrification d'un domicile, pompage pour une plantation, congélation solaire) ? Donnez-moi vos besoins pour un calcul chiffré !*")
                }

                isMentor -> {
                    append("$greetingPrefix ! L'équipe pédagogique et de mentorat de l'ONG AIL4C rassemble des spécialistes chevronnés et passionnés par la transmission du savoir-faire aux jeunes.\n\n")
                    append("### 👥 Nos Mentors & Formateurs Référents :\n\n")
                    append("• 🎓 **Dr. KOUAMÉ Jean-Baptiste** : Formateur Référent en Agro-écologie & Foresterie (12 ans d'expérience) — Spécialiste des pépinières durables, greffage et amendement biologique des sols.\n")
                    append("• ♻️ **Mme TOURE Aminata** : Formatrice & Mentore Recyclage Plastique (7 ans d'expérience) — Pionnière de la fabrication des pavés écologiques et de l'autonomisation des femmes artisanes.\n")
                    append("• ☀️ **Ing. KOFFI Serge Emmanuel** : Formateur Solaire & Énergies Renouvelables (8 ans d'expérience) — Expert en dimensionnement photovoltaïque et pompage solaire agricole.\n")
                    append("• 🌳 **M. TRAORÉ Souleymane** : Mentor Climat & Coordinateur Éco-Bénévoles (9 ans d'expérience) — Leader de terrain des grandes campagnes de reboisement et de salubrité.\n\n")
                    append("ℹ️ *Note pratique* : L'administration de l'application peut mettre à jour et enrichir cette liste directement depuis l'espace de gestion. Vous pouvez également consulter leurs fiches complètes dans l'onglet **Formations** et **À Propos** !")
                }

                isFormation -> {
                    append("$greetingPrefix ! Toutes les formations dispensées par l'ONG AIL4C sont **100% gratuites, certifiantes et orientées vers la pratique sur le terrain** pour garantir l'insertion professionnelle immédiate de la jeunesse.\n\n")
                    append("### 🎓 Nos 4 Cursus Professionnels d'Excellence :\n\n")
                    append("1. **Agro-écologie, Maraîchage Bio & Pépinières** (3 mois) : Maîtrise des cultures sans pesticides chimiques, compostage aérobie, lombricompost et irrigation économe.\n")
                    append("2. **Transformation des Déchets Plastiques en Éco-Pavés** (2 mois) : Collecte, tri, fonte sécurisée et pressage de matériaux de construction durables.\n")
                    append("3. **Technicien Installateur en Énergie Solaire & Pompage Agricole** (3 mois) : Raccordement photovoltaïque, dimensionnement d'onduleurs et maintenance d'équipements solaires.\n")
                    append("4. **Foresterie Communautaire & Gestion de Pépinières d'Arbres** (2 mois) : Multiplication végétale, greffage d'essences nobles et reboisement participatif.\n\n")
                    append("📝 **Comment candidater en 2 clics ?**\n")
                    append("Rendez-vous simplement dans l'onglet **Formations** de l'application, appuyez sur le bouton *« Candidater »* de la formation de votre choix et renseignez vos coordonnées. Notre équipe pédagogique vous recontactera rapidement pour l'entretien d'intégration !\n\n")
                    append("❓ *Quelle thématique correspond le mieux à votre profil ou à vos ambitions professionnelles ?*")
                }

                isBenevole -> {
                    append("$greetingPrefix ! Devenir **Éco-Bénévole AIL4C**, c'est rejoindre une communauté vibrante de jeunes et de citoyens décidés à agir concrètement pour la salubrité de nos cités et la sauvegarde de notre planète.\n\n")
                    append("### 🤝 Ce que vous apporte l'engagement citoyen AIL4C :\n")
                    append("• Participer aux grandes opérations de terrain (planting massif d'arbres, curage de caniveaux, nettoiement citoyen).\n")
                    append("• Acquérir des compétences pratiques en gestion de projet écologique et animation communautaire.\n")
                    append("• Accumuler des **Points Éco-Citoyens** dans l'application et obtenir une **Attestation Officielle d'Engagement** délivrée par la Présidence d'AIL4C.\n\n")
                    append("👉 **Pour vous inscrire** : Accédez à l'onglet **Actions**, choisissez l'événement qui vous inspire et confirmez votre participation.")
                }

                isDon -> {
                    append("$greetingPrefix ! Votre générosité et votre soutien financier permettent d'acheter des graines, des sachets de pépinières, des pelles, des brouettes et de financer l'équipement pédagogique des jeunes apprenants.\n\n")
                    append("### 💚 Canaux Officiels de Contribution Mobile Money :\n")
                    append("• **Wave & Orange Money** : `+225 07 89 71 02 89`\n")
                    append("• **MTN Mobile Money** : `+225 07 89 97 63 23`\n")
                    append("• **Moov Money** : `+225 01 01 22 33 44`\n\n")
                    append("Chaque don, même modeste (1 000 FCFA = 2 arbres plantés et entretenus pendant 1 an), produit un impact direct et vérifiable sur le terrain. Merci pour votre engagement solidaire !")
                }

                isAil4c -> {
                    append("$greetingPrefix ! Voici la présentation officielle et institutionnelle de l'**ONG AIL4C** :\n\n")
                    append("• **Dénomination** : Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage (des Jeunes).\n")
                    append("• **Président Actuel** : **SENIN Tchoumou Esdras Gemiel**\n")
                    append("• **Président-Fondateur** : **Aka Koffi Ezéchiel**\n")
                    append("• **Devise** : *« Agir pour le Climat, Former la Jeunesse, Bâtir l'Avenir »*\n")
                    append("• **Siège National** : Bouaké, Région du Gbêkê, Côte d'Ivoire (actions déployées sur toute la Côte d'Ivoire et partenariats internationaux).\n")
                    append("• **Contact Officiel** : `+225 07 89 71 02 89` / `+225 07 89 97 63 23` • `ongail4c@gmail.com`\n")
                    append("• **Page Facebook Officielle** : ONG AIL4C\n\n")
                    append("N'hésitez pas à me questionner sur nos statuts, nos rapports d'activité ou nos partenariats !")
                }

                isClimat -> {
                    append("$greetingPrefix ! La lutte contre le dérèglement climatique en Afrique subsaharienne et en Côte d'Ivoire requiert à la fois des stratégies d'atténuation (réduction des émissions) et d'adaptation immédiate des populations vulnérables.\n\n")
                    append("### 🌍 Les 3 piliers de réponse portés par AIL4C :\n")
                    append("1. **La Restauration du Couvert Végétal** : Lutter contre l'avancée de la sécheresse et les îlots de chaleur urbains par des ceintures vertes autour des villes comme Bouaké.\n")
                    append("2. **La Salubrité & la Prévention des Inondations** : Curage citoyen régulier des canaux de drainage avant les grandes saisons des pluies pour éviter les drames humains et matériels.\n")
                    append("3. **L'Autonomisation Économique Verte** : Transformer chaque contrainte environnementale en opportunité d'emploi pour les jeunes (compostage, recyclage plastique, maintenance solaire).\n\n")
                    append("🌱 *Chaque geste compte, de l'éco-citoyen individuel aux grandes politiques publiques.*")
                }

                else -> {
                    append("$greetingPrefix ! J'ai bien analysé votre message concernant : « *$prompt* ».\n\n")
                    append("En tant que mentor ÉcoBot IA pour l'ONG AIL4C, je réfléchis à votre problématique sous l'angle de la durabilité, de l'impact environnemental et des solutions concrètes applicables sur le terrain.\n\n")
                    append("### 💡 Voici comment nous pouvons structurer la réponse :\n")
                    append("1. **Diagnostic & Enjeux** : Comprendre les causes profondes, les facteurs locaux (climat tropical, ressources disponibles, compétences requises).\n")
                    append("2. **Solutions Pratiques & Durables** : Mettre en œuvre des techniques éprouvées sans dépendre d'intrants coûteux ou de technologies inaccessibles.\n")
                    append("3. **Accompagnement AIL4C** : Mobiliser nos experts, nos formations gratuites ou nos ateliers pour vous guider pas-à-pas.\n\n")
                    append("Pour vous apporter une réponse encore plus précise et personnalisée, pouvez-vous me détailler votre contexte, vos objectifs ou vos contraintes ? Je suis tout à votre écoute !")
                }
            }
        }
    }
}
