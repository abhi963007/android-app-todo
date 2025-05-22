# Todo App

A modern Android To-Do List application built with Kotlin and Jetpack Compose.

## Features

- Create, view, edit, and delete tasks
- Task properties include:
  - Title
  - Description
  - Due date and time
  - Priority (Low, Medium, High)
  - Category (Work, Personal, Shopping, Health, Other)
  - Favorite and completion status
- Sort tasks by due date or priority
- Filter tasks by category
- Search functionality to find tasks by title or description
- Task reminders via notifications
- Light/dark theme with system theme support
- Home screen widget showing today's tasks
- Import/export tasks as JSON

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3 design
- **Architecture**: MVVM with Clean Architecture
- **Database**: Room for local storage
- **Dependency Injection**: Hilt
- **Coroutines & Flow**: For asynchronous operations and reactive streams
- **DataStore**: For preferences management
- **WorkManager**: For background task scheduling
- **Navigation**: Jetpack Navigation Compose

## Project Structure

The project follows a modular structure based on Clean Architecture principles:

- **data**: Contains the data layer (database, repositories, models)
- **domain**: Contains the business logic (use cases)
- **ui**: Contains the UI components (screens, view models)
- **di**: Contains dependency injection modules
- **util**: Contains utility classes
- **notifications**: Contains components for handling notifications
- **widget**: Contains components for the home screen widget

## Building the App

### Prerequisites

- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 17 or newer

### Steps to Build

1. Clone the repository.
2. Open the project in Android Studio.
3. Wait for Gradle sync to complete.
4. Build the project using the "Build > Make Project" menu option.

### Running the App

You can run the app in several ways:

- **On an Emulator**:
  1. Create a virtual device via the AVD Manager.
  2. Select the virtual device and click the "Run" button.

- **On a Physical Device**:
  1. Enable Developer options and USB debugging on your device.
  2. Connect your device to your computer.
  3. Select your device from the device dropdown and click the "Run" button.

### Generating a Signed APK

To generate a signed APK for distribution:

1. In Android Studio, go to "Build > Generate Signed Bundle/APK".
2. Select "APK" and click "Next".
3. Create or use an existing keystore.
4. Fill in the required fields and click "Next".
5. Select the build variant (usually "release") and click "Finish".

The signed APK will be generated in the app/release directory.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Material Design 3 for the UI design guidelines
- Jetpack Compose for modern Android UI development
- The Android community for their continuous support and resources.
