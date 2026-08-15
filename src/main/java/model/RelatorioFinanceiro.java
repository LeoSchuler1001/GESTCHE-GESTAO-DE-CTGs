package model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RelatorioFinanceiro {
    //ATRIBUTOS
    private Date dataInicio;
    private Date dataFim;
    private String tipoMovimentacao;
    private List <Conta> contasSelecionadas = new ArrayList<>();
    private List <Movimentacao> movimentacoesSelecionadas = new ArrayList<>();
    
    //CONSTRUTORES
    public RelatorioFinanceiro(Date dataInicio, Date dataFim, String tipoMovimentacao, List<Conta> contasSelecionadas, List<Movimentacao> movimentacoesSelecionadas) {
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipoMovimentacao = tipoMovimentacao;
        this.contasSelecionadas = contasSelecionadas;
        this.movimentacoesSelecionadas = movimentacoesSelecionadas;
    }

    public RelatorioFinanceiro() {
    }

    //GETERS E SETERS
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public List<Conta> getContasSelecionadas() {
        return contasSelecionadas;
    }

    public void setContasSelecionadas(List<Conta> contasSelecionadas) {
        this.contasSelecionadas = contasSelecionadas;
    }

    public List<Movimentacao> getMovimentacoesSelecionadas() {
        return movimentacoesSelecionadas;
    }

    public void setMovimentacoesSelecionadas(List<Movimentacao> movimentacoesSelecionadas) {
        this.movimentacoesSelecionadas = movimentacoesSelecionadas;
    }
}
