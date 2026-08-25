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
Tu es AWA, l'Éco-Assistante IA officielle et bienveillante de l'ONG AIL4C (Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage), basée à Bouaké, Côte d'Ivoire.
Ta mission :
1. Accueillir chaleureusement et souhaiter la bienvenue aux utilisateurs sur l'application AIL4C.
2. Expliquer les actions concrètes de l'ONG : Reboisement communautaire (Objectif 50 000 arbres dans le Gbêkê), Salubrité urbaine à Bouaké, Formations gratuites aux métiers verts pour les jeunes (Agro-écologie, Recyclage & Éco-artisanat, Énergie solaire).
3. Guider les utilisateurs pour s'inscrire comme bénévoles, postuler aux formations professionnelles ou faire des dons (Orange Money, MTN MoMo, Wave).
4. Fournir des conseils pratiques sur le compostage, la gestion des déchets et les éco-gestes au quotidien.
5. Utiliser un ton encourageant, dynamique, bienveillant, avec des références ivoiriennes et locales de Bouaké (Koko, Nimbo, Dar-Es-Salam, Belle-Ville, Quartier Commerce).
Garde tes réponses claires, concises (2 à 4 paragraphes maximum), bien structurées avec des puces et des émojis écologiques pertinents.
"""

    /**
     * Ask Gemini AI for a response given user prompt and chat context history.
     */
    suspend fun generateAiResponse(
        userPrompt: String,
        recentHistory: List<Pair<String, Boolean>> = emptyList() // Pair<Message, isUser>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If no API key configured or is placeholder, use intelligent contextual response
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key found, generating rich local eco-assistant response.")
            return@withContext getLocalSmartEcoResponse(userPrompt)
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

            val contentsArray = JSONArray()

            // Include system instruction in contents or prompt context
            val systemContext = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", "System Instruction: $SYSTEM_INSTRUCTION"))
                })
            }
            contentsArray.put(systemContext)

            val systemAck = JSONObject().apply {
                put("role", "model")
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", "Compris ! Je suis AWA, l'Éco-Assistante officielle d'AIL4C à Bouaké. Je suis prête à guider nos membres et bénévoles."))
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
                return@withContext getLocalSmartEcoResponse(userPrompt)
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

            getLocalSmartEcoResponse(userPrompt)
        } catch (e: Exception) {
            Log.e(TAG, "Error contacting Gemini API: ${e.localizedMessage}", e)
            getLocalSmartEcoResponse(userPrompt)
        }
    }

    /**
     * Smart local Eco knowledge engine for AIL4C & Bouaké climate actions.
     */
    fun getLocalSmartEcoResponse(prompt: String): String {
        val query = prompt.lowercase()

        return when {
            query.contains("bonjour") || query.contains("salut") || query.contains("bienvenue") || query.contains("coucou") || query.contains("aide") -> {
                """
🌿 **Bienvenue sur l'application officielle de l'AIL4C !**

Je suis **AWA**, votre Éco-Assistante IA dédiée à la lutte contre le réchauffement climatique et à l'insertion professionnelle des jeunes à **Bouaké**.

Voici ce que je peux faire pour vous :
• 🌳 **Vous inscrire aux reboisements** & actions citoyennes dans le Gbêkê.
• 🎓 **Découvrir les formations gratuites** (Agro-écologie, Recyclage & Métiers Verts).
• 💰 **Faire un don solidaire** via Mobile Money (Orange Money, MTN, Wave).
• 📍 **Localiser le siège** au Quartier Commerce de Bouaké.

Que souhaitez-vous explorer aujourd'hui ?
                """.trimIndent()
            }

            query.contains("formation") || query.contains("apprendre") || query.contains("métier") || query.contains("cours") -> {
                """
🌱 **Formations Gratuites aux Métiers Verts AIL4C**

L'AIL4C forme chaque année des centaines de jeunes à Bouaké :
1. **Agro-écologie & Maraîchage Durable** : Compostage organique, biopesticides naturels et irrigation économe (Session de 3 mois).
2. **Recyclage Plastique & Éco-Artisanat** : Transformation des déchets plastiques en pavés écologiques et objets utilitaires.
3. **Technicien en Énergie Solaire & Pompage Photovoltaïque** : Installation et maintenance de panneaux solaires.

👉 Rendez-vous dans l'onglet **Formations** pour soumettre votre candidature gratuite !
                """.trimIndent()
            }

            query.contains("reboisement") || query.contains("arbre") || query.contains("plante") || query.contains("foret") -> {
                """
🌳 **Campagne « Bouaké Ville Verte & Durable »**

Notre objectif majeur : **planter plus de 50 000 arbres** pour restaurer le couvert végétal du Gbêkê et lutter contre les îlots de chaleur urbains !

• **Essences plantées** : Acacia mangium, Teck, Anacardier, Moringa et arbres fruitiers.
• **Prochaine action** : Grande journée de reboisement communautaire au quartier Nimbo / Belle-Ville ce samedi à 07h30.

Rejoignez-nous dans l'onglet **Actions** pour confirmer votre présence !
                """.trimIndent()
            }

            query.contains("don") || query.contains("financer") || query.contains("contribuer") || query.contains("argent") || query.contains("wave") || query.contains("orange") -> {
                """
💚 **Soutenez les Projets Climat d'AIL4C**

Chaque contribution permet d'acheter des plants d'arbres, du matériel de salubrité et de financer les kits de formation des jeunes en précarité :

• **Wave & Orange Money** : `+225 07 07 12 34 56`
• **MTN Mobile Money** : `+225 05 05 98 76 54`
• **Moov Money** : `+225 01 01 22 33 44`

Rendez-vous dans la section **Projets** pour soutenir un programme spécifique !
                """.trimIndent()
            }

            query.contains("contact") || query.contains("adresse") || query.contains("siege") || query.contains("localisation") || query.contains("bouake") || query.contains("où") -> {
                """
📍 **Siège de l'ONG AIL4C à Bouaké**

• **Adresse** : Boulevard de la Fraternité, Quartier Commerce, Face à la Préfecture, Bouaké, Côte d'Ivoire.
• **Téléphone** : +225 27 31 63 00 00 / +225 07 07 12 34 56
• **Email** : contact@ail4c-ci.org / direction@ail4c-ci.org
• **Horaires** : Du Lundi au Vendredi de 08h00 à 17h30, Samedi de 08h30 à 13h00.
                """.trimIndent()
            }

            query.contains("benevole") || query.contains("volontaire") || query.contains("rejoindre") || query.contains("inscrire") -> {
                """
🤝 **Devenez Éco-Bénévole AIL4C !**

En devenant bénévole, vous gagnez des **Points Éco-Citoyens**, obtenez des attestations d'engagement et participez directement à l'assainissement et au verdissement de Bouaké !

Pour vous inscrire :
1. Créez votre compte dans l'interface de connexion avec votre numéro ou email.
2. Choisissez une action dans l'onglet **Actions**.
3. Recevez votre badge de bénévole officiel !
                """.trimIndent()
            }

            else -> {
                """
🌿 **Réponse de l'Assistante AWA - AIL4C**

Merci pour votre message ! En tant qu'Éco-Assistante de l'AIL4C, je suis engagée pour faire de Bouaké un pôle d'excellence écologique et d'emploi vert pour la jeunesse.

Que voulez-vous savoir en détail ?
• 🌳 Nos campagnes de reboisement et pépinières
• 🎓 Les candidatures aux formations agro-écologiques
• 🤝 L'inscription comme éco-volontaire
• 💚 Les dons et partenariats communautaires
                """.trimIndent()
            }
        }
    }
}
