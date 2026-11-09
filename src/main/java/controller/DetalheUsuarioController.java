package controller;

import java.sql.SQLException;
import java.util.Optional;
import dao.ConexaoBanco;
import dao.EnderecoDAO;
import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Usuario;

public class DetalheUsuarioController {
    //ATRIBUTOS
    Usuario usuarioSelecionado;
    int idUsuarioSelecionado;

    //conexão com o banco de dados
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    UsuarioDAO usuarioDAO = new UsuarioDAO(conexaoBanco);
    EnderecoDAO enderecoDAO = new EnderecoDAO(conexaoBanco);

    @FXML
    private Button botaoFechar;

    @FXML
    private TextField campoBairroUsuario;

    @FXML
    private TextField campoCargoUsuario;

    @FXML
    private TextField campoCepUsuario;

    @FXML
    private TextField campoCidadeSocio;

    @FXML
    private TextField campoCpfUsuario;

    @FXML
    private TextField campoEstadoUsuario;

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

    //BOTÕES
    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //MÉTODOS
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //faz com que o usuário não possa mexer nos campos
        campoNomeUsuario.setMouseTransparent(true);
        campoNomeUsuario.setFocusTraversable(false);
        campoCpfUsuario.setMouseTransparent(true);
        campoCpfUsuario.setFocusTraversable(false);
        campoTelefoneUsuario.setMouseTransparent(true);
        campoTelefoneUsuario.setFocusTraversable(false);
        campoCargoUsuario.setMouseTransparent(true);
        campoCargoUsuario.setFocusTraversable(false);
        campoRuaUsuario.setMouseTransparent(true);
        campoRuaUsuario.setFocusTraversable(false);
        campoNumeroUsuario.setMouseTransparent(true);
        campoNumeroUsuario.setFocusTraversable(false);
        campoCepUsuario.setMouseTransparent(true);
        campoCepUsuario.setFocusTraversable(false);
        campoBairroUsuario.setMouseTransparent(true);
        campoBairroUsuario.setFocusTraversable(false);
        campoCidadeSocio.setMouseTransparent(true);
        campoCidadeSocio.setFocusTraversable(false);
        campoEstadoUsuario.setMouseTransparent(true);
        campoEstadoUsuario.setFocusTraversable(false);
    }

    public void carregarDadosEmSegundoPlano(int idUsuario) {
        this.idUsuarioSelecionado = idUsuario;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //busca os dados do usuario selecionado
                usuarioSelecionado = usuarioDAO.buscarPorId(idUsuario);
                return null;
            }
        };

        //atualiza os dados caso a busca tenha ocorrido bem
        task.setOnSucceeded(e -> {
            //atualiza os componentes na tela
            campoNomeUsuario.setText(usuarioSelecionado.getNomeUsuario());
            campoCpfUsuario.setText(usuarioSelecionado.getCpfUsuario());
            campoTelefoneUsuario.setText(usuarioSelecionado.getTelefoneUsuario());
            campoCargoUsuario.setText(usuarioSelecionado.getCargoUsuario());
            campoRuaUsuario.setText(usuarioSelecionado.getEndereco().getRua());
            campoNumeroUsuario.setText(String.valueOf(usuarioSelecionado.getEndereco().getNumero()));
            campoBairroUsuario.setText(usuarioSelecionado.getEndereco().getBairro());
            campoCidadeSocio.setText(usuarioSelecionado.getEndereco().getCidade());
            campoCepUsuario.setText(usuarioSelecionado.getEndereco().getCep());
            campoEstadoUsuario.setText(usuarioSelecionado.getEndereco().getEstado());
        });

        //mostra um aviso caso os dados não possam ser carregados
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() -> emitirAlerta("Erro ao carregar os dados.", AlertType.ERROR));
        });

        new Thread(task).start(); 
    }

    //método auxiliar para emitir alertas
    private boolean emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);

        Optional<ButtonType> resultado = alerta.showAndWait();

        // Verifica se o usuário clicou no botão OK
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}