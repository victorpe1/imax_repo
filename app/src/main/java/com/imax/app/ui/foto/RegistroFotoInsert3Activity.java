package com.imax.app.ui.foto;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.api.request.FotoRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.AsignacionModel;
import com.imax.app.ui.adapters.FilesAdapter;
import com.imax.app.utils.Constants;
import com.imax.app.utils.MyDetailDialog;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class RegistroFotoInsert3Activity extends AppCompatActivity implements FilesAdapter.OnFileRemoveListener{
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_REGISTRO = 1;

    public static final String EXTRA_DIRECCION = "direccion";

    public String idTipoDocumentoOriginal = "";

    private boolean cabeceraGuardada = false;

    private ProgressDialog progressDialog;
    private ViewPager mViewPager;
    private String numeroDocumentoValidado;

    private DAOExtras daoExtras;

    private Button btnTakePhoto1;
    private Button btnTakePhoto2;
    private ImageView imgPreview1;
    private ImageView imgPreview2;
    private Uri imageUri;
    private static final int PICK_FILES_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MAX_PIXELS = 8 * 1000000; // 8 MB
    private int currentImageIndex = -1; // -1 indica que no hay imagen seleccionada
    private TextView tvFileName;
    private TextView tvImageSize;
    private Drawable defaultBackground;
    private Calendar calendar;
    private App app;
    private android.app.AlertDialog.Builder dialogBuilder;
    private android.app.AlertDialog dialog;

    Button btnOpenMaps;
    private ProgressBar progress;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<AsignacionModel> lista = new ArrayList<>();

    private Button btnBrowseFiles;
    private RecyclerView recyclerFiles;
    private FilesAdapter filesAdapter;

    private String[] imagePaths = new String[6];
    private List<String> fileMIMEList = new ArrayList<>();
    private List<String> filesNameList = new ArrayList<>();
    private List<String> filePaths = new ArrayList<>();

    private String numAsignacion = "";
    private LinearLayout filesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_foto_3);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.nuevo_registro), true, this, R.drawable.ic_action_back);
        progress = findViewById(R.id.progress);
        dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());

        app = (App) getApplicationContext();
        calendar = Calendar.getInstance();
        daoExtras = new DAOExtras(getApplicationContext());

        btnTakePhoto1 = findViewById(R.id.btnTakePhoto1);
        imgPreview1 = findViewById(R.id.imgPreview1);
        btnTakePhoto2 = findViewById(R.id.btnTakePhoto2);
        imgPreview2 = findViewById(R.id.imgPreview2);

        btnBrowseFiles = findViewById(R.id.btn_browse_files);
        recyclerFiles = findViewById(R.id.recycler_files);
        filesContainer = findViewById(R.id.files_container);

        imgPreview1.setVisibility(View.GONE);
        imgPreview2.setVisibility(View.GONE);

        numAsignacion = (String) getIntent().getSerializableExtra("numAsignacion");
        if(!numAsignacion.trim().isEmpty()){
            FotoRequest fotoRequest =  daoExtras.getListFotoByNumero(numAsignacion);
            if (fotoRequest.getFotosArrayAdjunto3() != null && !fotoRequest.getFotosArrayAdjunto3().isEmpty()) {
                fileMIMEList.clear();
                filesNameList.clear();
                filePaths.clear();
                for (String file : fotoRequest.getFotosArrayAdjunto3()) {
                    int nameStart = file.indexOf("name=") + 5;
                    int nameEnd = file.indexOf(";index=");

                    String fileName = file.substring(nameStart, nameEnd);

                    filesNameList.add(fileName);
                    fileMIMEList.add(file);
                    filePaths.add("Rutas");
                }
            }
            if (fotoRequest.getFotoArray3() != null && !fotoRequest.getFotoArray3().isEmpty()) {
                for (String file : fotoRequest.getFotoArray3()) {
                    System.out.println("file -> " + file);
                    if (file == null || file.isEmpty()) {
                        continue;
                    }

                    int filePathStart = file.indexOf(";file,") + 6;
                    int nameStart = file.indexOf("name=") + 5;
                    int nameEnd = file.indexOf(";index=");
                    int indexStart = file.indexOf("index=") + 6;
                    int indexEnd = file.indexOf(";file,");

                    String fileName = file.substring(nameStart, nameEnd);
                    int fileIndex = Integer.parseInt(file.substring(indexStart, indexEnd));
                    String filePath = file.substring(filePathStart);
                    Uri imageUri = Uri.fromFile(new File(filePath));

                    imagePaths[fileIndex] = file;
                    setImageWhileIndex(fileIndex, imageUri);

                    System.out.println("Nombre del archivo: " + fileName);
                    System.out.println("Ruta del archivo: " + filePath);
                }
            }
            showFiles();
        }

        btnTakePhoto1.setOnClickListener(v -> {
            setCurrentImageIndex(0);
            openCamera();
        });
        btnTakePhoto2.setOnClickListener(v -> {
            setCurrentImageIndex(1);
            openCamera();
        });


        filesNameList = new ArrayList<>();

        filesAdapter = new FilesAdapter(filesNameList, this);
        recyclerFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiles.setAdapter(filesAdapter);

        btnBrowseFiles.setOnClickListener(v -> openFilePicker());

        progressDialog = new ProgressDialog(RegistroFotoInsert3Activity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

    }

    private Bitmap decodeBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void setImageWhileIndex(int currentImageIndex, Uri imageBitmap){
        switch (currentImageIndex) {
            case 0:
                imgPreview1.setImageURI(imageBitmap);
                imgPreview1.setVisibility(View.VISIBLE);
                btnTakePhoto1.setText("Actualizar");
                break;
            case 1:
                imgPreview2.setImageURI(imageBitmap);
                imgPreview2.setVisibility(View.VISIBLE);
                btnTakePhoto2.setText("Actualizar");
                break;
            default:
                Toast.makeText(this, "Por favor, selecciona una imagen para actualizar", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    void showAlertMaxMB(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_dialog_alert);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setCancelable(false);
        builder.setNegativeButton("ACEPTAR", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void removeFile(int index) {
        filesNameList.remove(index);
        fileMIMEList.remove(index);

        showFiles();
    }

    private void showFiles() {
        filesContainer.removeAllViews();
        for (int i = 0; i < filesNameList.size(); i++) {
            final int index = i;

            View fileItemView = LayoutInflater.from(this).inflate(R.layout.item_cliente, filesContainer, false);
            TextView tvFileName = fileItemView.findViewById(R.id.tv_file_name);
            tvFileName.setText(filesNameList.get(i));

            ImageView imgDelete = fileItemView.findViewById(R.id.img_delete);
            imgDelete.setOnClickListener(v -> {
                removeFile(index);
            });

            filesContainer.addView(fileItemView);
        }
    }

    private void setCurrentImageIndex(int index) {
        this.currentImageIndex = index;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivos"), PICK_FILES_REQUEST);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILES_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                filesContainer.removeAllViews();
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        String fileName = getFileName(fileUri);
                        String mimeType = getMimeType(fileUri.toString());

                        String filePath = saveFileToInternalStorage(fileUri, fileName);
                        if (filePath != null) {
                            filePaths.add(filePath);
                            String combinedItem = "data:" + mimeType + ";name=" + fileName + ";index=" + i + ";file," + filePath;
                            fileMIMEList.add(combinedItem);
                        }
                        filesNameList.add(fileName);
                    }
                } else if (data.getData() != null) {
                    Uri fileUri = data.getData();
                    String fileName = getFileName(fileUri);
                    String mimeType = getMimeType(fileUri.toString());

                    String filePath = saveFileToInternalStorage(fileUri, fileName);
                    if (filePath != null) {
                        filePaths.add(filePath);
                        String combinedItem = "data:" + mimeType + ";name=" + fileName + ";index=0;file," + filePath;
                        fileMIMEList.add(combinedItem);
                    }
                    filesNameList.add(fileName);
                }

                if (!filesNameList.isEmpty()) {
                    recyclerFiles.setVisibility(View.VISIBLE);
                }
                filesAdapter.notifyDataSetChanged();
            }

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (imageBitmap != null) {
                int width = imageBitmap.getWidth();
                int height = imageBitmap.getHeight();
                int totalPixels = width * height;

                if (totalPixels > MAX_PIXELS) {
                    Toast.makeText(this, "La imagen no debe superar los 8 MP", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();

                        String filePath = photoFile.getAbsolutePath();
                        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());

                        String combinedItem = "data:" + mimeType + ";name=" + photoFile.getName() + ";index=" +
                                currentImageIndex + ";file," + filePath;

                        imagePaths[currentImageIndex] = combinedItem;

                        imageUri = Uri.fromFile(photoFile);
                        setImageWhileIndex(currentImageIndex, imageUri);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    private static String getMimeType(String fileName) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static List<String> combineBase64AndFileNames(List<String> fileBase64List, List<String> filesList) {
        List<String> combined = new ArrayList<>();

        for (int i = 0; i < fileBase64List.size(); i++) {
            String base64 = fileBase64List.get(i);
            String fileName = filesList.get(i);

            String mimeType = getMimeType(fileName);

            String combinedItem = "data:" + mimeType + ";name=" + fileName + ";index=" + i + ";base64," + base64;
            combined.add(combinedItem);
        }
        return combined;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_pedido, menu);
        menu.findItem(R.id.menu_pedido_siguiente).setVisible(true);
        menu.findItem(R.id.menu_pedido_guardar).setVisible(false);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_pedido_siguiente:
                FotoRequest fotoRequest = new FotoRequest();
                fotoRequest.setNumInspeccion(numAsignacion);
                fotoRequest.setFotoArray3(Arrays.asList(imagePaths));
                fotoRequest.setFotosArrayAdjunto3(fileMIMEList);

                System.out.println("imagePaths: " + imagePaths);

                daoExtras.actualizarRegistroInpeccionFoto3(fotoRequest);

                Intent intent = new Intent(this, RegistroFotoInsert4Activity.class);
                intent.putExtra("numAsignacion", fotoRequest.getNumInspeccion());
                startActivity(intent);
                break;
            case R.id.menu_pedido_guardar:
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void noPermitirCerrar() {
        Util.actualizarToolBar("", false, this);
    }

    void showAlertError(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_dialog_error);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.aceptar), (dialog, which) -> finish());
        builder.show();
    }

    private String saveFileToInternalStorage(Uri fileUri, String fileName) {
        try {
            File directory = new File(getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdir();
            }

            File file = new File(directory, fileName);

            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String fileName = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            cursor.close();
        }
        return fileName;
    }

    @Override
    public void onFileRemoved(int position) {
        filesNameList.remove(position);
        filePaths.remove(position);
        filesAdapter.notifyItemRemoved(position);

        if (filesNameList.isEmpty()) {
            recyclerFiles.setVisibility(View.GONE);
        }

        Toast.makeText(this, "Archivo eliminado", Toast.LENGTH_SHORT).show();
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

}