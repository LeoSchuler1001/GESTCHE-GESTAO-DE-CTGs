package model.dto;

import java.util.List;

public class SocioResumoDTO {
    //ATRIBUTOS
    private String nomeSocio;
    private int idSocio;
    private boolean situacaoAdimplente;
    private List<String> dependentes;
    private List<String> departamentos;

    //CONSTRUTORES
    public SocioResumoDTO() {}

    public SocioResumoDTO(String nomeSocio, boolean situacaoAdimplente, List<String> dependentes, List<String> departamentos, int idSocio) {
        this.nomeSocio = nomeSocio;
        this.situacaoAdimplente = situacaoAdimplente;
        this.dependentes = dependentes;
        this.departamentos = departamentos;
        this.idSocio = idSocio;
    }

    // GETERS E SETERS
    public String getNomeSocio() { return nomeSocio; }
    public void setNomeSocio(String nomeSocio) { this.nomeSocio = nomeSocio; }

    public int getIdSocio() { return idSocio;}
    public void setIdSocio(int idSocio) { this.idSocio = idSocio; }

    public boolean isSituacaoAdimplente() { return situacaoAdimplente; }
    public void setSituacaoAdimplente(boolean situacaoAdimplente) { this.situacaoAdimplente = situacaoAdimplente; }

    public List<String> getDependentes() { return dependentes; }
    public void setDependentes(List<String> dependentes) { this.dependentes = dependentes; }

    public List<String> getDepartamentos() { return departamentos; }
    public void setDepartamentos(List<String> departamentos) { this.departamentos = departamentos; }

    //MÉTODOS
    //formata a lista de dependentes como uma string
    public String getDependentesFormatado() {
        return (dependentes == null || dependentes.isEmpty()) ? "Nenhum" : String.join(", ", dependentes);
    }

    //formara a lista de departamentos como uma string
    public String getDepartamentosFormatado() {
        return (departamentos == null || departamentos.isEmpty()) ? "Nenhum" : String.join(", ", departamentos);
    }

    //formata a situação do sócio como uma string
    public String getSituacaoTexto() {
        return situacaoAdimplente ? "Regular" : "Inadimplente";
    }
}