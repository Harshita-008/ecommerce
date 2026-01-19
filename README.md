# E-Commerce Backend API

A Spring Boot backend system for e-commerce with product management, cart, orders, and payment processing using mock service and Razorpay webhooks.

---

## Project Demo

**Video Demonstration:** [Watch Demo Video](https://drive.google.com/drive/u/1/folders/1RrkcWL8jctL-Bvldg_UmT14i5fEj3LkM)

---

## Features

- Product CRUD operations
- Shopping cart management
- Order creation and tracking
- Mock payment with webhook simulation
- Razorpay integration (Test Mode)
- Webhook-based payment confirmation

---

## Tech Stack

**Backend:** Spring Boot | **Database:** MongoDB | **Payment:** Razorpay | **Build:** Maven

---

## Project Structure

```
com.example.ecommerce
├── controller/          # REST endpoints
├── service/             # Business logic
├── repository/          # Database operations
├── model/               # Entities
├── dto/                 # Request/Response objects
├── webhook/             # Payment webhooks
└── config/              # Configuration
```

---

## API Endpoints

### Product APIs

#### Create Product
```http
POST /api/products
```

**Request Body**
```json
{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

#### Get All Products
```http
GET /api/products
```

---

### Cart APIs

#### Add Item to Cart
```http
POST /api/cart/add
```

**Request Body**
```json
{
  "userId": "user123",
  "productId": "PRODUCT_ID",
  "quantity": 2
}
```

#### Get User Cart
```http
GET /api/cart/{userId}
```

#### Clear Cart
```http
DELETE /api/cart/{userId}/clear
```

---

### Order APIs

#### Create Order from Cart
```http
POST /api/orders
```

**Request Body**
```json
{
  "userId": "user123"
}
```

#### Get Order Details
```http
GET /api/orders/{orderId}
```

---

### Payment APIs

#### Mock Payment (Standard)
```http
POST /api/payments/create
```

**Request Body**
```json
{
  "orderId": "ORDER_ID",
  "amount": 100000.0
}
```

- Payment is created with status `PENDING`
- Webhook is triggered automatically
- Order status updates to `PAID`

#### Razorpay Payment (Bonus)
```http
POST /api/payments/razorpay/create
```

**Request Body**
```json
{
  "orderId": "ORDER_ID",
  "amount": 100000.0
}
```

#### Razorpay Webhook
```http
POST /api/webhooks/payment
```

**Request Body**
```json
{
  "event": "payment.captured",
  "payload": {
    "payment": {
      "entity": {
        "id": "pay_test_123",
        "receipt": "ORDER_ID",
        "status": "captured"
      }
    }
  }
}
```

---

## Application Flow

1. Product is created by admin
2. User adds product to cart
3. Cart items are fetched
4. Order is created from cart
5. Order status is set to `CREATED`
6. Payment is initiated
7. Payment gateway sends webhook
8. Order status is updated to `PAID`

---

## Configuration

**application.yml**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/ecommerce

server:
  port: 8081

razorpay:
  key: YOUR_RAZORPAY_KEY
  secret: YOUR_RAZORPAY_SECRET
```

---

## Setup & Run

```bash
# Clone repository
git clone https://github.com/yourusername/ecommerce-backend.git

# Navigate to project
cd ecommerce-backend

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

Application runs on `http://localhost:8081`

---

## API Testing

### Using Postman

**Step 1: Create Product**
```http
POST http://localhost:8081/api/products
Content-Type: application/json

{
  "name": "iPhone 15",
  "description": "Latest iPhone",
  "price": 79999.0,
  "stock": 50
}
```
Save `productId` from response

---

**Step 2: Add to Cart**
```http
POST http://localhost:8081/api/cart/add
Content-Type: application/json

{
  "userId": "user123",
  "productId": "{{productId}}",
  "quantity": 1
}
```

---

**Step 3: View Cart**
```http
GET http://localhost:8081/api/cart/user123
```
Verify cart items and total amount

---

**Step 4: Create Order**
```http
POST http://localhost:8081/api/orders
Content-Type: application/json

{
  "userId": "user123"
}
```
Save `orderId` from response  
Verify status is `CREATED`

---

**Step 5: Mock Payment**
```http
POST http://localhost:8081/api/payments/create
Content-Type: application/json

{
  "orderId": "{{orderId}}",
  "amount": 79999.0
}
```
⏳ Wait 3 seconds for auto webhook

---

**Step 6: Verify Order Status**
```http
GET http://localhost:8081/api/orders/{{orderId}}
```
Verify status is `PAID`  
Cart should be cleared

---

### Using cURL

**Complete Test Flow**

```bash
# 1. Create Product
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","description":"Gaming","price":50000,"stock":10}'

# 2. Add to Cart (replace PRODUCT_ID)
curl -X POST http://localhost:8081/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123","productId":"PRODUCT_ID","quantity":2}'

# 3. View Cart
curl http://localhost:8081/api/cart/user123

# 4. Create Order
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123"}'

# 5. Mock Payment (replace ORDER_ID)
curl -X POST http://localhost:8081/api/payments/create \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORDER_ID","amount":100000}'

# 6. Check Order Status (wait 3 seconds)
curl http://localhost:8081/api/orders/ORDER_ID
```

---

### Razorpay Payment Testing

**Step 1: Create Razorpay Payment**
```http
POST http://localhost:8081/api/payments/razorpay/create
Content-Type: application/json

{
  "orderId": "{{orderId}}",
  "amount": 79999.0
}
```
Save `razorpayOrderId`

---

**Step 2: Simulate Webhook**
```http
POST http://localhost:8081/api/webhooks/payment
Content-Type: application/json

{
  "event": "payment.captured",
  "payload": {
    "payment": {
      "entity": {
        "id": "pay_test_abc123",
        "receipt": "{{orderId}}",
        "status": "captured",
        "amount": 7999900
      }
    }
  }
}
```

---

**Step 3: Verify Payment**
```http
GET http://localhost:8081/api/orders/{{orderId}}
```
Order status should be `PAID`

---

### Test Scenarios Covered

| Scenario | Endpoint | Expected Result |
|----------|----------|----------------|
| Create product with valid data | `POST /api/products` | Product created successfully |
| Add product to cart | `POST /api/cart/add` | Item added to cart |
| Create order with items | `POST /api/orders` | Order created with `CREATED` status |
| Mock payment processing | `POST /api/payments/create` | Payment initiated, webhook auto-triggers |
| Order status after payment | `GET /api/orders/{id}` | Status changes to `PAID` |
| Cart after order payment | `GET /api/cart/{userId}` | Cart is empty |
| Razorpay integration | `POST /api/payments/razorpay/create` | Razorpay order created |
| Webhook processing | `POST /api/webhooks/payment` | Payment verified, order updated |

---

## Key Highlights

- RESTful API design
- Layered architecture (Controller → Service → Repository)
- Stock validation and reduction
- Asynchronous webhook processing
- Real payment gateway integration
- Business rule validation

---

## Order Status Flow

```
CREATED → Payment Initiated → Webhook Received → PAID
```