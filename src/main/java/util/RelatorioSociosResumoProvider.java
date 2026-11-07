package util;

import model.dto.SocioResumoDTO;
import java.time.LocalDate;
import java.util.List;

public interface RelatorioSociosResumoProvider {
    List<SocioResumoDTO> getListaSociosResumo();
    String getTituloRelatorio();
    LocalDate getDataEmissao();
}