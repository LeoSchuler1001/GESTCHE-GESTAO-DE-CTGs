package model;

import java.sql.Date;

public class Movimentacao {
    //ATRIBUTOS
    private int idMovimentacao;
    private double valorMovimentacao;
    private Date dataMovimentacao;
    private String comentarioMovimentacao;
    private String tipoMovimentacao;
    private Conta conta;
    private Categoria categoria;
    private Usuario usuario;
    private Lembrete lembrete;

    //CONSTRUTORES
    public Movimentacao(int idMovimentacao, double valorMovimentacao, Date dataMovimentacao, String comentarioMovimentacao, String tipoMovimentacao, Conta conta, Categoria categoria, Usuario usuario, Lembrete lembrete) {
        this.idMovimentacao = idMovimentacao;
        this.valorMovimentacao = valorMovimentacao;
        this.dataMovimentacao = dataMovimentacao;
        this.comentarioMovimentacao = comentarioMovimentacao;
        this.tipoMovimentacao = tipoMovimentacao;
        this.conta = conta;
        this.categoria = categoria;
        this.usuario = usuario;
        this.lembrete = lembrete;
    }
    
    public Movimentacao(double valorMovimentacao, Date dataMovimentacao, String comentarioMovimentacao, String tipoMovimentacao, Conta conta, Categoria categoria, Usuario usuario, Lembrete lembrete) {
        this.valorMovimentacao = valorMovimentacao;
        this.dataMovimentacao = dataMovimentacao;
        this.comentarioMovimentacao = comentarioMovimentacao;
        this.tipoMovimentacao = tipoMovimentacao;
        this.conta = conta;
        this.categoria = categoria;
        this.usuario = usuario;
        this.lembrete = lembrete;
    }

    public Movimentacao() {
    }

    //GETERS E SETERS
    public int getIdMovimentacao() {
        return idMovimentacao;
    }


    public void setIdMovimentacao(int idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public double getValorMovimentacao() {
        return valorMovimentacao;
    }

    public void setValorMovimentacao(double valorMovimentacao) {
        this.valorMovimentacao = valorMovimentacao;
    }

    public Date getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(Date dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public String getComentarioMovimentacao() {
        return comentarioMovimentacao;
    }

    public void setComentarioMovimentacao(String comentarioMovimentacao) {
        this.comentarioMovimentacao = comentarioMovimentacao;
    }

    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }
}
