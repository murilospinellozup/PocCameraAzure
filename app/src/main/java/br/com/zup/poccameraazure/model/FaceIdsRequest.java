package br.com.zup.poccameraazure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceIdsRequest {
    @SerializedName("faceId1")
    @Expose
    private String faceId1;
    @SerializedName("faceId2")
    @Expose
    private String faceId2;

    public String getFaceId1() {
        return faceId1;
    }

    public void setFaceId1(String faceId1) {
        this.faceId1 = faceId1;
    }

    public String getFaceId2() {
        return faceId2;
    }

    public void setFaceId2(String faceId2) {
        this.faceId2 = faceId2;
    }

}
