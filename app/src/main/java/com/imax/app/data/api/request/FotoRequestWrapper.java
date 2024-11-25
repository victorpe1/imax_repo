package com.imax.app.data.api.request;

import java.util.ArrayList;

public class FotoRequestWrapper {
    private Params params;

    public FotoRequestWrapper(ArrayList<FotoRequest> data) {
        this.params = new Params(data);
    }

    public static class Params {
        private ArrayList<FotoRequest> data;

        public Params(ArrayList<FotoRequest> data) {
            this.data = data;
        }
    }
}
