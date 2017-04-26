package com.realcomp.consul;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Unmarshalls the JSON and Base64 encoded responses from the Consul server.
 */
public class ConsulResponse{

    private static final ObjectMapper JACKSON = new ObjectMapper();
    private static final TypeReference<List<Map<String, String>>> CONSUL_METADATA_TYPE
            = new TypeReference<List<Map<String, String>>>(){};
    private static final TypeReference<List<String>> CONSUL_KEYS_TYPE
            = new TypeReference<List<String>>(){};

    public static List<String> parseList(Optional<String> json) throws IOException{
        return json.isPresent() ? parseList(json.get()) : Collections.EMPTY_LIST;
    }

    public static List<String> parseList(String json) throws IOException{
        Objects.requireNonNull(json);
        return JACKSON.readValue(json, CONSUL_KEYS_TYPE);
    }

    public static Optional<String> parseSingleValue(Optional<String> json) throws IOException{
        return json.isPresent() ? parseSingleValue(json.get()) : Optional.empty();
    }

    public static Optional<String> parseSingleValue(String json) throws IOException{
        Objects.requireNonNull(json);
        List<Map<String,String>> metadata = JACKSON.readValue(json, CONSUL_METADATA_TYPE);
        if (metadata != null && metadata.size() == 1){
            return Optional.of(new String(
                    DatatypeConverter.parseBase64Binary(metadata.get(0).get("Value")), StandardCharsets.UTF_8));
        }
        else if (metadata != null && metadata.size() > 1){
            throw new IOException(
                    "There was more than one value for the specified key.  " +
                            "You likely queried for a key that has sub-keys.");
        }
        return Optional.empty();
    }
}
