package controller;

import java.sql.SQLException;

import app.App;
import dao.ConexaoBanco;
import dao.EnderecoDAO;
import dao.LogAuditoriaDAO;
import dao.UsuarioDAO;
import enums.CargosUsuarios;
import enums.EstadosBrasil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Endereco;
import model.Usuario;
import util.Criptografia;

public class CadastrarUsuarioController {
    //conexões com o banco de dados
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    UsuarioDAO usuarioDAO = new UsuarioDAO(conexaoBanco);
    EnderecoDAO enderecoDAO = new EnderecoDAO(conexaoBanco);
    LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexaoBanco);

    @FXML
    private Button botaoFechar;

    @FXML
    private Button botaoSalvar;

    @FXML
    private TextField campoBairroUsuario;

    @FXML
    private ComboBox<String> campoCargoUsuario;

    @FXML
    private TextField campoCepUsuario;

    @FXML
    private TextField campoCidadeSocio;

    @FXML
    private TextField campoCpfUsuario;

    @FXML
    private ComboBox<EstadosBrasil> campoEstadoUsuario;

    @FXML
    private TextField campoNomeUsuario;

    @FXML
    private TextField campoNumeroUsuario;

    @FXML
    private TextField campoRuaUsuario;

    @FXML
    private TextField campoTelefoneUsuario;

    @FXML
    private TextField campoSenha;

    @FXML
    private TextField campoRespostaSeguranca;

    @FXML
    private VBox painelFundo;

    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    @FXML
    void salvarAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return ;}

        Usuario usuarioSelecionado = new Usuario();

        //altera as informações no objeto usuario
        String cpfLimpo = campoCpfUsuario.getText().replaceAll("[^0-9]", "");
        usuarioSelecionado.setCpfUsuario(cpfLimpo);
        usuarioSelecionado.setNomeUsuario(campoNomeUsuario.getText());
        usuarioSelecionado.setTelefoneUsuario(campoTelefoneUsuario.getText());
        usuarioSelecionado.setCargoUsuario(campoCargoUsuario.getValue());

        String hashSenha = Criptografia.gerarHash(campoSenha.getText());
        String hashResposta = Criptografia.gerarHash(campoRespostaSeguranca.getText());
        usuarioSelecionado.setSenhaHash(hashSenha);
        usuarioSelecionado.setRespostaSeguranca(hashResposta);

        Endereco endereco = new Endereco();
        usuarioSelecionado.setEndereco(endereco);

        //altera os dados do endereço
        usuarioSelecionado.getEndereco().setRua(campoRuaUsuario.getText());
        usuarioSelecionado.getEndereco().setNumero(Integer.valueOf(campoNumeroUsuario.getText()));
        usuarioSelecionado.getEndereco().setBairro(campoBairroUsuario.getText());
        usuarioSelecionado.getEndereco().setCidade(campoCidadeSocio.getText());
        usuarioSelecionado.getEndereco().setCep(campoCepUsuario.getText());
        usuarioSelecionado.getEndereco().setEstado(campoEstadoUsuario.getValue().name());

        enderecoDAO.cadastrarEndereco(usuarioSelecionado.getEndereco());

        usuarioDAO.cadastrarUsuario(usuarioSelecionado);

        //registra um log de auditoria
        String descricao = "Cadastro do usuario " + usuarioSelecionado.getNomeUsuario() + " com o cargo de " + usuarioSelecionado.getCargoUsuario();
        usuarioDAO.cadastrarLogUsuario(descricao, App.usuarioLogado);


        emitirAlerta("Usuario cadastrado com sucesso", AlertType.INFORMATION);
            
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //FUNÇÕES
    //configura os elementos da tela
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //preenche a lista de estados
        campoEstadoUsuario.getItems().setAll(EstadosBrasil.values());

        //preenche o combobox das periodicidades
        campoCargoUsuario.getItems().clear();
        for (CargosUsuarios cargos : CargosUsuarios.values()) {
            campoCargoUsuario.getItems().add(cargos.getDescricao());
        }
    }

    public boolean verificaFormulario() {
        //verifica campos de texto
        if (campoNomeUsuario.getText() == null || campoNomeUsuario.getText().trim().isEmpty() ||
            campoCpfUsuario.getText() == null || campoCpfUsuario.getText().trim().isEmpty() ||
            campoRuaUsuario.getText() == null || campoRuaUsuario.getText().trim().isEmpty() ||
            campoNumeroUsuario.getText() == null || campoNumeroUsuario.getText().trim().isEmpty() ||
            campoBairroUsuario.getText() == null || campoBairroUsuario.getText().trim().isEmpty() ||
            campoCidadeSocio.getText() == null || campoCidadeSocio.getText().trim().isEmpty() ||
            campoRespostaSeguranca.getText() == null || campoRespostaSeguranca.getText().trim().isEmpty() ||
            campoSenha.getText() == null || campoSenha.getText().trim().isEmpty() ||
            campoCepUsuario.getText() == null || campoCepUsuario.getText().trim().isEmpty()) {
            
            emitirAlerta("Preencha todos os campos!", AlertType.ERROR);
            return false;
        }

        //verifica se o cpf está correto
        String cpfLimpo = campoCpfUsuario.getText().replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            emitirAlerta("O CPF deve conter exatamente 11 dígitos!", AlertType.ERROR);
            return false;
        }

        //verifica se o cep está correto
        String cepLimpo = campoCepUsuario.getText().replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            emitirAlerta("O CEP deve conter exatamente 8 dígitos!", AlertType.ERROR);
            return false;
        }

        //verificação do telefone
        String telefoneTexto = campoTelefoneUsuario.getText();
        if (telefoneTexto != null && !telefoneTexto.trim().isEmpty() && !telefoneTexto.equals("Telefone não cadastrado!")) {
            String telefoneLimpo = telefoneTexto.replaceAll("[^0-9]", "");
            if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
                emitirAlerta("O telefone deve conter 10 ou 11 dígitos (com DDD)!", AlertType.ERROR);
                return false;
            }
        }

        //verifica o estado
        if (campoEstadoUsuario.getValue() == null) {
            emitirAlerta("Selecione o estado!", AlertType.ERROR);
            return false;
        }

        //verifica o estado
        if (campoCargoUsuario.getValue() == null) {
            emitirAlerta("Selecione o cargo!", AlertType.ERROR);
            return false;
        }

        //verifica se o número do endereço contém apenas números
        String numeroTexto = campoNumeroUsuario.getText();
        if (numeroTexto != null && !numeroTexto.matches("\\d+")) {
            emitirAlerta("O número do endereço deve conter apenas dígitos!", AlertType.ERROR);
            return false;
        }
        
        return true;
    }

    //emite um alerta
    private void emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}