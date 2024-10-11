package com.imax.app.ui.foto;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.intents.AntesInspeccion;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.AsignacionModel;
import com.imax.app.models.CatalogModel;
import com.imax.app.ui.activity.RegistrarCaractGeneralesActivity;
import com.imax.app.ui.pedido.AgregarProductoActivity;
import com.imax.app.ui.pedido.AgregarProductoArgument;
import com.imax.app.utils.Constants;
import com.imax.app.utils.MyDetailDialog;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class RegistroFotoInsertActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_REGISTRO = 1;

    public static final String EXTRA_DIRECCION = "direccion";


    private final int REQUEST_CODE_AGREGAR_PRODUCTO = 0;

    public String idTipoDocumentoOriginal = "";

    private boolean cabeceraGuardada = false;

    private ProgressDialog progressDialog;
    private ViewPager mViewPager;
    private String numeroDocumentoValidado;

    private DAOExtras daoExtras;

    private Spinner spnAsignarNumero;

    private Button btnTakePhoto1;
    private ImageView imgPreview1;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Drawable defaultBackground;
    private Calendar calendar;
    private App app;
    private android.app.AlertDialog.Builder dialogBuilder;
    private android.app.AlertDialog dialog;

    Button btnOpenMaps;
    private ProgressBar progress;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<AsignacionModel> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_foto_1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.nuevo_registro), true, this, R.drawable.ic_action_close);
        progress = findViewById(R.id.progress);
        dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());

        app = (App) getApplicationContext();
        calendar = Calendar.getInstance();
        daoExtras = new DAOExtras(getApplicationContext());

        spnAsignarNumero = findViewById(R.id.spn_asignarNumero);

        btnTakePhoto1 = findViewById(R.id.btnTakePhoto1);
        imgPreview1 = findViewById(R.id.imgPreview1); // Asegúrate de tener un ImageView en el XML con este id

        // Manejar el click en el botón de tomar foto
        btnTakePhoto1.setOnClickListener(v -> openCamera());
        imgPreview1.setVisibility(View.GONE);

        new async_sincronizacion().execute();

        spnAsignarNumero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Item selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        progressDialog = new ProgressDialog(RegistroFotoInsertActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, "com.example.yourapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Crear el nombre del archivo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
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

                Intent intent = new Intent(RegistroFotoInsertActivity.this,
                        RegistroFotoInsertActivity.class);
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

    private void DialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroFotoInsertActivity.this);
        builder.setTitle(getString(R.string.descartar_cambios));
        builder.setMessage(getString(R.string.se_perdera_cambios));
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();

    }

    public void noPermitirCerrar() {
        Util.actualizarToolBar("", false, this);
        cabeceraGuardada = true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imgPreview1.setImageURI(imageUri);
            imgPreview1.setVisibility(View.VISIBLE); // Mostrar la imagen

            btnTakePhoto1.setText("Actualizar Foto");
        }

        if (requestCode == REQUEST_CODE_AGREGAR_PRODUCTO) {
            if (resultCode == RESULT_OK) {
                AgregarProductoArgument argument = (AgregarProductoArgument) data.getSerializableExtra(
                        AgregarProductoActivity.EXTRA_PRODUCTO_AGREGADO);
            }
        }
    }

    class async_sincronizacion extends AsyncTask<Void, String, String> {

        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                if (Util.isConnectingToRed(RegistroFotoInsertActivity.this)) {
                    Log.d(TAG, "sincronizando datos...");
                    Response<ResponseBody> response;

                    String domain = "[[\"user_id.login\",\"=\",\"jose.lunarejo@imax.com.pe\"],[\"stage_id.name\",\"in\",[\"Inspección (Perito)\",\"Elaboración (Perito)\"]]]";

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

    public void refreshLista() {
        lista.clear();
        lista.addAll(daoExtras.getListAsignacion());

        ArrayList<String> array = new ArrayList<String>();
        for (AsignacionModel model : lista) {
            array.add(model.getNumber());
        }
        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.my_spinner_item, array);
        spnAsignarNumero.setAdapter(adapter);

    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroFotoInsertActivity.this);
        builder.setTitle(tituloRes);
        builder.setMessage(mensajeRes);
        builder.setIcon(icon);
        builder.setCancelable(false);

       builder.setPositiveButton(getString(R.string.aceptar), (dialog, which) -> {
            /*Intent intent = new Intent(getApplicationContext(), DetallePedidoActivity.class);
            intent.putExtra("numeroPedido",numeroPedido);
            intent.putExtra("idCliente",idCliente);
            intent.putExtra("nombreCliente",autocomplete_busqueda.getText().toString());
            startActivity(intent); */
            setResult(RESULT_OK);
            finish();
        });
        builder.show();
    }

}
