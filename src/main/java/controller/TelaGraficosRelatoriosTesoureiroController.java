package controller;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.File;
import javafx.stage.FileChooser;
import model.Lembrete;
import model.dto.DatasTelaPeriodoDTO;
import util.GerarPdfFinanceiro;

public class TelaGraficosRelatoriosTesoureiroController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexao);
    GerarPdfFinanceiro gerarPdf = new GerarPdfFinanceiro();

    private double totalReceitasRelatorio;
    private double totalDespesasRelatorio;

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
    private Label semInf1;

    @FXML
    private Label semInf2;


    @FXML
    private TableColumn<Lembrete, String> nomeLembrete;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableColumn<Lembrete, String> valorLembrete;

    //BOTÕES

    @FXML
    void gerarPdfAction(ActionEvent event) {
        FileChooser seletorArquivo = new FileChooser();

        seletorArquivo.setTitle("Salvar Relatório Financeiro");

        //define o nome inicial do arquivo
        seletorArquivo.setInitialFileName("Relatorio_Financeiro.pdf");

        //define o formato do arquivo
        seletorArquivo.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "Arquivo PDF (*.pdf)", "*.pdf"
            )
        );

        //abre a janela para escolher onde salvar
        File arquivoDestino = seletorArquivo.showSaveDialog(
            botaoGerarPdf.getScene().getWindow()
        );

        //verifica se o usuário escolheu um destino
        if (arquivoDestino != null) {
            try {
                gerarPdf.gerarRelatorioFinanceiro(
                    graficoReceitaCategorias,
                    graficoDespesasCategoria,
                    arquivoDestino,
                    dataInicio,
                    dataFim,
                    totalReceitasRelatorio,
                    totalDespesasRelatorio
                );

                emitirAlerta("Relatório gerado com sucesso!", AlertType.INFORMATION);

            } catch (IOException e) {

                e.printStackTrace();

                emitirAlerta(
                    "Erro ao gerar o relatório PDF.",
                    AlertType.ERROR
                );
            }
        }
    }

    @FXML
    void selecinarPeriodoAction(ActionEvent event) throws IOException {
        //abre a tela de seleção do período
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaSelecionarPeriodo.fxml"));
        Parent root = fxmlLoader.load();

        Stage telaExibicao = new Stage();
        telaExibicao.setTitle("Selecionar Período");
        telaExibicao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaExibicao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaExibicao.initModality(Modality.WINDOW_MODAL);
        telaExibicao.initOwner(campoFechamento.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaExibicao.showAndWait();
        
        //troca as datas pelas que o usuario escolheu
        dataInicio = DatasTelaPeriodoDTO.getDataInicial().toLocalDate();
        dataFim = DatasTelaPeriodoDTO.getDataFinal().toLocalDate();

        carregarDadosEmSegundoPlano();
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

        carregarDadosEmSegundoPlano();
    }

    public void carregarDadosEmSegundoPlano() {
        tabelaLembretes.getItems().clear();

        botaoGerarPdf.setDisable(true);

        campoPeriodo.setEditable(false);

        campoFechamento.setVisible(false);
        campoTotaReceitas.setVisible(false);
        campoTotalDespesas.setVisible(false);

        semInf1.setVisible(false);
        semInf2.setVisible(false);

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            List<Lembrete> listaLembretesHoje;
            ObservableList<PieChart.Data> dadosReceitas;
            ObservableList<PieChart.Data> dadosDespesas;
            double totalReceitasPeriodo;
            double totalDespesasPeriodo;
            double fechamento;

            @Override
            protected Void call() throws Exception {
                listaLembretesHoje = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());
                
                dadosReceitas = movimentacaoDAO.buscarDadosReceitasCategorias(dataInicio, dataFim);
                dadosDespesas = movimentacaoDAO.buscarDadosDespesasCategorias(dataInicio, dataFim);

                totalDespesasPeriodo = movimentacaoDAO.calcularTotalDespesas(dataInicio, dataFim);
                totalReceitasPeriodo = movimentacaoDAO.calcularTotalReceitas(dataInicio, dataFim);
                fechamento = totalReceitasPeriodo - totalDespesasPeriodo;

                return null;
            }

            @Override
            protected void succeeded() {
                tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretesHoje));
                
                graficoReceitaCategorias.setData(dadosReceitas);
                graficoDespesasCategoria.setData(dadosDespesas);

                //preenche os campos de total com strings formatadas
                Locale brasil = Locale.of("pt", "BR");
                NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
                String totalReceitasFormatado = formatoMoeda.format(totalReceitasPeriodo);
                String totalDespesasFormatado = formatoMoeda.format(totalDespesasPeriodo);
                String fechamentoFormatado = formatoMoeda.format(fechamento);
                campoTotaReceitas.setText(totalReceitasFormatado);
                campoTotalDespesas.setText(totalDespesasFormatado);
                campoFechamento.setText(fechamentoFormatado);

                totalReceitasRelatorio = totalReceitasPeriodo;
                totalDespesasRelatorio = totalDespesasPeriodo;  

                if(listaLembretesHoje.isEmpty()) {
                    tabelaLembretes.setPlaceholder(new Label("Sem lembretes."));
                }

                if(dadosReceitas.isEmpty()) {
                    semInf1.setVisible(true);
                }

                if(dadosDespesas.isEmpty()) {
                    semInf2.setVisible(true);
                }

                botaoGerarPdf.setDisable(false);
                campoFechamento.setVisible(true);
                campoTotaReceitas.setVisible(true);
                campoTotalDespesas.setVisible(true);

                //apresenta a data de inicio e fim no campo de periodo
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String textoInicio = dataInicio.format(formatter);
                String textoFim = dataFim.format(formatter);

                campoPeriodo.setText(textoInicio + " até " + textoFim);
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
