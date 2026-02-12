# CineBase

Cinebase is a full-stack web application for movie and TV show discovery where users can browse content, write reviews, create a watchlist, add to favorites, play a simple game, and get personalized recommendations based on their taste. Built with Spring Boot and React.

All movie and TV data comes from [TMDB (The Movie Database)](https://www.themoviedb.org/) API.

This project is intended as a senior design project demonstrating
full-stack development, REST API design, authentication, and deployment.

## What it does

**Content discovery** — Browse popular, top-rated, and latest movies and TV shows. Search across both categories at once. View detailed info including cast, trailers, seasons, and similar content.

**Reviews and ratings** — Rate movies/shows on a 1-10 scale, write text reviews, and see what other users think. Users get an email confirmation when they post a review.

**Favorites and watchlist** — Save content to a favorites list or a "watch later" list. Both persist across sessions and are accessible from any page.

**Personalized recommendations** — During onboarding, new users pick 4 titles they like. The app uses these picks to generate a personalized feed by pulling similar and recommended content from TMDB, then scoring each result based on how often it appears across recommendations, genre overlap with the user's picks, rating quality, and popularity.

**Higher/Lower game** — A trivia game where users compare two movies or TV shows and guess which one has the higher box office revenue, IMDB rating, or episode count. 10 questions per round, 60-second time limit, with a global leaderboard tracking top scores.

**User accounts** — Registration, login, JWT-based authentication, and password change.

## Tech stack

### Backend
- Java Spring Boot, Maven
- Spring Security + JWT for authentication
- Spring Data JPA + Hibernate (PostgreSQL)
- Spring WebFlux for reactive TMDB API calls
- Spring Mail (Gmail SMTP) for email notifications
- Springdoc OpenAPI / Swagger UI for API documentation
- Spring Actuator for health checks

### Frontend
- React, React Router
- React Hook Form + Zod for form validation
- Axios for HTTP requests
- CSS Modules for styling

### Infrastructure
- PostgreSQL (Docker for local dev, Neon for production)
- Docker + Docker Compose for local development
- Render.com for production deployment
- Nginx as frontend server in production

## Project structure

```
cinebase/
├── backend/
│   └── src/main/java/com/sdp/cinebase/
│       ├── auth/           # Login, register, JWT token handling
│       ├── security/       # Spring Security config, JWT filter
│       ├── user/           # User model, onboarding, recommendation engine
│       ├── tmdb/           # TMDB API client and proxy controllers
│       ├── review/         # Review CRUD
│       ├── favorite/       # Favorites CRUD
│       ├── watchlist/      # Watchlist CRUD
│       ├── game/           # Higher/Lower game logic and leaderboard
│       ├── email/          # Async email service with HTML templates
│       ├── config/         # Async config, OpenAPI config
│       └── common/         # Global exception handler, health endpoint
│
├── frontend/src/
│   ├── api/                # Axios instance, TMDB API helpers
│   ├── context/            # AuthContext (global auth state)
│   ├── routes/             # ProtectedRoute wrapper
│   ├── pages/              # All 14 page components
│   │   ├── Login, Register
│   │   ├── Onboarding
│   │   ├── Dashboard       # Main feed with filters (Feed/Movies/TV)
│   │   ├── MovieDetails    # Handles both /movie/:id and /tv/:id
│   │   ├── SeasonDetails   # TV season episode list
│   │   ├── ActorDetails    # Person profile and filmography
│   │   ├── Profile         # User info and password change
│   │   ├── Favorites, Watchlist
│   │   ├── ReviewForm, HistoryRatings
│   │   └── HigherLowerGame, Leaderboard
│   ├── components/         # Navbar, SearchBar, MovieCard, etc.
│   └── styles/             # Global CSS, design tokens, layout
│
├── docker-compose.yml
└── .env.example
```

## Getting started

### Prerequisites

- Java 21+
- Node.js 18+ and npm
- PostgreSQL 14+ (or just use Docker)
- A free [TMDB API key](https://www.themoviedb.org/settings/api)
- Gmail account with an [app password](https://support.google.com/accounts/answer/185833) (for email notifications)

## Local URLs

- Frontend on http://localhost:3000
- Backend on http://localhost:8080
- Swagger UI available at http://localhost:8080/swagger-ui.html.
- PostgreSQL on port 5432

The database is automatically initialized with the Higher/Lower game questions.

## API overview

The backend exposes a REST API. All endpoints except auth require a valid JWT in the Authorization: Bearer <token> header.

Full API documentation is available via Swagger UI at `/swagger-ui.html` in development.

## Database

PostgreSQL with 7 tables:

- **users** - accounts (name, surname, username, email, password hash)
- **favorite_picks** - the 4 titles each user selects during onboarding (used for recommendations)
- **favorites** - saved favorite movies/shows
- **watchlist** - "watch later" list
- **reviews** - user ratings and text reviews (unique per user + media item)
- **game_scores** - Higher/Lower game score history
- **higher_lower_questions** - pre-seeded game question data

Tables are auto-created by Hibernate (`ddl-auto=update` in dev, `validate` in prod).


## Running tests

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && npm test
```

## Production deployment

The app is set up for deployment on Render.com with Neon PostgreSQL:

- Backend runs as a Docker container on Render
- Frontend is built and served via Nginx on Render
- Database is hosted on Neon (managed PostgreSQL)
- Swagger UI is disabled in production
- HTTPS is handled by Render

## Security notes

- TMDB API key is kept server-side, all content requests are proxied through the backend
- Passwords are hashed with BCrypt
- JWT tokens expire after 24 hours (dev)
- All protected routes require valid JWT in the Authorization header
- Input validation on both frontend (Zod) and backend (Spring Validation)

## Acknowledgments

- Movie and TV data provided by [TMDB](https://www.themoviedb.org/)
- Built as a senior design project