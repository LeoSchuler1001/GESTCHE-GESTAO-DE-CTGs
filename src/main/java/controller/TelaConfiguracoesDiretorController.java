package controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.Lembrete;
import model.LogAuditoria;
import util.GerarPdfAuditoria;
import util.RelatorioAuditoriaProvider;

public class TelaConfiguracoesDiretorController implements RelatorioAuditoriaProvider {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexao);


    @FXML
    private TableColumn<Lembrete, String> colunaLembretes;

    @FXML
    private TableColumn<LogAuditoria, String> dataLog;

    @FXML
    private TableColumn<LogAuditoria, String> descricaoLog;

    @FXML
    private TableColumn<LogAuditoria, String> horaLog;

    @FXML
    private Hyperlink linkInicio;
        
    @FXML
    private Button botaoGerarPdf;

    @FXML
    private Hyperlink linkGestaoAcesso;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableView<LogAuditoria> tabelaLogsAuditoria;

    @FXML
    private TableColumn<LogAuditoria, String> usuarioLog;

    //BOTÕES
    @FXML
    void gerarPdfAction(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar Relatório de Auditoria");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo PDF (*.pdf)", "*.pdf"));
            
            String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            fileChooser.setInitialFileName("Relatorio_Auditoria_" + dataHoje + ".pdf");
            
            File arquivoDestino = fileChooser.showSaveDialog(tabelaLogsAuditoria.getScene().getWindow());
            
            if (arquivoDestino != null) {
                GerarPdfAuditoria.gerar(this, arquivoDestino);
                emitirAlerta("Relatório de auditoria gerado com sucesso!", AlertType.INFORMATION);
            }
        } catch (java.io.FileNotFoundException e) {
            emitirAlerta("O arquivo PDF está aberto em outro programa. Feche-o e tente novamente.", AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            emitirAlerta("Erro ao gerar o PDF de auditoria: " + e.getMessage(), AlertType.ERROR);
        }
    }


    @FXML
    void lembretesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaLembretesDiretor");
    }

    @FXML
    void inicioAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInDiretor");
    }

    @FXML
    void gestaoAcessoAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaGestaoAcesso");
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

        this.colunaLembretes.setCellValueFactory(cellData -> 
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

        botaoGerarPdf.setDisable(true);

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a lista que vai armazenar os lembretes e logs
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());
                List<LogAuditoria> listaLogs = logAuditoriaDAO.listarLogs();

                // Atualiza as tabelas e os gráficos
                Platform.runLater(() -> {
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));

                    tabelaLogsAuditoria.setItems(FXCollections.observableArrayList(listaLogs));
                    
                    botaoGerarPdf.setDisable(false);

                    if (listaLembretes.isEmpty()) {
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

    // Método auxiliar para criar instâncias padronizadas do ProgressIndicator
    private ProgressIndicator criarIndicator() {
        ProgressIndicator indicador = new ProgressIndicator();
        indicador.setMaxSize(40, 40);
        return indicador;
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

    //implementação da interface de gerar pdf
    @Override
    public List<LogAuditoria> getListaLogs() {
        // Retorna os itens atualmente carregados na tabela de logs
        return tabelaLogsAuditoria.getItems();
    }

    @Override
    public String getTituloRelatorio() {
        return "Registro Geral de Atividades do Sistema";
    }

    @Override
    public LocalDate getDataEmissao() {
        return LocalDate.now();
    }
}