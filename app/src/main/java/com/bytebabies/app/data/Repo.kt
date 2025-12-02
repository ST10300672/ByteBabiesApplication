package com.bytebabies.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bytebabies.app.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Repo {

    // ---------------- Firebase instances ----------------
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ---------------- Session / Auth ----------------
    var currentRole by mutableStateOf<Role?>(null)
    var currentParentId by mutableStateOf<String?>(null)

    // ---------------- PayFast temp state ----------------
    var tempPaymentAmount: BigDecimal? = null
    var tempPaymentChildName: String? = null

    fun clearTempPayment() {
        tempPaymentAmount = null
        tempPaymentChildName = null
    }

    // ---------------- Authentication ----------------
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchUserRole(auth.currentUser?.uid ?: "") { success ->
                        onResult(success, null)
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
        currentRole = null
        currentParentId = null
    }

    private fun fetchUserRole(uid: String, callback: (Boolean) -> Unit) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { doc ->
                currentRole = when (doc.getString("role")) {
                    "admin" -> Role.ADMIN
                    "parent" -> Role.PARENT
                    else -> null
                }
                currentParentId = if (currentRole == Role.PARENT) uid else null
                callback(true)
            }
            .addOnFailureListener { callback(false) }
    }

    // ---------------- Firestore refs ----------------
    private val usersRef = db.collection("Users")
    private val teachersRef = db.collection("Teachers")
    private val childrenRef = db.collection("Children")
    private val eventsRef = db.collection("Events")
    private val attendanceRef = db.collection("Attendance")
    private val mealsRef = db.collection("Meals")
    private val ordersRef = db.collection("MealOrders")
    private val mediaRef = db.collection("Media")
    private val messagesRef = db.collection("Messages")

    // ---------------- Parent Registration ----------------
    fun registerParent(
        name: String,
        email: String,
        phone: String,
        password: String,
        consentMedia: Boolean = false,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                if (uid.isNotEmpty()) {
                    val parentData = mapOf(
                        "id" to uid,
                        "name" to name,
                        "email" to email,
                        "phone" to phone,
                        "consentMedia" to consentMedia,
                        "role" to "parent"
                    )
                    usersRef.document(uid).set(parentData)
                        .addOnSuccessListener { onComplete(true, null) }
                        .addOnFailureListener { e -> onComplete(false, e.message) }
                } else {
                    onComplete(false, "Failed to get UID from Firebase Auth")
                }
            }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // ---------------- Fetch Parents ----------------
    fun fetchParents(onResult: (List<Parent>) -> Unit) {
        usersRef.whereEqualTo("role", "parent").get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { doc ->
                    Parent(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        consentMedia = doc.getBoolean("consentMedia") ?: false
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // 🔥 NEW — Alias for AddChildScreen compatibility
    fun getAllParents(onResult: (List<Parent>) -> Unit) = fetchParents(onResult)

    // ---------------- Admin: Parent CRUD ----------------
    fun updateParent(
        id: String,
        name: String,
        email: String,
        phone: String,
        consentMedia: Boolean,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val updateData = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "consentMedia" to consentMedia
        )
        usersRef.document(id).update(updateData)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun deleteParent(
        id: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        usersRef.document(id).delete()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // Optional: validate before deleting (if needed)
    fun validateAndDeleteParent(
        id: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        // Example: Check if parent has children
        childrenRef.whereEqualTo("parentId", id).get()
            .addOnSuccessListener { snap ->
                if (snap.isEmpty) {
                    deleteParent(id, onComplete)
                } else {
                    onComplete(false, "Cannot delete parent with registered children")
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }


    // ---------------- Admin: Teachers CRUD ----------------
    fun createTeacher(
        name: String,
        email: String,
        phone: String,
        assignedClass: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val docId = teachersRef.document().id
        val teacher = Teacher(
            id = docId,
            name = name,
            email = email,
            phone = phone,
            assignedClass = assignedClass
        )
        teachersRef.document(docId).set(teacher)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun fetchTeachers(onResult: (List<Teacher>) -> Unit) {
        teachersRef.get()
            .addOnSuccessListener { snap -> onResult(snap.toTeacherList()) }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // 🔥 NEW — Alias for AddChildScreen compatibility
    fun getAllTeachers(onResult: (List<Teacher>) -> Unit) = fetchTeachers(onResult)

    fun updateTeacher(
        id: String,
        name: String,
        email: String,
        phone: String,
        assignedClass: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val updateData = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "assignedClass" to assignedClass
        )
        teachersRef.document(id).update(updateData)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun deleteTeacher(id: String, onComplete: (Boolean, String?) -> Unit) {
        teachersRef.document(id).delete()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // ---------------- Admin: Children CRUD ----------------
    fun createChild(
        name: String,
        parentId: String,
        teacherId: String,
        age: Int,
        emergencyContact: String,
        allergies: String,
        medicalNotes: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val data = mapOf(
            "name" to name,
            "parentId" to parentId,
            "teacherId" to teacherId,
            "age" to age,
            "emergencyContact" to emergencyContact,
            "allergies" to allergies,
            "medicalNotes" to medicalNotes
        )

        childrenRef.add(data)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun fetchChildren(onResult: (List<Child>) -> Unit) {
        childrenRef.get()
            .addOnSuccessListener { snap -> onResult(snap.toChildList()) }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateChild(id: String, updates: Map<String, Any>, onComplete: (Boolean, String?) -> Unit) {
        childrenRef.document(id).update(updates)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun deleteChild(id: String, onComplete: (Boolean, String?) -> Unit) {
        childrenRef.document(id).delete()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // ---------------- Attendance Tracking ----------------

    // Mark attendance for a child (Admin or Teacher)
    fun markAttendance(
        childId: String,
        date: LocalDate,
        present: Boolean,
        onComplete: ((Boolean, String?) -> Unit)? = null
    ) {
        val docId = "${childId}_${date.format(DateTimeFormatter.ISO_DATE)}"
        val data = mapOf(
            "childId" to childId,
            "date" to date.format(DateTimeFormatter.ISO_DATE),
            "present" to present
        )
        attendanceRef.document(docId).set(data)
            .addOnSuccessListener {
                onComplete?.invoke(true, null)
                if (!present) {
                    // Notify parent if absent
                    fetchChildById(childId) { child ->
                        child?.let {
                            fetchParentById(it.parentId) { parent ->
                                parent?.let { p ->
                                    sendAbsenceNotification(p.id, it.name, date)
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e -> onComplete?.invoke(false, e.message) }
    }

    // Fetch attendance for a specific child
    fun fetchAttendanceForChild(childId: String, onResult: (List<AttendanceRecord>) -> Unit) {
        attendanceRef.whereEqualTo("childId", childId).get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { d ->
                    AttendanceRecord(
                        id = d.id,
                        childId = d.getString("childId") ?: "",
                        date = LocalDate.parse(d.getString("date")),
                        present = d.getBoolean("present") ?: false
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Fetch attendance for a specific date (e.g., today)
    fun fetchAttendanceForDate(date: LocalDate, onResult: (List<AttendanceRecord>) -> Unit) {
        val today = date.format(DateTimeFormatter.ISO_DATE)
        attendanceRef.whereEqualTo("date", today).get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.map { d ->
                    AttendanceRecord(
                        id = d.id,
                        childId = d.getString("childId") ?: "",
                        date = LocalDate.parse(d.getString("date")),
                        present = d.getBoolean("present") ?: false
                    )
                }
                onResult(list)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Fetch child by ID (helper)
    fun fetchChildById(childId: String, onResult: (Child?) -> Unit) {
        childrenRef.document(childId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val child = Child(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        parentId = doc.getString("parentId") ?: "",
                        teacherId = doc.getString("teacherId") ?: "",
                        age = (doc.getLong("age") ?: 0).toInt(),
                        emergencyContact = doc.getString("emergencyContact") ?: "",
                        allergies = doc.getString("allergies") ?: "",
                        medicalNotes = doc.getString("medicalNotes") ?: ""
                    )
                    onResult(child)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    // Fetch parent by ID (helper)
    fun fetchParentById(parentId: String, onResult: (Parent?) -> Unit) {
        usersRef.document(parentId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val parent = Parent(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        consentMedia = doc.getBoolean("consentMedia") ?: false
                    )
                    onResult(parent)
                } else onResult(null)
            }
            .addOnFailureListener { onResult(null) }
    }

    // Send absence notification to parent (could be via in-app message)
    fun sendAbsenceNotification(parentId: String, childName: String, date: LocalDate) {
        val message = "Your child $childName was absent on ${date.format(DateTimeFormatter.ISO_DATE)}"
        val data = mapOf(
            "fromParentId" to null,
            "toAdmin" to false,
            "content" to message,
            "timestamp" to java.time.LocalDateTime.now().toString()
        )
        messagesRef.add(data)
    }


    // ---------------- Events ----------------
    fun createEvent(
        title: String,
        description: String,
        date: LocalDate,
        location: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val data = mapOf(
            "title" to title,
            "description" to description,
            "date" to date.format(DateTimeFormatter.ISO_DATE),
            "location" to location
        )
        eventsRef.add(data)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // Update an existing event
    fun updateEvent(
        id: String,
        title: String,
        description: String,
        date: LocalDate,
        location: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val data = mapOf(
            "title" to title,
            "description" to description,
            "date" to date.format(DateTimeFormatter.ISO_DATE),
            "location" to location
        )
        eventsRef.document(id).set(data)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // Delete an event
    fun deleteEvent(id: String, onComplete: (Boolean, String?) -> Unit) {
        eventsRef.document(id).delete()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    // Fetch all events
    fun fetchEvents(onResult: (List<Event>) -> Unit) {
        eventsRef.get()
            .addOnSuccessListener { snap -> onResult(snap.toEventList()) }
            .addOnFailureListener { onResult(emptyList()) }
    }



    // ---------------- Messaging ----------------
    fun postAnnouncement(content: String, onComplete: (Boolean, String?) -> Unit) {
        val data = mapOf(
            "fromParentId" to null,
            "toAdmin" to false,
            "content" to content,
            "timestamp" to java.time.LocalDateTime.now().toString()
        )
        messagesRef.add(data)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e.message) }
    }

    fun fetchParentMessages(onResult: (List<Message>) -> Unit) {
        messagesRef.whereEqualTo("toAdmin", true).get()
            .addOnSuccessListener { snap -> onResult(snap.toMessageList()) }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun fetchAnnouncements(onResult: (List<Message>) -> Unit) {
        messagesRef.whereEqualTo("toAdmin", false).get()
            .addOnSuccessListener { snap -> onResult(snap.toMessageList()) }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // ---------------- Helpers: snapshot -> models ----------------
    private fun com.google.firebase.firestore.QuerySnapshot.toChildList(): List<Child> =
        documents.map { doc ->
            Child(
                id = doc.id,
                name = doc.getString("name") ?: "",
                parentId = doc.getString("parentId") ?: "",
                teacherId = doc.getString("teacherId") ?: "",
                age = (doc.getLong("age") ?: 0).toInt(),
                emergencyContact = doc.getString("emergencyContact") ?: "",
                allergies = doc.getString("allergies") ?: "",
                medicalNotes = doc.getString("medicalNotes") ?: ""
            )
        }

    private fun com.google.firebase.firestore.QuerySnapshot.toTeacherList(): List<Teacher> =
        documents.map { doc ->
            Teacher(
                id = doc.id,
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: "",
                phone = doc.getString("phone") ?: "",
                assignedClass = doc.getString("assignedClass") ?: ""
            )
        }

    // ---------------- Helpers: QuerySnapshot -> Event ----------------
    private fun com.google.firebase.firestore.QuerySnapshot.toEventList(): List<Event> =
        documents.map { doc ->
            Event(
                id = doc.id,
                title = doc.getString("title") ?: "",
                description = doc.getString("description") ?: "",
                date = LocalDate.parse(doc.getString("date")),
                location = doc.getString("location") ?: ""
            )
        }


    private fun com.google.firebase.firestore.QuerySnapshot.toMessageList(): List<Message> =
        documents.map { doc ->
            Message(
                id = doc.id,
                fromParentId = doc.getString("fromParentId"),
                toAdmin = doc.getBoolean("toAdmin") ?: false,
                content = doc.getString("content") ?: "",
                timestamp = java.time.LocalDateTime.parse(doc.getString("timestamp"))
            )
        }



    // ---------------- UI lists (optional reactive caches) ----------------
    val uiTeachers = mutableStateListOf<Teacher>()
    val uiChildren = mutableStateListOf<Child>()
    val uiEvents = mutableStateListOf<Event>()
    val uiMessages = mutableStateListOf<Message>()
}
