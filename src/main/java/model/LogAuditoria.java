package model;

import java.time.LocalDateTime;

public class LogAuditoria {
    //ATRIBUTOS
    private int idLog;
    private LocalDateTime dataHoraLog;
    private String descricaoLog;
    private int idUsuario;
    private String nomeUsuario;

    //CONSTRUTORES
    public LogAuditoria(int idLog, LocalDateTime dataHoraLog, String descricaoLog, int idUsuario, String nomeUsuario) {
        this.idLog = idLog;
        this.dataHoraLog = dataHoraLog;
        this.descricaoLog = descricaoLog;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
    }

    public LogAuditoria(String descricaoLog, int idUsuario, String nomeUsuario) {
        this.dataHoraLog = LocalDateTime.now();
        this.descricaoLog = descricaoLog;
        this.idUsuario = idUsuario;
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

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    } 
}
