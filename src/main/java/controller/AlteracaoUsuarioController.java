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
import model.LogAuditoria;
import model.Usuario;

public class AlteracaoUsuarioController {
    //ATRIBUTOS
    private int idUsuarioSelecionado;
    Usuario usuarioSelecionado;

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
    private VBox painelFundo;

    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    @FXML
    void salvarAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return ;}

        String cargoantigo = usuarioSelecionado.getCargoUsuario();

        //altera as informações no objeto usuario
        String cpfLimpo = campoCpfUsuario.getText().replaceAll("[^0-9]", "");
        usuarioSelecionado.setCpfUsuario(cpfLimpo);
        usuarioSelecionado.setNomeUsuario(campoNomeUsuario.getText());
        usuarioSelecionado.setTelefoneUsuario(campoTelefoneUsuario.getText());
        usuarioSelecionado.setCargoUsuario(campoCargoUsuario.getValue());

        //altera os dados do endereço
        usuarioSelecionado.getEndereco().setRua(campoRuaUsuario.getText());
        usuarioSelecionado.getEndereco().setNumero(Integer.valueOf(campoNumeroUsuario.getText()));
        usuarioSelecionado.getEndereco().setBairro(campoBairroUsuario.getText());
        usuarioSelecionado.getEndereco().setCidade(campoCidadeSocio.getText());
        usuarioSelecionado.getEndereco().setCep(campoCepUsuario.getText());
        usuarioSelecionado.getEndereco().setEstado(campoEstadoUsuario.getValue().name());

        usuarioDAO.atualizarUsuario(usuarioSelecionado);
        enderecoDAO.alterarEndereco(usuarioSelecionado.getEndereco());

        //registra um log de auditoria
        if(cargoantigo != usuarioSelecionado.getCargoUsuario()) {
            String descricao = "Alteração do cargo do usuário " + usuarioSelecionado.getNomeUsuario() + " de " + cargoantigo + " para " + usuarioSelecionado.getCargoUsuario();
            LogAuditoria log = new LogAuditoria(descricao, App.usuarioLogado, App.usuarioLogado.getNomeUsuario());

            logAuditoriaDAO.cadastrarLog(log, App.usuarioLogado);
        }


        emitirAlerta("usuario alterado com sucesso", AlertType.INFORMATION);
            
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

    //define qual é o sócio que foi selecionado
    public void setIdUsuarioSelecionado(int idUsuario) throws SQLException {
        this.idUsuarioSelecionado = idUsuario;
        
        // Carrega o sócio e preenche a tela
        buscarDadosUsuario(); 
    }

    private void buscarDadosUsuario() throws SQLException {
        usuarioSelecionado = usuarioDAO.buscarPorId(idUsuarioSelecionado);

        //preenche os campos com os dados do usuário
        campoNomeUsuario.setText(usuarioSelecionado.getNomeUsuario());
        campoCpfUsuario.setText(usuarioSelecionado.getCpfUsuario());
        campoTelefoneUsuario.setText(usuarioSelecionado.getTelefoneUsuario());
        campoCargoUsuario.setValue(usuarioSelecionado.getCargoUsuario());
        campoRuaUsuario.setText(usuarioSelecionado.getEndereco().getRua());
        campoNumeroUsuario.setText(String.valueOf(usuarioSelecionado.getEndereco().getNumero()));
        campoBairroUsuario.setText(usuarioSelecionado.getEndereco().getBairro());
        campoCidadeSocio.setText(usuarioSelecionado.getEndereco().getCidade());
        campoCepUsuario.setText(usuarioSelecionado.getEndereco().getCep());
        campoEstadoUsuario.setValue(EstadosBrasil.valueOf(usuarioSelecionado.getEndereco().getEstado()));
    }

    public boolean verificaFormulario() {
        //verifica campos de texto
        if (campoNomeUsuario.getText() == null || campoNomeUsuario.getText().trim().isEmpty() ||
            campoCpfUsuario.getText() == null || campoCpfUsuario.getText().trim().isEmpty() ||
            campoRuaUsuario.getText() == null || campoRuaUsuario.getText().trim().isEmpty() ||
            campoNumeroUsuario.getText() == null || campoNumeroUsuario.getText().trim().isEmpty() ||
            campoBairroUsuario.getText() == null || campoBairroUsuario.getText().trim().isEmpty() ||
            campoCidadeSocio.getText() == null || campoCidadeSocio.getText().trim().isEmpty() ||
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