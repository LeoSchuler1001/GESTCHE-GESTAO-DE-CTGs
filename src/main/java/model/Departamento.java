package model;

import java.util.ArrayList;
import java.util.List;

public class Departamento {
    //ATRIBUTOS
    private int idDepartamento;
    private String nomeDepartamento;
    private String descricaoDepartamento;
    private List <Socio> listaSocios = new ArrayList<>();
    
    //CONSTRUTORES
    public Departamento(int idDepartamento, String nomeDepartamento, String descricaoDepartamento, List<Socio> listaSocios) {
        this.idDepartamento = idDepartamento;
        this.nomeDepartamento = nomeDepartamento;
        this.descricaoDepartamento = descricaoDepartamento;
        this.listaSocios = listaSocios;
    }

    public Departamento(String nomeDepartamento, String descricaoDepartamento, List<Socio> listaSocios) {
        this.nomeDepartamento = nomeDepartamento;
        this.descricaoDepartamento = descricaoDepartamento;
        this.listaSocios = listaSocios;
    }
    
    public Departamento() {
    }
    
    //GETERS E SETERS
    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNomeDepartamento() {
        return nomeDepartamento;
    }

    public void setNomeDepartamento(String nomeDepartamento) {
        this.nomeDepartamento = nomeDepartamento;
    }

    public String getDescricaoDepartamento() {
        return descricaoDepartamento;
    }

    public void setDescricaoDepartamento(String descricaoDepartamento) {
        this.descricaoDepartamento = descricaoDepartamento;
    }

    public List<Socio> getListaSocios() {
        return listaSocios;
    }

    public void setListaSocios(List<Socio> listaSocios) {
        this.listaSocios = listaSocios;
    }    
}
