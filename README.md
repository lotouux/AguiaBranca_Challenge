# Águia Branca – FIAP Challenge

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Retrofit](https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white)](https://square.github.io/retrofit/)
[![Gemini](https://img.shields.io/badge/Google_Gemini-8E75B2?style=for-the-badge&logo=google&logoColor=white)](https://aistudio.google.com/)
[![Status](https://img.shields.io/badge/status-Development-yellow?style=for-the-badge)](#)

---

## About the Project

This project was developed as part of the **FIAP + Águia Branca Challenge**, focusing on building a robust corporate innovation management platform.

Our goal is to digitize and streamline the flow of ideas and projects across the company — from operators submitting innovation ideas to leadership tracking financial results and ROI.

---

## Project Goal

The system aims to **connect all levels of the company** through a mobile platform that brings together:

- **Idea management** for operators to register and track innovation ideas.
- **Curation & approval workflow** for managers to evaluate and prioritize ideas.
- **Project tracking** with milestones, financial data, and progress indicators.
- **Strategic management** for leadership to define monthly focus and monitor results.
- **AI-powered assistant** (Águia IA) integrated with Google Gemini for smarter decisions.

---

## Technologies Used

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material 3
- **Networking:** Retrofit 3 + OkHttp + Gson
- **Security:** JWT via AuthInterceptor (OkHttp)
- **AI Integration:** Google Gemini API (gemini-2.0-flash)
- **Local Storage:** SharedPreferences (user session + theme)
- **Architecture:** Repository pattern with ViewModels

---

## User Profiles

### Operador
- Registers innovation ideas with title, description, area, deadline and effort level
- Tracks idea progress through all stages: Sent → Analysis → Approved → Execution → Completed
- Views the active strategic focus defined by leadership
- Earns **Innovation KM** points for ideas submitted and approved

### Gestor
- Reviews ideas submitted by operators (approve, analyze or archive)
- Prioritizes approved ideas (High, Medium, Low)
- Defines the execution plan for projects (deadline, investment, expected return)
- Adds and completes project milestones
- Uses the **Águia IA chat** to get AI assistance when evaluating ideas

### Liderança
- Full CRUD for strategic focuses (create, edit, activate, delete)
- Views the complete financial dashboard (ROI, profit, investment, return per project)
- Filters dashboard results by strategy
- Monitors all project progress
- Uses **Águia IA** to generate insights and analysis from dashboard data

---

## Features Implemented

### 1. Login & Authentication

- Profile selection screen (Operador, Gestor, Liderança)
- Credential screen with matricula and password
- **Real JWT authentication** — token is saved locally and sent on every request
- Session persistence: user stays logged in after closing the app
- Automatic logout clears token from memory and storage

---

### 2. Operador — Home Screen

- Personalized greeting with user's name
- **Águia IA card** with quick access to register a new idea
- Event calendar strip
- Performance card with Innovation KM balance and journey level
- Achievements strip with unlocked badges
- Current strategic focus card (defined by leadership)
- Recent ideas list with status indicators

---

### 3. Operador — My Ideas

- Full list of submitted ideas with expandable cards
- Each card shows: area, date, status badge and progress bar
- Expanded view shows description, KM earned, strategic bonus indicator and evolution stepper
- **New Idea dialog** with fields for title, description, area, deadline, impact and effort
- Idea is automatically linked to the active strategic focus on submission

---

### 4. Operador — Strategy Screen

- Displays the current active strategic focus in detail
- Lists upcoming focuses with area tags
- Strategic tip card encouraging aligned ideas

---

### 5. Gestor — Home Screen

- Overview cards: total ideas, approved, pending, in analysis
- Current strategic focus card with active month
- Quick access card to Inbox
- Quick access card to Projects with execution and completed counters

---

### 6. Gestor — Inbox

- **Curation tab:** card-by-card review of submitted ideas with pagination
- Shows idea details: title, description, author, date, area, impact, effort, priority
- Actions: Archive, Analyze, Approve (with optional strategic bonus checkbox)
- **"Evaluate with AI" button** opens a full chat panel powered by Google Gemini
- The AI receives the full context of the idea and assists the manager in the decision
- **Prioritization tab:** drag-style up/down reordering of approved ideas by priority level

---

### 7. Gestor — Projects & Details

- Lists all approved, in-execution and completed projects
- Filters: All, In Execution, Completed
- Project detail screen with:
  - Financial metrics: deadline, expected ROI, investment, return
  - Real progress bar calculated from milestones
  - Milestone timeline with completion status
  - Responsible person card
- Manager can define execution plan (deadline, investment, return) for approved ideas
- Manager can add new milestones and mark them as completed

---

### 8. Liderança — Home Screen (Dashboard)

- **Águia IA card** with "Análise" button that opens a full AI chat
- **Strategy filter chips** to filter all dashboard data by a specific focus
- Financial card: ROI Total, Investment, Return, Profit
- Status counters: Total, Approved, In Execution, Completed, In Analysis, Archived
- Impact by division card (ideas and projects per business area)
- Return per project list with individual ROI, investment and profit

---

### 9. Liderança — Strategic Management

- Full CRUD for strategic focuses
- Create / Edit form with title, description, month selector and immediate activation toggle
- Each focus card shows active/inactive state with switch to activate
- Activating a focus automatically deactivates all others in the backend

---

### 10. Águia IA — AI Chat (Gestor)

- Opens from the Inbox when evaluating a specific idea
- Context automatically loaded: title, description, area, impact, effort, current status
- Full conversational chat interface with message history
- Powered by **Google Gemini (gemini-2.0-flash)**
- System prompt pre-configured as a corporate innovation assistant in Portuguese

---

### 11. Águia IA — AI Chat (Liderança)

- Opens from the Home dashboard via the "Análise" button
- Context automatically loaded: ROI, investment, return, profit, project list with individual data, active strategic focus
- **Quick suggestion chips:** "Como melhorar o ROI?", "Quais projetos têm melhor retorno?", "Sugestões para a liderança", "Análise de riscos"
- Full conversational interface with the same Gemini integration

---

### 12. Profile Screen

- Displays user name and profile type
- **Operador:** shows Innovation KM total and submitted ideas count with achievement badges
- Navigation to sub-screens: Privacy, Settings, Help & Support
- **Dark/Light mode toggle** with persistent preference
- Logout clears session and JWT token

---

## Backend Integration

The app connects to a real REST API hosted at:

```
https://aguiabranca-api.onrender.com/
```

> Built with **Java Spring Boot**, **Spring Security**, **JWT** and **MongoDB**.

### Endpoints

| Method | Route | Description |
|---|---|---|
| POST | `/api/auth/login` | Authentication — returns JWT token and user profile |
| GET | `/api/ideias` | List all ideas |
| POST | `/api/ideias` | Create new idea (with strategic focus link) |
| PATCH | `/api/ideias/{id}` | Update status, priority, financial data |
| GET | `/api/estrategia/focos` | List all strategic focuses |
| POST | `/api/estrategia/focos` | Create strategic focus |
| PATCH | `/api/estrategia/focos/{id}` | Update strategic focus |
| DELETE | `/api/estrategia/focos/{id}` | Delete strategic focus |
| PATCH | `/api/estrategia/focos/{id}/ativar` | Activate a focus as current |
| POST | `/api/ideias/{id}/marcos` | Add milestone to a project |
| PATCH | `/api/ideias/{id}/marcos/{marcoId}` | Complete or update a milestone |

---

## AI Setup (Google Gemini)

To enable the AI chat features:

1. Go to [Google AI Studio](https://aistudio.google.com) and generate a free API key
2. Open `app/src/main/java/.../network/GeminiService.kt`
3. Replace the placeholder:

```kotlin
const val API_KEY = "AIzaSyDemo_substitua_pela_chave_real"
```

with your real key.

---

## How to Run

### Requirements
- Android Studio Hedgehog or newer
- JDK 11+
- Android device or emulator (API 24+, Android 7.0+)

### Steps

```bash
# Clone the repository
git clone <repository-url>

# Open the project in Android Studio
# Wait for Gradle sync to complete
# Click Run or press Shift + F10
```

### Test Credentials

Use credentials registered in the backend with one of the following profiles:
- `operador`
- `gestor`
- `lideranca`

---

## Project Structure

```
app/src/main/java/.../
├── data/
│   ├── models/           # API request/response DTOs
│   ├── preferences/      # Theme and session persistence
│   └── StrategicData.kt  # Domain models (Ideia, StrategicFocus...)
├── gestor/               # Gestor screens and ViewModel
├── lideranca/            # Liderança screens and ViewModel
├── operador/             # Operador screens
├── projetos/             # Projects screens and ViewModel
├── perfil/               # Profile screen and sub-screens
├── network/              # ApiService, RetrofitClient, GeminiService
├── repository/           # Data layer (Auth, Ideias, Estratégia)
├── navigation/           # BottomNavBar
├── components/           # Reusable components
└── MainActivity.kt       # Entry point and navigation controller
```

---

## Next Steps

- **AI Key Configuration**
  - Replace Gemini placeholder key with a real Google AI Studio key
  - Enable full AI chat for both Gestor and Liderança

- **Backend Finalization**
  - Complete all Sprint 2 backend endpoints
  - Update base URL in `RetrofitClient.kt` to the final backend address
  - Validate role-based access control responses from the API

- **Historical Strategy Registry**
  - Add date, category and campaign fields to strategic focuses
  - Build a history screen showing past strategies with timeline view

- **Notifications**
  - Push notifications when an idea status changes
  - In-app alerts for new ideas pending review (Gestor)

- **Polish & Testing**
  - End-to-end testing with real backend data
  - Accessibility review
  - Performance optimization for large idea lists

---

## Developed by

Project developed by FIAP students for the **Águia Branca Challenge — 2025**

[![FIAP](https://img.shields.io/badge/FIAP-ED1C24?style=for-the-badge&logo=academia&logoColor=white)](https://www.fiap.com.br/)
[![Águia Branca](https://img.shields.io/badge/Águia_Branca-003B71?style=for-the-badge&logoColor=white)](#)
