package model;

public class Categoria {
    //ATRIBUTOS
    private int idCategoria;
    private String nomeCategoria;

    //CONSTRUTORES
    public Categoria(int idCategoria, String nomeCategoria) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
    }
    
    public Categoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
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
}