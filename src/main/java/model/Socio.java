package model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Socio {
    //ATRIBUTOS
    private int idSocio;
    private String cpfSocio;
    private String nomeSocio;
    private String telefoneUsuario;
    private Date dataNascSocio;
    private String emailSocio;
    private boolean ativoSocio;
    private Endereco endereco;
    private Usuario usuario;
    private List <Departamento> listaDepartamentos = new ArrayList<>();
    
    //CONSTRUTORES
    public Socio(int idSocio, String cpfSocio, String nomeSocio, String telefoneUsuario, Date dataNascSocio, String emailSocio, boolean ativoSocio, Endereco endereco, Usuario usuario, List<Departamento> listaDepartamentos) {
        this.idSocio = idSocio;
        this.cpfSocio = cpfSocio;
        this.nomeSocio = nomeSocio;
        this.telefoneUsuario = telefoneUsuario;
        this.dataNascSocio = dataNascSocio;
        this.emailSocio = emailSocio;
        this.ativoSocio = ativoSocio;
        this.endereco = endereco;
        this.usuario = usuario;
        this.listaDepartamentos = listaDepartamentos;
    }

    public Socio(String cpfSocio, String nomeSocio, String telefoneUsuario, Date dataNascSocio, String emailSocio, boolean ativoSocio, Endereco endereco, Usuario usuario, List<Departamento> listaDepartamentos) {
        this.cpfSocio = cpfSocio;
        this.nomeSocio = nomeSocio;
        this.telefoneUsuario = telefoneUsuario;
        this.dataNascSocio = dataNascSocio;
        this.emailSocio = emailSocio;
        this.ativoSocio = ativoSocio;
        this.endereco = endereco;
        this.usuario = usuario;
        this.listaDepartamentos = listaDepartamentos;
    }

    public Socio() {
    }
    
    //GETERS E SETERS 
    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    public String getCpfSocio() {
        return cpfSocio;
    }

    public void setCpfSocio(String cpfSocio) {
        this.cpfSocio = cpfSocio;
    }

    public String getNomeSocio() {
        return nomeSocio;
    }

    public void setNomeSocio(String nomeSocio) {
        this.nomeSocio = nomeSocio;
    }

    public String getTelefoneUsuario() {
        return telefoneUsuario;
    }

    public void setTelefoneUsuario(String telefoneUsuario) {
        this.telefoneUsuario = telefoneUsuario;
    }

    public Date getDataNascSocio() {
        return dataNascSocio;
    }

    public void setDataNascSocio(Date dataNascSocio) {
        this.dataNascSocio = dataNascSocio;
    }

    public String getEmailSocio() {
        return emailSocio;
    }

    public void setEmailSocio(String emailSocio) {
        this.emailSocio = emailSocio;
    }

    public boolean isAtivoSocio() {
        return ativoSocio;
    }

    public void setAtivoSocio(boolean ativoSocio) {
        this.ativoSocio = ativoSocio;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Departamento> getListaDepartamentos() {
        return listaDepartamentos;
    }

    public void setListaDepartamentos(List<Departamento> listaDepartamentos) {
        this.listaDepartamentos = listaDepartamentos;
    }
}
