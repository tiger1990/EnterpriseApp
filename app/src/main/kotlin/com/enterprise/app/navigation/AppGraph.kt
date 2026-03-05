package com.enterprise.app.navigation

import com.enterprise.core.navigation.NavGraphBuilderScope
import com.enterprise.feature.detail.ui.detailGraph
import com.enterprise.feature.home.ui.homeGraph
import com.enterprise.feature.profile.ui.profileGraph
import com.enterprise.feature.search.ui.searchGraph
import com.enterprise.feature.settings.ui.settingsGraph

/**
 * AppGraph assembles all feature navigation graphs.
 *
 * This is the only place in :app that knows about individual features.
 *
 * ─── Dependency direction ────────────────────────────────────────────────────
 * :app → :feature:* (allowed)
 * :feature:* ↛ :app   (NEVER — would create a circular dependency)
 * :feature:* ↛ :feature:* (NEVER — features are isolated)
 *
 * Features register their destinations as NavGraphBuilderScope extension fns.
 * :app calls them all here — features remain completely decoupled.
 */
fun NavGraphBuilderScope.AppGraph() {
    homeGraph()
    detailGraph()
    searchGraph()
    profileGraph()
    settingsGraph()
}