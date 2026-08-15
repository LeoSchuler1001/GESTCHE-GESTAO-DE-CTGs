package model;

public class Categoria {
    //ATRIBUTOS
    private int idCategoria;
    private String nomeCategoria;
    private String corCategoria;
    private String iconeCategoria;

    //CONSTRUTORES
    public Categoria(int idCategoria, String nomeCategoria, String corCategoria, String iconeCategoria) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.corCategoria = corCategoria;
        this.iconeCategoria = iconeCategoria;
    }
    
    public Categoria(String nomeCategoria, String corCategoria, String iconeCategoria) {
        this.nomeCategoria = nomeCategoria;
        this.corCategoria = corCategoria;
        this.iconeCategoria = iconeCategoria;
    }
    
    public Categoria() {
    }
    
    //GETERS E SETERS
    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public String getCorCategoria() {
        return corCategoria;
    }

    public void setCorCategoria(String corCategoria) {
        this.corCategoria = corCategoria;
    }

    public String getIconeCategoria() {
        return iconeCategoria;
    }

    public void setIconeCategoria(String iconeCategoria) {
        this.iconeCategoria = iconeCategoria;
    }
}
