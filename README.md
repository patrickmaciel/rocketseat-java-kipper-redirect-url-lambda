# Rocketseat Java Course

- **Instructor**: Fernanda Kipper
- **Project**: Create URL Shortner and Redirect URL using AWS Lambda

## Funcionalities

### To create an URL - POST /lambda-url

**Payload**

```json
{
	"originalUrl": "https://your-url.com",
	"expirationTime": "1732981301"
}
```

To generate an expiration time, you can use the following command:

```bash
date -d "2024-12-31 23:59:59" +%s
```

Or access https://www.epochconverter.com/ website.

**Response**

```json
{
	"code": "code-generated-automatically"
}
```

 ### To get the URL and be redirected - GET /lambda-url/code-generated-automatically

**Response**

Redirect to the original URL, with status 302