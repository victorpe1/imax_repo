package com.imax.app.data.api.request;

import java.io.Serializable;
import java.util.List;

public class FotoRequest implements Serializable {

    private String numInspeccion;
    private List<String> fotoArray1;
    private List<String> fotosArrayAdjunto1;

    private List<String> fotoArray2;
    private List<String> fotosArrayAdjunto2;

    private List<String> fotoArray3;
    private List<String> fotosArrayAdjunto3;

    private List<String> fotoArray4;
    private List<String> fotosArrayAdjunto4;


    private List<String> fotosArrayAdjunto5;

    private String user_email;

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public FotoRequest() {
    }

    public FotoRequest(String numInspeccion, List<String> fotoArray1, List<String> fotosArrayAdjunto1, List<String> fotoArray2, List<String> fotosArrayAdjunto2, List<String> fotoArray3, List<String> fotosArrayAdjunto3, List<String> fotoArray4, List<String> fotosArrayAdjunto4, List<String> fotosArrayAdjunto5) {
        this.numInspeccion = numInspeccion;
        this.fotoArray1 = fotoArray1;
        this.fotosArrayAdjunto1 = fotosArrayAdjunto1;
        this.fotoArray2 = fotoArray2;
        this.fotosArrayAdjunto2 = fotosArrayAdjunto2;
        this.fotoArray3 = fotoArray3;
        this.fotosArrayAdjunto3 = fotosArrayAdjunto3;
        this.fotoArray4 = fotoArray4;
        this.fotosArrayAdjunto4 = fotosArrayAdjunto4;
        this.fotosArrayAdjunto5 = fotosArrayAdjunto5;
    }

    public String getNumInspeccion() {
        return numInspeccion;
    }

    public void setNumInspeccion(String numInspeccion) {
        this.numInspeccion = numInspeccion;
    }

    public List<String> getFotoArray1() {
        return fotoArray1;
    }

    public void setFotoArray1(List<String> fotoArray1) {
        this.fotoArray1 = fotoArray1;
    }

    public List<String> getFotosArrayAdjunto1() {
        return fotosArrayAdjunto1;
    }

    public void setFotosArrayAdjunto1(List<String> fotosArrayAdjunto1) {
        this.fotosArrayAdjunto1 = fotosArrayAdjunto1;
    }

    public List<String> getFotoArray2() {
        return fotoArray2;
    }

    public void setFotoArray2(List<String> fotoArray2) {
        this.fotoArray2 = fotoArray2;
    }

    public List<String> getFotosArrayAdjunto2() {
        return fotosArrayAdjunto2;
    }

    public void setFotosArrayAdjunto2(List<String> fotosArrayAdjunto2) {
        this.fotosArrayAdjunto2 = fotosArrayAdjunto2;
    }

    public List<String> getFotoArray3() {
        return fotoArray3;
    }

    public void setFotoArray3(List<String> fotoArray3) {
        this.fotoArray3 = fotoArray3;
    }

    public List<String> getFotosArrayAdjunto3() {
        return fotosArrayAdjunto3;
    }

    public void setFotosArrayAdjunto3(List<String> fotosArrayAdjunto3) {
        this.fotosArrayAdjunto3 = fotosArrayAdjunto3;
    }

    public List<String> getFotoArray4() {
        return fotoArray4;
    }

    public void setFotoArray4(List<String> fotoArray4) {
        this.fotoArray4 = fotoArray4;
    }

    public List<String> getFotosArrayAdjunto4() {
        return fotosArrayAdjunto4;
    }

    public void setFotosArrayAdjunto4(List<String> fotosArrayAdjunto4) {
        this.fotosArrayAdjunto4 = fotosArrayAdjunto4;
    }

    public List<String> getFotosArrayAdjunto5() {
        return fotosArrayAdjunto5;
    }

    public void setFotosArrayAdjunto5(List<String> fotosArrayAdjunto5) {
        this.fotosArrayAdjunto5 = fotosArrayAdjunto5;
    }
}
