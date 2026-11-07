package util;

import model.Conta;
import model.Movimentacao;
import java.time.LocalDate;
import java.util.List;

public interface RelatorioFinanceiroProvider {
    String getTituloPeriodo(); 
    LocalDate getDataInicio();
    LocalDate getDataFim();
    List<Movimentacao> getListaReceitas();
    List<Movimentacao> getListaDespesas();
    List<Conta> getListaContas();
    double getSaldoTotal();
}