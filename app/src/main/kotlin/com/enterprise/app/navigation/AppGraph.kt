package com.enterprise.app.navigation

import com.enterprise.core.navigation.EntryBuilder
import com.enterprise.feature.detail.ui.detailEntries
import com.enterprise.feature.home.ui.homeEntries
import com.enterprise.feature.profile.ui.profileEntries
import com.enterprise.feature.search.ui.searchEntries
import com.enterprise.feature.settings.ui.settingsEntries

/**
 * AppGraph assembles all feature entry destinations.
 *
 * This is the only place in :app that knows about individual features.
 *
 * ─── Dependency direction ────────────────────────────────────────────────────
 * :app → :feature:* (allowed)
 * :feature:* ↛ :app   (NEVER — would create a circular dependency)
 * :feature:* ↛ :feature:* (NEVER — features are isolated)
 *
 * Features register their destinations as EntryBuilder extension functions.
 * :app calls them all here — features remain completely decoupled.
 */
fun EntryBuilder.appEntries() {
    homeEntries()
    detailEntries()
    searchEntries()
    profileEntries()
    settingsEntries()
}
