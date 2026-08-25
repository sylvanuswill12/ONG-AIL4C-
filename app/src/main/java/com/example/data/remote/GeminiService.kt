package com.example.data.remote

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
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_INSTRUCTION = """
Tu es AWA, l'Éco-Assistante IA officielle et bienveillante de l'ONG AIL4C (Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage).
Le siège national de l'ONG est à Bouaké (Côte d'Ivoire), mais l'application et la mission d'AIL4C sont ouvertes à tout le monde sans exception, partout en Côte d'Ivoire, en Afrique et dans le monde entier.

RÈGLES CRUCIALES DE COMMUNICATION :
1. Tu dois TOUJOURS appeler et saluer la personne en fonction de son nom ou prénom fourni (ex: « Bonjour Sylvain », « Bonsoir Marc »). Si aucun nom précis n'est fourni ou qu'il s'agit d'un invité, utilise une formule chaleureuse (« Cher(e) ami(e) de la nature », « Cher éco-citoyen »).
2. Expliquer les actions concrètes de l'ONG : Reboisement communautaire, Salubrité publique, Formations gratuites aux métiers verts pour les jeunes (Agro-écologie, Recyclage & Éco-artisanat, Énergie solaire).
3. Guider les utilisateurs pour s'inscrire comme bénévoles, postuler aux formations professionnelles ou faire des dons de soutien (Orange Money, MTN MoMo, Wave).
4. Fournir des conseils pratiques sur le compostage, la gestion des déchets et les éco-gestes au quotidien.
5. Utiliser un ton encourageant, dynamique, bienveillant et ouvert à toutes les localités.
Garde tes réponses claires, concises (2 à 4 paragraphes maximum), bien structurées avec des puces et des émojis écologiques pertinents.
"""

    /**
     * Ask Gemini AI for a response given user prompt, chat context history and user's name.
     */
    suspend fun generateAiResponse(
        userPrompt: String,
        userName: String = "",
        recentHistory: List<Pair<String, Boolean>> = emptyList() // Pair<Message, isUser>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val cleanUserName = userName.trim()

        // If no API key configured or is placeholder, use intelligent contextual response
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key found, generating rich local eco-assistant response.")
            return@withContext getLocalSmartEcoResponse(userPrompt, cleanUserName)
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // Include system instruction in contents or prompt context
            val userContextInstruction = if (cleanUserName.isNotBlank()) {
                "$SYSTEM_INSTRUCTION\nNote : L'utilisateur avec qui tu échanges s'appelle '$cleanUserName'. Adresse-toi à lui/elle personnellement par son nom."
            } else {
                SYSTEM_INSTRUCTION
            }

            val systemContext = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", "System Instruction: $userContextInstruction"))
                })
            }
            contentsArray.put(systemContext)

            val systemAck = JSONObject().apply {
                put("role", "model")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", "Compris ! Je suis AWA, l'Éco-Assistante officielle d'AIL4C. Je m'adresse à ${if (cleanUserName.isNotBlank()) cleanUserName else "mon interlocuteur"} avec bienveillance."))
                })
            }
            contentsArray.put(systemAck)

            // Add recent history
            for ((text, isUser) in recentHistory.takeLast(4)) {
                val role = if (isUser) "user" else "model"
                val messageObj = JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", text))
                    })
                }
                contentsArray.put(messageObj)
            }

            // Add current user prompt
            val currentUserObj = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", userPrompt))
                })
            }
            contentsArray.put(currentUserObj)

            val requestBodyJson = JSONObject().apply {
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 800)
                })
            }

            val requestBody = requestBodyJson.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isBlank()) {
                Log.w(TAG, "Gemini API error code: ${response.code}, falling back to local engine.")
                return@withContext getLocalSmartEcoResponse(userPrompt, cleanUserName)
            }

            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) {
                        return@withContext text.trim()
                    }
                }
            }

            getLocalSmartEcoResponse(userPrompt, cleanUserName)
        } catch (e: Exception) {
            Log.e(TAG, "Error contacting Gemini API: ${e.localizedMessage}", e)
            getLocalSmartEcoResponse(userPrompt, cleanUserName)
        }
    }

    /**
     * Smart local Eco knowledge engine for AIL4C climate actions, personalized to user name.
     */
    fun getLocalSmartEcoResponse(prompt: String, userName: String = ""): String {
        val query = prompt.lowercase()
        val userGreeting = if (userName.isNotBlank()) " $userName" else ""

        return when {
            query.contains("bonjour") || query.contains("salut") || query.contains("bienvenue") || query.contains("coucou") || query.contains("aide") -> {
                """
🌿 **Bonjour$userGreeting et bienvenue sur l'application AIL4C !**

Je suis **AWA**, votre Éco-Assistante IA dédiée à la lutte contre le changement climatique et à l'insertion des jeunes. Notre siège national est à Bouaké, mais nos programmes et cette application sont ouverts à tous sans exception.

Voici ce que nous pouvons faire ensemble, $userName :
• 🌳 **Participer aux reboisements** & actions citoyennes.
• 🎓 **Découvrir les formations gratuites** (Agro-écologie, Recyclage & Métiers Verts).
• 💰 **Faire un don solidaire** via Mobile Money (Orange Money, MTN, Wave).
• 📍 **Découvrir nos projets et contacter l'ONG**.

Que souhaitez-vous explorer aujourd'hui$userGreeting ?
                """.trimIndent()
            }

            query.contains("formation") || query.contains("apprendre") || query.contains("métier") || query.contains("cours") -> {
                """
🌱 **Formations Gratuites aux Métiers Verts AIL4C**$userGreeting

L'AIL4C forme chaque année des centaines de jeunes aux opportunités durables :
1. **Agro-écologie & Maraîchage Durable** : Compostage organique, biopesticides naturels et irrigation économe (Session de 3 mois).
2. **Recyclage Plastique & Éco-Artisanat** : Transformation des déchets plastiques en pavés écologiques et objets utilitaires.
3. **Technicien en Énergie Solaire & Pompage Photovoltaïque** : Installation et maintenance de panneaux solaires.

👉 Rendez-vous dans l'onglet **Formations** pour soumettre votre candidature gratuite, $userName !
                """.trimIndent()
            }

            query.contains("reboisement") || query.contains("arbre") || query.contains("plante") || query.contains("foret") -> {
                """
🌳 **Campagne Nationale de Reboisement & Préservation**$userGreeting

Notre objectif majeur : **planter plus de 50 000 arbres** pour restaurer le couvert végétal et lutter contre les îlots de chaleur !

• **Essences plantées** : Acacia mangium, Teck, Anacardier, Moringa et arbres fruitiers.
• **Prochaine action** : Grande journée de reboisement communautaire ce samedi à 07h30.

Rejoignez-nous dans l'onglet **Actions** pour confirmer votre participation$userGreeting !
                """.trimIndent()
            }

            query.contains("don") || query.contains("financer") || query.contains("contribuer") || query.contains("argent") || query.contains("wave") || query.contains("orange") -> {
                """
💚 **Soutenez les Projets Climat d'AIL4C**$userGreeting

Chaque contribution permet d'acheter des plants d'arbres, du matériel de salubrité et de financer les kits de formation des jeunes en précarité :

• **Wave & Orange Money** : `+225 07 07 12 34 56`
• **MTN Mobile Money** : `+225 05 05 98 76 54`
• **Moov Money** : `+225 01 01 22 33 44`

Merci pour votre générosité, $userName ! Rendez-vous dans la section **Projets** pour soutenir une cause précise.
                """.trimIndent()
            }

            query.contains("contact") || query.contains("adresse") || query.contains("siege") || query.contains("localisation") || query.contains("bouake") || query.contains("où") -> {
                """
📍 **Siège National de l'ONG AIL4C**

• **Siège National** : Boulevard de la Fraternité, Quartier Commerce, Face à la Préfecture, Bouaké, Côte d'Ivoire (Ouvert à toute la communauté nationale et internationale).
• **Téléphone** : +225 27 31 63 00 00 / +225 07 07 12 34 56
• **Email** : contact@ail4c-ci.org / direction@ail4c-ci.org
• **Horaires** : Du Lundi au Vendredi de 08h00 à 17h30, Samedi de 08h30 à 13h00.
                """.trimIndent()
            }

            query.contains("benevole") || query.contains("volontaire") || query.contains("rejoindre") || query.contains("inscrire") -> {
                """
🤝 **Devenez Éco-Bénévole AIL4C$userGreeting !**

En devenant bénévole, vous gagnez des **Points Éco-Citoyens**, obtenez des attestations d'engagement et participez directement à l'assainissement et au reboisement durable !

Pour vous inscrire :
1. Créez ou connectez votre compte éco-citoyen avec votre nom et numéro.
2. Choisissez une action dans l'onglet **Actions**.
3. Recevez votre badge officiel !
                """.trimIndent()
            }

            else -> {
                """
🌿 **Réponse de l'Assistante AWA - AIL4C**

Merci pour votre message$userGreeting ! En tant qu'Éco-Assistante de l'AIL4C, je suis engagée pour promouvoir la transition écologique et l'emploi vert pour tous.

Que voulez-vous savoir en détail$userGreeting ?
• 🌳 Nos campagnes de reboisement et pépinières
• 🎓 Les candidatures aux formations agro-écologiques
• 🤝 L'inscription comme éco-volontaire
• 💚 Les dons et partenariats communautaires
                """.trimIndent()
            }
        }
    }
}
