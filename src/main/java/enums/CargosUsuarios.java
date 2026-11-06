package enums;

public enum CargosUsuarios {
    Diretor("Diretor"),
    Tesoureiro("Tesoureiro"),
    Secretario("Secretário");

    private final String descricao;

    CargosUsuarios(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
