package jonahkh.tacoma.uw.edu.fitnesstracker.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jonah on 4/22/2018.
 */
public class RestClient {
    public static String runRequest(String requestMethod, String requestBody, String... urls) {
        String response = "";
        HttpURLConnection urlConnection = null;
        for (String url : urls) {
            try {
                URL urlObject = new URL(url);

                urlConnection = (HttpURLConnection) urlObject.openConnection();
                urlConnection.setRequestMethod(requestMethod);
                if (StringUtils.isNotEmpty(requestBody)) {
                    urlConnection.setRequestProperty("content-type", "application/json");
                    try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                        wr.write(requestBody.getBytes());
                    }
                }
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (Exception e) {
                response = "Network connection unavailable, please try again later";
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
        return response;
    }

}
