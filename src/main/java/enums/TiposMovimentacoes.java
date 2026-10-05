package enums;

public enum TiposMovimentacoes {
    Receita("Receita"),
    Despesa("Despesa");

    private final String descricao;

    TiposMovimentacoes(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
