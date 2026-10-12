package model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RecebimentoPendenteDTO {
    //ATRIBUTOS
    private int idRecebimento;
    private LocalDateTime dataRecebimento;
    private String statusRecebimento;
    private int idDebito;
    private String tipoDebito;
    private Double valorDebito;
    private LocalDate vencimentoDebito;
    private int idSecretario;
    private String nomeSecretario;
    private String nomeSocio;

    //GETERS E SETERS
    public String getNomeSocio() { return nomeSocio; }
    public void setNomeSocio(String nomeSocio) { this.nomeSocio = nomeSocio; }

    public int getIdRecebimento() { return idRecebimento; }
    public void setIdRecebimento(int idRecebimento) { this.idRecebimento = idRecebimento; }

    public LocalDateTime getDataRecebimento() { return dataRecebimento; }
    public void setDataRecebimento(LocalDateTime dataRecebimento) { this.dataRecebimento = dataRecebimento; }

    public String getStatusRecebimento() { return statusRecebimento; }
    public void setStatusRecebimento(String statusRecebimento) { this.statusRecebimento = statusRecebimento; }

    public int getIdDebito() { return idDebito; }
    public void setIdDebito(int idDebito) { this.idDebito = idDebito; }

    public String getTipoDebito() { return tipoDebito; }
    public void setTipoDebito(String tipoDebito) { this.tipoDebito = tipoDebito; }

    public Double getValorDebito() { return valorDebito; }
    public void setValorDebito(Double valorDebito) { this.valorDebito = valorDebito; }

    public LocalDate getVencimentoDebito() { return vencimentoDebito; }
    public void setVencimentoDebito(LocalDate vencimentoDebito) { this.vencimentoDebito = vencimentoDebito; }

    public int getIdSecretario() { return idSecretario; }
    public void setIdSecretario(int idSecretario) { this.idSecretario = idSecretario; }

    public String getNomeSecretario() { return nomeSecretario; }
    public void setNomeSecretario(String nomeSecretario) { this.nomeSecretario = nomeSecretario; }

    public String getDataRecebimentoFormatada() {
        if (this.dataRecebimento != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return this.dataRecebimento.format(formatter);
        }
        return "";
    }
}