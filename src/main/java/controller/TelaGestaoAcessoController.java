package controller;

import java.util.List;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import model.Lembrete;
import model.Usuario;

public class TelaGestaoAcessoController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    UsuarioDAO UsuarioDAO = new UsuarioDAO(conexao);
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);

    @FXML
    private Button botaoAlterarDados;

    @FXML
    private Button botaoAtivarUsuario;

    @FXML
    private Button botaoCadastrarUsuario;

    @FXML
    private Button botaoInativarUsuario;

    @FXML
    private Button botaoVerDados;

    @FXML
    private TableColumn<Usuario, String> colunaCargoUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaEnderecoUsuarios;

    @FXML
    private TableColumn<Lembrete, String> colunaLembretes;

    @FXML
    private TableColumn<Usuario, String> colunaNomeUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaSituacaoUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaTelefoneUsuarios;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableView<Usuario> tabelaUsuarios;

    @FXML
    void alterarDadosAction(ActionEvent event) {

    }

    @FXML
    void ativarUsuarioAction(ActionEvent event) {

    }

    @FXML
    void cadastrarUsuarioAction(ActionEvent event) {

    }

    @FXML
    void configuracoesAction(ActionEvent event) {

    }

    @FXML
    void inativarUsuarioAction(ActionEvent event) {

    }

    @FXML
    void inicioAction(ActionEvent event) {

    }

    @FXML
    void lembretesAction(ActionEvent event) {

    }

    @FXML
    void sairAction(ActionEvent event) {

    }

    @FXML
    void verDadosAction(ActionEvent event) {

    }

    //MÉTODOS
    public void initialize() {
        //desativa a seleção na tabela de lembretes, mas mantêm o scroll
        Callback<TableView<Lembrete>, TableRow<Lembrete>> desativarSelecaoLembrete = tv -> {
            TableRow<Lembrete> row = new TableRow<>();
            
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return row;
        };
        tabelaLembretes.setRowFactory(desativarSelecaoLembrete);
        tabelaLembretes.setFocusTraversable(false);

        //configura as colunas das tabelas para receber os nomes dos sócios e lembretes
        this.colunaNomeUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeUsuario())
        );

        this.colunaTelefoneUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelefoneUsuario())
        );

        this.colunaCargoUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCargoUsuario())
        );

        this.colunaSituacaoUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSituacaoUsuario())
        );

        this.colunaEnderecoUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEnderecoString())
        );

        //configura a coluna da tabela de lembretes
        this.colunaLembretes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        //chama a função que irá carregar os dados das tabelas e dos mostradores
        carregarDadosSegundoPlano();
    }

    private void carregarDadosSegundoPlano() {
        tabelaLembretes.getItems().clear();
        tabelaUsuarios.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaUsuarios.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Usuario> listaUsuarios = UsuarioDAO.listarTodos();
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaUsuarios.setItems(FXCollections.observableArrayList(listaUsuarios));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));

                    if (listaLembretes.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }

                    if (listaUsuarios.isEmpty()) {
                        tabelaUsuarios.setPlaceholder(new javafx.scene.control.Label("Sem sócios cadastrados."));
                    }
                });

                return null;
            }
        };

        //mostra um aviso caso os dados não possam ser carregados
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() -> emitirAlerta("Erro ao carregar os dados.", AlertType.ERROR));
        });

        //cria uma nova Thread para rodar a tarefa de carregamento em segundo plano
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

    // Método auxiliar para criar instâncias padronizadas do ProgressIndicator
    private ProgressIndicator criarIndicator() {
        ProgressIndicator indicador = new ProgressIndicator();
        indicador.setMaxSize(40, 40);
        return indicador;
    }

    private boolean emitirAlertaConfirmacao(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);

        Optional<ButtonType> resultado = alerta.showAndWait();

        // Verifica se o usuário clicou no botão OK
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}