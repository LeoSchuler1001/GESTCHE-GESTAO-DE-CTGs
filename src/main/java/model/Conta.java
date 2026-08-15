package model;

public class Conta {
    //ATRIBUTOS
    private int idConta;
    private String nomeConta;
    private String corConta;
    private String iconeConta;

    //CONSTRUTORES
    public Conta(int idConta, String nomeConta, String corConta, String iconeConta) {
        this.idConta = idConta;
        this.nomeConta = nomeConta;
        this.corConta = corConta;
        this.iconeConta = iconeConta;
    }

    public Conta(String nomeConta, String corConta, String iconeConta) {
        this.nomeConta = nomeConta;
        this.corConta = corConta;
        this.iconeConta = iconeConta;
    }

    public Conta() {
    }

    //GETERS E SETERS
    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getNomeConta() {
        return nomeConta;
    }

    public void setNomeConta(String nomeConta) {
        this.nomeConta = nomeConta;
    }

    public String getCorConta() {
        return corConta;
    }

    public void setCorConta(String corConta) {
        this.corConta = corConta;
    }

    public String getIconeConta() {
        return iconeConta;
    }

    public void setIconeConta(String iconeConta) {
        this.iconeConta = iconeConta;
    }
}
