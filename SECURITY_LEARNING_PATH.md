# Spring Security Learning Path
### From Zero to JWT Auth — Follow This Order

---

## Before You Start

> Make sure the app is running on `http://localhost:8082`  
> Import `postman-collection.json` into Postman  
> Open the **"⚡ START HERE — Auth"** folder in Postman

**One big question to keep in mind as you read each file:**  
*"Who is allowed to do this, and how does Spring know?"*

---

## The Big Picture — Read This First

```
CLIENT                          SERVER
  │                               │
  │── POST /auth/register ────────▶  Save user (BCrypt password)
  │◀─ { token: "eyJhbG..." } ─────  Return JWT token
  │                               │
  │── POST /auth/login ───────────▶  Verify password
  │◀─ { token: "eyJhbG..." } ─────  Return JWT token
  │                               │
  │── GET /api/products ──────────▶  JwtFilter reads token
  │   Header: Bearer eyJhbG...       Validates it
  │                                  Sets "user is logged in"
  │◀─ { data: [...products] } ────  Controller runs
  │                               │
  │── GET /api/admin/dashboard ───▶  JwtFilter validates token
  │   Header: Bearer eyJhbG...       SecurityConfig checks role
  │◀─ 403 Forbidden ──────────────  ROLE_USER? Blocked!
```

**Two different errors to know:**
| Error | Meaning | When |
|-------|---------|------|
| `401 Unauthorized` | "Who are you? No token sent." | Missing or invalid token |
| `403 Forbidden` | "I know you, but you can't do this." | Wrong role (USER trying ADMIN endpoint) |

---

## Step 1 — Roles: What Users Are Allowed to Do
| | |
|---|---|
| **Open this file** | `model/Role.java` |
| **Reading time** | 2 minutes |

**What to understand:**
- A Role = permission level in the app
- `ROLE_USER` → can browse products, place orders
- `ROLE_ADMIN` → can also create/delete products, see admin panel
- Spring Security convention: role names **must** start with `ROLE_`

**Key question:** Why does `hasRole('ADMIN')` work when the enum value is `ROLE_ADMIN`?  
**Answer:** Spring automatically adds the `ROLE_` prefix when you use `hasRole()`.  
Use `hasAuthority('ROLE_ADMIN')` if you want no auto-prefix.

---

## Step 2 — User Entity: Storing Who Can Log In
| | |
|---|---|
| **Open this file** | `model/User.java` |
| **Then open H2 Console** | `http://localhost:8082/h2-console` after login |
| **Reading time** | 3 minutes |

**What to understand:**
- `User` is a normal JPA `@Entity` — just like `Product`
- The `password` field stores a **BCrypt hash**, never the real password
- `@Column(unique = true)` on `username` — no two users with same name

**Observe in H2 Console (after registering):**
```sql
SELECT * FROM USERS;
```
You will see the password is `$2a$10$...` — unreadable.  
The original password is gone forever. BCrypt is one-way.

**Key question:** How does login work if we can't un-hash the password?  
**Answer:** BCrypt re-hashes what the user types and **compares** the two hashes.

---

## Step 3 — Password Hashing: BCrypt
| | |
|---|---|
| **Open this file** | `security/SecurityConfig.java` |
| **Read only:** | The `passwordEncoder()` @Bean method (bottom of file) |
| **Reading time** | 2 minutes |

**What BCrypt does:**
```
"secret123"  ──BCrypt.encode()──▶  "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
"secret123"  ──BCrypt.encode()──▶  "$2a$10$XqzR7l5mDFkV6YQ2PwZhBe..."  ← different each time!

BCrypt.matches("secret123", "$2a$10$N9qo8uLOickgx2ZMRZoMye...") ──▶ true ✅
BCrypt.matches("wrongpass", "$2a$10$N9qo8uLOickgx2ZMRZoMye...") ──▶ false ❌
```

The `10` in `$2a$10$` is the **work factor** — how many rounds of hashing.  
Higher = slower = harder to crack. 10 is the default.

