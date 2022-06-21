package org.payconiq.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Secret {
    @SerializedName("token")
    @Expose
    private String token;
}
