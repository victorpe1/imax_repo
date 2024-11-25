package com.imax.app.data.api;

import com.imax.app.data.api.request.FotoRequestWrapper;
import com.imax.app.data.api.request.InspeccionRequest;
import com.imax.app.data.api.request.InspeccionRequestWrapper;
import com.imax.app.data.api.request.OrderRequest;
import com.imax.app.data.api.request.VentaRequest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EasyfactApiInterface {
    @Headers({"Content-Type: application/x-www-form-urlencoded","Accept: application/json"})
    @FormUrlEncoded
    @POST("see/server/receptor/find")
    Call<ResponseBody> consultarRucDni(@Header("Authorization") String authorization,
                                       @Field("type") String type, @Field("query") String query);

    @GET("api/auth/login")
    Call<ResponseBody> login(@Header("Authorization") String authorization);

    @GET("api/almacenes")
    Call<ResponseBody> getAlmacenes();

    @GET("api/condiciones")
    Call<ResponseBody> getCondiciones();

    @GET("api/clientes")
    Call<ResponseBody> getClientes();

    @GET("api/distritos")
    Call<ResponseBody> getDistritos();

    @GET("api/personal")
    Call<ResponseBody> getPersonal();

    @GET("api/productos")
    Call<ResponseBody> getProductos();

    @GET("api/tcambio")
    Call<ResponseBody> getTCambio();

    @GET("api/marca")
    Call<ResponseBody> getMarcas();

    @GET("api/unidad")
    Call<ResponseBody> getUnidadMedida();

    @GET("api/medidas")
    Call<ResponseBody> getMedidas();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/order/save")
    Call<ResponseBody> enviarPedido(@Body OrderRequest request);

    @GET("api/order")
    Call<ResponseBody> getPedidos(@Query("fecha") String fecha);

    /* -------------------------------------------------------------------------------------*/

    @Headers({
            "x-api-key: b7ZdO0amqiNlJYIzzQCL5PQ0rA4yfI2Q3dGRPqCom-mXxVpdt2TNHQ"
    })
    @GET("api/vapi_ticket_x_usuario/Ticket-atencion")
    Call<ResponseBody> obtenerTickets(
            @Query("domain") String domain,
            @Query("limit") int limit
    );

    @Headers({"Content-Type: application/json"})
    @POST("api/save_inspeccion")
    Call<ResponseBody> enviarRegistro(
            @Header("Access-Token") String token,
            @Body InspeccionRequestWrapper request);

    @Headers({"Content-Type: application/json"})
    @POST("api/save_rg_photo")
    Call<ResponseBody> enviarRegistroFoto(
            @Header("Access-Token") String token,
            @Body FotoRequestWrapper request);


    @Headers({"Content-Type: application/json"})
    @GET("api/auth/get_tokens")
    Call<ResponseBody> obtenerTokens(
            @Query("username") String username,
            @Query("password") String password,
            @Query("db") String db
    );

    @Headers({"Content-Type: application/json"})
    @POST("api/consultar_user")
    Call<ResponseBody> consultarUsuario(
            @Header("Access-Token") String token,
            @Body RequestBody params
    );

    @Headers({"Content-Type: application/json"})
    @POST("api/lista_configurations")
    Call<ResponseBody> consultarCatalogo(
            @Header("Access-Token") String token,
            @Body RequestBody params
    );

}
