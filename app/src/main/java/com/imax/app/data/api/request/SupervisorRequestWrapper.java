package com.imax.app.data.api.request;

import java.io.Serializable;
import java.util.ArrayList;

public class SupervisorRequestWrapper implements Serializable {
    private SupervisorRequestWrapper.Params params;

    public SupervisorRequestWrapper(ArrayList<SupervisorRequest> data) {
        this.params = new SupervisorRequestWrapper.Params(data);
    }

    public static class Params {
        private ArrayList<SupervisorRequest> data;

        public Params(ArrayList<SupervisorRequest> data) {
            this.data = data;
        }
    }
}
