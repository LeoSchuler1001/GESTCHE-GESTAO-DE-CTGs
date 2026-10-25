package model;

public class Categoria {
    //ATRIBUTOS
    private int idCategoria;
    private String nomeCategoria;
    private String tipoCategoria;

    //CONSTRUTORES
    public Categoria(int idCategoria, String nomeCategoria, String tipoCategoria) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.tipoCategoria = tipoCategoria;
    }
    
    public Categoria(String nomeCategoria, String tipoCategoria) {
        this.nomeCategoria = nomeCategoria;
        this.tipoCategoria = tipoCategoria;
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

    public String getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(String tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }
}