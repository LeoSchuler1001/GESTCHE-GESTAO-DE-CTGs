package model.dto;

import java.sql.Date;

public class DatasTelaPeriodoDTO {
    //ATRIBUTOS
    public static Date dataInicial;
    public static Date dataFinal;
    
    //GETERS E SETERS
    public static Date getDataInicial() {
        return dataInicial;
    }
    
    public static Date getDataFinal() {
        return dataFinal;
    } 
}