package com.backend.trafico.model;

import java.io.Serializable;
import java.util.Objects;

public class DetalleAccidenteId implements Serializable {
    private Integer accidente;
    private String vehiculo;

    public Integer getAccidente() {
        return accidente;
    }

    public void setAccidente(Integer accidente) {
        this.accidente = accidente;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleAccidenteId that)) return false;
        return Objects.equals(accidente, that.accidente) && Objects.equals(vehiculo, that.vehiculo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accidente, vehiculo);
    }

}
