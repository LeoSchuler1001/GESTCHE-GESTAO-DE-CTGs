package model;

public class Conta {
    //ATRIBUTOS
    private int idConta;
    private String nomeConta;
    private Double saldo;
    //CONSTRUTORES
    public Conta(int idConta, String nomeConta, Double saldo) {
        this.idConta = idConta;
        this.nomeConta = nomeConta;
        this.saldo = saldo;
    }

    public Conta(String nomeConta, Double saldo) {
        this.nomeConta = nomeConta;
        this.saldo = saldo;
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

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}