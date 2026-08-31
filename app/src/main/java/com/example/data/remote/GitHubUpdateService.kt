package com.example.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AppUpdateInfo(
    val hasUpdate: Boolean,
    val latestVersionName: String,
    val currentVersionName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val releaseUrl: String,
    val downloadApkUrl: String? = null,
    val publishedAt: String = ""
)

object GitHubUpdateService {

    // Default target GitHub Repository for ONG-AIL4C
    private const val DEFAULT_OWNER = "sylvanuswill12"
    private const val DEFAULT_REPO = "ONG-AIL4C-"
    private const val TAG = "GitHubUpdateService"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun checkForUpdates(
        currentVersionName: String,
        currentVersionCode: Int = 1,
        owner: String = DEFAULT_OWNER,
        repo: String = DEFAULT_REPO
    ): AppUpdateInfo = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "AIL4C-Android-App")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                // If releases/latest returns 404 (e.g. no release published yet), check releases list
                val listUrl = "https://api.github.com/repos/$owner/$repo/releases"
                val listRequest = Request.Builder()
                    .url(listUrl)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "AIL4C-Android-App")
                    .build()
                val listResp = client.newCall(listRequest).execute()
                if (listResp.isSuccessful) {
                    val listBody = listResp.body?.string() ?: ""
                    val jsonArray = JSONArray(listBody)
                    if (jsonArray.length() > 0) {
                        return@withContext parseReleaseJson(jsonArray.getJSONObject(0), currentVersionName)
                    }
                }

                return@withContext AppUpdateInfo(
                    hasUpdate = false,
                    latestVersionName = currentVersionName,
                    currentVersionName = currentVersionName,
                    releaseTitle = "Version à jour ($currentVersionName)",
                    releaseNotes = "Votre application AIL4C est à jour.",
                    releaseUrl = "https://github.com/$owner/$repo/releases"
                )
            }

            val responseBody = response.body?.string() ?: ""
            val json = JSONObject(responseBody)
            return@withContext parseReleaseJson(json, currentVersionName)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking updates from GitHub", e)
            return@withContext AppUpdateInfo(
                hasUpdate = false,
                latestVersionName = currentVersionName,
                currentVersionName = currentVersionName,
                releaseTitle = "Vérification indisponible",
                releaseNotes = "Impossible de joindre GitHub. Vérifiez votre connexion Internet.",
                releaseUrl = "https://github.com/$owner/$repo/releases"
            )
        }
    }

    private fun parseReleaseJson(json: JSONObject, currentVersionName: String): AppUpdateInfo {
        val tagName = json.optString("tag_name", "v1.0").removePrefix("v").trim()
        val name = json.optString("name", "Mise à jour AIL4C")
        val body = json.optString("body", "Nouvelle version disponible avec améliorations et corrections.")
        val htmlUrl = json.optString("html_url", "https://github.com/$DEFAULT_OWNER/$DEFAULT_REPO/releases")
        val publishedAt = json.optString("published_at", "")

        // Find direct APK download asset if present
        var directApkDownloadUrl: String? = null
        val assets = json.optJSONArray("assets")
        if (assets != null) {
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val assetName = asset.optString("name", "")
                if (assetName.endsWith(".apk", ignoreCase = true)) {
                    directApkDownloadUrl = asset.optString("browser_download_url", null)
                    break
                }
            }
        }

        val hasNewerVersion = isVersionNewer(tagName, currentVersionName)

        return AppUpdateInfo(
            hasUpdate = hasNewerVersion,
            latestVersionName = tagName,
            currentVersionName = currentVersionName,
            releaseTitle = name,
            releaseNotes = body,
            releaseUrl = htmlUrl,
            downloadApkUrl = directApkDownloadUrl,
            publishedAt = publishedAt
        )
    }

    private fun isVersionNewer(remoteVer: String, localVer: String): Boolean {
        try {
            val remoteParts = remoteVer.split(".").mapNotNull { it.trim().toIntOrNull() }
            val localParts = localVer.split(".").mapNotNull { it.trim().toIntOrNull() }

            val maxLen = maxOf(remoteParts.size, localParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val l = localParts.getOrElse(i) { 0 }
                if (r > l) return true
                if (r < l) return false
            }
            return false
        } catch (e: Exception) {
            return remoteVer != localVer && remoteVer.isNotBlank()
        }
    }

    fun openUpdateInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open browser for update url", e)
        }
    }
}
