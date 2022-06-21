package org.payconiq.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Booking {
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("totalprice")
    @Expose
    private Integer totalprice;
    @SerializedName("depositpaid")
    @Expose
    private Boolean depositpaid;
    @SerializedName("bookingdates")
    @Expose
    private Dates bookingdates;
    @SerializedName("additionalneeds")
    @Expose
    private String additionalneeds;
}
