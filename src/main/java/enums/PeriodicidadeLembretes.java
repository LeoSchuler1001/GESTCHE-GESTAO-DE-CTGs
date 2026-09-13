package enums;

public enum PeriodicidadeLembretes {
    umaVez("UMA VEZ"),
    diario("DIÁRIO"),
    semanal("SEMANAL"),
    quinzenal("QUINZENAL"),
    mensal("MENSAL"),
    anual("ANUAL");

    private final String descricao;

    PeriodicidadeLembretes(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
