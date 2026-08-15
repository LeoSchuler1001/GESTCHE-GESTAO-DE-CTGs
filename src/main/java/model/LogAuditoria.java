package model;

import java.time.LocalDateTime;

public class LogAuditoria {
    //ATRIBUTOS
    private int idLog;
    private LocalDateTime dataHoraLog;
    private String descricaoLog;
    private Usuario usuario;
    private String nomeUsuario;

    //CONSTRUTORES
    public LogAuditoria(int idLog, LocalDateTime dataHoraLog, String descricaoLog, Usuario usuario, String nomeUsuario) {
        this.idLog = idLog;
        this.dataHoraLog = dataHoraLog;
        this.descricaoLog = descricaoLog;
        this.usuario = usuario;
        this.nomeUsuario = nomeUsuario;
    }

    public LogAuditoria(String descricaoLog, Usuario usuario, String nomeUsuario) {
        this.dataHoraLog = LocalDateTime.now();
        this.descricaoLog = descricaoLog;
        this.usuario = usuario;
        this.nomeUsuario = nomeUsuario;
    }

    public LogAuditoria() {
    }
    
    //GETERS E SETERS
    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public LocalDateTime getDataHoraLog() {
        return dataHoraLog;
    }

    public void setDataHoraLog(LocalDateTime dataHoraLog) {
        this.dataHoraLog = dataHoraLog;
    }

    public String getDescricaoLog() {
        return descricaoLog;
    }

    public void setDescricaoLog(String descricaoLog) {
        this.descricaoLog = descricaoLog;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    } 
}
