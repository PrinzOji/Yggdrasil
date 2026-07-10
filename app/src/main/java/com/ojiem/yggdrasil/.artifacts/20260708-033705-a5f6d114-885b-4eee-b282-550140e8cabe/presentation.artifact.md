# 🌿 Yggdrasil: The World Tree Ecosystem
### *A Next-Generation Agricultural Hub Prototype*

---

## 🏗️ Project Overview
Yggdrasil is a digital ecosystem designed to synchronize "Eco Nodes" (farmers, markets, and price reports) into a unified agricultural intelligence network. Built with **Jetpack Compose**, **Firebase**, and modern Android architectural patterns.

---

## 🌟 Key Features & Improvements

### 🔐 1. Enhanced Authentication Flow
We moved beyond basic login to a professional authentication suite:
- **Forgot Password**: Integrated Firebase password reset emails with a custom glassmorphic UI dialog.
- **Dynamic Profile Sync**: On login, the app automatically syncs the user's Google/Firebase profile picture and display name to the Realtime Database.
- **Robust State Management**: Handled "Internal Errors" by decoupling navigation from the ViewModel and using `SharedFlow` for one-time authentication events.

### 🏠 2. Dynamic Home Feed (Eco Nodes)
The heart of the app where agricultural data lives:
- **Manual Synchronization**: Implemented a "Sync" (Refresh) button with visual feedback to pull the latest price reports.
- **Category Filtering**: Real-time filtering of "Realms" (Produce, Staples, etc.) using Jetpack Compose state.
- **Bloom System**: A community-driven vouching system where reports "Bloom" 🌿 once they reach a threshold of verified vouches.

### 👤 3. Immersive Profile & Social Experience
A social-first approach to agricultural networking:
- **Interactive Statuses**:
    - Auto-advancing stories with progress bars.
    - Gesture support: Tap to skip, Long-press to pause.
    - Support for Text, Image, and Video (ExoPlayer) statuses.
- **Professional Details**: Dedicated fields for Talents, Portfolio, and Skills, styled with a glassmorphic aesthetic.
- **Notes System**: A "What's on your mind" bubble that sits right on the profile picture.

### 🛠️ 4. Core Infrastructure Additions
To prepare for the next phase of growth, I've integrated:
- **CameraX**: Full setup for high-performance in-app photography.
- **Glide & Coil**: Dual-power image loading with KSP support for optimized performance.
- **Networking**: OkHttp logging interceptors for debugging "Eco Node" data transmissions.

---

## 📐 Architecture & Tech Stack
- **UI**: Jetpack Compose (100% Declarative)
- **Backend**: Firebase Auth, Realtime Database, Cloudinary (Media hosting)
- **Media**: Media3 ExoPlayer for video statuses.
- **Tools**: Version Catalogs (libs.versions.toml) for clean dependency management.

---

## 🚀 The Build Process: How I built it
1.  **Foundation**: Set up the core glassmorphic theme to give the app a modern, "organic" feel.
2.  **Logic**: Built the `FirebaseRepository` as a single source of truth for all data operations.
3.  **UI Iteration**: Refined complex components like the `StatusViewer` and `ProfileScreen` based on UX best practices (gestures, auto-advance).
4.  **Optimization**: Moved dependencies to Version Catalogs and added KSP for faster builds.

---

> [!TIP]
> **What's Next?**
> The project is now ready for full Camera integration and a "Real-time Marketplace" feature using the newly added permissions and networking tools.
