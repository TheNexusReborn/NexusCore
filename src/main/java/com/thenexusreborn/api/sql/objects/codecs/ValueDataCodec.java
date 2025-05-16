package com.thenexusreborn.api.sql.objects.codecs;

import com.google.gson.*;
import com.thenexusreborn.api.sql.objects.SqlCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ValueDataCodec implements SqlCodec<Map<String, String>> {
    @Override
    public String encode(Object object) {
        Map<String, String> data = (Map<String, String>) object;
        JsonObject dataObject = new JsonObject();
        data.forEach(dataObject::addProperty);
        return dataObject.toString();
        
//        StringBuilder sb = new StringBuilder();
//        data.forEach((key, value) -> sb.append(key).append("$-=").append(value).append("$-,"));
//        
//        if (!sb.isEmpty()) {
//            sb.deleteCharAt(sb.length() - 1);
//            sb.deleteCharAt(sb.length() - 1);
//            sb.deleteCharAt(sb.length() - 1);
//        }
//        
//        return sb.toString();
    }

    @Override
    public Map<String, String> decode(String encoded) {
        JsonParser jsonParser = new JsonParser();
        
        JsonObject rawData = jsonParser.parse(encoded).getAsJsonObject();
        Map<String, String> data = new HashMap<>();
        
        for (Entry<String, JsonElement> entry : rawData.entrySet()) {
            data.put(entry.getKey(), entry.getValue().getAsString());
        }
        
        return data;

//        String[] elements = encoded.split("\\$-,");
//        for (String element : elements) {
//            String[] keyValue = element.split("\\$-=");
//            if (keyValue.length != 2) {
//                if (keyValue.length > 0) {
//                    if (keyValue.length == 1) {
//                        if (keyValue[0].isEmpty()) {
//                            continue;
//                        }
//                    }
//                    System.out.println(keyValue.length + ": " + Arrays.toString(keyValue));
//                }
//            } else {
//                data.put(keyValue[0], keyValue[1]);
//            }
//        }
//        
//        return data;
    }
}
