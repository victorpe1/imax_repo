package com.sales.storeapp.data.api;

import com.sales.storeapp.data.api.request.OrderRequest;
import com.sales.storeapp.data.api.request.VentaRequest;

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

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/order/save")
    Call<ResponseBody> enviarPedido(@Body OrderRequest request);

    /* -------------------------------------------------------------------------------------*/

    @GET("see/server/listaprecio/list")
    Call<ResponseBody> getListaPrecios(@Query("idEmpresa") String idEmpresa);

    @GET("see/server/listaprecio/{idLista}")
    Call<ResponseBody> getListaPrecio(@Path("idLista") int idLista, @Query("idEmpresa") String idEmpresa);

    @GET("see/server/document/{idPedido}")
    Call<ResponseBody> getPDFPedido(@Path("idPedido") String id,
                                 @Query("file") String PDF
                                 );

    @GET("see/server/document/list/")
    Call<ResponseBody> getListaPedidoGeneral(@Query("idEmpresa") String idEmpresa,
                                             @Query("opcionFecha") boolean opcionFecha,
                                             @Query("desde") String desde,
                                             @Query("hasta") String hasta,
                                             @Query("porUsuario") boolean porUsuario
                                             );


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("see/rest/{tipoDocumento}/{tde}/{rucUser}/{boleta}")
    Call<ResponseBody> getListaPedidoDetallado(
                                             @Path("tipoDocumento") String tipoDocumento,
                                             @Path("tde") String tde,
                                             @Path("rucUser") String rucUser,
                                             @Path("boleta") String boleta,
                                             @Query("content") int content);

    @PUT("see/server/document/01")
    Call<ResponseBody> enviarFactura(@Body VentaRequest factura);

    @PUT("see/server/document/03")
    Call<ResponseBody> enviarBoleta(@Body VentaRequest boleta);

    @PUT("see/server/document/12")
    Call<ResponseBody> enviarTicket(@Body VentaRequest ticket);

}
