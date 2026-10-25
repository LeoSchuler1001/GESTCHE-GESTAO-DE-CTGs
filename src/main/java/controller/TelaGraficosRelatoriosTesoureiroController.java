package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.MovimentacaoDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import model.Lembrete;
import util.GerarPdf;

public class TelaGraficosRelatoriosTesoureiroController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexao);
    GerarPdf gerarPdf = new GerarPdf();

    LocalDate dataInicio;
    LocalDate dataFim;

    @FXML
    private Button botaoGerarPdf;

    @FXML
    private Button botaoSelecionarPeriodo;

    @FXML
    private Label campoFechamento;

    @FXML
    private TextField campoPeriodo;

    @FXML
    private Label campoTotaReceitas;

    @FXML
    private Label campoTotalDespesas;

    @FXML
    private ProgressIndicator carregamentoDespesas;

    @FXML
    private ProgressIndicator carregamentoFechamento;

    @FXML
    private ProgressIndicator carregamentoGrafico1;

    @FXML
    private ProgressIndicator carregamentoGrafico2;

    @FXML
    private ProgressIndicator carregamentoReceitas;

    @FXML
    private PieChart graficoDespesasCategoria;

    @FXML
    private PieChart graficoReceitaCategorias;

    @FXML
    private Hyperlink linkCategorias;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkContas;

    @FXML
    private Hyperlink linkDebitosSocios;

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

    //BOTÕES

    @FXML
    void gerarPdfAction(ActionEvent event) {

    }

    @FXML
    void selecinarPeriodoAction(ActionEvent event) {

    }

    @FXML
    void categoriasAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaCategorias");
    }

    @FXML
    void configuracoesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaConfiguracoesTesoureiro");
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
        //define as datas que mostrarão os gráficos iniciais
        dataInicio = LocalDate.now().withDayOfMonth(1);
        dataFim = LocalDate.now();

        //desativa a seleção na tabela de lembretes, mas mantêm o scroll
        Callback<TableView<Lembrete>, TableRow<Lembrete>> desativarSelecaoLembrete = tv -> {
            TableRow<Lembrete> row = new TableRow<>();
            
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return row;
        };
        tabelaLembretes.setRowFactory(desativarSelecaoLembrete);
        tabelaLembretes.setFocusTraversable(false);
        
        //configura as colunas das tabelas de lembrete
        this.nomeLembrete.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        this.valorLembrete.setCellValueFactory(cellData -> {
            double valor = cellData.getValue().getValorLembrete();
            String formatado = String.format("R$ %.2f", valor);
            return new SimpleStringProperty(formatado);
        });

        botaoGerarPdf.setDisable(true);

        carregarDadosEmSegundoPlano();
    }

    public void carregarDadosEmSegundoPlano() {
        tabelaLembretes.getItems().clear();
        botaoGerarPdf.setDisable(true);

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            List<Lembrete> listaLembretesHoje;
            ObservableList<PieChart.Data> dadosReceitas;
            ObservableList<PieChart.Data> dadosDespesas;

            @Override
            protected Void call() throws Exception {
                // Busca tudo do banco em segundo plano
                listaLembretesHoje = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());
                
                // Certifique-se de que os nomes aqui batem com os métodos criados no seu MovimentacaoDAO
                dadosReceitas = movimentacaoDAO.buscarDadosReceitasCategorias(dataInicio, dataFim);
                dadosDespesas = movimentacaoDAO.buscarDadosDespesasCategorias(dataInicio, dataFim);
                

                System.out.println("Qtd de Receitas para o gráfico: " + (dadosReceitas != null ? dadosReceitas.size() : "null"));
    System.out.println("Qtd de Despesas para o gráfico: " + (dadosDespesas != null ? dadosDespesas.size() : "null"));
    System.out.println("Período analisado: " + dataInicio + " até " + dataFim);
    
                return null;
            }

            @Override
            protected void succeeded() {
                // Este método roda automaticamente na Thread Principal (UI Thread) quando o call() termina com sucesso!
                tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretesHoje));
                
                graficoReceitaCategorias.setData(dadosReceitas);
                graficoDespesasCategoria.setData(dadosDespesas);

                if (listaLembretesHoje.isEmpty()) {
                    tabelaLembretes.setPlaceholder(new Label("Sem lembretes."));
                }

                botaoGerarPdf.setDisable(false);
            }
        };

        //mostra os icones de carregamento enquanto a tarefa está rodando em segundo plano
        carregamentoDespesas.visibleProperty().bind(task.runningProperty());
        carregamentoReceitas.visibleProperty().bind(task.runningProperty());
        carregamentoFechamento.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico1.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico2.visibleProperty().bind(task.runningProperty());

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
