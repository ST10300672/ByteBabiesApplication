#  ByteBabies – Crèche Management Mobile App

**ByteBabies** is a mobile application designed to streamline daily operations in crèches and early childhood care centers.  
It connects **Admins**, **Teachers**, and **Parents** through a unified digital platform built with **Android Jetpack Compose** and **Kotlin**.

---

##  Key Features

###  Admin Module
- **Parent & Child Management** – Add, edit, and assign parents, children, and teachers.  
- **Attendance Tracking** – Mark daily attendance for children.  
- **Event Management** – Create and announce school events with calendar integration.  
- **Meal Planning** – Define and publish menus with dietary details.  
- **Media Uploads** – Share classroom photos or videos with parental consent.  
- **Messaging System** – Send announcements and receive messages from parents.  
- **Payment Gateway (PayFast Sandbox)** – Manage and simulate tuition or meal payments securely.  

---

###  Parent Module
- **Child Profile Access** – View your child’s information, attendance, and assigned teacher.  
- **Attendance History** – Check attendance records for the month or term.  
- **Meal Menu & Pre-Orders** – Browse daily meals and pre-order for your child.  
- **Event Calendar** – See upcoming events and add them directly to your device calendar.  
- **Media Gallery** – View and download classroom media (requires consent).  
- **Messages** – Send messages to the admin and receive updates or alerts.  
- **Payment Integration** – Pay school fees or meal charges via PayFast sandbox.  

---

###  General Features
- **Secure Authentication** – Role-based login (Admin or Parent).  
- **Modern UI** – Material 3 design system with dynamic gradients and intuitive navigation.  
- **Profile Switching** – Quickly sign out and switch between Admin and Parent roles.  


---

##  Tech Stack

| Component | Technology |
|------------|-------------|
| **Frontend** | Kotlin, Jetpack Compose, Material 3 |
| **Architecture** | MVVM + Repository Pattern |
| **Navigation** | Jetpack Navigation Compose |
| **Image Loading** | Coil |
| **Payments** | PayFast Sandbox (WebView Integration) |
| **Build Tool** | Gradle 8+ |
| **Minimum SDK** | 24 (Android 7.0) |
| **Target SDK** | 35 (Android 15) |

---

##  Folder Structure
app/
├── data/
│ └── Repo.kt # In-memory data repository
├── model/ # Data models (Parent, Child, Event, etc.)
├── navigation/
│ └── Route.kt # Navigation routes
├── ui/
│ ├── components/ # Reusable UI components
│ └── screens/ # Feature screens (Admin & Parent)
└── MainActivity.kt # Entry point


---

##  Setup Instructions

1. **Clone or download** the project:
   ```bash
   git clone https://github.com/<your-username>/ByteBabies.git
Open the project in Android Studio (Giraffe or newer).

Allow Gradle sync to complete.

Run on an emulator or connected device (Android 7.0+).
