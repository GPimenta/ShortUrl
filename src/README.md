# URL Shortener

A small Scala 3 + Pekko HTTP service that converts long URLs into short, shareable codes — similar to bit.ly or tinyurl.com.

## How it works

- `POST /shorten` with a JSON body `{"url": "https://example.com"}` — returns a short code and full short URL
- `GET /{code}` — redirects (HTTP 302) to the original URL

## Running it
- `sbt run`
- `curl -X POST localhost:8080/shorten -H "Content-Type: application/json" -d '{"url": "https://example.com"}'`
- `curl -v localhost:8080/<code-from-response>`


## Architecture

Controller → Service → Repository:

- **Controller** (`Routes`) exposes the HTTP endpoints
- **Service** (`UrlShortenerService`) holds the business logic: validating URLs, generating codes, coordinating the repository
- **Repository** (`UrlRepository`) in-memory implementation backing it for this project

## Decisions & Trade-offs

- **Counter + base62 encoding, instead of random codes**: every request gets a unique id from an atomic counter, which is then base62-encoded into a short code. This guarantees codes never collide by construction, without needing a retry loop. The trade-off: codes are sequential/guessable, which a random-string approach avoids at the cost of needing collision detection and retries.
- **`CodeCollision` as a defensive guard**: the repository still checks for and rejects code collisions on save, even though — given the counter-based id strategy — a collision can't actually happen through normal usage. 
- **In-memory storage, not a real database**: chosen for simplicity within the project's scope. `UrlRepository` is defined as a trait specifically so a Postgres- or Redis-backed implementation could be swapped in later without touching the service layer.
- **Errors as values (`Either`), not exceptions**: domain failures (`InvalidURL`, `CodeCollision`, `NotFound`) are modeled as a sealed type and threaded through as `Either` values end-to-end, rather than thrown exceptions — making failure cases explicit in method signatures and forcing callers to handle them.