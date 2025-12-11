
# TuteDude E-Commerce Android App
APK is provided in this repo itself.

This project is an e-commerce Android application built as part of the **TuteDude Android Assignment**.  
The app demonstrates modern Android development including **Firebase Authentication, MVVM, Hilt, Room, Jetpack Compose and  XML UI.

---

## 🚀 Features

### 🔐 Authentication
- Register & Login using Firebase Authentication (Email/Password)
- Basic validation and error handling

### 🏠 Home Screen
- Shows a list of all uploaded products
- Each item displays: image, title, short description, price
- Tap to open full product details

### 📄 Product Details
- Full description
- Price
- Product images
- Uploader information
- Add to **Favorites** (stored locally using Room)

### ⬆️ Upload Product
Users can upload:
- Title
- Description
- Price
- Minimum 3 images  
  Images are uploaded via **Firebase Storage** and data stored in **Firestore / Realtime Database**.

### ❤️ Favorites (Room)
- Add/remove favorite products
- Stored locally using Room Database
- Accessible from a dedicated screen

### 🧩 Architecture & Tech
- **MVVM** architecture
- **Hilt** for dependency injection
- **Room** for local storage
- **Firebase** for backend services
- Jetpack Compose / XML for UI
- FakeStore API
- Fetch via **Retrofit** + Gson/Moshi

---

## 🧰 Tech Stack
- Kotlin
- Android Studio
- Firebase Authentication
- Firestore / Realtime Database
- Firebase Storage
- Hilt
- Room
- Retrofit (optional)
- Jetpack Compose / XML


---

## ▶️ How to Run
1. Clone the repository
2. Open the project in Android Studio
3. Place your **google-services.json** inside:  app/
4. Connect a device / open emulator
5. Click **Run**

---

## 👤 Author
Ishan Bhati 
GitHub: Ishan1120

---

## 📄 License
This project is for educational purposes as part of the TuteDude assignment.



