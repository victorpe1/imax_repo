package com.imax.app.intents;

import java.io.Serializable;
import java.util.List;

public class DespuesInspeccion implements Serializable {

    private boolean tiene;
    private boolean tiene2;
    private String especificar;
    private String especificar2;
    private List<String> files;
    private List<String> filesBase64;

    public DespuesInspeccion(boolean tiene, boolean tiene2, String especificar, String especificar2, List<String> files) {
        this.tiene = tiene;
        this.tiene2 = tiene2;
        this.especificar = especificar;
        this.especificar2 = especificar2;
        this.files = files;
    }

    public List<String> getFilesBase64() {
        return filesBase64;
    }

    public void setFilesBase64(List<String> filesBase64) {
        this.filesBase64 = filesBase64;
    }

    public boolean isTiene() {
        return tiene;
    }

    public boolean isTiene2() {
        return tiene2;
    }

    public String getEspecificar() {
        return especificar;
    }

    public String getEspecificar2() {
        return especificar2;
    }

    public List<String> getFiles() {
        return files;
    }
}
