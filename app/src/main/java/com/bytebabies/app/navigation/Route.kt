package com.bytebabies.app.navigation

sealed class Route(val r: String) {

    // ---------- Authentication ----------
    data object Login : Route("login")
    data object ParentRegister : Route("parent_register")

    // ---------- Admin ----------
    data object AdminHome : Route("admin_home")
    data object AdminTeachers : Route("admin_teachers")

    // ---------- Admin: View Parents ----------
    data object AdminViewParents : Route("admin_view_parents")


    // ---------- Admin: Children ----------
    data object AdminAddChild : Route("admin_add_child")
    data object AdminViewChildren : Route("admin_view_children")   // NEW

    // ---------- Admin: Attendance ----------
    data object AdminAttendance : Route("admin_attendance")

    // ---------- Admin: Events ----------
    data object AdminEvents : Route("admin_events")


    // ---------- Admin: Messages & Announcements ----------
    data object AdminAnnouncements : Route("admin_announcements")

    // ---------- Admin Messages ----------
    data object AdminMessages : Route("admin_messages")


    // ---------- Parent ----------
    data object ParentHome : Route("parent_home")
    data object ParentChildren : Route("parent_children")
    data object ParentAttendance : Route("parent_attendance")

    data object ParentAnnouncements : Route("parent_announcements")

    data object ParentEvents : Route("parent_events")

    data object ParentMessages : Route("parent_messages")




}
