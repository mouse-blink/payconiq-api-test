package org.payconiq.clients;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.payconiq.models.Booking;
import org.payconiq.models.BookingResource;
import org.payconiq.models.Credentials;
import org.payconiq.models.Secret;

public class RESTClient {

    private static Gson gson = new Gson();

    private String URL;

    private Credentials credentials;
    private String token;


    public RESTClient(String URL, Credentials credentials){
        this.URL = URL;
        this.credentials = credentials;
        Unirest.setTimeouts(0, 0);
        Unirest.setObjectMapper(new ObjectMapper() {
            private  Gson gson = new Gson();

            public <T> T readValue(String s, Class<T> aClass) {
                try{
                    return gson.fromJson(s, aClass);
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object o) {
                try{
                    return gson.toJson(o);
                }catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void authenticate() throws UnirestException {
        HttpResponse<Secret> response = Unirest.post("https://restful-booker.herokuapp.com/auth")
                .header("Content-Type", "application/json")
                .body(credentials).asObject(Secret.class);

//                .asString();
//        Secret secret = gson.fromJson(response.getBody(), );
        this.token=response.getBody().getToken();
    }

    public BookingResource createBooking(Booking booking) throws Exception {
        HttpResponse<String> response = Unirest.post(this.URL+"/booking")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token="+this.token)
                .body(gson.toJson(booking))
                .asString();
        if (response.getStatus()==200){
            return gson.fromJson(response.getBody(), BookingResource.class);
        } else {
            throw  new  Exception();
        }
    }
}
