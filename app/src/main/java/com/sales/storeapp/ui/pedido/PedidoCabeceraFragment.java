package com.sales.storeapp.ui.pedido;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sales.storeapp.App;
import com.sales.storeapp.R;
import com.sales.storeapp.data.dao.DAOCliente;
import com.sales.storeapp.data.dao.DAOExtras;
import com.sales.storeapp.data.dao.DAOPedido;
import com.sales.storeapp.models.Almacen;
import com.sales.storeapp.models.ClienteModel;
import com.sales.storeapp.models.CondicionPago;
import com.sales.storeapp.models.GenericModel;
import com.sales.storeapp.models.Order;
import com.sales.storeapp.models.Personal;
import com.sales.storeapp.ui.adapters.AutoCompleteClienteAdapter;
import com.sales.storeapp.ui.adapters.AutoCompletePersonalAdapter;
import com.sales.storeapp.utils.Util;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidoCabeceraFragment extends Fragment {
    private final String TAG = getClass().getName();
    private TextInputEditText edt_numeroPedido, edt_direccion, edt_observaciones,
            edt_fechaEmision, edt_fechaVencimiento, edt_distrito, edt_ubigeo;
    private TextInputLayout til_direccion, til_distrito, til_ubigeo;
    private AutoCompleteTextView autocomplete_busqueda, autocomplete_busquedaVendedor;
    private Spinner spn_CondicionPago, spn_Almacen, spn_tipoDocumento;
    DecimalFormat formateador;
    List<CondicionPago> listCondicionPago;
    List<Almacen> listAlmacen;
    List<GenericModel> listaTipoDocumento;

    String idCliente = "";
    int idUsuario = 0;
    int idVendedor = 0;
    String razonSocial = "";

    String numeroPedido;
    private int ACCION_PEDIDO;
    App app;

    DAOExtras daoExtras;
    DAOPedido daoPedido;
    DAOCliente daoCliente;

    boolean flgInitSetFormaPago = false;
    public PedidoCabeceraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_cabecera, container, false);
        setHasOptionsMenu(false);

        daoExtras = new DAOExtras(getActivity());
        daoPedido = new DAOPedido(getActivity());
        daoCliente = new DAOCliente(getActivity());

        formateador = Util.formateador();

        edt_numeroPedido    = view.findViewById(R.id.edt_numeroPedido);
        edt_observaciones   = view.findViewById(R.id.edt_observaciones);

        edt_direccion       = view.findViewById(R.id.edt_direccion);
        edt_distrito       = view.findViewById(R.id.edt_distrito);
        edt_ubigeo       = view.findViewById(R.id.edt_ubigeo);
        til_direccion       = view.findViewById(R.id.til_direccion);
        til_distrito       = view.findViewById(R.id.til_distrito);
        til_ubigeo       = view.findViewById(R.id.til_ubigeo);

        spn_tipoDocumento   = view.findViewById(R.id.spn_tipo_documento);

        edt_fechaEmision    = view.findViewById(R.id.edt_fecha_emision);
        edt_fechaVencimiento    = view.findViewById(R.id.edt_fecha_vencimiento);

        autocomplete_busqueda = view.findViewById(R.id.autocomplete_busqueda);
        autocomplete_busqueda.setHint(getString(R.string.hint_cliente_general));
        autocomplete_busquedaVendedor = view.findViewById(R.id.autocomplete_busqueda_vendedor);
        autocomplete_busquedaVendedor.setHint(getString(R.string.hint_vendedor_general));

        spn_CondicionPago   = view.findViewById(R.id.spn_condicion);
        spn_Almacen = view.findViewById(R.id.spn_almacen);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Se obtienen los campos del Activity en este fragment
        this.idCliente = ((PedidoActivity)getActivity()).getIdClienteFromActivity();
        this.numeroPedido = ((PedidoActivity)getActivity()).getNumeroPedidoFromActivity();
        this.razonSocial = ((PedidoActivity)getActivity()).getDNIRUCFromActivity();

        app = (App) getActivity().getApplicationContext();
        idUsuario = app.getPref_idUsuario();

        listCondicionPago = daoExtras.getListCondicionPago();
        ArrayList<String> arrayCondicionPago = new ArrayList<String>();
        for (CondicionPago cPago : listCondicionPago) {
            arrayCondicionPago.add(cPago.getNombre());
        }
        ArrayAdapter adapterCondicionPago = new ArrayAdapter<>(getActivity(),
                R.layout.my_spinner_item, arrayCondicionPago);
        spn_CondicionPago.setAdapter(adapterCondicionPago);

        listAlmacen = daoExtras.getAlmacenes();
        ArrayList<String> arrayAlmacenes = new ArrayList<>();
        for (Almacen model : listAlmacen) {
            arrayAlmacenes.add(model.getNombre());
        }
        ArrayAdapter adapterAlmacen = new ArrayAdapter<>(getActivity(),
                R.layout.my_spinner_item, arrayAlmacenes);
        spn_Almacen.setAdapter(adapterAlmacen);

        ACCION_PEDIDO = ((PedidoActivity)getActivity()).getACCION_PEDIDO();
        if (ACCION_PEDIDO  == PedidoActivity.ACCION_EDITAR_PEDIDO || ACCION_PEDIDO == PedidoActivity.ACCION_COPIAR_PEDIDO){
            flgInitSetFormaPago = true;
        }

        edt_fechaEmision.setOnFocusChangeListener((v, hasFocus) -> {
            //Si la vista ha sido seleccionada mostrar el Picker
            if (hasFocus){
                Calendar mcurrentTime = Calendar.getInstance();
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);

                //Si el campo fecha ya tiene un valor, obtenemos los que ya estan seleccionados
                if (!edt_fechaEmision.getText().toString().isEmpty()){
                    try {
                        String[] fechaArray = edt_fechaEmision.getText().toString().split("/");
                        day = Integer.parseInt(fechaArray[0]);
                        month = (Integer.parseInt(fechaArray[1])-1);
                        year = Integer.parseInt(fechaArray[2]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), (view1, selectedYear, selectedMonthOfYear, selectedDayOfMonth) -> {
                    Calendar pickerDay = Calendar.getInstance();
                    pickerDay.set(Calendar.DAY_OF_MONTH,selectedDayOfMonth);
                    pickerDay.set(Calendar.MONTH,selectedMonthOfYear);
                    pickerDay.set(Calendar.YEAR, selectedYear);

                    Calendar today = Calendar.getInstance();

                    long diferencia = today.getTimeInMillis() - pickerDay.getTimeInMillis(); //result in millis
                    long diasDiferencia = diferencia / (24 * 60 * 60 * 1000);

                    if (diasDiferencia > 7){
                        Toast.makeText(getActivity(), getString(R.string.message_fecha_emision_limite), Toast.LENGTH_LONG).show();
                        edt_fechaEmision.clearFocus();
                        edt_fechaEmision.requestFocus();
                    }else{
                        String selectedDay = String.valueOf(selectedDayOfMonth);
                        String selectedMonth =  String.valueOf(selectedMonthOfYear + 1);

                        if (selectedDayOfMonth<10){
                            selectedDay = "0"+selectedDay;
                        }
                        if (selectedMonthOfYear + 1 <10){
                            selectedMonth = "0"+selectedMonth;
                        }
                        edt_fechaEmision.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
                        edt_fechaEmision.clearFocus();//Quitamos el focus para que pueda volver a ser seleccionado
                    }
                },year,month,day);

                /*
                Al cancelar el Picker, se debe limpiar el focus sobre el TextInputEditText, de lo contrario se quedará abierto (con el hint arriba)
                y la proxima vez que se seleccione ya no ejecutarpa el evento "OnFocusChange" puesto que ya tiene el focus puesto
                */
                mDatePicker.setOnCancelListener(dialog -> {
                    edt_fechaEmision.clearFocus();
                    edt_numeroPedido.requestFocus();
                });

                mDatePicker.show();
            }
        });
        edt_fechaEmision.setText(Util.getFechaTelefonoString());

        edt_fechaVencimiento.setOnFocusChangeListener((v, hasFocus) -> {
            //Si la vista ha sido seleccionada mostrar el Picker
            if (hasFocus){
                Calendar mcurrentTime = Calendar.getInstance();
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                int month = mcurrentTime.get(Calendar.MONTH);
                int year = mcurrentTime.get(Calendar.YEAR);

                //Si el campo fecha ya tiene un valor, obtenemos los que ya estan seleccionados
                if (!edt_fechaVencimiento.getText().toString().isEmpty()){
                    try {
                        String[] fechaArray = edt_fechaVencimiento.getText().toString().split("/");
                        day = Integer.parseInt(fechaArray[0]);
                        month = (Integer.parseInt(fechaArray[1])-1);
                        year = Integer.parseInt(fechaArray[2]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), (view12, selectedYear, selectedMonthOfYear, selectedDayOfMonth) -> {
                    Calendar pickerDay = Calendar.getInstance();
                    pickerDay.set(Calendar.DAY_OF_MONTH,selectedDayOfMonth);
                    pickerDay.set(Calendar.MONTH,selectedMonthOfYear);
                    pickerDay.set(Calendar.YEAR, selectedYear);

                    Calendar today = Calendar.getInstance();

                    long diferencia = today.getTimeInMillis() - pickerDay.getTimeInMillis(); //result in millis
                    long diasDiferencia = diferencia / (24 * 60 * 60 * 1000);

                    if (diasDiferencia > 7){
                        Toast.makeText(getActivity(), getString(R.string.message_fecha_emision_limite), Toast.LENGTH_LONG).show();
                        edt_fechaVencimiento.clearFocus();
                        edt_fechaVencimiento.requestFocus();
                    }else{
                        String selectedDay = String.valueOf(selectedDayOfMonth);
                        String selectedMonth =  String.valueOf(selectedMonthOfYear + 1);

                        if (selectedDayOfMonth<10){
                            selectedDay = "0"+selectedDay;
                        }
                        if (selectedMonthOfYear + 1 <10){
                            selectedMonth = "0"+selectedMonth;
                        }
                        edt_fechaVencimiento.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
                        edt_fechaVencimiento.clearFocus();//Quitamos el focus para que pueda volver a ser seleccionado
                    }
                },year,month,day);

                /*
                Al cancelar el Picker, se debe limpiar el focus sobre el TextInputEditText, de lo contrario se quedará abierto (con el hint arriba)
                y la proxima vez que se seleccione ya no ejecutarpa el evento "OnFocusChange" puesto que ya tiene el focus puesto
                */
                mDatePicker.setOnCancelListener(dialog -> {
                    edt_fechaVencimiento.clearFocus();
                    edt_numeroPedido.requestFocus();
                });

                mDatePicker.show();
            }
        });
        edt_fechaVencimiento.setText(Util.getFechaTelefonoString());


        //String numeropedidoSimple = numeroPedido.substring(6,numeroPedido.length());
        edt_numeroPedido.setText("0001");

        this.cargarClientes();
        this.cargarVendedor();

        listaTipoDocumento = daoExtras.getTipoDocumento();
        ArrayList<String> arrayDocumentos = new ArrayList<>();
        for (GenericModel model : listaTipoDocumento) {
            arrayDocumentos.add(model.getDescripcion());
        }
        ArrayAdapter adapterDocumento = new ArrayAdapter<>(getActivity(),
                R.layout.my_spinner_item, arrayDocumentos);
        spn_tipoDocumento.setAdapter(adapterDocumento);


        autocomplete_busqueda.setOnItemClickListener((parent, view13, position, id) -> {
           ClienteModel clienteModel = (ClienteModel) parent.getItemAtPosition(position);
           autocomplete_busqueda.setText(clienteModel.getRazonSocial());

           til_direccion.setVisibility(View.VISIBLE);
           til_ubigeo.setVisibility(View.VISIBLE);
           til_distrito.setVisibility(View.VISIBLE);

           edt_direccion.setText(clienteModel.getDomicilio());
           edt_ubigeo.setText(clienteModel.getCodUbigeo());
           edt_distrito.setText(clienteModel.getDistrito());
           idCliente = clienteModel.getIdCliente();

           //Una vez se obtenga el codigo del cliente se tiene que mandar ese codigo al activity para poder usarlo
           ((PedidoActivity)getActivity()).setCliente(clienteModel);//Ahora el activity

            int idVendedorCliente = clienteModel.getIdVendedor();
            if (idVendedorCliente != 0) {
                for (int i = 0; i < autocomplete_busquedaVendedor.getAdapter().getCount(); i++) {
                    Personal personalModel = (Personal) autocomplete_busquedaVendedor.getAdapter().getItem(i);
                    if (personalModel.getIdPersonal() == idVendedorCliente) {
                        autocomplete_busquedaVendedor.setText(personalModel.getNombre());
                        idVendedor = personalModel.getIdPersonal();

                        ((PedidoActivity)getActivity()).setVendedor(personalModel);
                        break;
                    }
                }
            }

           edt_numeroPedido.requestFocus();
           Util.cerrarTeclado(getContext(), autocomplete_busqueda);
       });
        autocomplete_busqueda.setOnLongClickListener(v -> {
            idCliente = "0";
            autocomplete_busqueda.setText("");

            til_direccion.setVisibility(View.GONE);
            til_ubigeo.setVisibility(View.GONE);
            til_distrito.setVisibility(View.GONE);

            edt_direccion.setText("");
            edt_ubigeo.setText("");
            edt_distrito.setText("");

            ((PedidoActivity)getActivity()).setCliente(null);//Ahora el activity
            return true;
        });

        autocomplete_busquedaVendedor.setOnItemClickListener((parent, view13, position, id) -> {
            Personal personalModel = (Personal) parent.getItemAtPosition(position);
            autocomplete_busquedaVendedor.setText(personalModel.getNombre());
            //edt_dni_ev.setText(clienteModel.getRucDni());
            idVendedor = personalModel.getIdPersonal();

            //Una vez se obtenga el codigo del cliente se tiene que mandar ese codigo al activity para poder usarlo
            ((PedidoActivity)getActivity()).setVendedor(personalModel);//Ahora el activity

            edt_numeroPedido.requestFocus();
            Util.cerrarTeclado(getContext(), autocomplete_busquedaVendedor);
        });
        autocomplete_busquedaVendedor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                idVendedor = 0;
                autocomplete_busquedaVendedor.setText("");
                ((PedidoActivity)getActivity()).setVendedor(null);//Ahora el activity
                return true;
            }
        });

        ACCION_PEDIDO = ((PedidoActivity)getActivity()).getACCION_PEDIDO();
        if (ACCION_PEDIDO  == PedidoActivity.ACCION_EDITAR_PEDIDO || ACCION_PEDIDO == PedidoActivity.ACCION_VER_PEDIDO
                || ACCION_PEDIDO == PedidoActivity.ACCION_COPIAR_PEDIDO){
            cargarPedido();

            //Siempre debe mostrar la Razón Social del cliente (En caso haya sido creado en la app, se trae la razon Social registrada)
            String a = ((PedidoActivity)getActivity()).getDNIRUCFromActivity();
            String ax = ((PedidoActivity)getActivity()).getRazonSocialFromActivity();
            //autocomplete_busqueda.setText(((PedidoActivity)getActivity()).getDNIRUCFromActivity());

            //edt_dni_ev.setText(((PedidoActivity)getActivity()).getIdClienteFromActivity());
            //autocomplete_busqueda.setInputType(InputType.TYPE_NULL);//Indicamos que el AutoCompleteTextView no sea editable
            //Una vez se obtenga el codigo del cliente se tiene que mandar ese codigo a los fragment hijos y estos puedan usarlo
            //pedidoCabeceraFragment.setCodigoCliente(codigoCliente);//Ahora el fragment tiene el codigo del cliente
            autocomplete_busqueda.dismissDropDown();
            autocomplete_busqueda.clearFocus();

            if (ACCION_PEDIDO == PedidoActivity.ACCION_VER_PEDIDO){
                //habilitarCampos(false);
            }
        }

        if(idCliente.isEmpty() || idCliente.equals("0")){
            til_direccion.setVisibility(View.GONE);
            til_ubigeo.setVisibility(View.GONE);
            til_distrito.setVisibility(View.GONE);
        }else{
            til_direccion.setVisibility(View.VISIBLE);
            til_ubigeo.setVisibility(View.VISIBLE);
            til_distrito.setVisibility(View.VISIBLE);
        }
    }

    private void cargarPedido() {
        Order pedido = daoPedido.getPedidoCabecera(numeroPedido);
        if (pedido != null){

            for (int i = 0; i < listCondicionPago.size(); i++) {
                if (listCondicionPago.get(i).equals(pedido.getIdCond())) {
                    spn_CondicionPago.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < listCondicionPago.size(); i++) {
                Log.d(TAG,"Forma de pago:"+listCondicionPago.get(i).getIdCondicion()+" Pedido: "+pedido.getIdCond());
                if (String.valueOf(listCondicionPago.get(i).getIdCondicion()).equals(String.valueOf(pedido.getIdCond()))) {
                    spn_CondicionPago.setSelection(i);
                    break;
                }
            }
            //autocomplete_busqueda.setText(pedido.getr());

            //edt_observaciones.setText(pedido.getObservacion());
            //edt_direccion.setText(daoCliente.getDireccionCliente(idCliente));
            // ((PedidoActivity)getActivity()).idTipoDocumentoOriginal = pedido.getIdTipoDocumento();

        }else{
            showAlertError("No se pudo cargar el pedido","Comuníquiese con el administrador");
        }
    }

    public boolean validarCampos(){
        String ERROR = "Campo requerido";

        if (edt_numeroPedido.getText().length() == 0){
            edt_numeroPedido.setError(ERROR);
            edt_numeroPedido.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(edt_fechaEmision.getText().toString())){
            Toast.makeText(getActivity(),getString(R.string.message_fecha_emision_requerido),Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(edt_fechaVencimiento.getText().toString())){
            Toast.makeText(getActivity(),getString(R.string.message_fecha_vencimiento_requerido),Toast.LENGTH_SHORT).show();
            return false;
        }

        if (listCondicionPago.size() == 0){
            Toast.makeText(getActivity(),"No se tiene condicion de pago",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (listAlmacen.size() == 0){
            Toast.makeText(getActivity(),"No se tiene almacen",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean validarCamposObligatorios(){
        String ERROR = "Campo requerido";

        if (edt_numeroPedido.getText().length() == 0){
            edt_numeroPedido.setError(ERROR);
            edt_numeroPedido.requestFocus();
            return false;
        }

        Order pedidoCabeceraModel = daoPedido.getPedidoCabecera(numeroPedido);
        if (pedidoCabeceraModel != null){
            if (pedidoCabeceraModel.getIdCliente() == 0){
                 Toast.makeText(getActivity(), getString(R.string.message_ingresar_cliente), Toast.LENGTH_LONG).show();
                 return false;
             }
            if (pedidoCabeceraModel.getIdVendedor() == 0){
                Toast.makeText(getActivity(), getString(R.string.message_ingresar_vendedor), Toast.LENGTH_LONG).show();
                return false;
            }
        }else {
            Toast.makeText(getActivity(), getString(R.string.message_ingresar_producto), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public Order getPedido(){

        this.razonSocial = ((PedidoActivity)getActivity()).getDNIRUCFromActivity();

        Order pedidoModel = new Order();

        pedidoModel.setIdNumero(numeroPedido);
        pedidoModel.setIdCliente(Integer.parseInt(idCliente));
        pedidoModel.setIdUsuario(idUsuario);
        pedidoModel.setIdVendedor(idVendedor);

        pedidoModel.setFecha(Util.getFechaHoraTelefonoString_formatoSql());
        pedidoModel.setFechaEntrega(Util.parseDateFormat(edt_fechaEmision.getText().toString()));
        pedidoModel.setFechaVencimiento(Util.parseDateFormat(edt_fechaVencimiento.getText().toString()));
        pedidoModel.setIdAlmacen(listAlmacen.get(spn_Almacen.getSelectedItemPosition()).getIdAlmacen());
        pedidoModel.setIdCond(listCondicionPago.get(spn_CondicionPago.getSelectedItemPosition()).getIdCondicion());
        pedidoModel.setDirecc(edt_direccion.getText().toString());
        pedidoModel.setCodubigeo(edt_ubigeo.getText().toString());
        pedidoModel.setTypeDocument(listaTipoDocumento.get(spn_tipoDocumento.getSelectedItemPosition()).getId());

        if (TextUtils.isEmpty(edt_observaciones.getText().toString())){
            pedidoModel.setObservacion("");
        }else{
            pedidoModel.setObservacion(edt_observaciones.getText().toString());
        }

        //pedidoModel.setFlag(PedidoCabeceraModel.FLAG_PENDIENTE);
        // pedidoModel.setMedioPago(listaMedioPago.get(spn_CondicionPago.getSelectedItemPosition()));
       // pedidoModel.setFormaPagoGlosa(listaFormaPago.get(spn_formaPago.getSelectedItemPosition()).getIdFormaPago());
        return pedidoModel;
    }

    public String getIdCliente() {
        return idCliente;
    }

    /**
     * Este método normalmente se ejecuta cuando se selecciona a un cliente, se pasa el codigo al fragment y se muestra la lista de direcciones del mismo
     * @param --idCliente
     */
    /*public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
        edt_direccion.setText(daoCliente.getDireccionCliente(idCliente));
    }*/

    void showAlertError(String titulo,String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_dialog_error);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setCancelable(false);
        builder.setNegativeButton("ACEPTAR", (dialog, which) -> getActivity().finish());
        builder.show();
    }

    private void cargarClientes(){
        AutoCompleteClienteAdapter adapter = new AutoCompleteClienteAdapter(getActivity(), daoCliente.getClientes());
        autocomplete_busqueda.setThreshold(1);
        autocomplete_busqueda.setAdapter(adapter);

        Util.cerrarTeclado(getActivity(), autocomplete_busqueda);
    }
    private void cargarVendedor(){
        AutoCompletePersonalAdapter adapter = new AutoCompletePersonalAdapter(getActivity(), daoExtras.getPersonal());
        autocomplete_busquedaVendedor.setThreshold(1);
        autocomplete_busquedaVendedor.setAdapter(adapter);

        Util.cerrarTeclado(getActivity(), autocomplete_busquedaVendedor);
    }
}
