package controller;

import java.io.IOException;
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
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import model.Lembrete;
import model.LogAuditoria;

public class TelaConfiguracoesTesoureiroController {
    //ATRIBUTOS
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexaoBanco);
    LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexaoBanco);

    @FXML
    private Hyperlink linkCategorias;

    @FXML
    private Hyperlink linkContas;

    @FXML
    private Hyperlink linkDebitosSocios;

    @FXML
    private Hyperlink linkGraficosRelatorios;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableColumn<Lembrete, String> nomeLembrete;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableColumn<Lembrete, String> valorLembrete;

    @FXML
    private TableView<LogAuditoria> tabelaLogsAuditoria;

    @FXML
    private TableColumn<LogAuditoria, String> usuarioLog;

    @FXML
    private TableColumn<LogAuditoria, String> dataLog;

    @FXML
    private TableColumn<LogAuditoria, String> descricaoLog;

    @FXML
    private TableColumn<LogAuditoria, String> horaLog;

    //BOTÕES
    @FXML
    void categoriasAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaCategorias");
    }

    @FXML
    void contasAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaContas");
    }

    @FXML
    void debitosSociosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaDebitosSocios");
    }

    @FXML
    void graficosRelatoriosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaGraficosRelatoriosTesoureiro");
    }

    @FXML
    void inicioAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroMes");
    }

    @FXML
    void lembretesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaLembretesTesoureiro");
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

        //desativa a seleção nas tabelas de log, mas mantêm o scroll
        Callback<TableView<LogAuditoria>, TableRow<LogAuditoria>> desativarSelecao = tv -> {
            TableRow<LogAuditoria> row = new TableRow<>();
            
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return row;
        };
        tabelaLogsAuditoria.setRowFactory(desativarSelecao);
        tabelaLogsAuditoria.setFocusTraversable(false);

        //configura as colunas das tabelas para receber os nomes dos sócios e lembretes
        this.nomeLembrete.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        this.valorLembrete.setCellValueFactory(cellData -> {
            double valor = cellData.getValue().getValorLembrete();
            String formatado = String.format("R$ %.2f", valor);
            return new SimpleStringProperty(formatado);
        });

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

        carregarDadosEmSegundoPlano();
    }

    private void carregarDadosEmSegundoPlano() {
        tabelaLembretes.getItems().clear();
        tabelaLogsAuditoria.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaLembretes.setPlaceholder(criarIndicator());
        tabelaLogsAuditoria.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Lembrete> listaLembretesHoje = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());
                List<LogAuditoria> listaLogs = logAuditoriaDAO.listarLogs();

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretesHoje));

                    tabelaLogsAuditoria.setItems(FXCollections.observableArrayList(listaLogs));
                    
                    if (listaLembretesHoje.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }

                    if (listaLogs.isEmpty()) {
                        tabelaLogsAuditoria.setPlaceholder(new javafx.scene.control.Label("Sem dados."));
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