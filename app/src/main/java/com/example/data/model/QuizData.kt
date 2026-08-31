package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String, // "Agro-écologie", "Recyclage & Déchets", "Énergies & Climat", "ONG AIL4C & Citoyenneté", "Salubrité & Eau"
    val pointsReward: Int = 10
)

data class EcoActivityPreset(
    val key: String,
    val title: String,
    val category: String,
    val points: Int,
    val description: String,
    val iconKey: String,
    val tips: String
)

object QuizBank {

    val AVAILABLE_ACTIVITIES = listOf(
        EcoActivityPreset(
            key = "TREE_PLANTED",
            title = "Planter un arbre ou semer une graine",
            category = "Agroforesterie",
            points = 40,
            description = "Mise en terre d'un jeune plant (Teck, Moringa, Acacia) ou semis d'une essence locale pour lutter contre la déforestation.",
            iconKey = "Tree",
            tips = "Creusez un trou de 40x40 cm, ajoutez du compost mûr et arrosez généreusement le soir."
        ),
        EcoActivityPreset(
            key = "PLASTIC_RECYCLED",
            title = "Collecte et tri de plastiques pour éco-pavés",
            category = "Recyclage",
            points = 25,
            description = "Ramassage et tri de sachets ou bouteilles PEHD/PEBD destinés à la fabrication d'éco-pavés AIL4C.",
            iconKey = "Recycle",
            tips = "Rincez et compactez les bouteilles pour faciliter leur transport vers le centre de valorisation."
        ),
        EcoActivityPreset(
            key = "COMPOST_MADE",
            title = "Démarrer ou alimenter un compost ménager",
            category = "Agro-écologie",
            points = 30,
            description = "Mélange équilibré de résidus verts azotés (épluchures) et bruns carbonés (feuilles mortes) pour fertiliser les sols.",
            iconKey = "Compost",
            tips = "Respectez la règle des 3 parts de brun pour 1 part de vert et brassez une fois par semaine."
        ),
        EcoActivityPreset(
            key = "ENERGY_SAVED",
            title = "Éco-Geste Énergie : Éteindre les veilles et ampoules",
            category = "Énergie & Climat",
            points = 15,
            description = "Réduction active de la consommation électrique et adoption d'ampoules LED basse consommation.",
            iconKey = "Solar",
            tips = "Débranchez les chargeurs inutilisés : ils continuent de consommer même sans téléphone !"
        ),
        EcoActivityPreset(
            key = "CLEANUP_DONE",
            title = "Nettoyage citoyen de quartier ou caniveau",
            category = "Salubrité Urbaine",
            points = 50,
            description = "Participation active au curage de caniveaux ou désherbage pour prévenir les inondations à Bouaké.",
            iconKey = "Cleanup",
            tips = "Portez des gants de protection et évitez de brûler les déchets après le ramassage."
        ),
        EcoActivityPreset(
            key = "WATER_SAVED",
            title = "Récupération d'eau de pluie ou zéro gaspillage",
            category = "Ressources en Eau",
            points = 20,
            description = "Installation d'un récipient de collecte sous gouttière pour l'arrosage ou fermeture stricte des robinets.",
            iconKey = "Water",
            tips = "L'eau de pluie est naturellement non calcaire et idéale pour vos plantes et potagers."
        ),
        EcoActivityPreset(
            key = "MOBILITY_GREEN",
            title = "Mobilité verte : Trajet à pied ou à vélo",
            category = "Climat & Mobilité",
            points = 15,
            description = "Choix d'un mode de déplacement non polluant pour réduire l'empreinte carbone urbaine.",
            iconKey = "Walk",
            tips = "30 minutes de marche par jour améliorent votre santé tout en réduisant les émissions de CO2."
        ),
        EcoActivityPreset(
            key = "AIL4C_SHARE",
            title = "Sensibiliser un proche ou partager un éco-conseil",
            category = "Sensibilisation",
            points = 20,
            description = "Discussion éco-citoyenne avec un ami ou partage d'une astuce d'ÉcoBot pour amplifier l'impact vert.",
            iconKey = "Share",
            tips = "Le changement commence par le dialogue : chaque personne sensibilisée est une victoire !"
        )
    )

