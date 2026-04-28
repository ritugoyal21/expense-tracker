# 💰 Expense Tracker REST API
### Built with Java 17 + Spring Boot 3 + MySQL

---

## 📁 Project Structure

```
expense-tracker/
├── pom.xml
└── src/main/
    ├── java/com/expensetracker/
    │   ├── ExpenseTrackerApplication.java     ← Main entry point
    │   ├── controller/
    │   │   └── ExpenseController.java         ← REST endpoints
    │   ├── service/
    │   │   ├── ExpenseService.java            ← Interface (contract)
    │   │   └── ExpenseServiceImpl.java        ← Business logic
    │   ├── repository/
    │   │   └── ExpenseRepository.java         ← DB queries (JPA)
    │   ├── entity/
    │   │   ├── Expense.java                   ← DB table mapping
    │   │   └── Category.java                  ← Enum (FOOD, TRAVEL...)
    │   ├── dto/
    │   │   └── ExpenseDTO.java                ← API request/response shape
    │   └── exception/
    │       ├── ResourceNotFoundException.java ← Custom 404 exception
    │       └── GlobalExceptionHandler.java    ← Central error handler
    └── resources/
        └── application.properties             ← DB + app config
```

---

## ⚙️ Prerequisites

Make sure you have installed:
- **Java 17** or later (`java -version`)
- **Maven 3.6+** (`mvn -version`)
- **MySQL 8.0+** (running locally or via XAMPP/WAMP)

---

## 🚀 Step-by-Step Run Instructions

### Step 1: Set Up MySQL Database

Open MySQL Workbench or terminal and run:
```sql
CREATE DATABASE expense_tracker_db;
```
> ✅ The app will auto-create the `expenses` table on first run.

---

### Step 2: Configure Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL password.

---

### Step 3: Build the Project

```bash
cd expense-tracker
mvn clean install
```

You should see: `BUILD SUCCESS`

---

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR:
```bash
java -jar target/expense-tracker-1.0.0.jar
```

You should see:
```
✅ Expense Tracker API is running on http://localhost:8080
```

---

## 📡 API Endpoints

| Method | Endpoint                | Description                    |
|--------|-------------------------|--------------------------------|
| POST   | `/expenses`             | Add a new expense              |
| GET    | `/expenses`             | Get all expenses               |
| GET    | `/expenses/{id}`        | Get expense by ID              |
| PUT    | `/expenses/{id}`        | Update an expense              |
| DELETE | `/expenses/{id}`        | Delete an expense              |
| GET    | `/expenses/filter`      | Filter by category/date range  |
| GET    | `/expenses/total`       | Get total spending             |

---

## 📋 Sample JSON Requests & Responses

### 1. POST /expenses — Add Expense

**Request:**
```json
POST http://localhost:8080/expenses
Content-Type: application/json

{
  "title": "Weekly Groceries",
  "amount": 1250.75,
  "category": "FOOD",
  "date": "2024-12-01",
  "description": "Big Bazaar monthly shopping"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Weekly Groceries",
  "amount": 1250.75,
  "category": "FOOD",
  "date": "2024-12-01",
  "description": "Big Bazaar monthly shopping"
}
```

---

### 2. GET /expenses — Get All Expenses

**Request:**
```
GET http://localhost:8080/expenses
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Weekly Groceries",
    "amount": 1250.75,
    "category": "FOOD",
    "date": "2024-12-01",
    "description": "Big Bazaar monthly shopping"
  },
  {
    "id": 2,
    "title": "Flight to Goa",
    "amount": 5500.00,
    "category": "TRAVEL",
    "date": "2024-12-05",
    "description": "IndiGo round trip"
  }
]
```

---

### 3. GET /expenses/5 — Get by ID

**Response (200 OK):**
```json
{
  "id": 5,
  "title": "Netflix Subscription",
  "amount": 649.00,
  "category": "BILLS",
  "date": "2024-12-01",
  "description": "Monthly OTT subscription"
}
```

**Response when not found (404):**
```json
{
  "status": 404,
  "message": "Expense not found with id: 5",
  "timestamp": "2024-12-01T10:30:00"
}
```

---

### 4. PUT /expenses/1 — Update Expense

**Request:**
```json
PUT http://localhost:8080/expenses/1
Content-Type: application/json

{
  "title": "Weekly Groceries (Updated)",
  "amount": 1400.00,
  "category": "FOOD",
  "date": "2024-12-01",
  "description": "Added household items too"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Weekly Groceries (Updated)",
  "amount": 1400.00,
  "category": "FOOD",
  "date": "2024-12-01",
  "description": "Added household items too"
}
```

---

### 5. DELETE /expenses/3

**Response (200 OK):**
```json
{
  "message": "Expense with id 3 deleted successfully"
}
```

---

### 6. GET /expenses/filter — Filter Expenses

**Filter by category only:**
```
GET http://localhost:8080/expenses/filter?category=FOOD
```

**Filter by date range only:**
```
GET http://localhost:8080/expenses/filter?startDate=2024-12-01&endDate=2024-12-31
```

**Filter by both category + date range:**
```
GET http://localhost:8080/expenses/filter?category=TRAVEL&startDate=2024-01-01&endDate=2024-12-31
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "title": "Flight to Goa",
    "amount": 5500.00,
    "category": "TRAVEL",
    "date": "2024-12-05",
    "description": "IndiGo round trip"
  }
]
```

---

### 7. GET /expenses/total — Total Spending

**Request:**
```
GET http://localhost:8080/expenses/total
```

**Response (200 OK):**
```json
{
  "totalExpenses": 9399.75,
  "currency": "INR"
}
```

---

## ❌ Validation Error Examples

**Request with missing fields (400 Bad Request):**
```json
POST /expenses
{
  "title": "",
  "amount": -50
}
```

**Response:**
```json
{
  "title": "Title is required",
  "amount": "Amount must be greater than 0",
  "category": "Category is required",
  "date": "Date is required"
}
```

---

## 🗃️ Database Table Schema (Auto-Created)

```sql
CREATE TABLE expenses (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(100)   NOT NULL,
  amount      DECIMAL(10, 2) NOT NULL,
  category    VARCHAR(20)    NOT NULL,
  date        DATE           NOT NULL,
  description VARCHAR(255)
);
```

---

## 📦 Valid Category Values

| Value      | Use For                                    |
|------------|--------------------------------------------|
| `FOOD`     | Groceries, restaurants, snacks, drinks     |
| `TRAVEL`   | Flights, fuel, hotels, cabs, transport     |
| `SHOPPING` | Clothes, electronics, retail purchases     |
| `BILLS`    | Rent, electricity, internet, subscriptions |
| `OTHER`    | Anything that doesn't fit above            |

---

## 🧪 Test with cURL

```bash
# Add expense
curl -X POST http://localhost:8080/expenses \
  -H "Content-Type: application/json" \
  -d '{"title":"Coffee","amount":120,"category":"FOOD","date":"2024-12-01"}'

# Get all
curl http://localhost:8080/expenses

# Get total
curl http://localhost:8080/expenses/total

# Filter by category
curl "http://localhost:8080/expenses/filter?category=FOOD"
```
