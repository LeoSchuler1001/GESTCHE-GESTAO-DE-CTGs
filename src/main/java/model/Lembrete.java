package model;

import java.sql.Date;
import java.sql.Time;

public class Lembrete {
    //ATRIBUTOS
    private int idLembrete;
    private String nomeLembrete;
    private Date dataInicioLembrete;
    private Date dataFimLembrete;
    private String periodicidadeLembrete;
    private String descricaoLembrete;
    private Time horarioLembrete;
    private boolean pagoLembrete;
    private Usuario usuario;

    //CONSTRUTORES
    public Lembrete(int idLembrete, String nomeLembrete, Date dataInicioLembrete, Date dataFimLembrete, String periodicidadeLembrete, String descricaoLembrete, Time horarioLembrete, boolean pagoLembrete, Usuario usuario) {
        this.idLembrete = idLembrete;
        this.nomeLembrete = nomeLembrete;
        this.dataInicioLembrete = dataInicioLembrete;
        this.dataFimLembrete = dataFimLembrete;
        this.periodicidadeLembrete = periodicidadeLembrete;
        this.descricaoLembrete = descricaoLembrete;
        this.horarioLembrete = horarioLembrete;
        this.pagoLembrete = pagoLembrete;
        this.usuario = usuario;
    }

    public Lembrete(String nomeLembrete, Date dataInicioLembrete, Date dataFimLembrete, String periodicidadeLembrete, String descricaoLembrete, Time horarioLembrete, boolean pagoLembrete, Usuario usuario) {
        this.nomeLembrete = nomeLembrete;
        this.dataInicioLembrete = dataInicioLembrete;
        this.dataFimLembrete = dataFimLembrete;
        this.periodicidadeLembrete = periodicidadeLembrete;
        this.descricaoLembrete = descricaoLembrete;
        this.horarioLembrete = horarioLembrete;
        this.pagoLembrete = pagoLembrete;
        this.usuario = usuario;
    }
    
    public Lembrete() {
    }
    
    //GETERS E SETTERS
    public int getIdLembrete() {
        return idLembrete;
    }

    public void setIdLembrete(int idLembrete) {
        this.idLembrete = idLembrete;
    }

    public String getNomeLembrete() {
        return nomeLembrete;
    }

    public void setNomeLembrete(String nomeLembrete) {
        this.nomeLembrete = nomeLembrete;
    }

    public Date getDataInicioLembrete() {
        return dataInicioLembrete;
    }

    public void setDataInicioLembrete(Date dataInicioLembrete) {
        this.dataInicioLembrete = dataInicioLembrete;
    }

    public Date getDataFimLembrete() {
        return dataFimLembrete;
    }

    public void setDataFimLembrete(Date dataFimLembrete) {
        this.dataFimLembrete = dataFimLembrete;
    }

    public String getPeriodicidadeLembrete() {
        return periodicidadeLembrete;
    }

    public void setPeriodicidadeLembrete(String periodicidadeLembrete) {
        this.periodicidadeLembrete = periodicidadeLembrete;
    }

    public String getDescricaoLembrete() {
        return descricaoLembrete;
    }

    public void setDescricaoLembrete(String descricaoLembrete) {
        this.descricaoLembrete = descricaoLembrete;
    }

    public Time getHorarioLembrete() {
        return horarioLembrete;
    }

    public void setHorarioLembrete(Time horarioLembrete) {
        this.horarioLembrete = horarioLembrete;
    }

    public boolean isPagoLembrete() {
        return pagoLembrete;
    }

    public void setPagoLembrete(boolean pagoLembrete) {
        this.pagoLembrete = pagoLembrete;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
