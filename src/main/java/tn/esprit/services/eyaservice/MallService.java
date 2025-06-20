package tn.esprit.services.eyaservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import tn.esprit.entities.MallData;

import java.io.IOException;

public class MallService {
    private static final String API_URL = "https://api.jsonbin.io/v3/qs/67bf3e94acd3cb34a8f15055"; // Replace with your JSON URL or local file path

    public MallData getMallData() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("JSON Response: " + jsonResponse); // Debugging

                // Parse JSON response into a MallData object
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(jsonResponse, MallData.class);
            } else {
                throw new IOException("Failed to fetch mall data: " + response.getStatusLine().getReasonPhrase());
            }
        }
    }
}
