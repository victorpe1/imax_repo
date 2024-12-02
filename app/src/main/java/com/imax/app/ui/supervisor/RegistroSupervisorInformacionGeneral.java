package com.imax.app.ui.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.imax.app.App;
import com.imax.app.R;
import com.imax.app.data.api.XMSApi;
import com.imax.app.data.api.request.SupervisorRequest;
import com.imax.app.data.dao.DAOExtras;
import com.imax.app.intents.supervisor.InspeccionSupervisor_1;
import com.imax.app.managers.DataBaseHelper;
import com.imax.app.managers.TablesHelper;
import com.imax.app.models.AsignacionModel;
import com.imax.app.utils.Constants;
import com.imax.app.utils.MyDetailDialog;
import com.imax.app.utils.UnauthorizedException;
import com.imax.app.utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class RegistroSupervisorInformacionGeneral extends AppCompatActivity {
    private final String TAG = getClass().getName();

    public static final int ACCION_NUEVO_REGISTRO = 1;

    private ProgressDialog progressDialog;
    private App app;
    private DAOExtras daoExtras;

    private Spinner spnAsignarNumero;
    private EditText ed_Fecha, ed_Hora, ed_Proyecto, ed_Solicitud, ed_Respon_Obra;
    private EditText ed_Cargo, ed_Sotano, ed_Piso, ed_Obra, ed_Torres;

    private ProgressBar progress;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<AsignacionModel> lista = new ArrayList<>();
    private Drawable defaultBackground;
    private String proyectoId = "", solicitanteId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_general);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.actualizarToolBar(getString(R.string.registro_supervisor),
                true, this, R.drawable.ic_action_close);

        progress = findViewById(R.id.progress);
        dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());

        app = (App) getApplicationContext();
        daoExtras = new DAOExtras(getApplicationContext());

        spnAsignarNumero = findViewById(R.id.spn_asignarNumero);
        ed_Fecha = findViewById(R.id.ed_Fecha);
        ed_Hora = findViewById(R.id.ed_Hora);
        ed_Proyecto = findViewById(R.id.ed_Proyecto);
        ed_Solicitud =  findViewById(R.id.ed_Solicitud);
        ed_Respon_Obra = findViewById(R.id.ed_Respon_Obra);
        ed_Cargo = findViewById(R.id.ed_Cargo);
        ed_Sotano = findViewById(R.id.ed_Sotano);
        ed_Piso = findViewById(R.id.ed_Piso);
        ed_Obra = findViewById(R.id.ed_Obra);
        ed_Torres = findViewById(R.id.ed_Torres);

        new RegistroSupervisorInformacionGeneral.async_sincronizacion().execute();

        spnAsignarNumero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ed_Proyecto.setText(lista.get(position).getTaskName());
                ed_Solicitud.setText(lista.get(position).getApplicantName());
                proyectoId = lista.get(position).getTaskId();
                solicitanteId = lista.get(position).getApplicantId();

                setDateTimeInEditText(lista.get(position).getInspectionDate(), ed_Fecha, ed_Hora);
                loadDataIfExists(lista.get(position).getNumber());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        configurarValidacionCampoError(ed_Proyecto);
        defaultBackground = ContextCompat.getDrawable(this, R.drawable.default_border);

        progressDialog = new ProgressDialog(RegistroSupervisorInformacionGeneral.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    private void configurarValidacionCampoError(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editText.setBackgroundResource(android.R.drawable.edit_text);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    editText.setBackground(
                            ContextCompat.getDrawable(RegistroSupervisorInformacionGeneral.this,
                                    R.drawable.error_border));
                }
            }
        });
    }

    private void loadDataIfExists(String numero){
        SupervisorRequest inspeccionRequest =  daoExtras.getListAsignacionByNumeroSupervisor(numero);

        if(inspeccionRequest.getNumInspeccion().trim().length() != 0){
            ed_Respon_Obra.setText(inspeccionRequest.getResponsableObra());
            ed_Cargo.setText(inspeccionRequest.getCargo());
            ed_Sotano.setText(inspeccionRequest.getSotanos());
            ed_Piso.setText(inspeccionRequest.getPisos());
            ed_Obra.setText(inspeccionRequest.getMesas());
            ed_Torres.setText(inspeccionRequest.getTorres());

            proyectoId = inspeccionRequest.getProyectoId();
            solicitanteId = inspeccionRequest.getSolicitanteId();
        }else{
            ed_Respon_Obra.setText("");
            ed_Cargo.setText("");
            ed_Sotano.setText("");
            ed_Piso.setText("");
            ed_Obra.setText("");
            ed_Torres.setText("");
        }
    }

    private boolean validarCampos() {
        if(lista.size() == 0){
            Toast.makeText(this, "No exite numeros de inspecciones registradas.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(ed_Proyecto.getText().toString().equals("-")){
            Toast.makeText(this, "No exite proyecto asignado.", Toast.LENGTH_SHORT).show();
            return true; //false
        }


        boolean isValid = true;
        Drawable errorBackground = ContextCompat.getDrawable(this, R.drawable.error_border);

        isValid &= validarEditText(ed_Hora, errorBackground);
        isValid &= validarEditText(ed_Fecha, errorBackground);

        isValid &= validarEditText(ed_Proyecto, errorBackground);
        isValid &= validarEditText(ed_Solicitud, errorBackground);
        isValid &= validarEditText(ed_Respon_Obra, errorBackground);
        isValid &= validarEditText(ed_Cargo, errorBackground);
        isValid &= validarEditText(ed_Sotano, errorBackground);
        isValid &= validarEditText(ed_Piso, errorBackground);
        isValid &= validarEditText(ed_Obra, errorBackground);
        isValid &= validarEditText(ed_Torres, errorBackground);

        //isValid &= validarSpinner(spnAsignarNumero, errorBackground);

        if (!isValid) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private boolean validarEditText(EditText editText, Drawable errorBackground) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setBackground(errorBackground);
            return false;
        } else {
            editText.setBackground(defaultBackground);
            return true;
        }
    }

    public void setDateTimeInEditText(String fecha, EditText edtFecha, EditText edtHora) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = sdf.parse(fecha);  // Convierte el string a Date
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // Mes de 0 a 11, por eso sumamos 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String formattedDate = String.format("%02d/%02d/%04d", day, month, year);  // dd/MM/yyyy
        String formattedTime = String.format("%02d:%02d", hour, minute);  // HH:mm

        edtFecha.setText(formattedDate);
        edtHora.setText(formattedTime);
    }

    public void showLoader() {
        progressDialog.show();
    }
    public void hideLoader() {
        progressDialog.dismiss();
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

                if (validarCampos()) {
                    InspeccionSupervisor_1 inspeccion = new InspeccionSupervisor_1(
                            spnAsignarNumero.getSelectedItem().toString(),
                            ed_Fecha.getText().toString(),
                            ed_Hora.getText().toString(),
                            ed_Proyecto.getText().toString(),
                            ed_Solicitud.getText().toString(),
                            ed_Respon_Obra.getText().toString(),
                            ed_Cargo.getText().toString(),
                            ed_Sotano.getText().toString(),
                            ed_Piso.getText().toString(),
                            ed_Obra.getText().toString(),
                            ed_Torres.getText().toString(),
                            proyectoId,
                            solicitanteId
                    );

                    if (daoExtras.existeRegistroSupervisor(spnAsignarNumero.getSelectedItem().toString())) {
                        daoExtras.actualizarRegistroInpeccionSupervisor(inspeccion);
                    } else {
                        daoExtras.crearRegistroSupervisor(inspeccion);
                    }

                    Intent intent = new Intent(RegistroSupervisorInformacionGeneral.this,
                            RegistroListadoDocumentos.class);
                    intent.putExtra("InspeccionSupervisor_1", inspeccion);
                    startActivity(intent);
                }

                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    class async_sincronizacion extends AsyncTask<Void, String, String> {

        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                String usuario = app.getPref_serieUsuario();

                if (Util.isConnectingToRed(RegistroSupervisorInformacionGeneral.this)) {
                    Log.d(TAG, "sincronizando datos...");
                    Response<ResponseBody> response;

                    String domain = "[[\"inspector_id.login\",\"=\",\"jose.lunarejo@imax.com.pe\"],[\"stage_id.name\",\"in\",[\"Inspección (Perito)\",\"Elaboración (Perito)\"]]]";
                    domain = domain.replace("jose.lunarejo@imax.com.pe", usuario);

                    response = XMSApi.getApiEasyfact(RegistroSupervisorInformacionGeneral.this.getApplicationContext())
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
                    Toast.makeText(RegistroSupervisorInformacionGeneral.this, R.string.error_no_autorizado, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    MyDetailDialog myDetailDialog = new MyDetailDialog(RegistroSupervisorInformacionGeneral.this,
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

}
