package com.bytebabies.app.navigation

sealed class Route(val r: String) {

    // ---------- Authentication ----------
    data object Login : Route("login")
    data object ParentRegister : Route("parent_register")

    // ---------- Admin ----------
    data object AdminHome : Route("admin_home")
    data object AdminTeachers : Route("admin_teachers")

    // ---------- Admin: Children ----------
    data object AdminAddChild : Route("admin_add_child")
    data object AdminViewChildren : Route("admin_view_children")   // NEW

    // ---------- Parent ----------
    data object ParentHome : Route("parent_home")
}
