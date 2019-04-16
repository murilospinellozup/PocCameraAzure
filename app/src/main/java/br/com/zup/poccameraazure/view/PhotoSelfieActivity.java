package br.com.zup.poccameraazure.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import br.com.zup.poccameraazure.BuildConfig;
import br.com.zup.poccameraazure.R;
import br.com.zup.poccameraazure.common.Common;
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

public class PhotoSelfieActivity extends AppCompatActivity {

    private static final String LOG_TAG = "FACE API";
    private static final int PHOTO_REQUEST = 10;
    private static final int PHOTO_REQUEST_ONE = 101;
    private static final int PHOTO_REQUEST_TWO = 102;
    private String localArquivoFoto;
    private static final int TIRA_FOTO = 123;
    private boolean fotoResource = false;
    private TextView scanResults;
    private ImageView imgPersonOne;
    private ProgressBar progressBar;
    private Button btnProcess;
    private Button btnPersonOne;
    private Uri imageUri;
    private FaceDetector detector;
    private IAzureService serviceApi;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_BITMAP = "bitmap";
    private static final String SAVED_INSTANCE_RESULT = "result";
    public static final String URL = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0/detect";
    Bitmap editedBitmap;

    ImageView imageView;
    LinearLayout layout_browsefile;
    Bitmap bitmap;
    private static final int REQUEST_CODE_READ_EXTERNAL_PERMISSION = 2;
    String image_name;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie);
        btnPersonOne = findViewById(R.id.btnPersonOne);
        btnProcess = findViewById(R.id.btnProcess);
        progressBar = findViewById(R.id.progressBar);
        scanResults = findViewById(R.id.results);
        imgPersonOne = findViewById(R.id.imgPersonOne);

        Retrofit retrofit = RetrofitClient.getInstance();
        serviceApi = retrofit.create(IAzureService.class);

