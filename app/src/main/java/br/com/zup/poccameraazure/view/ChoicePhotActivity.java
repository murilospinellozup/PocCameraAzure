package br.com.zup.poccameraazure.view;

import androidx.appcompat.app.AppCompatActivity;
import br.com.zup.poccameraazure.R;
import br.com.zup.poccameraazure.common.Common;
import br.com.zup.poccameraazure.model.FaceIdsRequest;
import br.com.zup.poccameraazure.model.FaceIdsResponse;
import br.com.zup.poccameraazure.model.FaceResponse;
import br.com.zup.poccameraazure.retrofit.IAzureService;
import br.com.zup.poccameraazure.retrofit.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ChoicePhotActivity extends AppCompatActivity {

    private Button btFotoDocumento;
    private Button btSelfie;
    private Button btVerify;
    private TextView tvFaceId1;
    private TextView tvFaceId2;
    private TextView tvMessage;

    IAzureService serviceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_phot);

        btFotoDocumento = findViewById(R.id.btTirarFotoDocumento);
        btSelfie = findViewById(R.id.btSelfie);
        btVerify = findViewById(R.id.btVerify);
        tvFaceId1= findViewById(R.id.tvFaceId1);
        tvFaceId2= findViewById(R.id.tvFaceId2);
        tvMessage= findViewById(R.id.tvMessage);
        Retrofit retrofit = RetrofitClient.getInstance();
        serviceApi = retrofit.create(IAzureService.class);

        btFotoDocumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fotoDocumento();
            }
        });

        btSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fotoSelfie();
            }
        });

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });
        tvMessage.setText(Common.message);
        tvFaceId1.setText(Common.faceId1);
        tvFaceId2.setText(Common.faceId2);

    }

    private void fotoDocumento() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void fotoSelfie(){
        startActivity(new Intent(this, PhotoSelfieActivity.class));
    }

    private void verify(){

        FaceIdsRequest request = new FaceIdsRequest();
        request.setFaceId1("0ec13829-18a8-474c-afb5-d86b2381719c");
        request.setFaceId2("7fc0da55-9b80-4aef-8559-8429df66c473");

//        Call<FaceIdsResponse> call = serviceApi.verifyImage("82f375bc-0077-456e-98ac-77f6cf3dd262", "735a4dcd-6503-46bf-874a-e72cc24a5ec9");
                Call<FaceIdsResponse> call = serviceApi.verifyImage(request);

        call.enqueue(new Callback<FaceIdsResponse>() {
            @Override
            public void onResponse(Call<FaceIdsResponse> call, Response<FaceIdsResponse> response) {
                if (response.isSuccessful()){
                Toast.makeText(ChoicePhotActivity.this, "VERDADEIRO", Toast.LENGTH_SHORT).show();

                }else
                    Toast.makeText(ChoicePhotActivity.this, "Falso", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<FaceIdsResponse> call, Throwable t) {
                Toast.makeText(ChoicePhotActivity.this, "T "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
