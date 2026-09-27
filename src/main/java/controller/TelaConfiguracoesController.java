package controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.scene.input.MouseEvent;
import java.util.List;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.LogAuditoriaDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import model.Lembrete;
import model.LogAuditoria;

public class TelaConfiguracoesController {
    //ATRIBUTOS
    @FXML
    private TableColumn<Lembrete, String> lembretes;

    @FXML
    private TableColumn<LogAuditoria, String> dataLog;

    @FXML
    private TableColumn<LogAuditoria, String> descricaoLog;

    @FXML
    private TableColumn<LogAuditoria, String> horaLog;

    @FXML
    private TableColumn<LogAuditoria, String> usuarioLog;

    @FXML
    private Hyperlink linkDepartamentos;

    @FXML
    private Hyperlink linkGraficosRelatorios;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSociosDependentes;

    @FXML
    private TableView<Lembrete> tabelaLembretes;
    
    @FXML
    private TableView<LogAuditoria> tabelaLogsAuditoria;

    @FXML
    private Hyperlink linkSair;

    //BOTÕES
    @FXML
    void departamentosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaDepartamentos");
    }

    @FXML
    void graficosRelatoriosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaGraficosRelatorios");
    }

    @FXML
    void inicioAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInicialSecretario");
    }

    @FXML
    void lembretesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaLembretes");
    }

    @FXML
    void sociosDependentesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaSociosDependentes");
    }

    @FXML
    void sairAction(ActionEvent event) throws IOException {
        boolean confirmaSaida = emitirAlerta("Deseja realmente sair?", AlertType.CONFIRMATION);

        if (confirmaSaida) {
            App.trocarTela("TelaLogin");
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
    }

    //MÉTODOS
    //inicializa a tela
    public void initialize() throws SQLException {
        //desativa a seleção nas tabelas, mas mantêm o scroll
        Callback<TableView<LogAuditoria>, TableRow<LogAuditoria>> desativarSelecao = tv -> {
            TableRow<LogAuditoria> row = new TableRow<>();
            
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return row;
        };

        tabelaLogsAuditoria.setRowFactory(desativarSelecao);
        tabelaLogsAuditoria.setFocusTraversable(false);

        //desativa a seleção na tabela de lembretes, mas mantêm o scroll
        Callback<TableView<Lembrete>, TableRow<Lembrete>> desativarSelecaoLembrete = tv -> {
            TableRow<Lembrete> row = new TableRow<>();
            
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return row;
        };

        tabelaLembretes.setRowFactory(desativarSelecaoLembrete);
        tabelaLembretes.setFocusTraversable(false);

        //configura as colunas das tabelas
        this.descricaoLog.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescricaoLog())
        );

        this.dataLog.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getDataLog())
        );

        this.horaLog.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getHoraLog())
        );

        this.usuarioLog.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeUsuario())
        );

        this.lembretes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        //chama a função que irá carregar os dados das tabelas e dos mostradores
        carregarDadosSegundoPlano();        
    }

    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaLembretes.setPlaceholder(criarIndicator());
        tabelaLogsAuditoria.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                LembreteDAO lembreteDAO = new LembreteDAO(conexao);
                LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexao);

                //cria a lista que vai armazenar os lembretes e logs
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());
                List<LogAuditoria> listaLogs = logAuditoriaDAO.listarLogs();

                // Atualiza as tabelas e os gráficos
                Platform.runLater(() -> {
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));

                    tabelaLogsAuditoria.setItems(FXCollections.observableArrayList(listaLogs));

                    if (listaLembretes.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }

                    if (listaLogs.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem dados."));
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
}