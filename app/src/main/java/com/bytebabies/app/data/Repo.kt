package com.bytebabies.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.bytebabies.app.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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
                if (currentRole == Role.PARENT) currentParentId = uid
                callback(true)
            }
            .addOnFailureListener { callback(false) }
    }

    // ---------------- Firestore references ----------------
    private val parentsRef = db.collection("Parents")
    private val childrenRef = db.collection("Children")
    private val eventsRef = db.collection("Events")
    private val attendanceRef = db.collection("Attendance")
    private val mealsRef = db.collection("Meals")
    private val ordersRef = db.collection("MealOrders")
    private val mediaRef = db.collection("Media")
    private val messagesRef = db.collection("Messages")

    // ---------------- Firestore helpers ----------------
    fun fetchChildrenOfParent(parentId: String, callback: (List<Child>) -> Unit) {
        childrenRef.whereEqualTo("parentId", parentId).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.toChildList())
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    fun markAttendance(childId: String, date: LocalDate, present: Boolean) {
        val docId = "${childId}_${date.format(DateTimeFormatter.ISO_DATE)}"
        val data = mapOf(
            "childId" to childId,
            "date" to date.format(DateTimeFormatter.ISO_DATE),
            "present" to present
        )
        attendanceRef.document(docId).set(data)
    }

    fun fetchAbsentTodayForParent(parentId: String, callback: (List<Child>) -> Unit) {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        fetchChildrenOfParent(parentId) { kids ->
            attendanceRef.whereEqualTo("date", today).whereEqualTo("present", false).get()
                .addOnSuccessListener { snapshot ->
                    val absentIds = snapshot.documents.mapNotNull { it.getString("childId") }
                    callback(kids.filter { it.id in absentIds })
                }
                .addOnFailureListener { callback(emptyList()) }
        }
    }

    // ---------------- Extensions ----------------
    private fun QuerySnapshot.toChildList(): List<Child> =
        documents.map { doc ->
            Child(
                id = doc.id,
                name = doc.getString("name") ?: "",
                parentId = doc.getString("parentId") ?: "",
                teacherId = doc.getString("teacherId") ?: "",
                allergies = doc.getString("allergies") ?: "",
                medicalNotes = doc.getString("medicalNotes") ?: ""
            )
        }

    // ---------------- In-memory seed data ----------------
    val parents = mutableStateListOf<Parent>()
    val children = mutableStateListOf<Child>()
    val events = mutableStateListOf<Event>()
    val attendance = mutableStateListOf<AttendanceRecord>()
    val messages = mutableStateListOf<Message>()
    val media = mutableStateListOf<MediaItem>()
    val meals = mutableStateListOf<Meal>()
    val orders = mutableStateListOf<MealOrder>()
}
