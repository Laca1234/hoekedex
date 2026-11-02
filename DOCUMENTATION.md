

# Hoekedex — Technical Documentation

## 1. Project Overview

Hoekedex is an offline Android profile-management application designed around a dark, glassmorphism-inspired interface.

The application allows users to create, organize, search, filter, and manage personal profiles directly on their Android device.

All profile information and associated images are stored locally. The application does not require an internet connection, user registration, authentication, or cloud synchronization.

The interface follows the V3 design concept, with an adaptive profile grid, floating search bar, customizable filtering, and image galleries.

## 2. Core Features

### Profile Management

Users can manage profiles through the following operations:

- Create new profiles.
- Open existing profiles.
- Edit profile information.
- Delete profiles.
- Browse profiles in a grid layout.

### Search

The application includes a floating search bar that supports name-based profile searches.

The search bar is designed to disappear while scrolling and return when appropriate, keeping the interface accessible without permanently occupying screen space.

### Filtering

Profiles can be filtered using multiple criteria:

- Age.
- Rating.
- Nationality.

The filters can be combined to narrow down the displayed profiles.

### Image Gallery

Each profile can contain multiple images.

Image functionality includes:

- Selecting and storing profile images.
- Saving images in the application's private storage.
- Using the first image as the profile thumbnail.
- Displaying the complete image gallery.
- Opening images in a full-screen viewing mode.

### Adaptive Profile Grid

The profile overview uses an adaptive grid layout supporting two or three columns depending on the available screen space.

This allows the application to make efficient use of different Android screen sizes.

## 3. Technology and Architecture

The project is an Android application built around the following technologies and architectural concepts.

| Technology / Component | Purpose |
|---|---|
| Android | Target platform |
| Kotlin | Application development language |
| Jetpack Compose | Declarative user interface |
| SQLite | Local database storage |
| Android private storage | Local image persistence |
| Gradle | Android project build system |

The application follows an offline-first approach.

Profile data is stored locally rather than relying on a remote server or external API.

## 4. User Interface and Design System

### Dark Mode

Hoekedex uses a dark-mode-only visual design.

The application follows a glassmorphism-inspired appearance, combining dark surfaces, layered elements, and contrasting accent colors.

### Color Palette

The design uses the following color palette:

| Color | Hexadecimal |
|---|---|
| Primary dark red | `#4A0006` |
| Dark burgundy | `#311414` |
| Main background | `#181314` |
| Secondary dark brown | `#4A3A3A` |
| Muted rose | `#8C5A5E` |
| Warm accent | `#A86A45` |

These colors establish the application's visual identity and are used throughout its interface.

### Interaction Design

The interface focuses on straightforward profile management.

Important interaction elements include:

- Floating search functionality.
- Scroll-responsive search bar behavior.
- Adaptive profile cards.
- Combined filtering controls.
- Full-screen image viewing.
- Dedicated profile creation and editing workflows.

## 5. Data Management

### Local Database

Profile information is stored in a local SQLite database.

This allows the application to maintain profile data without an internet connection.

### Image Storage

Profile images are stored inside the application's private storage directory.

This keeps image files separate from the database itself while allowing the application to associate images with individual profiles.

The first image in a profile's gallery is automatically used as its thumbnail.

### Data Privacy

The application is designed to operate entirely offline.

Its current project description specifies:

- No internet dependency.
- No registration.
- No login system.
- No cloud synchronization.
- Local storage of profile information and images.

The Android manifest does not declare an internet permission.

## 6. Application Configuration

The Android manifest defines the main application activity:

```text
.MainActivity
```

The activity is configured with:

- An exported launcher activity.
- Portrait screen orientation.
- Application label and launcher icons.
- The `Theme.Hoekedex` application theme.
- Android backup disabled.

The application also declares Android data-extraction and backup-rule resources.

## 7. Installation and Development

### Requirements

- Android Studio.
- Android SDK.
- Gradle build environment.
- A compatible Android device or emulator.

### Opening the Project

1. Clone or download the repository.
2. Open Android Studio.
3. Select the `HoekedexAndroid` project directory.
4. Allow Gradle synchronization to complete.
5. Run the application on an emulator or connected Android device.

### Building a Debug APK

In Android Studio, select:

```text
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

Alternatively, if Gradle is installed and configured:

```powershell
gradle assembleDebug
```

The generated debug APK is expected at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The APK can then be transferred to a compatible Android device and installed.

## 8. Repository Structure

The documented project entry point is the `HoekedexAndroid` Android Studio project.

The application includes an Android manifest and the resources required for its application identity and theme.

The exact source package structure and individual Kotlin class names are intentionally not specified here.

## 9. Current Scope and Limitations

The project is designed as a local profile-management application rather than a cloud-based service.

Its documented scope includes:

- Local profile management.
- SQLite-based persistence.
- Local image storage.
- Search and filtering.
- Adaptive profile browsing.
- Dark-mode interface.

The application does not currently describe any cloud synchronization, account system, or remote backend functionality.

## 10. Potential Future Improvements

Possible future development directions include:

- Database backup and restore.
- Profile export and import.
- Additional profile filtering options.
- Improved gallery management.
- Configurable profile sorting.
- More detailed profile statistics.
- Optional encrypted local data storage.

These are potential extensions and are not presented as existing features.

## 11. Project Goal

Hoekedex aims to provide a simple, visually distinctive, and completely offline Android environment for organizing and managing profiles.

Its main focus is combining convenient profile management, local data persistence, image galleries, and a consistent dark-mode user experience.

