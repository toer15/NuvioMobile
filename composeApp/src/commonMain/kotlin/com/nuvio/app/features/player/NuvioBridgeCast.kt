package com.nuvio.app.features.player

import com.nuvio.app.features.addons.AddonRepository
import io.ktor.http.encodeURLParameter

/** The addon id the cast bridge publishes in its manifest.json. */
internal const val NUVIO_CAST_BRIDGE_ADDON_ID = "community.nuviocast.bridge"

/**
 * The link a cast button inside the player opens.
 *
 * The bridge address is taken from the addon the user already installed, so the
 * button needs no separate setup: whatever URL the addon was added with is the
 * address the button talks to. The bridge answers /player-cast on the LAN only,
 * mints a single-stream token and redirects to its TV picker, so the phone lands
 * on the TV list with the stream that is playing already loaded.
 */
internal fun nuvioBridgeCastLink(sourceUrl: String, title: String): String? {
    val mediaUrl = sourceUrl.trim()
    if (!mediaUrl.startsWith("http://") && !mediaUrl.startsWith("https://")) return null
    val addon = AddonRepository.uiState.value.addons
        .firstOrNull { addon -> addon.enabled && addon.manifest?.id == NUVIO_CAST_BRIDGE_ADDON_ID }
        ?: return null
    val baseUrl = addon.manifestUrl
        .substringBefore("?")
        .removeSuffix("/addon/manifest.json")
        .trimEnd('/')
    if (baseUrl.isEmpty()) return null
    return "$baseUrl/player-cast?url=${mediaUrl.encodeURLParameter()}&title=${title.encodeURLParameter()}"
}
