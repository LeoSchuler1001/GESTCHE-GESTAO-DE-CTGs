package model;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Lembrete {
    //ATRIBUTOS
    private int idLembrete;
    private String nomeLembrete;
    private Date dataInicioLembrete;
    private String periodicidadeLembrete;
    private String descricaoLembrete;
    private Time horarioLembrete;
    private BooleanProperty ativoLembrete = new SimpleBooleanProperty(true);
    private Usuario usuario;

    //CONSTRUTORES
    public Lembrete(int idLembrete, String nomeLembrete, Date dataInicioLembrete, String periodicidadeLembrete, String descricaoLembrete, Time horarioLembrete, BooleanProperty ativoLembrete, Usuario usuario) {
        this.idLembrete = idLembrete;
        this.nomeLembrete = nomeLembrete;
        this.dataInicioLembrete = dataInicioLembrete;
        this.periodicidadeLembrete = periodicidadeLembrete;
        this.descricaoLembrete = descricaoLembrete;
        this.horarioLembrete = horarioLembrete;
        this.ativoLembrete = ativoLembrete;
        this.usuario = usuario;
    }

    public Lembrete(String nomeLembrete, Date dataInicioLembrete, String periodicidadeLembrete, String descricaoLembrete, Time horarioLembrete, BooleanProperty ativoLembrete, Usuario usuario) {
        this.nomeLembrete = nomeLembrete;
        this.dataInicioLembrete = dataInicioLembrete;
        this.periodicidadeLembrete = periodicidadeLembrete;
        this.descricaoLembrete = descricaoLembrete;
        this.horarioLembrete = horarioLembrete;
        this.ativoLembrete = ativoLembrete;
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

    public boolean isAtivoLembrete() {
        return ativoLembrete.get();
    }

    public void setAtivoLembrete(boolean ativo) {
        this.ativoLembrete.set(ativo);
    }

    public BooleanProperty ativoLembreteProperty() {
        return ativoLembrete;
    }

    public void setAtivoLembreteProperty(BooleanProperty ativo) {
        this.ativoLembrete.set(ativo.get());
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public StringProperty dataInicioFormatada() {
        if (dataInicioLembrete != null) {
            SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
            String dataFormatada = formatador.format(dataInicioLembrete);
            
            return new SimpleStringProperty(dataFormatada);
        }
        return new SimpleStringProperty("");
    }
}
