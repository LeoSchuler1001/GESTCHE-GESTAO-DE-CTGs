package model;

public class Usuario {
    //ATRIBUTOS
    private int idUsuario;
    private String cpfUsuario;
    private String nomeUsuario;
    private String telefoneUsuario;
    private String cargoUsuario;
    private String senhaHash;
    private String respostaSeguranca;
    private Endereco endereco;

    //CONSTRUTORES
    //criar novos usuários - INSERT no banco de dados
    public Usuario(String cpfUsuario, String nomeUsuario, String telefoneUsuario, Endereco endereco, String cargoUsuario, String senhaHash, String respostaSeguranca) {
        this.cpfUsuario = cpfUsuario;
        this.nomeUsuario = nomeUsuario;
        this.telefoneUsuario = telefoneUsuario;
        this.endereco = endereco;
        this.cargoUsuario = cargoUsuario;
        this.senhaHash = senhaHash;
        this.respostaSeguranca = respostaSeguranca;
    }

    //alterar usuário - SELECT, UPDATE e DELETE no banco de dados
    public Usuario(int idUsuario, String cpfUsuario, String nomeUsuario, String telefoneUsuario, Endereco endereco, String cargoUsuario, String senhaHash, String respostaSeguranca) {
        this.idUsuario = idUsuario;
        this.cpfUsuario = cpfUsuario;
        this.nomeUsuario = nomeUsuario;
        this.telefoneUsuario = telefoneUsuario;
        this.endereco = endereco;
        this.cargoUsuario = cargoUsuario;
        this.senhaHash = senhaHash;
        this.respostaSeguranca = respostaSeguranca;
    }

    //construtor vazio
    public Usuario() {
    }
    
    //GETERS E SETERS
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getTelefoneUsuario() {
        return telefoneUsuario;
    }

    public void setTelefoneUsuario(String telefoneUsuario) {
        this.telefoneUsuario = telefoneUsuario;
    }

    public String getCargoUsuario() {
        return cargoUsuario;
    }

    public void setCargoUsuario(String cargoUsuario) {
        this.cargoUsuario = cargoUsuario;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getRespostaSeguranca() {
        return respostaSeguranca;
    }

    public void setRespostaSeguranca(String respostaSeguranca) {
        this.respostaSeguranca = respostaSeguranca;
    }

    //MÉTODOS
    public String exibirDados() {
        String mensagem = "";
        mensagem += "Nome: " + this.nomeUsuario;
        mensagem += "\nCPF: " + this.cpfUsuario;
        mensagem += "\nTelefone: " + this.telefoneUsuario;
        mensagem += "\nEndereco: \n" + this.endereco.exibirDados();
        mensagem += "\nCargo: " + this.cargoUsuario;

        return mensagem;
    }

    public void cadastrarLog(String descricao) {
        //instancia um objeto do log de auditoria
        LogAuditoria logAuditoria = new LogAuditoria(descricao, this, this.nomeUsuario);

        //salva no banco de dados
    }
}
