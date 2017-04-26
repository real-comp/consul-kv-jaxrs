# consul-kv-jaxrs
A minimalistic Java client for the Consul Key/Value HTTP API

[Consul](https://www.consul.io) is fantastic. 
One nice feature of Consul is it's Key/Value store that is exposed with an [HTTP API](https://www.consul.io/api/kv.html).

This consul-kv-jaxrs client exposes only the Key/Value features of Consul via a JAX-RS client.
If you need access to the full functionality of the Consul HTTP API, this library is _not_ for you. 
There are already several Java Consul clients.  Here are two:
                                                                                               
* https://github.com/OrbitzWorldwide/consul-client
* https://github.com/Ecwid/consul-api 


## Example
```
ConsulKeyValue consul = new ConsulKeyValue("http://my-consul-server.com");
Optional<String> value = consul.get("foo");    
consul.put("foo", "bar");    
consul.remove("foo");    
Set<String> keys = consul.keySet();
```  

You can also provide your own JAX-RS client if needed.  
We use this to access a HA Consul server protected by HTTP BASIC auth from DropWizard services.
```     
//Authenticator is a simple JAX-RS ClientRequestFilter for BASIC HTTP Auth. 
Client jaxrs = ClientBuilder.newBuilder().register(new Authenticator("username","password")).build();
ConsulKeyValue consul = new ConsulKeyValue("http://my-consul-server.com", jaxrs);
```

If you are not storing Strings values, there is also a RawConsulKeyValue class that leaves the JSON 
parsing and Base64 decoding up to you.

Any unexpected response is packaged as an IOException.

## Dependencies
* Java 8
* JAX-RS 2.0.1 Client
* Jackson 2.8.7


## Maven
```xml
<dependency>
    <groupId>com.real-comp</groupId>
    <artifactId>consul-kv-jaxrs</artifactId>
    <version>0.0.1</version>
</dependency>
```
