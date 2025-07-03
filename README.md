# Library Management System – Java Full-Stack Project

This is a web-based Library Management System built using **Java (Spring Boot)** for the backend and **HTML/CSS/JavaScript (Bootstrap)** for the frontend. The system is designed for managing book records and borrowing operations with two main user roles: **Admin** and **Student**.

## Key Features

### 🔐 Authentication
- Student registration and login (with password hashing)
- Admin login (manually seeded or hardcoded)

### 📚 Student Functionalities
- View available books
- Borrow and return books
- View their own borrow history

### 🛠️ Admin Functionalities
- Add, edit, and delete book records
- View all borrow records

## Tech Stack
- **Backend**: Java, Spring Boot, MySQL, Spring Security
- **Frontend**: HTML, CSS, Bootstrap, JavaScript
- **API Testing**: Postman
- **Authentication**: JWT or session-based login

## Database Structure
- `users`: Stores user info (students/admins)
- `books`: Stores book data
- `borrow_records`: Tracks borrowing activity

## Project Timeline
- **Week 1**: Backend setup – APIs, models, authentication, and business logic
- **Week 2**: Frontend UI – registration/login pages, dashboards, integration, and final testing

---
