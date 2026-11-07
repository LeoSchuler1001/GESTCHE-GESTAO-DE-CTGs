package util;

import model.Conta;
import java.time.LocalDate;
import java.util.List;

public interface RelatorioContasProvider {
    List<Conta> getListaContas();
    Double getSaldoTotal();
    String getTituloRelatorio();
    LocalDate getDataEmissao();
}