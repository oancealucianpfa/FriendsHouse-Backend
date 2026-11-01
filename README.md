# 🏨 Friends House Booking - Setup & Running Guide

This guide contains all the necessary steps for a collaborator to run the full application locally (MySQL Database in Docker, Spring Boot Backend, and Angular Frontend).

---

## 📋 Prerequisites

Ensure you have the following installed on your machine:
* [Git](https://git-scm.com/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) *(make sure the Docker daemon is running in the background)*
* [Java JDK 17+](https://adoptium.net/)
* [Node.js & npm](https://nodejs.org/) *(for the Angular Frontend)*

---

## 🚀 Step 1: Clone the Repositories

Open a terminal in the folder where you want to store the projects and run:

git clone https://github.com/oancealucianpfa/FriendsHouse-Backend.git
cd FriendsHouse-Backend

cd ..
git clone https://github.com/oancealucianpfa/FriendsHouse-Angular.git

---

## 🔐 Step 2: Configure Environment Variables (.env)

Sensitive credentials are strictly ignored by Git and kept secure.

1. Navigate to the `FriendsHouse-Backend` folder.
2. Create a file named `.env` in the root directory (next to `docker-compose.yml`).
3. Copy the template content from `.env.example` into `.env`.
4. Populate your `.env` file using the following placeholders as a guide:

# MySQL Configuration
MYSQL_DATABASE=hotel
MYSQL_ROOT_PASSWORD=your_secure_db_password
MYSQL_PORT=3307

# Spring Boot Data Source
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3307/hotel
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_secure_db_password

# Security Keys
JWT_SECRET_STRING=your_jwt_secret_key_here

# Mail Credentials
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_google_app_password

# Stripe API Keys
STRIPE_API_PUBLIC_KEY=your_stripe_public_key
STRIPE_API_SECRET_KEY=your_stripe_secret_key

---

## 🐳 Step 3: Start the MySQL Database in Docker

Open your terminal inside the `FriendsHouse-Backend` directory and execute:

docker compose up -d

What happens automatically in the background:
* Docker spins up the `friends_house_mysql` container bound to port `3307`.
* It automatically executes `src/main/resources/sql/schema.sql` to build the `hotel` database tables.
* Data is stored persistently inside the `mysql_data` Docker volume.

To confirm the container is active:
docker ps

---

## ☕ Step 4: Run the Spring Boot Backend

1. Open the `FriendsHouse-Backend` repository in IntelliJ IDEA.
2. Allow Maven to download all dependencies.
3. Run the main class `HotelBookingApplication` (or `FriendsHouseBookingApplication`), or execute in terminal:

./mvnw spring-boot:run

The Backend API will be live at: http://localhost:7070.

---

## 💻 Step 5: Run the Angular Frontend

1. Open a terminal inside the `FriendsHouse-Angular` project folder.
2. Install dependencies and start the development server:

npm install
npm start

The Frontend application will open in your browser at: http://localhost:4200.

---

## 🛑 Stop & Cleanup

To stop the MySQL container when you finish working:

docker compose down