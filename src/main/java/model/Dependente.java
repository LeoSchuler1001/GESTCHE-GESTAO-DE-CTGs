package model;

import java.sql.Date;

public class Dependente {
    //ATRIBUTOS
    private int idDependente;
    private String nomeDependente;
    private String cpfDependente;
    private Date dataNascDependente;
    private Socio socio;
    
    //CONSTRUTORES
    public Dependente(int idDependente, String nomeDependente, String cpfDependente, Date dataNascDependente, Socio socio) {
        this.idDependente = idDependente;
        this.nomeDependente = nomeDependente;
        this.cpfDependente = cpfDependente;
        this.dataNascDependente = dataNascDependente;
        this.socio = socio;
    }

    public Dependente(String nomeDependente, String cpfDependente, Date dataNascDependente, Socio socio) {
        this.nomeDependente = nomeDependente;
        this.cpfDependente = cpfDependente;
        this.dataNascDependente = dataNascDependente;
        this.socio = socio;
    }

    public Dependente() {
    }
    
    //GETERS E SETERS
    public int getIdDependente() {
        return idDependente;
    }

    public void setIdDependente(int idDependente) {
        this.idDependente = idDependente;
    }

    public String getNomeDependente() {
        return nomeDependente;
    }

    public void setNomeDependente(String nomeDependente) {
        this.nomeDependente = nomeDependente;
    }

    public String getCpfDependente() {
        return cpfDependente;
    }

    public void setCpfDependente(String cpfDependente) {
        this.cpfDependente = cpfDependente;
    }

    public Date getDataNascDependente() {
        return dataNascDependente;
    }

    public void setDataNascDependente(Date dataNascDependente) {
        this.dataNascDependente = dataNascDependente;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }    
}
