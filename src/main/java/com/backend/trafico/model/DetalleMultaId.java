package com.backend.trafico.model;

import java.io.Serializable;
import java.util.Objects;

public class DetalleMultaId implements Serializable {
    private Integer multa;
    private String vehiculo;

    public Integer getMulta() {
        return multa;
    }

    public void setMulta(Integer multa) {
        this.multa = multa;
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
        if (!(o instanceof DetalleMultaId that)) return false;
        return Objects.equals(multa, that.multa) && Objects.equals(vehiculo, that.vehiculo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(multa, vehiculo);
    }
}
