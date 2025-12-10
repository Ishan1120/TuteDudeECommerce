# TuteDude Assignment: E-Commerce Platform (Android)

Basic e-commerce Android app using modern practices (MVVM, Hilt, Room, Firebase). Users can authenticate, browse/upload products, view details, and manage favorites. Optional: Recommended products via FakeStore API.

## Features
- Authentication: Email/password with Firebase Auth
- Home: Product list (thumbnail, title, short description, price); navigate to details; favorites from list
- Product Details: Full description, images carousel, price, uploader email, add/remove favorites
- Upload Product: Title, description, price, 3+ images to Firebase (Firestore + Storage)
- Favorites: Local persistence with Room; separate screen
- Architecture: MVVM + Repository, DI with Hilt, Material Design theme
- Optional: Retrofit + Gson/Moshi for FakeStore recommended products (Implemented as a "Recommended" section via FakeStore API)

## Tech Stack
- Kotlin, Jetpack Compose, Navigation, Hilt, Room
- Firebase Auth, Firestore, Firebase Storage
- Retrofit (scaffolded for optional FakeStore)

## Project Structure
- app: UI (Compose), ViewModels, Navigation
- data: Repositories, DAOs, Firebase/Retrofit data sources, Hilt modules
- domain: Models and repository interfaces

## Setup
1. Create a Firebase project; enable Email/Password Auth, Firestore, and Storage.
2. Download `google-services.json` and place it at `app/google-services.json`.
3. Open in Android Studio (JDK 21) and Sync (or run `gradle wrapper` then `./gradlew :app:assembleDebug` on macOS/Linux or `gradlew.bat :app:assembleDebug` on Windows).

## Notes
- On launch, the app listens to Firestore changes and mirrors to local Room; if nothing remote, it seeds example products locally; uploads immediately sync both remote and local.
- User profiles are stored in Firestore (`users/{uid}`) with email and display name on sign-up; details screen shows uploader name and email.
- To test uploads, sign in, go to Home → Upload, provide title/desc/price/category, pick at least 3 images, and submit.

## Screenshots
- Add screenshots to `docs/` and link here (e.g., Home, Details, Upload, Favorites).
