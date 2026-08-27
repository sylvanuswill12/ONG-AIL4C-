package com.example.ui.components

import com.example.data.model.EcoActionEntity
import com.example.data.model.ImpactMetricEntity
import com.example.data.model.NewsArticleEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.TrainingEntity

object AilWebPortalGenerator {

    fun generatePortalHtml(
        orgMap: Map<String, String>,
        newsList: List<NewsArticleEntity>,
        actionsList: List<EcoActionEntity>,
        projectsList: List<ProjectEntity>,
        trainingsList: List<TrainingEntity>,
        metricsList: List<ImpactMetricEntity>
    ): String {
        val orgName = orgMap["org_name"] ?: "Association Ivoirienne de Lutte contre le Changement Climatique et le Chômage"
        val orgAcronym = orgMap["org_acronym"] ?: "AIL4C"
        val president = orgMap["org_president"] ?: "SENIN Tchoumou Esdras Gemiel"
        val founder = orgMap["org_founder"] ?: "Aka Koffi Ezéchiel"
        val motto = orgMap["org_motto"] ?: "Agir pour le Climat, Former la Jeunesse, Bâtir l'Avenir"
        val headquarters = orgMap["org_headquarters"] ?: "Bouaké, Région du Gbêkê, Côte d'Ivoire"
        val address = orgMap["org_address"] ?: "Siège National : Bouaké - Quartier Tchelekro / Koko / Commerce"
        val phone1 = orgMap["org_phone_1"] ?: "+225 07 89 71 02 89"
        val phone2 = orgMap["org_phone_2"] ?: "+225 07 89 97 63 23"
        val email = orgMap["org_email"] ?: "ongail4c@gmail.com"
        val websiteDomain = orgMap["org_website_domain"] ?: "www.ongail4c.com"
        val facebookUrl = orgMap["org_facebook_url"] ?: "https://www.facebook.com/share/1GvChYFAMY/"
        val cleanPhone = phone1.replace(" ", "").replace("+", "")

        val newsCardsHtml = if (newsList.isEmpty()) {
            """
            <div class="empty-state">
                <div class="empty-icon">📰</div>
                <h3>Aucune actualité publiée pour le moment</h3>
                <p>Les prochains communiqués et reportages de terrain seront publiés ici et synchronisés en direct.</p>
            </div>
            """.trimIndent()
        } else {
            newsList.joinToString("\n") { news ->
                """
                <div class="card">
                    <div class="card-tag">${escapeHtml(news.category)}</div>
                    <h3 class="card-title">${escapeHtml(news.title)}</h3>
                    <div class="card-meta">📅 ${escapeHtml(news.dateText)} • ✍️ ${escapeHtml(news.author)}</div>
                    <p class="card-desc">${escapeHtml(news.summary.ifBlank { news.content.take(150) + "..." })}</p>
                </div>
                """.trimIndent()
            }
        }

        val actionsCardsHtml = if (actionsList.isEmpty()) {
            """
            <div class="empty-state">
                <div class="empty-icon">🌱</div>
                <h3>Aucun événement programmé actuellement</h3>
                <p>Ajoutez des actions de reboisement et de salubrité via l'espace Administration pour les afficher ici.</p>
            </div>
            """.trimIndent()
        } else {
            actionsList.joinToString("\n") { action ->
                """
                <div class="card">
                    <div class="card-badge ${if (action.status == "À venir") "badge-active" else "badge-info"}">${escapeHtml(action.status)}</div>
                    <h3 class="card-title">${escapeHtml(action.title)}</h3>
                    <div class="card-meta">📍 ${escapeHtml(action.location)} • 📅 ${escapeHtml(action.dateText)}</div>
                    <p class="card-desc">${escapeHtml(action.description.take(150))}</p>
                    <div class="card-footer">
                        <span class="participants">👥 <strong>${action.registeredCount}</strong> / ${action.maxSpots} Volontaires</span>
                        <a href="https://wa.me/$cleanPhone?text=Bonjour%20ONG%20AIL4C%2C%20je%20souhaite%20participer%20%C3%A0%20l'action%20%3A%20${escapeUrl(action.title)}" class="btn-sm">Participer</a>
                    </div>
                </div>
                """.trimIndent()
            }
        }

        val projectsCardsHtml = if (projectsList.isEmpty()) {
            """
            <div class="empty-state">
                <div class="empty-icon">🌳</div>
                <h3>Aucun projet actif pour le moment</h3>
                <p>Les grands programmes écologiques et partenariats apparaîtront dès leur enregistrement.</p>
            </div>
            """.trimIndent()
        } else {
            projectsList.joinToString("\n") { p ->
                val pct = if (p.targetBudget > 0) ((p.raisedBudget.toFloat() / p.targetBudget.toFloat()) * 100).toInt() else 0
                """
                <div class="card">
                    <div class="card-tag">${escapeHtml(p.status)}</div>
                    <h3 class="card-title">${escapeHtml(p.title)}</h3>
                    <div class="card-meta">🤝 Partenaire : <strong>${escapeHtml(p.partnerName)}</strong></div>
                    <p class="card-desc">${escapeHtml(p.targetObjective)}</p>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: ${pct.coerceIn(0, 100)}%;"></div>
                    </div>
                    <div class="card-meta" style="margin-top:6px; font-weight:bold; color:#0A5C36;">
                        ${p.raisedBudget} / ${p.targetBudget} FCFA financés (${pct}%)
                    </div>
                </div>
                """.trimIndent()
            }
        }

        val trainingsCardsHtml = if (trainingsList.isEmpty()) {
            """
            <div class="empty-state">
                <div class="empty-icon">🎓</div>
                <h3>Aucune session de formation ouverte pour le moment</h3>
                <p>Consultez régulièrement cette rubrique pour les futures sessions certifiantes aux métiers verts.</p>
            </div>
            """.trimIndent()
        } else {
            trainingsList.joinToString("\n") { t ->
                """
                <div class="card">
                    <div class="card-tag">Formation Métiers Verts</div>
                    <h3 class="card-title">${escapeHtml(t.title)}</h3>
                    <div class="card-meta">⏱️ Durée : ${escapeHtml(t.duration)} • 📍 ${escapeHtml(t.location)}</div>
                    <p class="card-desc">${escapeHtml(t.description.take(160))}</p>
                    <div class="card-footer">
                        <span class="badge-active">Places limitées</span>
                        <a href="https://wa.me/$cleanPhone?text=Bonjour%20ONG%20AIL4C%2C%20je%20souhaite%20postuler%20%C3%A0%20la%20formation%20%3A%20${escapeUrl(t.title)}" class="btn-sm">Postuler</a>
                    </div>
                </div>
                """.trimIndent()
            }
        }

        return """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <title>$orgAcronym - $orgName</title>
            <style>
                :root {
                    --primary: #0D7C4D;
                    --primary-dark: #07472B;
                    --primary-light: #E6F5ED;
                    --accent: #E8A838;
                    --text: #1E293B;
                    --text-muted: #64748B;
                    --bg: #F8FAF9;
                    --card-bg: #FFFFFF;
                    --border: #E2E8F0;
                }
                * {
                    box-sizing: border-box;
                    margin: 0;
                    padding: 0;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                }
                body {
                    background-color: var(--bg);
                    color: var(--text);
                    line-height: 1.5;
                    padding-bottom: 60px;
                }
                /* Top Banner */
                .top-bar {
                    background: linear-gradient(135deg, #07472B 0%, #0D7C4D 100%);
                    color: white;
                    padding: 16px 20px;
                    text-align: center;
                    position: sticky;
                    top: 0;
                    z-index: 100;
                    box-shadow: 0 2px 10px rgba(0,0,0,0.15);
                }
                .brand-container {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 12px;
                }
                .brand-logo {
                    width: 44px;
                    height: 44px;
                    background: white;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 24px;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.2);
                }
                .brand-title {
                    font-size: 20px;
                    font-weight: 800;
                    letter-spacing: 1px;
                }
                .brand-sub {
                    font-size: 11px;
                    opacity: 0.9;
                    max-width: 450px;
                    margin: 2px auto 0 auto;
                }
                .live-badge {
                    display: inline-flex;
                    align-items: center;
                    gap: 5px;
                    background: rgba(255,255,255,0.2);
                    padding: 3px 10px;
                    border-radius: 12px;
                    font-size: 11px;
                    margin-top: 6px;
                    font-weight: bold;
                }
                .pulse-dot {
                    width: 7px;
                    height: 7px;
                    background: #2ECC71;
                    border-radius: 50%;
                    animation: pulse 1.5s infinite;
                }
                @keyframes pulse {
                    0% { transform: scale(0.9); opacity: 0.7; }
                    50% { transform: scale(1.3); opacity: 1; }
                    100% { transform: scale(0.9); opacity: 0.7; }
                }

                /* Hero Section */
                .hero {
                    background: white;
                    padding: 24px 20px;
                    border-bottom: 1px solid var(--border);
                    text-align: center;
                }
                .hero-tag {
                    display: inline-block;
                    background: var(--primary-light);
                    color: var(--primary-dark);
                    font-size: 11px;
                    font-weight: 700;
                    padding: 4px 12px;
                    border-radius: 20px;
                    margin-bottom: 12px;
                    text-transform: uppercase;
                }
                .hero h1 {
                    font-size: 22px;
                    font-weight: 800;
                    color: var(--primary-dark);
                    margin-bottom: 10px;
                    line-height: 1.3;
                }
                .hero p {
                    font-size: 14px;
                    color: var(--text-muted);
                    max-width: 600px;
                    margin: 0 auto 16px auto;
                }
                .hero-actions {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 10px;
                    justify-content: center;
                    margin-top: 14px;
                }
                .btn-primary {
                    background: var(--primary);
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 10px;
                    font-size: 13px;
                    font-weight: 700;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 6px;
                    cursor: pointer;
                }
                .btn-outline {
                    background: white;
                    color: var(--primary-dark);
                    border: 1.5px solid var(--primary);
                    padding: 10px 18px;
                    border-radius: 10px;
                    font-size: 13px;
                    font-weight: 700;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 6px;
                    cursor: pointer;
                }
                .btn-whatsapp {
                    background: #25D366;
                    color: white;
                    border: none;
                    padding: 10px 18px;
                    border-radius: 10px;
                    font-size: 13px;
                    font-weight: 700;
                    text-decoration: none;
                    display: inline-flex;
                    align-items: center;
                    gap: 6px;
                }

                /* Nav Tabs */
                .nav-tabs {
                    display: flex;
                    background: white;
                    border-bottom: 1px solid var(--border);
                    overflow-x: auto;
                    padding: 0 10px;
                    scrollbar-width: none;
                }
                .nav-tabs::-webkit-scrollbar {
                    display: none;
                }
                .nav-tab {
                    padding: 12px 16px;
                    font-size: 13px;
                    font-weight: 600;
                    color: var(--text-muted);
                    cursor: pointer;
                    border-bottom: 3px solid transparent;
                    white-space: nowrap;
                    transition: all 0.2s;
                }
                .nav-tab.active {
                    color: var(--primary);
                    border-bottom-color: var(--primary);
                    font-weight: 700;
                }

                /* Container */
                .container {
                    padding: 16px 14px;
                    max-width: 800px;
                    margin: 0 auto;
                }

                /* Section Titles */
                .section-header {
                    margin-bottom: 14px;
                    margin-top: 6px;
                }
                .section-title {
                    font-size: 18px;
                    font-weight: 800;
                    color: var(--primary-dark);
                }
                .section-sub {
                    font-size: 12px;
                    color: var(--text-muted);
                }

                /* Cards Grid */
                .grid {
                    display: grid;
                    grid-template-columns: 1fr;
                    gap: 14px;
                }
                @media (min-width: 600px) {
                    .grid {
                        grid-template-columns: repeat(2, 1fr);
                    }
                }

                /* Card */
                .card {
                    background: var(--card-bg);
                    border: 1px solid var(--border);
                    border-radius: 16px;
                    padding: 16px;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.03);
                    position: relative;
                }
                .card-tag {
                    display: inline-block;
                    background: var(--primary-light);
                    color: var(--primary-dark);
                    font-size: 10px;
                    font-weight: 700;
                    padding: 3px 8px;
                    border-radius: 6px;
                    margin-bottom: 8px;
                }
                .card-badge {
                    display: inline-block;
                    font-size: 10px;
                    font-weight: 700;
                    padding: 3px 8px;
                    border-radius: 6px;
                    margin-bottom: 8px;
                }
                .badge-active {
                    background: #DEF7EC;
                    color: #03543F;
                }
                .badge-info {
                    background: #E1EFFE;
                    color: #1E429F;
                }
                .card-title {
                    font-size: 15px;
                    font-weight: 700;
                    color: var(--text);
                    margin-bottom: 6px;
                    line-height: 1.3;
                }
                .card-meta {
                    font-size: 11px;
                    color: var(--text-muted);
                    margin-bottom: 8px;
                }
                .card-desc {
                    font-size: 13px;
                    color: #475569;
                    line-height: 1.4;
                    margin-bottom: 12px;
                }
                .card-footer {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    border-top: 1px solid #F1F5F9;
                    padding-top: 10px;
                    margin-top: 8px;
                }
                .participants {
                    font-size: 11px;
                    color: var(--text-muted);
                }
                .btn-sm {
                    background: var(--primary);
                    color: white;
                    font-size: 11px;
                    font-weight: 700;
                    padding: 6px 12px;
                    border-radius: 8px;
                    text-decoration: none;
                }
                .progress-bar-bg {
                    width: 100%;
                    height: 8px;
                    background: #E2E8F0;
                    border-radius: 4px;
                    overflow: hidden;
                    margin-top: 10px;
                }
                .progress-bar-fill {
                    height: 100%;
                    background: var(--primary);
                    border-radius: 4px;
                }

                /* Empty State */
                .empty-state {
                    text-align: center;
                    padding: 36px 20px;
                    background: white;
                    border-radius: 16px;
                    border: 1px dashed var(--border);
                }
                .empty-icon {
                    font-size: 36px;
                    margin-bottom: 10px;
                }
                .empty-state h3 {
                    font-size: 15px;
                    color: var(--text);
                    margin-bottom: 6px;
                }
                .empty-state p {
                    font-size: 12px;
                    color: var(--text-muted);
                    max-width: 400px;
                    margin: 0 auto;
                }

                /* Tab Panes */
                .tab-content {
                    display: none;
                }
                .tab-content.active {
                    display: block;
                }

                /* Interactive Volunteer Form */
                .form-card {
                    background: white;
                    border-radius: 18px;
                    padding: 20px;
                    border: 1px solid var(--border);
                    margin-top: 10px;
                }
                .form-group {
                    margin-bottom: 14px;
                }
                .form-label {
                    display: block;
                    font-size: 12px;
                    font-weight: 700;
                    color: var(--text);
                    margin-bottom: 4px;
                }
                .form-input, .form-select, .form-textarea {
                    width: 100%;
                    padding: 10px 12px;
                    border: 1.5px solid var(--border);
                    border-radius: 10px;
                    font-size: 13px;
                    outline: none;
                    transition: border 0.2s;
                }
                .form-input:focus, .form-select:focus, .form-textarea:focus {
                    border-color: var(--primary);
                }
                .form-success {
                    display: none;
                    background: #DEF7EC;
                    color: #03543F;
                    padding: 16px;
                    border-radius: 12px;
                    text-align: center;
                    font-size: 13px;
                    font-weight: 600;
                    margin-bottom: 14px;
                }

                /* Footer */
                .footer {
                    background: var(--primary-dark);
                    color: white;
                    padding: 24px 20px;
                    margin-top: 30px;
                    border-radius: 20px 20px 0 0;
                }
                .footer h4 {
                    font-size: 15px;
                    font-weight: 800;
                    margin-bottom: 8px;
                }
                .footer p {
                    font-size: 12px;
                    color: rgba(255,255,255,0.8);
                    line-height: 1.6;
                    margin-bottom: 6px;
                }
                .footer-links {
                    display: flex;
                    flex-direction: column;
                    gap: 8px;
                    margin-top: 14px;
                }
                .footer-link {
                    color: #86EFAC;
                    text-decoration: none;
                    font-size: 12px;
                    font-weight: 600;
                }
            </style>
        </head>
        <body>

            <!-- Top Header -->
            <div class="top-bar">
                <div class="brand-container">
                    <div class="brand-logo">🌱</div>
                    <div>
                        <div class="brand-title">$orgAcronym CÔTE D'IVOIRE</div>
                    </div>
                </div>
                <div class="brand-sub">$orgName</div>
                <div class="live-badge">
                    <div class="pulse-dot"></div>
                    Portail Officiel $websiteDomain • En Ligne 24h/24
                </div>
            </div>

            <!-- Hero Section -->
            <div class="hero">
                <span class="hero-tag">Justice Climatique & Insertion Jeunesse</span>
                <h1>$motto</h1>
                <p>Rejoignez les initiatives citoyennes à Bouaké et dans toute la Côte d'Ivoire. Reboisement massif, salubrité, agroforesterie et formations certifiantes.</p>
                <div class="hero-actions">
                    <button class="btn-primary" onclick="switchTab('volontariat')">✍️ Devenir Bénévole</button>
                    <a href="https://wa.me/$cleanPhone?text=Bonjour%20ONG%20AIL4C%2C%20je%20souhaite%20des%20renseignements" class="btn-whatsapp">💬 WhatsApp Direct</a>
                    <a href="tel:$cleanPhone" class="btn-outline">📞 Appeler Siège</a>
                </div>
            </div>

            <!-- Navigation Tabs -->
            <div class="nav-tabs">
                <div class="nav-tab active" onclick="switchTab('accueil')">🏠 Accueil & Actualités</div>
                <div class="nav-tab" onclick="switchTab('actions')">🌱 Actions de Terrain</div>
                <div class="nav-tab" onclick="switchTab('projets')">🌳 Projets & Partenariats</div>
                <div class="nav-tab" onclick="switchTab('formations')">🎓 Formations</div>
                <div class="nav-tab" onclick="switchTab('volontariat')">✍️ Inscription Volontaire</div>
                <div class="nav-tab" onclick="switchTab('apropos')">ℹ️ À Propos</div>
            </div>

            <!-- Content Area -->
            <div class="container">

                <!-- Tab 1: Accueil & Actualités -->
                <div id="tab-accueil" class="tab-content active">
                    <div class="section-header">
                        <h2 class="section-title">Dernières Actualités & Communiqués</h2>
                        <p class="section-sub">Informations certifiées en direct de la coordination AIL4C</p>
                    </div>
                    <div class="grid">
                        $newsCardsHtml
                    </div>
                </div>

                <!-- Tab 2: Actions de Terrain -->
                <div id="tab-actions" class="tab-content">
                    <div class="section-header">
                        <h2 class="section-title">Événements & Mobilisations Écologiques</h2>
                        <p class="section-sub">Rejoignez nos opérations de reboisement et salubrité urbaine</p>
                    </div>
                    <div class="grid">
                        $actionsCardsHtml
                    </div>
                </div>

                <!-- Tab 3: Projets -->
                <div id="tab-projets" class="tab-content">
                    <div class="section-header">
                        <h2 class="section-title">Grands Projets & Financements</h2>
                        <p class="section-sub">Campagnes d'arbres, pépinières communautaires et soutien UNFPA</p>
                    </div>
                    <div class="grid">
                        $projectsCardsHtml
                    </div>
                </div>

                <!-- Tab 4: Formations -->
                <div id="tab-formations" class="tab-content">
                    <div class="section-header">
                        <h2 class="section-title">Formations Certifiantes aux Métiers Verts</h2>
                        <p class="section-sub">Insertion socio-professionnelle des jeunes par l'écologie</p>
                    </div>
                    <div class="grid">
                        $trainingsCardsHtml
                    </div>
                </div>

                <!-- Tab 5: Inscription Volontariat -->
                <div id="tab-volontariat" class="tab-content">
                    <div class="section-header">
                        <h2 class="section-title">Rejoindre l'AIL4C comme Bénévole</h2>
                        <p class="section-sub">Votre engagement citoyen pour préserver l'environnement</p>
                    </div>

                    <div id="formSuccessAlert" class="form-success">
                        ✅ Votre inscription a bien été prise en compte ! Un responsable de l'ONG vous contactera sous peu.
                    </div>

                    <div class="form-card">
                        <form id="volunteerForm" onsubmit="handleVolunteerSubmit(event)">
                            <div class="form-group">
                                <label class="form-label">Nom et Prénoms *</label>
                                <input type="text" id="vName" class="form-input" placeholder="Ex: Kouamé Jean-Marc" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Numéro de Téléphone (WhatsApp de préférence) *</label>
                                <input type="tel" id="vPhone" class="form-input" placeholder="Ex: 07 89 71 02 89" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Email (Optionnel)</label>
                                <input type="email" id="vEmail" class="form-input" placeholder="Ex: monemail@gmail.com">
                            </div>
                            <div class="form-group">
                                <label class="form-label">Ville / Commune de résidence *</label>
                                <input type="text" id="vCity" class="form-input" value="Bouaké" required>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Domaine d'intérêt principal</label>
                                <select id="vInterest" class="form-select">
                                    <option value="Reboisement & Agroforesterie">Reboisement & Agroforesterie</option>
                                    <option value="Salubrité & Gestion des déchets">Salubrité & Gestion des déchets</option>
                                    <option value="Formations aux métiers verts">Formations aux métiers verts</option>
                                    <option value="Sensibilisation & Climat">Sensibilisation & Climat</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Disponibilité</label>
                                <select id="vAvail" class="form-select">
                                    <option value="Week-ends">Les Week-ends</option>
                                    <option value="Semaine et week-ends">Semaine et Week-ends</option>
                                    <option value="Plein temps">Plein temps</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label class="form-label">Motivation (Quelques mots)</label>
                                <textarea id="vMotivation" class="form-textarea" rows="3" placeholder="Pourquoi souhaitez-vous rejoindre l'AIL4C ?"></textarea>
                            </div>
                            <button type="submit" class="btn-primary" style="width:100%; justify-content:center; padding:12px;">🌱 Valider mon Inscription</button>
                        </form>
                    </div>
                </div>

                <!-- Tab 6: À Propos -->
                <div id="tab-apropos" class="tab-content">
                    <div class="section-header">
                        <h2 class="section-title">À Propos de l'Organisation</h2>
                        <p class="section-sub">Gouvernance, coordonnées et missions officielles</p>
                    </div>
                    <div class="card" style="margin-bottom:14px;">
                        <h3 class="card-title">Gouvernance Exécutive</h3>
                        <p class="card-desc">
                            👑 <strong>Président Actuel :</strong> $president<br>
                            🌱 <strong>Président-Fondateur :</strong> $founder<br>
                            🏛️ <strong>Siège National :</strong> $headquarters<br>
                            📍 <strong>Adresse :</strong> $address
                        </p>
                    </div>
                    <div class="card">
                        <h3 class="card-title">Contacts Certifiés</h3>
                        <p class="card-desc">
                            📞 <strong>Téléphone / WhatsApp :</strong> <a href="tel:$cleanPhone" style="color:var(--primary); font-weight:bold;">$phone1</a><br>
                            📞 <strong>Ligne secondaire :</strong> $phone2<br>
                            📧 <strong>Email :</strong> <a href="mailto:$email" style="color:var(--primary); font-weight:bold;">$email</a><br>
                            🌐 <strong>Domaine :</strong> $websiteDomain<br>
                            📘 <strong>Facebook :</strong> <a href="$facebookUrl" target="_blank" style="color:#1877F2; font-weight:bold;">Page Officielle AIL4C</a>
                        </p>
                    </div>
                </div>

            </div>

            <!-- Footer -->
            <div class="footer">
                <h4>$orgAcronym - $orgName</h4>
                <p>Organisation Non Gouvernementale engagée pour la transition écologique et l'autonomisation de la jeunesse ivoirienne.</p>
                <p>📍 $headquarters • 📞 $phone1</p>
                <div class="footer-links">
                    <a href="https://wa.me/$cleanPhone" class="footer-link">💬 Assistance WhatsApp Direct</a>
                    <a href="$facebookUrl" class="footer-link">📘 Rejoindre notre Communauté Facebook</a>
                    <a href="mailto:$email" class="footer-link">📧 Envoyer un courriel officiel</a>
                </div>
            </div>

            <script>
                function switchTab(tabId) {
                    const tabs = document.querySelectorAll('.nav-tab');
                    tabs.forEach(t => t.classList.remove('active'));

                    const panes = document.querySelectorAll('.tab-content');
                    panes.forEach(p => p.classList.remove('active'));

                    const targetPane = document.getElementById('tab-' + tabId);
                    if (targetPane) {
                        targetPane.classList.add('active');
                    }

                    const tabIndex = {
                        'accueil': 0,
                        'actions': 1,
                        'projets': 2,
                        'formations': 3,
                        'volontariat': 4,
                        'apropos': 5
                    }[tabId] || 0;

                    if (tabs[tabIndex]) {
                        tabs[tabIndex].classList.add('active');
                    }

                    window.scrollTo({ top: 0, behavior: 'smooth' });
                }

                function handleVolunteerSubmit(e) {
                    e.preventDefault();
                    const name = document.getElementById('vName').value;
                    const phone = document.getElementById('vPhone').value;
                    const city = document.getElementById('vCity').value;
                    const interest = document.getElementById('vInterest').value;

                    document.getElementById('formSuccessAlert').style.display = 'block';
                    document.getElementById('volunteerForm').reset();

                    // Optional redirect to WhatsApp with confirmation
                    const waText = encodeURIComponent("Bonjour ONG AIL4C, je viens de soumettre mon inscription bénévole sur le portail Web.\nNom: " + name + "\nTéléphone: " + phone + "\nVille: " + city + "\nIntérêt: " + interest);
                    setTimeout(() => {
                        window.location.href = "https://wa.me/$cleanPhone?text=" + waText;
                    }, 1200);
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun escapeUrl(text: String): String {
        return try {
            java.net.URLEncoder.encode(text, "UTF-8")
        } catch (e: Exception) {
            text.replace(" ", "%20")
        }
    }
}
