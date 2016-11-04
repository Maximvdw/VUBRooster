package be.vubrooster.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.Map.Entry;

public class HtmlUtils {
    /**
     * User Agent
     */
    private final static String USER_AGENT = "Mozilla/5.0";

    /**
     * Get the body contents of an url
     *
     * @param url URL Link
     * @return String with the body
     * @throws IOException
     */
    public static String getHtmlSource(String url) throws IOException {
        // The URL address of the page to open.
        URL address = new URL(url);

        // Open the address and create a BufferedReader with the source code.
        InputStreamReader pageInput = new InputStreamReader(address.openStream());
        BufferedReader source = new BufferedReader(pageInput);

        return source.readLine();
    }

    /**
     * Download file from the internet
     *
     * @param url  url to download from
     * @param file file to save it to
     * @return saved file
     * @throws IOException
     */
    public static File downloadFile(String url, File file) throws IOException {
        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        return file;
    }

    public static HtmlResponse sendGetRequest(String url, Map<String, String> inputcookies, int timeout)
            throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setReadTimeout(timeout);
        con.setConnectTimeout(timeout);
        if (inputcookies != null) {
            String cookieString = "";
            for (Entry<String, String> cookieEntry : inputcookies.entrySet()) {
                cookieString += cookieEntry.getKey() + "=" + cookieEntry.getValue() + ";";
            }
            con.setRequestProperty("Cookie", cookieString);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        String[] cookies = new String[0];
        if (con.getHeaderField("Set-Cookie") != null)
            cookies = con.getHeaderField("Set-Cookie").split(";");

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new HtmlResponse(response.toString(), con.getResponseCode(), cookies);
    }

    /**
     * Send a get request
     *
     * @param url Url
     * @return Response
     * @throws Exception Exception
     */
    public static HtmlResponse sendGetRequest(String url, Map<String, String> inputcookies) throws Exception {
        return sendGetRequest(url, inputcookies, 2500);
    }

    /**
     * Send post request
     *
     * @param url    Url
     * @param params Params
     * @return Response
     * @throws Exception Exception
     */
    public static HtmlResponse sendPostRequest(String url, Map<String, String> params, String[] inputcookies)
            throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "";
        for (String key : params.keySet()) {
            String value = params.get(key);
            urlParameters += key + "=" + value + "&";
        }
        urlParameters = urlParameters.substring(0, urlParameters.length() - 1);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        String[] cookies = new String[0];
        if (con.getHeaderField("Set-Cookie") != null)
            cookies = con.getHeaderField("Set-Cookie").split(";");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new HtmlResponse(response.toString(), con.getResponseCode(), cookies);

    }

    public static String getIPFromHost(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return (address.getHostAddress());
        } catch (Exception ex) {

        }
        return "0.0.0.0";
    }
}
