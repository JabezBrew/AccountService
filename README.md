# 🏦 Account Service

A secure corporate payroll management system built with Spring Boot that allows companies to manage employee payrolls through a web portal instead of email.

## 📝 Description

This service provides a secure way for companies to manage employee payroll information through a web interface instead of using corporate email. It features:

## 🎯 Features

- 👤 User Authentication & Authorization
  - Secure signup and login
  - Role-based access control (RBCO)
  - Password security policies
  - Account locking mechanism

- 💰 Payroll Management 
  - Upload employee payrolls
  - View payment history
  - Track salary information

- 👮 Administrative Controls
  - User management
  - Role management
  - Access control
  - Security audit logging

## 🛠️ Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- H2 Database
- Gradle
- Lombok

## 🔒 Security Features

- Password validation
- Breached password protection
- HTTPS/TLS support
- Basic authentication
- Role-based authorization
- Security event logging

## 📚 API Endpoints

#### Authentication
- `POST /api/auth/signup` - Register new users
- `POST /api/auth/changepass` - Change user password

#### Employee Management 
- `GET /api/empl/payment` - View employee payroll information
- `POST /api/acct/payments` - Upload payroll information
- `PUT /api/acct/payments` - Updates payroll information

#### Administration
- `GET /api/admin/user` - List all users
- `DELETE /api/admin/user` - Delete users
- `PUT /api/admin/user/role` - Modify user roles
- `PUT /api/admin/user/access` - Lock/unlock user accounts

## 🚀 Getting Started

1. Clone the repository
```sh
git clone <repository-url>
```

2. Build the project
```sh
./gradlew build
```

3. Run the application
```sh
./gradlew bootRun
```

The service will be available at `http://localhost:8080`

## 🔐 Security Policies

- Passwords must be at least 12 characters long
- Passwords cannot be found in the breached password database
- New passwords must be different from old passwords
- Accounts are locked after suspicious activities

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

Project completed as part of the [JetBrains Academy Java Backend Developer track](https://hyperskill.org/projects/217)
