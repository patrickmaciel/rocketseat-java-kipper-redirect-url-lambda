package info.patrickmaciel;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {
  private final S3Client s3Client = S3Client.builder().build();
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Map<String, Object> handleRequest(Map<String, Object> stringObjectMap, Context context) {
    String pathParameters = (String) stringObjectMap.get("rawPath");
    String shortUrlCode = pathParameters.replace("/", "");

    if (shortUrlCode.isEmpty()) {
      throw new IllegalArgumentException("Short URL code is required");
    }

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket("url-shortener-storage-patrickmaciel")
        .key(shortUrlCode + ".json")
        .build();

    // stream, pega de pacote em pacote, até chegar tudo e se transformar no arquivo final
    InputStream s3ObjectStream;
    try {
      s3ObjectStream = s3Client.getObject(getObjectRequest);
    } catch (Exception e) {
      throw new RuntimeException("Error getting data from S3", e);
    }

    UrlData urlData;
    try {
      urlData = objectMapper.readValue(s3ObjectStream, UrlData.class);
    } catch (Exception e) {
      throw new RuntimeException("Error parsing data", e);
    }

    long currentTimeInSeconds = System.currentTimeMillis() / 1000;

    Map<String, Object> response = new HashMap<>();

    if (urlData.getExpirationTimeInSeconds() < currentTimeInSeconds) {
      response.put("statusCode", 410);
      response.put("body", "URL expired");
      return response;
    }

    response.put("statusCode", 302);
    Map<String, String> headers = new HashMap<>();
    headers.put("Location", urlData.getOriginalUrl());
    response.put("headers", headers);

    return response;
  }
}