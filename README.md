## Pre-requisites

This project requires
 
* JDK 1.8 or higher.
* Selenium 3.14.0

## Working with the Grid-acolyte.

Currently the following servlets are available:

### At Hub Level

1. `com.rationale.emotions.servlets.hub.ListNodes` - This servlet when injected at the Hub would get wired in and is accessible via the URL `http://localhost:4444/grid/admin/ListNodes` where `localhost` is the server on which the Hub is running and is listening on port `4444`

When invoked with a `GET` or a `POST` the below response is obtained:

```json
{
  "nodes": [
    {
      "ip": "192.168.1.4",
      "port": 34350,
      "sessions": {
        "busy": 0,
        "free": 11,
        "total": 11
      }
    }
  ],
  "success": true
}
```