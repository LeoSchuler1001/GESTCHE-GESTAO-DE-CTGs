package util;

import model.LogAuditoria;
import java.time.LocalDate;
import java.util.List;

public interface RelatorioAuditoriaProvider {
    List<LogAuditoria> getListaLogs();
    String getTituloRelatorio();
    LocalDate getDataEmissao();
}