//        if (savedInstanceState != null) {
//            editedBitmap = savedInstanceState.getParcelable(SAVED_INSTANCE_BITMAP);
//            if (savedInstanceState.getString(SAVED_INSTANCE_URI) != null) {
//                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
//            }
//            imgPersonOne.setImageBitmap(editedBitmap);
//            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
//        }

        detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        btnPersonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });


        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
                fetchPath(data);
                launchMediaScanIntent();
                scanFaces();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Leitura da imagem falhou.", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, e.toString());
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading Image...");
        pd.setCancelable(false);
        pd.show();
        try {
            InputStream in = new FileInputStream(new File(localArquivoFoto));
            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            RequestBody requestBody = RequestBody
                    .create(MediaType.parse("application/octet-stream"), buf);
            Call<FaceResponse> call = serviceApi.postEventPhoto(requestBody);
            call.enqueue(new Callback<FaceResponse>() {
                @Override
                public void onResponse(Call<FaceResponse> call, Response<FaceResponse> response) {
                    pd.dismiss();
                    if (response.isSuccessful()) {
                        Common.faceId2 = response.body().getFaceId();
                        Toast.makeText(PhotoSelfieActivity.this, response.body().getFaceId(), Toast.LENGTH_SHORT).show();
                        showMain();
                    } else {
                        Toast.makeText(PhotoSelfieActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        Common.faceId2 = response.message();
                        showMain();
                    }
                }

                @Override
                public void onFailure(Call<FaceResponse> call, Throwable t) {
                    Toast.makeText(PhotoSelfieActivity.this, "Fallha " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void carregaImagem(String localArquivoFoto) {
        if (localArquivoFoto != null) {
            Bitmap imagemFoto = BitmapFactory.decodeFile(localArquivoFoto);
            imgPersonOne.setImageBitmap(imagemFoto);
            imgPersonOne.setTag(localArquivoFoto);
        }
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    private void uploadImage(byte[] imageBytes) {

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), convertImageViewToByteArray(imgPersonOne));
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

        Call<FaceResponse> call = serviceApi.uploadImage(body);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<FaceResponse>() {
            @Override
            public void onResponse(Call<FaceResponse> call, Response<FaceResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Common.faceId2 = response.body().getFaceId();
                    Toast.makeText(PhotoSelfieActivity.this, response.body().getFaceId(), Toast.LENGTH_SHORT).show();
                    showMain();
                } else {
                    Toast.makeText(PhotoSelfieActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Common.faceId2 = response.message();
                    showMain();
                }
            }

            @Override
            public void onFailure(Call<FaceResponse> call, Throwable t) {
                Toast.makeText(PhotoSelfieActivity.this, "Fallha " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchPath(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        imgPersonOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgPersonOne.setImageBitmap(photo);
        // Chame este método pra obter a URI da imagem
        Uri uri = getImageUri(getApplicationContext(), photo);
        // Em seguida chame este método para obter o caminho do arquivo
        File file = new File(getRealPathFromURI(uri));
        localArquivoFoto = file.getPath();
    }

    private void scanFaces() throws Exception {
        Bitmap bitmap = decodeBitmapUri(this, imageUri);
        if (detector.isOperational() && bitmap != null) {
            editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(255, 61, 61));
            paint.setTextSize((int) (14 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3f);
            Canvas canvas = new Canvas(editedBitmap);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
            SparseArray<Face> faces = detector.detect(frame);
            scanResults.setText(null);
            for (int index = 0; index < faces.size(); ++index) {
                Face face = faces.valueAt(index);
                canvas.drawRect(
                        face.getPosition().x,
                        face.getPosition().y,
                        face.getPosition().x + face.getWidth(),
                        face.getPosition().y + face.getHeight(), paint);
                if (imgPersonOne != null) {
                    if (face.getIsSmilingProbability() >= 0.4) {
                        scanResults.setText(scanResults.getText() + "SORRIDENTE" + "\n");
//                    tvTitle.setText("Agora tire foto com olho esquerdo fechado");
                    } else {
                        scanResults.setText(scanResults.getText() + "SÉRIO" + "\n");
                    }
                    if (face.getIsLeftEyeOpenProbability() >= 0.4) {
                        scanResults.setText(scanResults.getText() + "Olho direito aberto" + "\n");
                    } else {
                        scanResults.setText(scanResults.getText() + "Olho direito fechado" + "\n");
                    }
                    if (face.getIsRightEyeOpenProbability() >= 0.4) {
                        scanResults.setText(scanResults.getText() + "Olho esquerdo aberto" + "\n");
                    } else {
                        scanResults.setText(scanResults.getText() + "Olho esquerdo fechado" + "\n");
                    }
                }

                for (Landmark landmark : face.getLandmarks()) {
                    int cx = (int) (landmark.getPosition().x);
                    int cy = (int) (landmark.getPosition().y);
                    canvas.drawCircle(cx, cy, 5, paint);
                }
            }

            if (faces.size() == 0) {
                scanResults.setText("Scan Failed: Found nothing to scan");
            } else {
                imgPersonOne.setImageBitmap(editedBitmap);
                scanResults.setText(scanResults.getText() + "No of Faces Detected: " + "\n");
                scanResults.setText(scanResults.getText() + String.valueOf(faces.size()) + "\n");
                scanResults.setText(scanResults.getText() + "---------" + "\n");
            }
        } else {
            scanResults.setText("Could not set up the detector!");
        }
    }


    public byte[] convertImageViewToByteArray(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(PhotoSelfieActivity.this, BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    private void requestBody() {

        progressBar.setVisibility(View.VISIBLE);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), "foto");
        RequestBody descricao = RequestBody.create(MediaType.parse("text/plain"), "request");

        Call<FaceResponse> call = serviceApi.upload(requestBody, descricao);

        call.enqueue(new Callback<FaceResponse>() {
            @Override
            public void onResponse(Call<FaceResponse> call, Response<FaceResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Common.faceId2 = response.body().getFaceId();
                    Toast.makeText(PhotoSelfieActivity.this, response.body().getFaceId(), Toast.LENGTH_SHORT).show();
                    showMain();
                } else {
                    Toast.makeText(PhotoSelfieActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Common.faceId2 = response.message();
                    showMain();
                }

            }

            @Override
            public void onFailure(Call<FaceResponse> call, Throwable t) {
                Toast.makeText(PhotoSelfieActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Common.faceId2 = t.getMessage();
                showMain();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putParcelable(SAVED_INSTANCE_BITMAP, editedBitmap);
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.release();
    }

    private void showMain() {
        startActivity(new Intent(this, ChoicePhotActivity.class));
        finish();
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }
}
