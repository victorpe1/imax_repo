package com.imax.app.data.api.request;

import java.util.ArrayList;

public class InspeccionRequestWrapper {
    private Params params;

    public InspeccionRequestWrapper(ArrayList<InspeccionRequest> data) {
        this.params = new Params(data);
    }

    public static class Params {
        private ArrayList<InspeccionRequest> data;

        public Params(ArrayList<InspeccionRequest> data) {
            this.data = data;
        }
    }
}
