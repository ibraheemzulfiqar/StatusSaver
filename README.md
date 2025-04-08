# Status Saver

This project is a simple yet effective implementation of a WhatsApp Status Saver app, built natively for Android using Kotlin and Jetpack Compose. It follows a modular architecture with a focus on clarity, reusability, and responsiveness.

The app allows users to view and save WhatsApp statuses directly from their device. It observes the WhatsApp status directory in real time, and notifies the user when new statuses become available.

This project demonstrates the practical application of modern Android development practices including Jetpack Compose, state management, and file system observation, while keeping performance and user experience in focus.

### Features

- Layer separation
    - UI
    - Data
    - Data Source
    - Design System
    - Common
- Navigation
- Language
- Analytics
- Permission handling
- Storage handling
- Foreground Service
- Demonstrates MVVM & Clean Architecture
- Demonstrates use of Jetpack Compose
- Demonstrates use of Coroutines including Flow
- Custom ExoPlayer view made with Compose

### Choices

- **Simplicity**

  "Where’s the domain layer?"

  You're right — it’s not here. And that’s entirely intentional.

  This project was built with a clear and limited scope: view, save, and notify about WhatsApp statuses.
  Given the simplicity of the features and the tight focus of the app, introducing a full domain layer
  with use cases and abstractions would have added unnecessary overhead without meaningful gain.

  In real-world production apps with growing complexity, a domain layer is invaluable for separation of concerns
  and long-term maintainability. But here, where business logic is minimal and tightly coupled to the UI and service
  layers, skipping that abstraction allows us to stay lean and focused.

  As for testing — there aren’t many unit tests yet. With only a few key features and most logic tied directly to
  Android components (like file system observation and services), much of the verification happens through manual testing.

  In short, this is a small, purpose-built project — so it stays simple, by design.

- **Dependency injection framework**

  A critical part of most modern apps, dependency injection (DI) helps us obtain the objects
  that build our app. It also helps manage their scope. The most popular choices in the
  Android world are [Hilt](https://dagger.dev/hilt/) (which is built on top of [Dagger](
  https://dagger.dev/)) and [Koin](https://insert-koin.io/).

  **Hilt** was chosen for two main reasons:
    1. **Compile time safety** - having the confidence that all my dependencies are provided
       before the app starts is a huge time saver and helps maintain a stable app.
    2. **Simplicity** - from experience, setting up and using Hilt (unlike the underlying Dagger)
       is considerably easier than using Koin. Hilt also introduces fewer breaking changes
       over time.

- **Glide vs Coil**
  
  I chose Glide because it handles both images and video thumbnails seamlessly with minimal setup.
  Coil, while great for Compose and lightweight, required extra handling for videos and showed some
  performance issues in testing. For this app’s media-heavy use case, Glide proved more reliable.


