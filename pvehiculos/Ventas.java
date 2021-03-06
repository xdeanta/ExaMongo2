/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pvehiculos;

/**
 *
 * @author oracle
 */
public class Ventas {
    private int id;
    private String dni;
    private String codveh;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCodveh() {
        return codveh;
    }

    public void setCodveh(String codveh) {
        this.codveh = codveh;
    }

    public Ventas(Double id, String dni, String codveh) {
        double tempid = id;
        this.id = (int)tempid;
        this.dni = dni;
        this.codveh = codveh;
    }
    
    public String toString(){
        return "{id:" + id + ", dni:" + dni + ", codveh:" + codveh + "}";
    }
    
}