**Postman test:** Register a user → open H2 Console → verify the password hash is stored.

---

## Step 4 — UserDetailsService: The Bridge
| | |
|---|---|
| **Open this file** | `security/UserDetailsServiceImpl.java` |
| **Reading time** | 5 minutes |

**The problem this solves:**
Spring Security has no idea about your `User` entity or `UserRepository`.  
It only knows about its own `UserDetails` interface.

`UserDetailsServiceImpl` is the **translator**:

```
Spring Security asks: "Give me the user named 'praveen'"
         ↓
UserDetailsServiceImpl.loadUserByUsername("praveen")
         ↓
userRepository.findByUsername("praveen")  ← query YOUR database
         ↓
Convert: User entity → Spring's UserDetails object
         ↓
Spring Security gets: username + hashed password + roles
```

**What to observe:**  
The method `loadUserByUsername()` is called in TWO places:
1. During **login** — AuthenticationManager calls it to load the user for password check
2. In **JwtFilter** — called after extracting username from token, to set authentication

---

## Step 5 — JWT: The Token (Most Important!)
| | |
|---|---|
| **Open this file** | `security/JwtUtil.java` |
| **Reading time** | 8 minutes |

**First, understand WHAT a JWT is:**

A JWT looks like this (three parts separated by `.`):
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwcmF2ZWVuIiwiaWF0IjoxNzA1MzE2MDAwLCJleHAiOjE3MDU0MDI0MDB9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
│─── Part 1 ──────│ │─────────────── Part 2 ───────────────────────────────────│ │──────── Part 3 ──────────────────────────────────────────│
    Header              Payload (username, issued at, expires at)                   Signature
```

Go to **https://jwt.io** → paste your token → see the decoded contents.

**Part 1 — Header:** Algorithm used (`HS256`)  
**Part 2 — Payload:** Who the token belongs to + expiry (NOT secret! Anyone can read it)  
**Part 3 — Signature:** Proves nobody tampered with it (uses your secret key)

**Three methods to know:**

| Method | When called | Does what |
|--------|------------|-----------|
| `generateToken(userDetails)` | After login/register | Creates and signs a new JWT |
| `extractUsername(token)` | In JwtFilter | Reads username from Part 2 |
| `isTokenValid(token, userDetails)` | In JwtFilter | Checks username matches + not expired |

**Postman test:**
1. Register → copy the token
2. Go to jwt.io → paste → see your username in the payload
3. Wait 24 hours (or shorten `jwt.expiration`) → token expires → requests return 401

---

## Step 6 — JwtFilter: Checking Every Request
| | |
|---|---|
| **Open this file** | `security/JwtFilter.java` |
| **Reading time** | 8 minutes |

**This is the most active security file** — it runs on EVERY single HTTP request.

**Read through `doFilterInternal()` step by step:**

```
Step 1: Read "Authorization" header
        "Bearer eyJhbGciOiJIUzI1NiJ9..."
             ↓
Step 2: No header? → skip, pass to next filter
        (SecurityConfig will block if endpoint needs auth)
             ↓
Step 3: Strip "Bearer " → get just the token
             ↓
Step 4: extractUsername(token) → "praveen"
             ↓
Step 5: loadUserByUsername("praveen") → load from DB
             ↓
Step 6: isTokenValid(token, userDetails) → true/false
             ↓
