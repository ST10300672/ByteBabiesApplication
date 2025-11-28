package com.bytebabies.app.navigation

sealed class Route(val r: String) {

    // ---------- Auth ----------
    data object Login : Route("login")

    // ---------- Admin ----------
    data object AdminHome : Route("admin_home")

    // ---------- Parent ----------
    data object ParentHome : Route("parent_home")
}
