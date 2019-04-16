package br.com.zup.poccameraazure.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import br.com.zup.poccameraazure.R;
import br.com.zup.poccameraazure.common.Common;
import br.com.zup.poccameraazure.model.FaceResponse;
import br.com.zup.poccameraazure.retrofit.IAzureService;
import br.com.zup.poccameraazure.retrofit.RetrofitClient;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final int TIRA_FOTO = 123;
    static MainActivity instance;
    private IAzureService serviceApi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String localArquivoFoto;
    private Uri imageUri;
    private File file;
    private boolean fotoResource = false;
    private Button botaoFoto;
    private Button botaoEnviar;
    private ImageView imagemContato;
    private byte[] b;
    private String caminhoFoto;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.botaoFoto = findViewById(R.id.botaoFotoForm);
        this.botaoEnviar = findViewById(R.id.botaoEnviar);
        this.imagemContato = findViewById(R.id.imagemForm);
        progressBar = findViewById(R.id.progressBar);
        Retrofit retrofit = RetrofitClient.getInstance();
        serviceApi = retrofit.create(IAzureService.class);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregaFotoCamera();
            }
        });
        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

    }

    public void carregaFotoCamera() {
        fotoResource = true;
        localArquivoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
        Intent irParaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        irParaCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(localArquivoFoto)));
        startActivityForResult(irParaCamera, 123);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (!fotoResource) {
            if (resultCode == RESULT_OK
                    && null != data) {

                Uri imagemSel = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imagemSel,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                caminhoFoto = cursor.getString(columnIndex);
                cursor.close();

                carregaImagem(caminhoFoto);
            }

        } else {
            if (requestCode == TIRA_FOTO) {
                if (resultCode == Activity.RESULT_OK) {
                    carregaImagem(this.localArquivoFoto);
                } else {
                    this.localArquivoFoto = null;
                }
            }
        }
    }

    public void carregaImagem(String localArquivoFoto) {
        if (localArquivoFoto != null) {
            Bitmap imagemFoto = BitmapFactory.decodeFile(localArquivoFoto);
            imagemContato.setImageBitmap(imagemFoto);
            imagemContato.setTag(localArquivoFoto);
//            b = convertImageViewToByteArray(imagemContato);
        }
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
//        //Create a file object using file path
//        file = new File(localArquivoFoto);
//        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
//
//        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
//        RequestBody description = RequestBody.create(MediaType.parse("application/octet-stream"), "image-type");

        final byte[] byteArray = convertImageViewToByteArray(imagemContato);
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), byteArray);

        Call<FaceResponse> call = serviceApi.sendImageAzure(body);

        call.enqueue(new Callback<FaceResponse>() {
            @Override
            public void onResponse(Call<FaceResponse> call, Response<FaceResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "X" + response.body().getFaceId(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Veio nulo", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<FaceResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "X" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendData() {
        progressBar.setVisibility(View.VISIBLE);
        MultipartBody.Part imagenPerfil = null;
        RequestBody requestFile = null;
        if (localArquivoFoto != null) {
            File file = new File(localArquivoFoto);
            Log.i("Register", "Nombre del archivo " + file.getName());
            requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            imagenPerfil = MultipartBody.Part.createFormData("imagenPerfil", file.getName(), requestFile);
        }

        Call<FaceResponse> call = serviceApi.uploadAttachment(requestFile, imagenPerfil);
        call.enqueue(new Callback<FaceResponse>() {
            @Override
            public void onResponse(Call<FaceResponse> call, Response<FaceResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {

                    Toast.makeText(MainActivity.this, "X" + response.body().getFaceId(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Veio nulo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FaceResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void requestBody() {
        progressBar.setVisibility(View.VISIBLE);
        RequestBody requestBody = null;
        File file = new File(localArquivoFoto);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), Files.readAllBytes(file.toPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<FaceResponse> call = serviceApi.requestBody(requestBody);
        call.enqueue(new Callback<FaceResponse>() {
            @Override
            public void onResponse(Call<FaceResponse> call, Response<FaceResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "X" + response.body().getFaceId(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Veio nulo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FaceResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void requestBody2() {
        progressBar.setVisibility(View.VISIBLE);
        File filename = new File(localArquivoFoto);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody descricao = RequestBody.create(MediaType.parse("text/plain"), file);
        Call<FaceResponse> call = serviceApi.upload(requestBody, descricao);
        call.enqueue(new Callback<FaceResponse>() {
            @Override
            public void onResponse(Call<FaceResponse> call, Response<FaceResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Common.faceId1 = response.body().getFaceId();
                    Toast.makeText(MainActivity.this, response.body().getFaceId(), Toast.LENGTH_SHORT).show();
                    showMain();
                } else {
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Common.faceId1 = response.message();
                    showMain();
                }

            }

            @Override
            public void onFailure(Call<FaceResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Common.faceId1 = t.getMessage();
                showMain();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showMain() {
        startActivity(new Intent(this, ChoicePhotActivity.class));
        finish();
    }

    public byte[] convertImageViewToByteArray(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


}
