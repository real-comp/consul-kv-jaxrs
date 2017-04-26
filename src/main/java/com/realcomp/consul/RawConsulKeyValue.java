package com.realcomp.consul;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class RawConsulKeyValue{

    private final Client jaxrs;
    private final String url;

    public RawConsulKeyValue(String url){
        Objects.requireNonNull(url);
        jaxrs = ClientBuilder.newClient();
        this.url = url;
    }

    public RawConsulKeyValue(String url, Client jaxrs){
        Objects.requireNonNull(url);
        Objects.requireNonNull(jaxrs);
        this.jaxrs = jaxrs;
        this.url = url;
    }

    public Optional<String> get(String key) throws IOException{
        Objects.requireNonNull(key);
        WebTarget target = jaxrs.target(url).path("v1/kv").path(key);
        Optional<String> json = Optional.empty();
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        try{
            switch (response.getStatus()){
                case 200:
                    json = Optional.ofNullable(response.readEntity(String.class));
                    break;
                case 404:
                    break;
                default:
                    throw new IOException(String.format("Unexpected response [%d] from [%s]", response.getStatus(), url));
            }
        }
        finally{
            response.close();
        }
        return json;
    }

    public void put(String key, String value) throws IOException{
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        WebTarget target = jaxrs.target(url).path("v1/kv").path(key);
        Optional<String> json = Optional.empty();
        Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.text(value));
        try{
            switch (response.getStatus()){
                case 200:
                    json = Optional.ofNullable(response.readEntity(String.class));
                    break;
                default:
                    throw new IOException(String.format("Unexpected response [%d] from [%s]", response.getStatus(), url));
            }
        }
        finally{
            response.close();
        }
        if (!json.isPresent() || !json.get().equalsIgnoreCase("true")){
            throw new IOException(
                    String.format("Response OK from [%s] for key [%s] = [%s], but value not set.", url, key, value));
        }
    }

    public void remove(String key) throws IOException{
        Objects.requireNonNull(key);
        WebTarget target = jaxrs.target(url).path("v1/kv").path(key);
        Optional<String> json = Optional.empty();
        Response response = target.request(MediaType.APPLICATION_JSON).delete();
        try{
            switch (response.getStatus()){
                case 200:
                    json = Optional.ofNullable(response.readEntity(String.class));
                    break;
                default:
                    throw new IOException(String.format("Unexpected response [%d] from [%s]", response.getStatus(), url));
            }
        }
        finally{
            response.close();
        }

        if (!json.isPresent() || !json.get().equalsIgnoreCase("true")){
            throw new IOException(
                    String.format("Response [%d] when attempting to remove key [%s]", response.getStatus(), key));
        }
    }

    public Set<String> keySet() throws IOException{
        WebTarget target = jaxrs.target(url).path("v1/kv/").queryParam("keys", "");
        Set<String> keys = new LinkedHashSet<>();
        Optional<String> json = Optional.empty();
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        try{
            switch (response.getStatus()){
                case 200:
                    json = Optional.ofNullable(response.readEntity(String.class));
                    break;
                default:
                    throw new IOException(String.format("Unexpected response [%d] from [%s]", response.getStatus(), url));
            }
        }
        finally{
            response.close();
        }

        keys.addAll(ConsulResponse.parseList(json));
        return keys;
    }

}
