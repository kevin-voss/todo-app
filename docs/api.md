# Todo API Reference

Base URL: `http://localhost:8080` (or `VITE_API_URL` in frontend)

## Endpoints

### List todos

```
GET /api/todos
```

**Response:** `200 OK` — Array of todos

```json
[
  { "id": 1, "title": "First todo", "completed": false },
  { "id": 2, "title": "Second todo", "completed": true }
]
```

### Get one todo

```
GET /api/todos/{id}
```

**Response:** `200 OK` — Todo object  
**Error:** `404 Not Found` — id not found

### Create todo

```
POST /api/todos
Content-Type: application/json

{ "title": "New todo" }
```

**Response:** `201 Created` — Created todo with `id`, `title`, `completed: false`  
**Error:** `400 Bad Request` — empty or whitespace-only title

### Update todo

```
PUT /api/todos/{id}
Content-Type: application/json

{ "title": "Updated", "completed": true }
```

**Response:** `200 OK` — Updated todo  
**Error:** `404 Not Found` — id not found

### Delete todo

```
DELETE /api/todos/{id}
```

**Response:** `204 No Content`  
**Error:** `404 Not Found` — id not found
