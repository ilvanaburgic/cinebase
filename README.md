# Cinebase

A modern web application for browsing movies and TV shows powered by TMDB (The Movie Database) API.

## Features

- **User Authentication**: Secure login and registration with JWT-based authentication
- **Browse Content**: Discover popular, latest, and top-rated movies and TV shows
- **Search**: Search for movies and TV shows by title
- **Feed**: View trending content across both movies and TV shows
- **Responsive Design**: Mobile-first design that works on all screen sizes

## Tech Stack

### Backend
- **Java 21** with Spring Boot 3.x
- **Spring Security** with JWT authentication
- **PostgreSQL** database
- **Spring WebFlux** for reactive TMDB API calls
- **Maven** for dependency management

### Frontend
- **React 19** with modern hooks
- **React Router** for navigation
- **Axios** for HTTP requests
- **React Hook Form** + Zod for form validation
- **CSS Modules** for styling

## Prerequisites

- **Java 21** or higher
- **Node.js 18+** and npm
- **PostgreSQL 14+**
- **TMDB API Key** (get one at [themoviedb.org](https://www.themoviedb.org/settings/api))

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd cinebase
```

### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE cinebase_db;
```

### 3. Backend Configuration

1. Copy the example environment file:
   ```bash
   cd backend
   cp .env.example .env
   ```

2. Edit `.env` and fill in your credentials:
   ```env
   DB_PASSWORD=your_postgres_password
   JWT_SECRET=your_secure_jwt_secret_min_256_bits
   TMDB_KEY=your_tmdb_api_key
   ```

3. Build and run the backend:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### 4. Frontend Configuration

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend will start on `http://localhost:3000`

## Project Structure

```
cinebase/
├── backend/
│   ├── src/main/java/com/sdp/cinebase/
│   │   ├── auth/          # Authentication controllers and services
│   │   ├── security/      # JWT security configuration
│   │   ├── tmdb/          # TMDB API integration
│   │   │   ├── dto/       # Data transfer objects
│   │   │   ├── service/   # TMDB client service
│   │   │   └── web/       # REST controllers
│   │   ├── user/          # User management
│   │   └── common/        # Shared utilities
│   └── src/main/resources/
│       └── application.properties
│
├── frontend/
│   ├── src/
│   │   ├── api/           # API clients
│   │   ├── components/    # Reusable React components
│   │   ├── context/       # React context providers
│   │   ├── pages/         # Page components
│   │   ├── routes/        # Route configuration
│   │   └── styles/        # Global CSS
│   └── public/
│
└── README.md
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login with credentials

### Movies
- `GET /api/tmdb/movies/popular` - Get popular movies
- `GET /api/tmdb/movies/top-rated` - Get top-rated movies
- `GET /api/tmdb/movies/latest` - Get latest movies
- `GET /api/tmdb/movies/search?q=query` - Search movies

### TV Shows
- `GET /api/tmdb/tv/popular` - Get popular TV shows
- `GET /api/tmdb/tv/top-rated` - Get top-rated TV shows
- `GET /api/tmdb/tv/latest` - Get latest TV shows
- `GET /api/tmdb/tv/search?q=query` - Search TV shows

### Feed
- `GET /api/tmdb/feed/popular` - Get trending today (mixed)
- `GET /api/tmdb/feed/latest` - Get trending this week (mixed)
- `GET /api/tmdb/feed/top-rated` - Get combined top-rated content

## Security Notes

- The TMDB API key is used client-side **only** for fetching public data (genres, configuration)
- All movie/TV data requests are proxied through the backend to keep the main API key secure
- JWT tokens expire after 24 hours
- Passwords are hashed using bcrypt

## Development

### Running Tests

Backend:
```bash
cd backend
./mvnw test
```

Frontend:
```bash
cd frontend
npm test
```

### Building for Production

Backend:
```bash
cd backend
./mvnw clean package
java -jar target/cinebase-0.0.1-SNAPSHOT.jar
```

Frontend:
```bash
cd frontend
npm run build
```

## License

This project is for educational purposes.

## Acknowledgments

- Movie data provided by [TMDB](https://www.themoviedb.org/)
- Built with Spring Boot and React
