package com.realcomp.consul;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ConsulKeyValue{

    private final RawConsulKeyValue consul;

    public ConsulKeyValue(String url){
        Objects.requireNonNull(url);
        consul = new RawConsulKeyValue(url);
    }

    public ConsulKeyValue(String url, Client jaxrs){
        consul = new RawConsulKeyValue(url, jaxrs);
    }

    public Optional<String> get(String key) throws IOException{
        Optional<String> json = consul.get(key);
        return json.isPresent() ? ConsulResponse.parseSingleValue(json) : Optional.empty();
    }

    public void put(String key, String value) throws IOException{
        consul.put(key, value);
    }

    public void remove(String key) throws IOException{
        consul.remove(key);
    }
}
