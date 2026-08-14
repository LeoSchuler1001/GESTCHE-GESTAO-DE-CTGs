package model;

public class Endereco {
    //ATRIBUTOS
    private int idEndereco;
    private String rua;
    private int numero;
    private String cep;
    private String bairro;
    private String cidade;
    private String estado;
    
    //CONSTRUTORES
    public Endereco(int idEndereco, String rua, int numero, String cep, String bairro, String cidade, String estado) {
        this.idEndereco = idEndereco;
        this.rua = rua;
        this.numero = numero;
        this.cep = cep;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public Endereco(String rua, int numero, String cep, String bairro, String cidade, String estado) {
        this.rua = rua;
        this.numero = numero;
        this.cep = cep;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }
    
    public Endereco() {
    }
    
    //GETERS E SETERS
    public int getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(int idEndereco) {
        this.idEndereco = idEndereco;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    //MÉTODOS
    public String exibirDados() {
        String mensagem = "";
        mensagem += "   Rua: " + this.rua;
        mensagem += "\n   Número: " + String.valueOf(this.numero);
        mensagem += "\n   CEP: " + this.cep;
        mensagem += "\n   Bairro: " + this.bairro;
        mensagem += "\n   Cidade: " + this.cidade;
        mensagem += "\n   Estado: " + this.estado;

        return mensagem;
    }
}
