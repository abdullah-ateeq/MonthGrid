# MonthGrid 📅

**MonthGrid** is a modern, lightweight, and feature-packed Android calendar application built using **Jetpack Compose** and **Material 3**. Designed for efficiency and clarity, MonthGrid allows you to explore any month and year with custom grid densities, week-numbering systems, detailed month/day insights, and flexible navigation options.

---

## ✨ Features

- **Any Month & Year Navigation**: Quickly jump to any date, picker, or scroll through months and years seamlessly.
- **Customizable Calendar Grid**:
  - Configurable **Week Start Day** (Monday, Sunday, Saturday).
  - Flexible **Grid Density** (Compact, Normal, Spacious).
  - Toggle **Adjacent Month Days** (show/hide days from prior/next months).
  - **Week Numbers** & **ISO 8601 Week Support**.
- **Detailed Insights**:
  - Day details, month progress indicators, day of the year, and leap-year markers.
- **History Tracking**: Automatically keep track of previously viewed months with history management.
- **Material You & Modern UI**:
  - Full **Material 3** styling with **Dynamic Color** support.
  - Smooth animations, dark mode, light mode, and haptic feedback.
- **Export & Share**: Easily copy formatted calendar grids or share month stats with others.
- **Offline & Private**: 100% offline functionality with no data collection or internet permissions required.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Asynchronous & Reactive**: Kotlin Coroutines & `StateFlow`
- **Architecture**: Clean Architecture / MVVM (ViewModel, UseCases, Repositories, Data Storage)
- **Minimum SDK**: API Level 26 (Android 8.0)
- **Target SDK**: API Level 37

---

## 📂 Project Structure

```text
com.example.monthgrid/
├── calendar/       # Core calendar and date calculations
├── data/           # Local storage and persistent state management
├── domain/         # Use cases and business logic
├── formatter/      # Calendar text and data formatters
├── generator/      # Month grid generation logic
├── history/        # History tracking manager
├── model/          # Data models (CalendarDay, MonthGridSettings, etc.)
├── navigation/     # Jetpack Compose navigation setup
├── repository/     # Data repository interfaces and implementations
├── ui/             # Composable UI screens, viewmodels, and M3 themes
│   ├── about/      # About screen
│   ├── history/    # Navigation history view
│   ├── home/       # Main grid display & picker dialogs
│   ├── settings/   # Settings & customization screen
│   └── theme/      # Color palettes, typography, and Material theme definitions
└── util/           # System helpers & utility functions
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Ladybug / 2024.2+ recommended)
- **JDK**: 11 or higher
- **Android SDK**: API 37

### Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/MonthGrid.git
   cd MonthGrid
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies: `./gradlew build`
4. Deploy to an emulator or physical Android device.

---

## 📄 License

```text
Copyright 2026 Abdullah Ateeq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, upside down or
otherwise, distributed in any language without warranty.
```
