package model.dto;

import java.sql.Date;

public class DatasTelaPeriodoDTO {
    //ATRIBUTOS
    private Date dataInicial;
    private Date dataFinal;
    
    //GETERS E SETERS
    public Date getDataInicial() {
        return dataInicial;
    }
    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }
    public Date getDataFinal() {
        return dataFinal;
    }
    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }   
}