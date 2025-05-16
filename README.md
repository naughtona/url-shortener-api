## URL Shortener API

This is a simple URL shortener API. It allows users to shorten long URLs and redirect to the original URL when the
shortened version is provided.

## How to run locally

1. Run the application using the following commands from the root directory of the project:

```bash
gradle build
gradle bootRun
```

2. Pull up Postman (desktop application)
3. Set the route address to: `http://localhost:9085/graphql`
4. Hit the GraphQL endpoints as follows:
    - **Shorten a URL**
      - **Endpoint**: `operation name -> shortenUrl`
      - **Request Body**:
      ```graphql
      mutation {
        shortenUrl(longUrl: "https://www.originenergy.com.au/electricity-gas/plans.html") {
          shortUrl
        }
      }
      ```
      - **Response**:
      ```json
      {
        "data": {
          "shortenUrl": {
            "shortUrl": "https://short.ly/767BUp"
          }
        }
      }
      ```
    - **Get the original URL**
      - **Endpoint**: `operation name -> resolveUrl`
      - **Request Body**:
      ```graphql
      query {
        resolveUrl(urlWithShortKey: "https://short.ly/767BUp") {
          longUrl
        }
      }
      ```
      - **Response**:
      ```json
      {
        "data": {
          "resolveUrl": {
            "longUrl": "https://www.originenergy.com.au/electricity-gas/plans.html"
          }
        }
      }
      ```
      
## Test cases

- Error handling for invalid URLs

- Invalid request to shorten a URL
  - **Request Body**:
  ```graphql
  mutation {
    shortenUrl(longUrl: "invalid-url") {
      shortUrl
    }
  }
  ```
  - **Response**:
  ```json
  {
    "errors": [
        {
            "message": "org.springframework.web.server.ResponseStatusException: 400 BAD_REQUEST \"Invalid Long URL format\"",
            "locations": [],
            "path": [
                "shortenUrl"
            ],
            "extensions": {
                "errorType": "INTERNAL"
            }
        }
    ],
    "data": null
  }
  ```
  
- Invalid request to resolve a URL
  - **Request Body**:
  ```graphql
  query {
    resolveUrl(urlWithShortKey: "invalid-url") {
      longUrl
    }
  }
  ```
  - **Response**:
  ```json
  {
    "errors": [
      {
          "message": "org.springframework.web.server.ResponseStatusException: 400 BAD_REQUEST \"Invalid Short URL format\"",
          "locations": [],
          "path": [
              "resolveUrl"
          ],
          "extensions": {
              "errorType": "INTERNAL"
          }
      }
    ],
    "data": null
  }
  ```

More test cases are located in the `src/test` directory. The test cases are written using JUnit 5 and Mockito.

## Constraints
- Use an in-memory store for simplicity
- URL codes should be unique, short, and non-sequential
- Validate the incoming URL format
- Error handling (404 for not found, 400 for invalid input etc.)
- Programming Language – Java/Kotlin
- Framework – Spring Ecosystem (Spring Boot, Spring Data JPA etc)