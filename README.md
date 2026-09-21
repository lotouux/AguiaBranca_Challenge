# Águia Branca – FIAP Challenge

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Retrofit](https://img.shields.io/badge/Retrofit-48B983?style=for-the-badge&logo=square&logoColor=white)](https://square.github.io/retrofit/)
[![Groq](https://img.shields.io/badge/Groq-F55036?style=for-the-badge&logo=groq&logoColor=white)](https://console.groq.com/)
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
- **AI-powered assistant** (Águia IA) integrated with Groq for smarter decisions.
- 
---

## Technologies Used

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material 3
- **Networking:** Retrofit 3 + OkHttp + Gson
- **Security:** JWT via AuthInterceptor (OkHttp)
- **AI Integration:** Groq 
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







