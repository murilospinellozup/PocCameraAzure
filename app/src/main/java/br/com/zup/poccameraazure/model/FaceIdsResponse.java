package br.com.zup.poccameraazure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceIdsResponse {

    @SerializedName("isIdentical")
    private Boolean isIdentical;
    @SerializedName("confidence")
    private Integer confidence;

    public Boolean getIsIdentical() {
        return isIdentical;
    }

    public void setIsIdentical(Boolean isIdentical) {
        this.isIdentical = isIdentical;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }
}
