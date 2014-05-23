package sqlrodeo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class UrlRetriever {
    // Logger log = LoggerFactory.getLogger(UrlRetriever.class);

    public static String retrieveTextForUrl(URL url) throws IOException {
        StringBuilder sb = new StringBuilder();

        // read text returned by server
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {

            String line;
            while((line = in.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            in.close();
        }

        return sb.toString();
    }

    private UrlRetriever() {
    }
}