    val ALL_QUESTIONS = listOf(
        QuizQuestion(
            id = 1,
            question = "Dans le compostage écologique, quel est le ratio idéal entre matières brunes (carbone) et matières vertes (azote) ?",
            options = listOf("1 part brune pour 1 part verte", "3 parts brunes pour 1 part verte", "5 parts vertes pour 1 part brune", "Aucune matière brune nécessaire"),
            correctIndex = 1,
            explanation = "Le ratio 3:1 (3 parts de carbone pour 1 part d'azote) permet aux micro-organismes d'avoir assez d'énergie sans provoquer de mauvaises odeurs ou d'acidité.",
            category = "Agro-écologie",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 2,
            question = "Quel arbre à croissance rapide est surnommé 'arbre miracle' pour ses feuilles ultra-nutritives et ses vertus pour le sol ?",
            options = listOf("L'Eucalyptus", "Le Moringa Oleifera", "Le Teck sauvage", "Le Palmier à huile"),
            correctIndex = 1,
            explanation = "Le Moringa Oleifera pousse très vite en zone tropicale, enrichit le sol en azote et ses feuilles regorgent de vitamines, minéraux et protéines.",
            category = "Agro-écologie",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 3,
            question = "En quoi l'ONG AIL4C transforme-t-elle principalement les déchets plastiques non biodégradables à Bouaké ?",
            options = listOf("En engrais chimique", "En pavés écologiques pour voiries et cours", "En combustible fossile", "En papier journal"),
            correctIndex = 1,
            explanation = "AIL4C recycle les plastiques (PEHD, PEBD) par fusion contrôlée avec du sable pour produire des pavés écologiques ultra-résistants et durables.",
            category = "Recyclage & Déchets",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 4,
            question = "Quelle est la principale mission portée par l'ONG AIL4C en Côte d'Ivoire ?",
            options = listOf("Vente de véhicules d'occasion", "Lutte conjointe contre le changement climatique et le chômage des jeunes", "Extraction minière industrielle", "Commerce de bois exotique"),
            correctIndex = 1,
            explanation = "AIL4C allie préservation environnementale (reboisement, recyclage) et formations gratuites pour insérer les jeunes dans les métiers verts.",
            category = "ONG AIL4C & Citoyenneté",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 5,
            question = "Quel est le type de panneau solaire photovoltaïque le plus performant sous un ensoleillement direct tropical ?",
            options = listOf("Monocristallin", "Amorphe basse densité", "Panneau thermique à eau", "Polymère simple"),
            correctIndex = 0,
            explanation = "Les panneaux en silicium monocristallin offrent le meilleur rendement énergétique (18-22%) et une excellente longévité sous le soleil ivoirien.",
            category = "Énergies & Climat",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 6,
            question = "Pourquoi est-il fortement déconseillé de brûler les déchets plastiques à l'air libre dans son quartier ?",
            options = listOf("Cela attire les moustiques", "Cela dégage des dioxines et gaz toxiques cancérigènes", "Cela refroidit trop la température", "Cela fait pousser les mauvaises herbes"),
            correctIndex = 1,
            explanation = "La combustion sauvage du plastique libère des dioxines, furanes et microparticules toxiques qui polluent l'air et attaquent les poumons.",
            category = "Recyclage & Déchets",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 7,
            question = "Quel purin végétal 100% biologique est réputé en Côte d'Ivoire pour repousser efficacement les chenilles et pucerons ?",
            options = listOf("L'eau de javel diluée", "Le purin ou extrait de feuilles de Neem (Margousier)", "L'huile de moteur usagée", "Le jus de canne fermenté"),
            correctIndex = 1,
            explanation = "Le Neem (Azadirachta indica) contient de l'azadirachtine, un puissant biopesticide naturel qui repousse les ravageurs sans polluer la nappe phréatique.",
            category = "Agro-écologie",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 8,
            question = "Quel est l'objectif majeur de la campagne de reboisement massif lancée par l'ONG AIL4C ?",
            options = listOf("Planter plus de 50 000 arbres et restaurer le couvert forestier", "Créer une usine de pâte à papier", "Abattre les forêts galeries", "Interdire toute agriculture"),
            correctIndex = 0,
            explanation = "AIL4C mobilise les communautés, pépiniéristes et bénévoles pour planter plus de 50 000 arbres et restaurer les écosystèmes du Gbêkê et de Côte d'Ivoire.",
            category = "ONG AIL4C & Citoyenneté",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 9,
            question = "Pourquoi le curage régulier des caniveaux avant la saison des pluies est-il un acte éco-citoyen vital à Bouaké ?",
            options = listOf("Pour embellir les murs uniquement", "Pour prévenir les inondations destructrices et les foyers de paludisme", "Pour assécher les rivières", "Pour creuser des puits"),
            correctIndex = 1,
            explanation = "Des caniveaux bouchés par les ordures provoquent des inondations violentes en saison des pluies et favorisent la prolifération de moustiques vecteurs du paludisme.",
            category = "Salubrité & Eau",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 10,
            question = "Quelle essence d'arbre est particulièrement adaptée pour régénérer les sols dégradés grâce à sa fixation biologique de l'azote ?",
            options = listOf("L'Acacia mangium", "Le Baobab nain", "Le Cactus candélabre", "Le Sapin"),
            correctIndex = 0,
            explanation = "Les acacias sont des légumineuses arborescentes qui captent l'azote de l'air et l'injectent dans le sol, restaurant rapidement la fertilité des terres.",
            category = "Agro-écologie",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 11,
            question = "Quelle technique d'irrigation permet d'économiser jusqu'à 70% d'eau en maraîchage écologique ?",
            options = listOf("L'inondation de parcelle", "L'arrosage au goutte-à-goutte ciblé aux racines", "L'aspersion haute pression en plein midi", "L'arrosage par canalisation ouverte"),
            correctIndex = 1,
            explanation = "Le goutte-à-goutte apporte l'eau directement au pied des plants, limitant l'évaporation et préservant les nappes phréatiques précieuses.",
            category = "Salubrité & Eau",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 12,
            question = "Quel geste citoyen simple permet d'éviter la formation de dépotoirs sauvages dans les quartiers ?",
            options = listOf("Jeter ses sachets dans la rue", "Trier ses déchets à la source et participer aux pré-collectes", "Brûler tout au fond de sa cour", "Laisser les ordures dans les caniveaux"),
            correctIndex = 1,
            explanation = "Le tri sélectif à la maison et l'utilisation des bacs de collecte agréés permettent de valoriser les matières recyclables et de garder les rues saines.",
            category = "Recyclage & Déchets",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 13,
            question = "Quel est le rôle crucial du paillage (mulching) autour des plants dans un potager tropical ?",
            options = listOf("Attirer les rongeurs", "Conserver l'humidité du sol et limiter les mauvaises herbes", "Empêcher la pluie de tomber", "Refroidir la plante la nuit uniquement"),
            correctIndex = 1,
            explanation = "Le paillis de paille ou feuilles sèches protège la terre du soleil brûlant, retient l'humidité et se décompose lentement en humus fertile.",
            category = "Agro-écologie",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 14,
            question = "En combien de temps un sac plastique ordinaire jeté dans la nature se dégrade-t-il ?",
            options = listOf("2 semaines", "1 an", "Entre 100 et 400 ans", "Jamais plus de 3 mois"),
            correctIndex = 2,
            explanation = "Un sac plastique met des siècles à se fragmenter en microplastiques toxiques polluant l'eau, les poissons et la chaîne alimentaire.",
            category = "Recyclage & Déchets",
            pointsReward = 10
        ),
        QuizQuestion(
            id = 15,
            question = "Quel est le bénéfice immédiat de remplacer les ampoules à incandescence par des ampoules LED ?",
            options = listOf("Une baisse de 80% de la consommation électrique et une plus longue durée de vie", "Une facture plus élevée", "Une chaleur excessive", "Une obligation légale uniquement"),
            correctIndex = 0,
            explanation = "Les LED consomment 80% d'électricité en moins tout en produisant une lumière éclatante et durant plus de 15 000 heures.",
            category = "Énergies & Climat",
            pointsReward = 10
        )
    )

