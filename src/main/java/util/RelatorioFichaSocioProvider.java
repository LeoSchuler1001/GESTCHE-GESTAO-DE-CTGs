package util;

import model.Debito;
import model.Endereco;
import model.Socio;
import java.time.LocalDate;
import java.util.List;

public interface RelatorioFichaSocioProvider {
    Socio getSocio();
    Endereco getEndereco();
    List<String> getListaDepartamentos();
    List<String> getListaDependentes();
    List<Debito> getListaDebitos();
    LocalDate getDataEmissao();
}