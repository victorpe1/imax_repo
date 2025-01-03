package com.imax.app.ui.foto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.imax.app.App;
import com.imax.app.BuildConfig;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class RegistroFotoInsertActivity extends AppCompatActivity implements FilesAdapter.OnFileRemoveListener{
    private final String TAG = getClass().getName();

    public String idTipoDocumentoOriginal = "";

    private ProgressDialog progressDialog;
    private ViewPager mViewPager;
    private String numeroDocumentoValidado;

    private DAOExtras daoExtras;

    private Spinner spnAsignarNumero;

    private Button btnTakePhoto1;
    private Button btnTakePhoto2;
    private Button btnTakePhoto3;
    private Button btnTakePhoto4;
    private Button btnTakePhoto5;
    private Button btnTakePhoto6;
    private ImageView imgPreview1;
    private ImageView imgPreview2;
    private ImageView imgPreview3;
    private ImageView imgPreview4;
    private ImageView imgPreview5;
    private ImageView imgPreview6;
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

    private Button btnBrowseFiles;
    private RecyclerView recyclerFiles;
    private FilesAdapter filesAdapter;

    private List<String> filePaths = new ArrayList<>();
    private List<String> fileMIMEList = new ArrayList<>();
    private List<String> filesNameList = new ArrayList<>();
    private ArrayList<AsignacionModel> lista = new ArrayList<>();

    private String[] imagePaths = new String[6];
    private LinearLayout filesContainer;
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_foto_1);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.nuevo_registro), true, this, R.drawable.ic_action_back);
        progress = findViewById(R.id.progress);
        dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());

        app = (App) getApplicationContext();
        calendar = Calendar.getInstance();
        daoExtras = new DAOExtras(getApplicationContext());

        spnAsignarNumero = findViewById(R.id.spn_asignarNumero);
        btnTakePhoto1 = findViewById(R.id.btnTakePhoto1);
        imgPreview1 = findViewById(R.id.imgPreview1);
        btnTakePhoto2 = findViewById(R.id.btnTakePhoto2);
        imgPreview2 = findViewById(R.id.imgPreview2);
        btnTakePhoto3 = findViewById(R.id.btnTakePhoto3);
        imgPreview3 = findViewById(R.id.imgPreview3);
        btnTakePhoto4 = findViewById(R.id.btnTakePhoto4);
        imgPreview4 = findViewById(R.id.imgPreview4);
        btnTakePhoto5 = findViewById(R.id.btnTakePhoto5);
        imgPreview5 = findViewById(R.id.imgPreview5);
        btnTakePhoto6 = findViewById(R.id.btnTakePhoto6);
        imgPreview6 = findViewById(R.id.imgPreview6);

        filesContainer = findViewById(R.id.files_container);

        imgPreview1.setVisibility(View.GONE);
        btnBrowseFiles = findViewById(R.id.btn_browse_files);

        recyclerFiles = findViewById(R.id.recycler_files);

        btnTakePhoto1.setOnClickListener(v -> {
            setCurrentImageIndex(0);
            openCamera();
        });
        btnTakePhoto2.setOnClickListener(v -> {
            setCurrentImageIndex(1);
            openCamera();
        });
        btnTakePhoto3.setOnClickListener(v -> {
            setCurrentImageIndex(2);
            openCamera();
        });
        btnTakePhoto4.setOnClickListener(v -> {
            setCurrentImageIndex(3);
            openCamera();
        });
        btnTakePhoto5.setOnClickListener(v -> {
            setCurrentImageIndex(4);
            openCamera();
        });
        btnTakePhoto6.setOnClickListener(v -> {
            setCurrentImageIndex(5);
            openCamera();
        });

        initBotones();

        new async_sincronizacion().execute();

        spnAsignarNumero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fileMIMEList.clear();
                filesNameList.clear();
                filePaths.clear();

                initBotones();
                loadFotorByNumber(lista.get(position).getNumber());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        filesNameList = new ArrayList<>();

        filesAdapter = new FilesAdapter(filesNameList, this);
        recyclerFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiles.setAdapter(filesAdapter);

        btnBrowseFiles.setOnClickListener(v -> openFilePicker());

        progressDialog = new ProgressDialog(RegistroFotoInsertActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

        showAlertMaxMB("ADVENTENCIA", "Solo se acepta fotograficas con maximo de 8MB, de otro modo configurar resolución.");
    }


    private void initBotones(){
        imgPreview1.setVisibility(View.GONE);
        btnTakePhoto1.setText("TOMAR FOTO");
        imgPreview2.setVisibility(View.GONE);
        btnTakePhoto2.setText("TOMAR FOTO");
        imgPreview3.setVisibility(View.GONE);
        btnTakePhoto3.setText("TOMAR FOTO");
        imgPreview4.setVisibility(View.GONE);
        btnTakePhoto4.setText("TOMAR FOTO");
        imgPreview5.setVisibility(View.GONE);
        btnTakePhoto5.setText("TOMAR FOTO");
        imgPreview6.setVisibility(View.GONE);
        btnTakePhoto6.setText("TOMAR FOTO");
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
            case 2:
                imgPreview3.setImageURI(imageBitmap);
                imgPreview3.setVisibility(View.VISIBLE);
                btnTakePhoto3.setText("Actualizar");
                break;
            case 3:
                imgPreview4.setImageURI(imageBitmap);
                imgPreview4.setVisibility(View.VISIBLE);
                btnTakePhoto4.setText("Actualizar");
                break;
            case 4:
                imgPreview5.setImageURI(imageBitmap);
                imgPreview5.setVisibility(View.VISIBLE);
                btnTakePhoto5.setText("Actualizar");
                break;
            case 5:
                imgPreview6.setImageURI(imageBitmap);
                imgPreview6.setVisibility(View.VISIBLE);
                btnTakePhoto6.setText("Actualizar");
                break;
            default:
                Toast.makeText(this, "Por favor, selecciona una imagen para actualizar", Toast.LENGTH_SHORT).show();
                break;
        }
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

    private void setCurrentImageIndex(int index) {
        this.currentImageIndex = index;
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivos"), PICK_FILES_REQUEST);
    }

    private void openCamera() {
        Uri photoURI;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                imageFile = createImageFile();
                if (imageFile != null) {
                    photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Error al crear el archivo de la imagen", Toast.LENGTH_SHORT).show();
            }
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
            if (imageFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

                int width = options.outWidth;
                int height = options.outHeight;
                int totalPixels = width * height;

                if (totalPixels > MAX_PIXELS) {
                    Toast.makeText(this, "La imagen no debe superar los 8 MP", Toast.LENGTH_SHORT).show();
                    if (imageFile.delete()) {
                        Toast.makeText(this, "Imagen eliminada debido a tamaño excesivo", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                try {
                    String filePath = imageFile.getAbsolutePath();
                    String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());

                    String combinedItem = "data:" + mimeType + ";name=" + imageFile.getName() + ";index=" +
                            currentImageIndex + ";file," + filePath;

                    imagePaths[currentImageIndex] = combinedItem;

                    imageUri = Uri.fromFile(imageFile);
                    setImageWhileIndex(currentImageIndex, imageUri);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "El archivo de imagen no existe", Toast.LENGTH_SHORT).show();
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

                if(verificarCampos()){
                    FotoRequest fotoRequest = new FotoRequest();
                    fotoRequest.setNumInspeccion(spnAsignarNumero.getSelectedItem().toString());
                    fotoRequest.setFotoArray1(Arrays.asList(imagePaths));
                    fotoRequest.setFotosArrayAdjunto1(fileMIMEList);

                    System.out.println("imagePaths: " + imagePaths);

                    if(daoExtras.existeRegistroFoto(spnAsignarNumero.getSelectedItem().toString())){
                        daoExtras.actualizarRegistroInpeccionFoto(fotoRequest);
                    }else{
                        daoExtras.crearRegistroFoto(fotoRequest);
                    }

                    Intent intent = new Intent(this, RegistroFotoInsert2Activity.class);
                    intent.putExtra("numAsignacion", fotoRequest.getNumInspeccion());
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Por favor subir las foto obligatorias (*)", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.menu_pedido_guardar:
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificarCampos(){
        boolean isComplete = true;
        for (String path : imagePaths) {
            if (path == null || path.isEmpty()) {
                isComplete = false;
                break;
            }
        }
        return isComplete;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    class async_sincronizacion extends AsyncTask<Void, String, String> {

        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String usuario = app.getPref_serieUsuario();
                if (Util.isConnectingToRed(RegistroFotoInsertActivity.this)) {
                    Log.d(TAG, "sincronizando datos...");
                    Response<ResponseBody> response;

                    String domain = "[[\"inspector_id.login\",\"=\",\"dumi121@imax.com.pe\"],[\"stage_id.name\",\"in\",[\"Inspección (Perito)\"]]]";
                    domain = domain.replace("dumi121@imax.com.pe", usuario);

                    response = XMSApi.getApiEasyfact(RegistroFotoInsertActivity.this.getApplicationContext())
                            .obtenerTickets(domain, 500).execute();
                    dataBaseHelper.sincro(response, TablesHelper.xms_asignacion.table);

                    return Constants.CORRECT;
                } else {
                    return Constants.FAIL_CONNECTION;
                }
            } catch (UnauthorizedException e) {
                e.printStackTrace();
                return Constants.FAIL_UNAUTHORIZED;
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return Constants.FAIL_RESOURCE;
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return Constants.FAIL_TIMEOUT;
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute " + result);
            progress.setVisibility(View.GONE);
            switch (result) {
                case Constants.CORRECT:
                case Constants.FAIL_UNAUTHORIZED:
                case Constants.FAIL_TIMEOUT:

                    break;
                case Constants.FAIL_CONNECTION:
                    Toast.makeText(RegistroFotoInsertActivity.this, R.string.error_no_autorizado, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    MyDetailDialog myDetailDialog = new MyDetailDialog(RegistroFotoInsertActivity.this,
                            R.drawable.ic_dialog_alert, getString(R.string.oops), getString(R.string.error_sincronizacion), result);
                    myDetailDialog.show();
                    break;
            }
            refreshLista();
        }
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

    public void refreshLista() {
        lista.clear();
        lista.addAll(daoExtras.getListAsignacionFoto());

        ArrayList<String> array = new ArrayList<String>();
        for (AsignacionModel model : lista) {
            array.add(model.getNumber());
        }
        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.my_spinner_item, array);
        spnAsignarNumero.setAdapter(adapter);

        if (lista.size() != 0) {
            loadFotorByNumber(lista.get(0).getNumber());
        }
    }

    private void loadFotorByNumber(String number){
        FotoRequest fotoRequest = daoExtras.getListFotoByNumero(number);
        if (fotoRequest.getFotosArrayAdjunto1() != null && !fotoRequest.getFotosArrayAdjunto1().isEmpty()) {
            fileMIMEList.clear();
            filesNameList.clear();
            filePaths.clear();
            for (String file : fotoRequest.getFotosArrayAdjunto1()) {
                int nameStart = file.indexOf("name=") + 5;
                int nameEnd = file.indexOf(";index=");

                String fileName = file.substring(nameStart, nameEnd);

                filesNameList.add(fileName);
                fileMIMEList.add(file);
                filePaths.add("Rutas");
            }
        }
        if (fotoRequest.getFotoArray1() != null && !fotoRequest.getFotoArray1().isEmpty()) {
            for (String file : fotoRequest.getFotoArray1()) {
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

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

}