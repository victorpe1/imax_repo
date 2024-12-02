package com.imax.app.intents.supervisor;

import java.io.Serializable;

public class InspeccionSupervisor_2 implements Serializable {
    private boolean isChecked;
    private String description;
    private String spinnerValue;

    // Constructor
    public InspeccionSupervisor_2(boolean isChecked, String description, String spinnerValue) {
        this.isChecked = isChecked;
        this.description = description;
        this.spinnerValue = spinnerValue;
    }

    // Getters y Setters
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpinnerValue() {
        return spinnerValue;
    }

    public void setSpinnerValue(String spinnerValue) {
        this.spinnerValue = spinnerValue;
    }

}
