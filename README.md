# Pix

This repository contains an Android application showcasing the use of the Flickr API (`flickr.photos.search`) within a native UI built using **Jetpack Compose**.

**Features:**

*   Fetches and displays images from Flickr in an adaptive grid layout.
*   Tap an image to view it in a dedicated screen.
*   **Pinch-to-zoom and rotation** gestures supported on the detail screen for closer inspection.

**Tech Stack Highlights:**

*   **UI:** Jetpack Compose (UI, Material 3, Navigation Compose)
*   **Networking:** Retrofit & Gson
*   **Image Loading:** Coil Compose
*   **Asynchronous Programming:** Kotlin Coroutines
*   **Dependency Injection:** Hilt
*   **Testing:** JUnit, MockK, Compose UI Tests

**CI/CD:**

*   Automated testing via **GitHub Actions** on pushes and pull requests to the `main` branch.
