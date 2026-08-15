package model;

import java.sql.Date;

public class Debito {
    //ATRIBUTOS
    private int idDebito;
    private String tipoDebito;
    private double valorDebito;
    private Date vencimentoDebito;
    private Date dtPgmtDebito;
    private Socio socio;

    //CONSTRUTORES
    public Debito(int idDebito, String tipoDebito, double valorDebito, Date vencimentoDebito, Date dtPgmtDebito, Socio socio) {
        this.idDebito = idDebito;
        this.tipoDebito = tipoDebito;
        this.valorDebito = valorDebito;
        this.vencimentoDebito = vencimentoDebito;
        this.dtPgmtDebito = dtPgmtDebito;
        this.socio = socio;
    }

    public Debito(String tipoDebito, double valorDebito, Date vencimentoDebito, Date dtPgmtDebito, Socio socio) {
        this.tipoDebito = tipoDebito;
        this.valorDebito = valorDebito;
        this.vencimentoDebito = vencimentoDebito;
        this.dtPgmtDebito = dtPgmtDebito;
        this.socio = socio;
    }

    public Debito() {
    }
    
    //GETERS E SETERS
    public int getIdDebito() {
        return idDebito;
    }

    public void setIdDebito(int idDebito) {
        this.idDebito = idDebito;
    }

    public String getTipoDebito() {
        return tipoDebito;
    }

    public void setTipoDebito(String tipoDebito) {
        this.tipoDebito = tipoDebito;
    }

    public double getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(double valorDebito) {
        this.valorDebito = valorDebito;
    }

    public Date getVencimentoDebito() {
        return vencimentoDebito;
    }

    public void setVencimentoDebito(Date vencimentoDebito) {
        this.vencimentoDebito = vencimentoDebito;
    }

    public Date getDtPgmtDebito() {
        return dtPgmtDebito;
    }

    public void setDtPgmtDebito(Date dtPgmtDebito) {
        this.dtPgmtDebito = dtPgmtDebito;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }    
}
