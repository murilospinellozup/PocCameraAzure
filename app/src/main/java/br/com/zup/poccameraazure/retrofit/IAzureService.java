package br.com.zup.poccameraazure.retrofit;


import br.com.zup.poccameraazure.model.FaceIdsRequest;
import br.com.zup.poccameraazure.model.FaceIdsResponse;
import br.com.zup.poccameraazure.model.FaceResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IAzureService {

    @Headers({"Ocp-Apim-Subscription-Key: 8cea123763c64d4a856783bb9f6f4388"})
    @Multipart
    @POST("detect")
    Call<FaceResponse> sendImageAzure(@Body RequestBody body);

    @Headers({"Content-Type: application/octet-stream","Ocp-Apim-Subscription-Key: 8cea123763c64d4a856783bb9f6f4388"})
    @Multipart
    @POST("detect")
    Call<FaceResponse> uploadAttachment(@Part("description") RequestBody description,@Part MultipartBody.Part imagenPerfil);

    @Headers({"Ocp-Apim-Subscription-Key: 8cea123763c64d4a856783bb9f6f4388",
            "Content-Type: multipart/form-data"})
    @POST("detect")
    Call<FaceResponse> requestBody(@Body RequestBody body);

    @Headers({"Content-Type: application/octet-stream","Ocp-Apim-Subscription-Key: 8cea123763c64d4a856783bb9f6f4388"})
    @Multipart
    @POST("detect")
    Call<FaceResponse> upload(@Part("file\"; filename=\"image.jpg\"") RequestBody file,
                                    @Part("descricao") RequestBody descricao);

    @Headers({"Content-Type: application/json",
            "Ocp-Apim-Subscription-Key: 8cea123763c64d4a856783bb9f6f4388"})
    @POST("verify")
    Call<FaceIdsResponse> verifyImage(@Body FaceIdsRequest verify);

    @Headers({"Ocp-Apim-Subscription-Key: 8cea123763c64d4a856783bb9f6f4388","Content-Type: application/octet-stream"})
    @Multipart
    @POST("detect")
    Call<FaceResponse> uploadImage(@Part MultipartBody.Part image);

    @Headers({"Ocp-Apim-Subscription-Key: 8cea123763c64d4a856783bb9f6f4388"})
    @POST("detect")
    Call<FaceResponse> postEventPhoto(@Body RequestBody photo);


}
