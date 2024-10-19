package com.imax.app.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.request.InspeccionRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.data.tasks.EnviarDocumentoTask;
import com.imax.app.intents.AntesInspeccion;
import com.imax.app.intents.CaracteristicasEdificacion;
import com.imax.app.intents.CaracteristicasGenerales;
import com.imax.app.intents.DespuesInspeccion;
import com.imax.app.intents.SignatureView;
import com.imax.app.ui.adapters.FilesAdapter;
import com.imax.app.utils.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegistroDespuesInspeccionFirmaActivity extends AppCompatActivity{
    private final String TAG = getClass().getName();


    private ProgressDialog progressDialog;
    private ViewPager mViewPager;

    private DAOExtras daoExtras;

    private App app;

    private AntesInspeccion inspeccion;
    private CaracteristicasGenerales caracteristicasGenerales;
    private CaracteristicasEdificacion caracteristicasEdificacion;
    private String infraestructura_comentario;

    Button btnClearSignature;
    EditText edt_especificar, edt_especificar_2;

    private SignaturePad signaturePad;
    DespuesInspeccion despuesInspeccion;
    ArrayList<String> fileNames = new ArrayList<>();

    private InspeccionRequest inspeccionRequest = new InspeccionRequest();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despues_inspeccion_firma);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.nuevo_registro), true, this, R.drawable.ic_action_back);

        inspeccion = (AntesInspeccion) getIntent().getSerializableExtra("inspeccion");
        caracteristicasGenerales = (CaracteristicasGenerales) getIntent().getSerializableExtra("caracteristicasGenerales");
        caracteristicasEdificacion = (CaracteristicasEdificacion) getIntent().getSerializableExtra("caracteristicasEdificacion");
        infraestructura_comentario = (String) getIntent().getSerializableExtra("infraestructura_comentario");
        despuesInspeccion = (DespuesInspeccion) getIntent().getSerializableExtra("despuesInspeccion");
        fileNames = getIntent().getStringArrayListExtra("filesListName");


        app = (App) getApplicationContext();

        daoExtras = new DAOExtras(getApplicationContext());

        inspeccionRequest =  daoExtras.getListAsignacionByNumero(inspeccion.getNumInspeccion());
        
        edt_especificar = findViewById(R.id.edt_observaciones);
        signaturePad = findViewById(R.id.signature_pad);
        btnClearSignature = findViewById(R.id.btn_clear_signature);

        btnClearSignature.setOnClickListener(v -> clearSignature());

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // El usuario ha comenzado a firmar
                Toast.makeText(RegistroDespuesInspeccionFirmaActivity.this, "Comenzó a firmar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                // La firma está completa
                Toast.makeText(RegistroDespuesInspeccionFirmaActivity.this, "Firma completa", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClear() {
                // La firma ha sido limpiada
                Toast.makeText(RegistroDespuesInspeccionFirmaActivity.this, "Firma eliminada", Toast.LENGTH_SHORT).show();
            }
        });

        progressDialog = new ProgressDialog(RegistroDespuesInspeccionFirmaActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    public boolean hasUserSigned() {
        return !signaturePad.isEmpty();
    }
    public Bitmap getSignatureBitmap() {
        return signaturePad.getSignatureBitmap();
    }
    public String getSignatureAsBase64() {
        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void clearSignature() {
        signaturePad.clear();
    }
    private boolean validarCampos(){
        boolean isValid = true;

        Drawable errorBackground = ContextCompat.getDrawable(this, R.drawable.error_border);

        isValid = validarEditText(edt_especificar, errorBackground);

        if (!hasUserSigned()) {
            isValid = false;
            Toast.makeText(this, "Por favor, firme antes de continuar", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }
    private boolean validarEditText(EditText editText, Drawable errorBackground) {
        Drawable defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

        if (editText.getText().toString().isEmpty()) {
            editText.setBackground(errorBackground);
            return false;
        } else {
            editText.setBackground(defaultBackground);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_pedido, menu);
        menu.findItem(R.id.menu_pedido_siguiente).setVisible(false);
        menu.findItem(R.id.menu_pedido_guardar).setVisible(true);
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
                break;
            case R.id.menu_pedido_guardar:
                if(validarCampos()){
                    mostrarPreFirma();
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarPreFirma(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_report, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroDespuesInspeccionFirmaActivity.this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView tvAsignacion = dialogView.findViewById(R.id.tv_asignacion_valor);
        TextView tvTipoInspeccion = dialogView.findViewById(R.id.tv_tipo_inspeccion_valor);
        TextView tvFechaInspeccion = dialogView.findViewById(R.id.tv_fecha_inspeccion_valor);
        TextView tvContacto = dialogView.findViewById(R.id.tv_contacto_valor);
        TextView tvTipoInmueble = dialogView.findViewById(R.id.tv_tipo_inmueble_valor);
        TextView tvUsoInmueble = dialogView.findViewById(R.id.tv_uso_inmueble_valor);
        TextView tvMencionarCadaUno = dialogView.findViewById(R.id.tv_mencionar_cada_uno);
        TextView tvQuienRecibeInmueble = dialogView.findViewById(R.id.tv_quien_inmueble);
        TextView tvDireccion = dialogView.findViewById(R.id.tv_direccion_valor);
        TextView tvCoordenadas = dialogView.findViewById(R.id.tv_coordenadas);
        TextView tvDescripcion = dialogView.findViewById(R.id.tv_descripcion);
        TextView tvDistribucion = dialogView.findViewById(R.id.tv_distribucion);
        TextView tvDepartamento = dialogView.findViewById(R.id.tv_departamento);
        TextView tvEstacionamiento = dialogView.findViewById(R.id.tv_estacionamiento);
        TextView tvDeposito = dialogView.findViewById(R.id.tv_deposito);
        TextView tvEstructura = dialogView.findViewById(R.id.tv_estructura);
        TextView tvMuros = dialogView.findViewById(R.id.tv_muros);
        TextView tvPisos = dialogView.findViewById(R.id.tv_pisos);
        TextView tvTipoPuertas = dialogView.findViewById(R.id.tv_tipo_puertas);
        TextView tvMaterialPuertas = dialogView.findViewById(R.id.tv_material_puerta);
        TextView tvSistemaPuertas = dialogView.findViewById(R.id.tv_sistema_puerta);
        TextView tvMarcoVentanas = dialogView.findViewById(R.id.tv_marco_ventana);
        TextView tvVidrioVentanas = dialogView.findViewById(R.id.tv_vidrio_ventana);
        TextView tvSistemaVentanas = dialogView.findViewById(R.id.tv_sistema_ventana);
        TextView tvMarcoMamparas = dialogView.findViewById(R.id.tv_marco_mamparas);
        TextView tvVidrioMamparas = dialogView.findViewById(R.id.tv_vidrio_mamparas);
        TextView tvSistemaMamparas = dialogView.findViewById(R.id.tv_sistema_mamparas);
        TextView tvPisoCocina = dialogView.findViewById(R.id.tv_piso_cocina);
        TextView tvParedCocina = dialogView.findViewById(R.id.tv_pared_cocina);
        TextView tvTipoMuebleCocina = dialogView.findViewById(R.id.tv_tipo_mueble_cocina);
        TextView tvMaterialMuebleCocina = dialogView.findViewById(R.id.tv_material_mueble_cocina);
        TextView tvTableros = dialogView.findViewById(R.id.tv_tableros);
        TextView tvLavadero = dialogView.findViewById(R.id.tv_lavadero);
        TextView tvPisoBaño = dialogView.findViewById(R.id.tv_piso_baño);
        TextView tvParedBaño = dialogView.findViewById(R.id.tv_pared_baño);
        TextView tvSanitariosTipo = dialogView.findViewById(R.id.tv_sanitarios_tipo);
        TextView tvSanitariosColor = dialogView.findViewById(R.id.tv_sanitarios_color);
        TextView tvSanitarios = dialogView.findViewById(R.id.tv_sanitarios);
        TextView tvIISS = dialogView.findViewById(R.id.tv_iiss);
        TextView tvSistemasIncendios = dialogView.findViewById(R.id.tv_sistemas_incendios);
        TextView tvIIEE = dialogView.findViewById(R.id.tv_iiee);
        TextView tvInfraestructura = dialogView.findViewById(R.id.tv_infraestructura);
        TextView tvCoincideValor = dialogView.findViewById(R.id.tv_coincide_valor);
        TextView tvEspecificarCoincide = dialogView.findViewById(R.id.et_especificar_coincide);
        TextView tvDocumentacionValor = dialogView.findViewById(R.id.tv_documentacion_valor);
        TextView tvEspecificarDocumentacion = dialogView.findViewById(R.id.et_especificar_documentacion);
        TextView tvObservaciones = dialogView.findViewById(R.id.et_observaciones);

        TextView tvOtros_1 = dialogView.findViewById(R.id.tv_otros_1);
        TextView tvOtros_2 = dialogView.findViewById(R.id.tv_otros_2);
        TextView tvOtros_3 = dialogView.findViewById(R.id.tv_otros_3);
        TextView tvOtros_4 = dialogView.findViewById(R.id.tv_otros_4);
        TextView tvOtros_5 = dialogView.findViewById(R.id.tv_otros_5);
        TextView tvOtros_6 = dialogView.findViewById(R.id.tv_otros_6);
        TextView tvOtros_7 = dialogView.findViewById(R.id.tv_otros_7);
        TextView tvOtros_8 = dialogView.findViewById(R.id.tv_otros_8);
        TextView tvOtros_9 = dialogView.findViewById(R.id.tv_otros_9);
        TextView tvOtros_10 = dialogView.findViewById(R.id.tv_otros_10);
        ImageView imgFirma =  dialogView.findViewById(R.id.iv_firma);


        tvAsignacion.setText(inspeccion.getNumInspeccion());
        tvTipoInspeccion.setText(inspeccion.getInscripcion());
        tvFechaInspeccion.setText(inspeccion.getFecha() + " " + inspeccion.getHora());
        tvContacto.setText(inspeccion.getContacto());
        tvTipoInmueble.setText(caracteristicasGenerales.getTipoInmueble());

        List<String> usosSeleccionados = new ArrayList<>();
        if (caracteristicasGenerales.isCbVivienda()) usosSeleccionados.add("Vivienda");
        if (caracteristicasGenerales.isCbComercio()) usosSeleccionados.add("Comercio");
        if (caracteristicasGenerales.isCbIndustria()) usosSeleccionados.add("Industria");
        if (caracteristicasGenerales.isCbEducativo()) usosSeleccionados.add("Educativo");
        if (caracteristicasGenerales.isCbOther()) usosSeleccionados.add("Otro");
        String textoResultado = TextUtils.join(", ", usosSeleccionados);
        tvUsoInmueble.setText(textoResultado);

        tvMencionarCadaUno.setText(caracteristicasGenerales.getComentarios());
        tvQuienRecibeInmueble.setText(caracteristicasGenerales.getRecibeInmueble());
        tvDireccion.setText(inspeccion.getDepartamento() + ", " + inspeccion.getProvincia()+ ", " + inspeccion.getDireccion());
        tvCoordenadas.setText("Longitud: " +inspeccion.getLongitud() + ", Lattitud: " + inspeccion.getLatitud() );
        tvDescripcion.setText(caracteristicasGenerales.getNPisos());
        tvDistribucion.setText(caracteristicasGenerales.getDistribucion());
        tvDepartamento.setText(caracteristicasGenerales.getDepto());
        tvEstacionamiento.setText(caracteristicasGenerales.getEstacionamiento());
        tvDeposito.setText(caracteristicasGenerales.getDeposito());
        tvEstructura.setText(caracteristicasEdificacion.getEstructura());
        tvOtros_1.setText(caracteristicasEdificacion.getOtroEstructura());
        tvMuros.setText(caracteristicasEdificacion.getMuros());
        tvOtros_2.setText(caracteristicasEdificacion.getOtroAlbañeria());
        tvPisos.setText(caracteristicasEdificacion.getPisos());
        tvOtros_3.setText(caracteristicasEdificacion.getOtroPisos());
        tvTipoPuertas.setText(caracteristicasEdificacion.getTipoPuerta());
        tvMaterialPuertas.setText(caracteristicasEdificacion.getMaterialPuerta());
        tvSistemaPuertas.setText(caracteristicasEdificacion.getSistemaPuerta());
        tvOtros_4.setText(caracteristicasEdificacion.getOtroPuertas());
        tvMarcoVentanas.setText(caracteristicasEdificacion.getMarcoVentana());
        tvVidrioVentanas.setText(caracteristicasEdificacion.getVidrioVentana());
        tvSistemaVentanas.setText(caracteristicasEdificacion.getSistemaVentana());
        tvOtros_5.setText(caracteristicasEdificacion.getOtroVentana());
        tvMarcoMamparas.setText(caracteristicasEdificacion.getMarcoMampara());
        tvVidrioMamparas.setText(caracteristicasEdificacion.getVidrioMampara());
        tvSistemaMamparas.setText(caracteristicasEdificacion.getSistemaMampara());
        tvOtros_6.setText(caracteristicasEdificacion.getOtroMampara());
        tvPisoCocina.setText(caracteristicasEdificacion.getPisosCocina());
        tvParedCocina.setText(caracteristicasEdificacion.getParedesCocina());
        tvTipoMuebleCocina.setText(caracteristicasEdificacion.getMuebleCocina());
        tvMaterialMuebleCocina.setText(caracteristicasEdificacion.getMuebleCocina2());
        tvOtros_7.setText(caracteristicasEdificacion.getOtroPisos());
        tvTableros.setText(caracteristicasEdificacion.getTablero());
        tvLavadero.setText(caracteristicasEdificacion.getLavaderos());
        tvPisoBaño.setText(caracteristicasEdificacion.getPisosBaños());
        tvParedBaño.setText(caracteristicasEdificacion.getParedesBaño());
        tvSanitariosTipo.setText(caracteristicasEdificacion.getSanitarioTipo());
        tvSanitariosColor.setText(caracteristicasEdificacion.getSanitarioColor());
        tvSanitarios.setText(caracteristicasEdificacion.getSanitario());
        tvOtros_8.setText(caracteristicasEdificacion.getOtroBaño());
        tvIISS.setText(caracteristicasEdificacion.getIss());
        tvOtros_9.setText(caracteristicasEdificacion.getOtroSanitarias());
        tvSistemasIncendios.setText(caracteristicasEdificacion.getSistemaIncendio());
        tvIIEE.setText(caracteristicasEdificacion.getIiee());
        tvOtros_10.setText(caracteristicasEdificacion.getOtroElectricas());
        tvInfraestructura.setText(infraestructura_comentario);
        tvCoincideValor.setText(despuesInspeccion.getEspecificar());
        tvEspecificarCoincide.setText(despuesInspeccion.isTiene() ? "SI" : "NO");
        tvDocumentacionValor.setText(despuesInspeccion.getEspecificar());
        tvEspecificarDocumentacion.setText(despuesInspeccion.isTiene2() ? "SI" : "NO");
        tvObservaciones.setText(edt_especificar.getText());
        setBase64Image(imgFirma, getSignatureAsBase64());

        LinearLayout layoutAdjuntos = dialogView.findViewById(R.id.layout_adjuntos);

        for (int i = 0; i < fileNames.size(); i++) {
            String nombreArchivo = fileNames.get(i);
            agregarArchivoAdjunto(layoutAdjuntos, nombreArchivo);
        }

        inspeccionRequest.setObservacion(edt_especificar.getText().toString());
        inspeccionRequest.setBase64Firma(getSignatureAsBase64());

        String requestJSON = new Gson().toJson(inspeccionRequest);
        Log.i("inspeccionRequest -> ", requestJSON);

        daoExtras.actualizarRegistroDespuesFirmaInspeccion(inspeccionRequest, inspeccion.getNumInspeccion());

        Button btnAceptar = dialogView.findViewById(R.id.btn_enviar_reporte);
        btnAceptar.setOnClickListener(v -> {
            dialog.dismiss();
            //finish();
            new EnviarDocumentoTask(this, inspeccionRequest).execute();
        });
    }
    private void agregarArchivoAdjunto(LinearLayout layoutAdjuntos, String nombreArchivo) {
        LinearLayout contenedorArchivo = new LinearLayout(this);
        contenedorArchivo.setOrientation(LinearLayout.HORIZONTAL);
        contenedorArchivo.setPadding(0, 8, 0, 0);  // Agregar padding para separar los elementos

        ImageView iconoArchivo = new ImageView(this);
        LinearLayout.LayoutParams paramsIcono = new LinearLayout.LayoutParams(48, 48);  // Tamaño del ícono
        iconoArchivo.setLayoutParams(paramsIcono);
        iconoArchivo.setImageResource(R.drawable.icon_pdf);  // Reemplaza con tu recurso de imagen

        TextView nombreArchivoTexto = new TextView(this);
        LinearLayout.LayoutParams paramsTexto = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTexto.setMargins(16, 0, 0, 0);  // Margen izquierdo para separar del icono
        nombreArchivoTexto.setLayoutParams(paramsTexto);
        nombreArchivoTexto.setText(nombreArchivo);

        contenedorArchivo.addView(iconoArchivo);
        contenedorArchivo.addView(nombreArchivoTexto);

        layoutAdjuntos.addView(contenedorArchivo);
    }
    public void setBase64Image(ImageView imageView, String base64String) {
        if (base64String.contains(",")) {
            base64String = base64String.split(",")[1];
        }

        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        imageView.setImageBitmap(decodedBitmap);
    }
    public void mostrarPopup(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int iconResId) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView2 = inflater.inflate(R.layout.popup_gracias, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView2);

        AlertDialog dialog2 = builder.create();
        dialog2.show();

        ImageView imageViewCheck = dialogView2.findViewById(R.id.imageViewCheck);
        TextView tvSubtitulo = dialogView2.findViewById(R.id.tvSubtitulo);
        Button btnAceptar = dialogView2.findViewById(R.id.btnAceptar);

        imageViewCheck.setImageResource(iconResId);
        tvSubtitulo.setText(mensajeRes);

        btnAceptar.setOnClickListener(v -> {
            dialog2.dismiss();
            finish();
            Intent intent = new Intent(RegistroDespuesInspeccionFirmaActivity.this, MenuPrincipalActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void DialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroDespuesInspeccionFirmaActivity.this);
        builder.setTitle(getString(R.string.descartar_cambios));
        builder.setMessage(getString(R.string.se_perdera_cambios));
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.show();

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

    public void showLoader() {
        progressDialog.show();
    }
    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void showDialogoPostEnvio(@StringRes int tituloRes, @StringRes int mensajeRes, @DrawableRes int icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroDespuesInspeccionFirmaActivity.this);
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
