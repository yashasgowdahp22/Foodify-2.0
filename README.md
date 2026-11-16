# Foodify 2.0 – Full Stack Food Delivery Application

Foodify 2.0 is a full-stack food delivery application built using **Java Spring Boot, React, and MySQL**. It allows customers to explore restaurants, browse menus, manage their cart, place orders, and track their order history.

The application is deployed using cloud hosting services.

## Live Demo

- **Frontend:** https://foodify-frontend-seven.vercel.app
- **Backend API:** https://foodify-backend-qsdy.onrender.com
- **API Documentation:** https://foodify-backend-qsdy.onrender.com/swagger-ui/index.html

> Note: The backend uses Render's free tier and may take some time to respond after inactivity.

## Technologies Used

### Backend
- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- Hibernate
- Spring Security
- JWT Authentication
- MySQL
- Maven
- Swagger / OpenAPI
- Docker

### Frontend
- React
- JavaScript
- Vite
- Tailwind CSS
- React Router
- Axios
- Context API

### Deployment and Tools
- Git and GitHub
- Render – Backend Hosting
- Vercel – Frontend Hosting
- TiDB Cloud – Cloud Database

## Features

### Customer Features
- User registration and login
- JWT-based authentication
- Browse available restaurants
- Search and explore restaurant menus
- Add items to cart
- Update and remove cart items
- Place food orders
- Cash on Delivery (COD) and manual UPI payment options
- View order history and order details
- Cancel eligible orders

### Backend Features
- RESTful API architecture
- Entity relationships using JPA and Hibernate
- Secure authentication using JWT
- Request validation
- Global exception handling
- Order and delivery status management
- API documentation using Swagger

## Application Architecture

```text
             React Frontend
                  |
             Axios API Calls
                  |
             Spring Boot
                  |
          Spring Security + JWT
                  |
          Service and Repository
                  |
             MySQL Database
```

## Deployment Architecture

| Component | Platform |
|---|---|
| Frontend | Vercel |
| Backend | Render |
| Database | TiDB Cloud |

Environment variables are used to manage production configuration and sensitive credentials.

## Run Locally

### Prerequisites
- Java 17
- Maven
- MySQL
- Node.js and npm
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/yashasgowdahp22/Foodify-2.0.git
cd Foodify-2.0
```

### 2. Backend Setup

Configure the required environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/foodify
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=your_secure_32_byte_or_longer_secret
```

Create a MySQL database named `foodify`.

Run the backend:

```bash
mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

### 3. Frontend Setup

Open another terminal:

```bash
cd frontend
npm install
```

Create a frontend `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Start the frontend:

```bash
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

## Application Screenshots

### Home Page

![Foodify Home](Screenshot%202026-11-16%20134504.png)

### Restaurant Menu

![Restaurant Menu](Screenshot%202026-11-16%20134617.png)

### Restaurant Browsing

![Restaurant Browsing](Screenshot%202026-11-16%20134639.png)

### Shopping Cart

![Shopping Cart](Screenshot%202026-11-16%20134653.png)

### Checkout

![Checkout](Screenshot%202026-11-16%20134703.png)

### Order History

![Order History](Screenshot%202026-11-16%20134726.png)

### Order Details

![Order Details](Screenshot%202026-11-16%20134736.png)

## Author

**Yashas Gowda H.P.**

- GitHub: https://github.com/yashasgowdahp22
- Portfolio: https://yashasgowda.lovable.app

---

*Foodify 2.0 – A full-stack food delivery application developed as a portfolio project.*