Step 7: Create UsernamePasswordAuthenticationToken
        (Spring's object meaning "this user is authenticated")
             ↓
Step 8: SecurityContextHolder.setAuthentication(authToken)
        → "praveen is logged in for THIS request"
             ↓
Step 9: filterChain.doFilter() → pass to controller
```

**Key concept — SecurityContextHolder:**  
This is like a storage box for "who is currently logged in".  
It lives only for the duration of one request, then gets cleared.  
Setting it in JwtFilter means every controller and service can access it.

**Postman test — what happens with a bad token:**
- Remove last character of the token → `io.jsonwebtoken.security.SignatureException`
- JwtFilter catches it, logs it, and moves on
- SecurityConfig then returns 401 because no authentication was set

---

## Step 7 — SecurityConfig: The Master Rules
| | |
|---|---|
| **Open this file** | `security/SecurityConfig.java` |
| **Reading time** | 10 minutes — most important file |

**Read the `securityFilterChain()` method line by line.**

**5 things configured here:**

**1. CSRF disabled**
```java
.csrf(AbstractHttpConfigurer::disable)
```
CSRF attacks only matter when browsers auto-send cookies.  
REST APIs use tokens in headers → CSRF irrelevant → disable it.

**2. Stateless sessions**
```java
.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```
Server remembers NOTHING between requests.  
Every request must carry the JWT. This is what makes REST APIs scalable.

**3. URL access rules (read top to bottom)**
```java
.requestMatchers("/auth/**").permitAll()              // no token needed
.requestMatchers(HttpMethod.GET, "/api/products/**").authenticated() // any user
.requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN") // admin only
.anyRequest().authenticated()                         // everything else: logged in
```
First matching rule wins. Order matters!

**4. Add JwtFilter BEFORE the default Spring login filter**
```java
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
```
This ensures our JWT check runs first on every request.

**5. AuthenticationProvider**
Wires together: `UserDetailsServiceImpl` (load user) + `BCryptPasswordEncoder` (check password)  
Used when `AuthenticationManager.authenticate()` is called in `AuthController`.

---

## Step 8 — AuthController: Register and Login
| | |
|---|---|
| **Open this file** | `controller/AuthController.java` |
| **Postman folder** | `⚡ START HERE — Auth` |
| **Reading time** | 10 minutes |

**REGISTER flow — read `register()` method:**
```
Client: POST /auth/register  { username: "praveen", password: "pass123" }
   ↓
1. Check username not taken → userRepository.existsByUsername()
   ↓
2. Hash password → passwordEncoder.encode("pass123") → "$2a$10$..."
   ↓
3. Save User to DB → userRepository.save(user)
   ↓
4. Generate token → jwtUtil.generateToken(userDetails)
   ↓
5. Return token to client
```

**LOGIN flow — read `login()` method:**
```
Client: POST /auth/login  { username: "praveen", password: "pass123" }
   ↓
1. authenticationManager.authenticate(username, password)
   Internally:
     → loadUserByUsername("praveen") → load from DB
     → BCrypt.matches("pass123", storedHash) → true or false
     → throws BadCredentialsException if wrong
   ↓
2. Credentials correct → generate token
   ↓
3. Return token to client
```

**Postman tests to run in order:**
```
1. POST /auth/register  { "username": "admin", "password": "admin123", "role": "admin" }
   → 201 Created + token

2. Copy token → paste into Postman collection variable "token"

3. POST /auth/register (same username again)
   → 400 Bad Request: "Username admin is already taken"

4. POST /auth/login  { "username": "admin", "password": "wrongpass" }
   → 401 Unauthorized: "Invalid username or password"

5. POST /auth/login  { "username": "admin", "password": "admin123" }
   → 200 OK + fresh token
```

---

## Step 9 — Role-Based Access: @PreAuthorize
| | |
|---|---|
| **Open this file** | `controller/AdminController.java` |
| **Postman folder** | `Admin Only` |
| **Reading time** | 5 minutes |

**Two ways to restrict by role:**

**Way 1 — In SecurityConfig (URL-level):**
```java
.requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
```
Blocks at the filter level — controller code never even runs.

**Way 2 — @PreAuthorize on the method:**
```java
@PreAuthorize("hasRole('ADMIN')")
public ApiResponse<?> dashboard() { ... }
```
More fine-grained — can protect one method in an otherwise open controller.  
Requires `@EnableMethodSecurity` in `SecurityConfig`.

**@AuthenticationPrincipal:**
```java
public ApiResponse<?> dashboard(@AuthenticationPrincipal UserDetails currentUser) {
    currentUser.getUsername();  // "admin"
}
```
Spring injects the currently logged-in user directly.  
This works because JwtFilter set it in `SecurityContextHolder`.

**Postman tests:**
```
1. Register as admin → copy token → GET /api/admin/dashboard → ✅ 200 OK
2. Register as user  → copy token → GET /api/admin/dashboard → ❌ 403 Forbidden
3. No token at all               → GET /api/admin/dashboard → ❌ 401 Unauthorized
```

**Notice:** 401 vs 403 — they are different errors with different meanings!

---

## Step 10 — How It All Connects

Read the files in this order one more time, but this time trace ONE full request:

**Scenario:** Admin user calls `POST /api/products` (create a product)

```
Request: POST /api/products
Header:  Authorization: Bearer eyJhbG...
Body:    { "name": "iPad", "price": 599, ... }

FILE 1: JwtFilter.java
  → reads "eyJhbG..." from header
  → calls JwtUtil.extractUsername() → "admin"
  → calls UserDetailsServiceImpl.loadUserByUsername("admin") → loads from DB
  → calls JwtUtil.isTokenValid() → true
  → creates UsernamePasswordAuthenticationToken
  → sets it in SecurityContextHolder

FILE 2: SecurityConfig.java
  → request matches: POST /api/products/**
  → rule: hasAuthority("ROLE_ADMIN")
  → checks SecurityContextHolder → user has ROLE_ADMIN ✅
  → allows request through

FILE 3: ProductController.java
  → createProduct() method runs
  → calls ProductServiceImpl

FILE 4: ProductServiceImpl.java
  → saves to DB
  → returns new Product

Response: 201 Created { "success": true, "data": { "id": 11, "name": "iPad" ... } }
```

---

## Summary Table — All Security Files

| File | Role | When It Runs |
|------|------|-------------|
| `model/Role.java` | Defines `ROLE_USER` / `ROLE_ADMIN` | - |
| `model/User.java` | DB table for user accounts | - |
| `repository/UserRepository.java` | Queries users from DB | - |
| `security/UserDetailsServiceImpl.java` | Loads user for Spring Security | At login + every JWT request |
| `security/JwtUtil.java` | Creates and reads JWT tokens | At login (create) + every request (read) |
| `security/JwtFilter.java` | Validates token on every request | **Every HTTP request** |
| `security/SecurityConfig.java` | Master access rules + wiring | App startup + every request |
| `dto/auth/RegisterRequest.java` | Request body for `/auth/register` | - |
| `dto/auth/LoginRequest.java` | Request body for `/auth/login` | - |
| `dto/auth/AuthResponse.java` | Token sent back to client | - |
| `controller/AuthController.java` | `/auth/register` and `/auth/login` | When client registers or logs in |
| `controller/AdminController.java` | Admin-only endpoints | When admin calls `/api/admin/**` |

---

## Common Errors and What They Mean

| Error | HTTP Code | Reason | Fix |
|-------|-----------|--------|-----|
| No token sent | `401` | Authorization header missing | Add `Bearer <token>` header |
| Token expired | `401` | 24 hours passed | Login again to get a fresh token |
| Token tampered | `401` | Signature invalid | Use the original token |
| Wrong role | `403` | ROLE_USER calling ROLE_ADMIN endpoint | Login as admin |
| Username taken | `400` | Already registered | Use a different username |
| Wrong password | `401` | BCrypt mismatch | Check your password |

---

## What to Learn Next (not yet in project)

| Topic | What it adds |
|-------|-------------|
| **Refresh Tokens** | Get a new access token without logging in again |
| **Logout / Token Blacklist** | Invalidate a JWT before it expires |
| **@PreAuthorize with SpEL** | `@PreAuthorize("hasRole('ADMIN') or #id == principal.id")` |
| **OAuth2 / Google Login** | "Login with Google" button |
| **Method-level security** | Protect individual service methods, not just controllers |
| **Rate Limiting** | Limit login attempts to prevent brute force |