    /**
     * Returns today's specific single question based on today's calendar day.
     */
    fun getDailyQuestion(): QuizQuestion {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1).coerceAtLeast(0) % ALL_QUESTIONS.size
        return ALL_QUESTIONS[index]
    }

    /**
     * Returns today's date key string in yyyy-MM-dd format.
     */
    fun getTodayDateKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    /**
     * Returns formatted French date for today (e.g. "Vendredi 28 Août 2026").
     */
    fun getTodayDisplayDate(): String {
        return try {
            val sdf = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH)
            val dateStr = sdf.format(Date())
            dateStr.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.FRENCH) else it.toString() }
        } catch (e: Exception) {
            "Aujourd'hui"
        }
    }

    /**
     * Builds bot dynamic commentary based on answer correctness.
     */
    fun getBotCommentary(isCorrect: Boolean, question: QuizQuestion): String {
        return if (isCorrect) {
            "🌟 **Bravo, excellente réponse !**\n\n${question.explanation}\n\n💡 *Félicitations, vous remportez +${question.pointsReward} Points Éco-Citoyens aujourd'hui !*"
        } else {
            "🌱 **Pas tout à fait, mais c'est un super apprentissage !**\n\nLa bonne réponse était : **${question.options[question.correctIndex]}**.\n\n${question.explanation}\n\n💡 *Revenez demain pour une nouvelle question et remportez vos 10 points !*"
        }
    }
}

