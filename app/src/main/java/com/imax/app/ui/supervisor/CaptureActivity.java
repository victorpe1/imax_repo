package com.imax.app.ui.supervisor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.imax.app.BuildConfig;
import com.imax.app.R;
import com.imax.app.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CaptureActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();

    private ProgressDialog progressDialog;

    private TextView tvSeccionActual;
    private Button btnTomarFoto, btnEliminarTodas, btnFinalizar;
    private LinearLayout galeriaFotos;

    private String torre, piso, asignacion;
    private File seccionDir;
    private ArrayList<File> fotosList;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.tomar_foto), true, this, R.drawable.ic_action_close);

        tvSeccionActual = findViewById(R.id.tvSeccionActual);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnEliminarTodas = findViewById(R.id.btnEliminarTodas);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        galeriaFotos = findViewById(R.id.galeriaFotos);

        Intent intent = getIntent();
        torre = intent.getStringExtra("torre");
        piso = intent.getStringExtra("piso");
        asignacion = intent.getStringExtra("asignacion");;

        tvSeccionActual.setText(torre + " - " + piso);

        solicitarPermisos();

        fotosList = new ArrayList<>();
        loadFotos();

        btnTomarFoto.setOnClickListener(view -> tomarFoto());

        btnEliminarTodas.setOnClickListener(view -> eliminarTodasFotos());

        btnFinalizar.setOnClickListener(view -> {
            finish();
        });

        progressDialog = new ProgressDialog(CaptureActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

    }

    private void solicitarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        //seccionDir = new File(Environment.getExternalStorageDirectory()+ "/Fotos/" + asignacion + "/" + torre + "/" + piso);
        seccionDir = new File(getExternalFilesDir(null) + "/Fotos/" + asignacion + "/" + torre + "/" + piso);


        if (!seccionDir.exists()) {
            if (seccionDir.mkdirs()) {
                Log.i("Directorio", "Directorio creado exitosamente: " + seccionDir.getAbsolutePath());
            } else {
                Log.i("Directorio", "No se pudo crear el directorio: " + seccionDir.getAbsolutePath());
            }
        }
    }

    private void loadFotos() {
        galeriaFotos.removeAllViews();
        fotosList.clear();

        File[] fotos = seccionDir.listFiles();
        if (fotos != null) {
            for (File foto : fotos) {
                if (foto.isFile() && (foto.getName().endsWith(".jpg") || foto.getName().endsWith(".png"))) {
                    fotosList.add(foto);
                    addFotoToGallery(foto);
                }
            }
        }
    }

    private void eliminarTodasFotos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Todas las Fotos");
        builder.setMessage("¿Estás seguro de que deseas eliminar todas las fotos de esta sección?");
        builder.setPositiveButton("Sí", (dialogInterface, which) -> {
            for (File foto : fotosList) {
                foto.delete();
            }
            loadFotos();
            Toast.makeText(CaptureActivity.this, "Todas las fotos han sido eliminadas.", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void confirmarEliminarFoto(final File foto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Foto");
        builder.setMessage("¿Deseas eliminar esta foto?");
        builder.setPositiveButton("Sí", (dialogInterface, which) -> {
            if (foto.delete()) {
                loadFotos();
                Toast.makeText(CaptureActivity.this, "Foto eliminada.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CaptureActivity.this, "Error al eliminar la foto.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void addFotoToGallery(final File foto) {
        ImageView imageView = new ImageView(this);

        Bitmap bitmap = BitmapFactory.decodeFile(foto.getAbsolutePath());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        imageView.setImageBitmap(scaledBitmap);
        imageView.setPadding(8, 8, 8, 8);

        imageView.setOnLongClickListener(view -> {
            confirmarEliminarFoto(foto);
            return true;
        });

        galeriaFotos.addView(imageView);
    }

    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp + ".jpg";
            imageFile = new File(seccionDir, imageFileName);

            Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                Toast.makeText(this, "Permisos requeridos para tomar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_pedido, menu);
        menu.findItem(R.id.menu_pedido_siguiente).setVisible(false);
        menu.findItem(R.id.menu_pedido_guardar).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (!seccionDir.exists()) {
                if (seccionDir.mkdirs()) {
                    Log.i("Directorio", "Directorio creado exitosamente: " + seccionDir.getAbsolutePath());
                } else {
                    Log.i("Directorio", "No se pudo crear el directorio: " + seccionDir.getAbsolutePath());
                }
            }

            fotosList.add(imageFile);
            addFotoToGallery(imageFile);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
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
