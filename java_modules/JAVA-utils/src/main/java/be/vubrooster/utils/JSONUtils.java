package be.vubrooster.utils;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * JSONUtils
 * Created by maxim on 05-Oct-16.
 */
public class JSONUtils {
    public static String prettyPrint(JsonObject jobj) {
        StringWriter sw = new StringWriter();

        try {
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);

            JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
            JsonWriter jsonWriter = writerFactory.createWriter(sw);

            jsonWriter.writeObject(jobj);
            jsonWriter.close();
        } catch (Exception e) {
        }

        String prettyPrinted = sw.toString();
        return prettyPrinted;
    }

    public static String prettyPrint(String json) {
        try {
            JsonReader jr = Json.createReader(new StringReader(json));
            JsonObject jobj = jr.readObject();
            return prettyPrint(jobj);
        } catch (Exception e) {
        }
        return json;
    }
}
