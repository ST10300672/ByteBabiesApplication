package com.bytebabies.app.navigation

sealed class Route(val r: String) {

    // ---------- Auth ----------
    data object Login : Route("login")
    data object ParentRegister : Route("parent_register")

    // ---------- Admin ----------
    data object AdminHome : Route("admin_home")
    data object AdminTeachers : Route("admin_teachers")

    // ---------- Parent ----------
    data object ParentHome : Route("parent_home")
}
