package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.ContaDAO;
import dao.LembreteDAO;
import dao.MovimentacaoDAO;
import dao.SocioDAO;
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
import model.Conta;
import model.Lembrete;
import model.dto.DatasTelaPeriodoDTO;

public class TelaInDiretorController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    ContaDAO contaDAO = new ContaDAO(conexao);
    MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexao);
    SocioDAO socioDAO = new SocioDAO(conexao);
    
    Double saldoTotal = 0.0;
    List<Conta> listaContas;

    LocalDate dataInicio;
    LocalDate dataFim;

    //cria as variáveis que vão armazenar as quantidades de sócios e dependentes
    int quantidadeSociosAtivos;
    int quantidadeDependentesAtivos;

    @FXML
    private Button botaoAcessarSecretaria;

    @FXML
    private Button botaoAcessarTesouraria;

    @FXML
    private Button botaoSelecionarPeriodo;

    @FXML
    private Label campoDespesas;

    @FXML
    private Label campoFechamento;

    @FXML
    private Label campoInadimplentes;

    @FXML
    private TextField campoPeriodo;

    @FXML
    private Label campoReceitas;

    @FXML
    private Label campoSaldoTotal;

    @FXML
    private Label campoSociosAtivos;

    @FXML
    private Label campoTotalDependentes;

    @FXML
    private Label campoTotalSocios;

    @FXML
    private Label campoTotalSociosDependentes;

    @FXML
    private ProgressIndicator carregamentoDependentes;

    @FXML
    private ProgressIndicator carregamentoDespesas;

    @FXML
    private ProgressIndicator carregamentoFechamento;

    @FXML
    private ProgressIndicator carregamentoGrafico1;

    @FXML
    private ProgressIndicator carregamentoGrafico2;

    @FXML
    private ProgressIndicator carregamentoGrafico3;

    @FXML
    private ProgressIndicator carregamentoGrafico4;

    @FXML
    private ProgressIndicator carregamentoGrafico5;

    @FXML
    private ProgressIndicator carregamentoGrafico6;

    @FXML
    private ProgressIndicator carregamentoGrafico7;

    @FXML
    private ProgressIndicator carregamentoInadimplentes;

    @FXML
    private ProgressIndicator carregamentoReceitas;

    @FXML
    private ProgressIndicator carregamentoSaldoTotal;

    @FXML
    private ProgressIndicator carregamentoSocios;

    @FXML
    private ProgressIndicator carregamentoSociosAtivos;

    @FXML
    private ProgressIndicator carregamentoTotalSocios;

    @FXML
    private TableColumn<Lembrete, String> colunaLembretes;

    @FXML
    private PieChart graficoAtivosInativos;

    @FXML
    private PieChart graficoDespesaCategorias;

    @FXML
    private PieChart graficoEmdiaInadimplentes;

    @FXML
    private PieChart graficoEtnias;

    @FXML
    private PieChart graficoFaixaEtaria;

    @FXML
    private PieChart graficoHomensMulheres;

    @FXML
    private PieChart graficoReceitaCategorias;

    @FXML
    private Label semInf1;

    @FXML
    private Label semInf2;

    @FXML
    private Label semInf3;

    @FXML
    private Label semInf4;

    @FXML
    private Label semInf5;

    @FXML
    private Label semInf6;

    @FXML
    private Label semInf7;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkGestaoAcesso;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    //BOTÕES
    @FXML
    void lembretesAction(ActionEvent event) {

    }

    @FXML
    void acessarSecretariaAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInicialSecretario");
    }

    @FXML
    void acessarTesourariaAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroMes");
    }

    @FXML
    void configuracoesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaConfiguracoesDiretor");
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

    @FXML
    void selecionarPeriodoAction(ActionEvent event) throws IOException {
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

        carregarDadosSegundoPlano();
    }

    //FUNÇÕES
    public void initialize() throws SQLException {
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

        //configura a coluna da tabela de lembretes
        this.colunaLembretes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        carregarDadosSegundoPlano();
    }

    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        tabelaLembretes.getItems().clear();
        campoPeriodo.clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaLembretes.setPlaceholder(criarIndicator());

        campoPeriodo.setEditable(false);

        campoFechamento.setVisible(false);
        campoReceitas.setVisible(false);
        campoDespesas.setVisible(false);

        semInf1.setVisible(false);
        semInf2.setVisible(false);
        semInf3.setVisible(false);
        semInf4.setVisible(false);
        semInf5.setVisible(false);
        semInf6.setVisible(false);
        semInf7.setVisible(false);
        
        Task<Void> task = new Task<>() {
            ObservableList<PieChart.Data> dadosReceitas;
            ObservableList<PieChart.Data> dadosDespesas;
            double totalReceitasPeriodo;
            double totalDespesasPeriodo;
            double fechamento;

            @Override 
            protected Void call() throws Exception {
                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());
                
                //cria as listas de dados que vão armazenar as informações dos gráficos
                ObservableList<PieChart.Data> dadosAtivosInativos = socioDAO.buscarPorcentagemAtivosInativos();
                ObservableList<PieChart.Data> dadosEmdiaInadimplentes = socioDAO.buscarPorcentagemEmdiaInadimplentes();
                ObservableList<PieChart.Data> dadosHomensMulheres = socioDAO.buscarPorcentagemHomensMulheres();
                ObservableList<PieChart.Data> dadosEtnias = socioDAO.buscarPorcentagemEtnias();
                ObservableList<PieChart.Data> dadosFaixaEtaria = socioDAO.buscarPorcentagemFaixaEtaria();              


                int ativos = socioDAO.contarSociosAtivos();
                int inadimplentes = socioDAO.contarSociosInadimplentes();

                dadosReceitas = movimentacaoDAO.buscarDadosReceitasCategorias(dataInicio, dataFim);
                dadosDespesas = movimentacaoDAO.buscarDadosDespesasCategorias(dataInicio, dataFim);

                //armazena as contas cadastradas
                listaContas = contaDAO.listarContas();
                saldoTotal = 0.0;
                //calcula o total de saldo em todas as contas
                for (Conta conta : listaContas) {
                    saldoTotal += conta.getSaldo();
                }

                //cria as variáveis que vão armazenar as quantidades de sócios e dependentes
                quantidadeSociosAtivos = socioDAO.contarSociosAtivos();
                quantidadeDependentesAtivos = socioDAO.contarDependentesAtivos();
                int totalAtivos = quantidadeDependentesAtivos + quantidadeSociosAtivos;


                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));

                    campoSociosAtivos.setText(String.valueOf(ativos));
                    campoInadimplentes.setText(String.valueOf(inadimplentes));
                    
                    campoSaldoTotal.setText("R$" + saldoTotal);

                    campoTotalSocios.setText(String.valueOf(quantidadeSociosAtivos));
                    campoTotalDependentes.setText(String.valueOf(quantidadeDependentesAtivos));
                    campoTotalSociosDependentes.setText(String.valueOf(totalAtivos));

                    graficoAtivosInativos.setData(dadosAtivosInativos);
                    graficoEmdiaInadimplentes.setData(dadosEmdiaInadimplentes);
                    graficoHomensMulheres.setData(dadosHomensMulheres);
                    graficoEtnias.setData(dadosEtnias);
                    graficoFaixaEtaria.setData(dadosFaixaEtaria);

                    //verifica se os gráficos estão vazios e ai mostra as mensagens
                    if(dadosEmdiaInadimplentes.isEmpty()) {
                        semInf3.setVisible(true);
                    }
                    if(dadosHomensMulheres.isEmpty()) {
                        semInf4.setVisible(true);
                    }
                    if(dadosEtnias.isEmpty()) {
                        semInf5.setVisible(true);
                    }
                    if(dadosAtivosInativos.isEmpty()) {
                        semInf6.setVisible(true);
                    }
                    if(dadosFaixaEtaria.isEmpty()) {
                        semInf7.setVisible(true);
                    }

                    // Define mensagens amigáveis caso as listas venham vazias
                    if (listaLembretes.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }
                });

                totalDespesasPeriodo = movimentacaoDAO.calcularTotalDespesas(dataInicio, dataFim);
                totalReceitasPeriodo = movimentacaoDAO.calcularTotalReceitas(dataInicio, dataFim);
                fechamento = totalReceitasPeriodo - totalDespesasPeriodo;

                return null;
            }

            @Override
            protected void succeeded() {
                //preenche os gráficos da tesouraria
                graficoReceitaCategorias.setData(dadosReceitas);
                graficoDespesaCategorias.setData(dadosDespesas);

                //verifica se os gráficos estão vazios e ai mostra as mensagens
                if(dadosReceitas.isEmpty()) {
                    semInf1.setVisible(true);
                }

                if(dadosDespesas.isEmpty()) {
                    semInf2.setVisible(true);
                }

                //preenche os campos de total com strings formatadas
                Locale brasil = Locale.of("pt", "BR");
                NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(brasil);
                String totalReceitasFormatado = formatoMoeda.format(totalReceitasPeriodo);
                String totalDespesasFormatado = formatoMoeda.format(totalDespesasPeriodo);
                String fechamentoFormatado = formatoMoeda.format(fechamento);
                campoReceitas.setText(totalReceitasFormatado);
                campoDespesas.setText(totalDespesasFormatado);
                campoFechamento.setText(fechamentoFormatado);

                campoFechamento.setVisible(true);
                campoReceitas.setVisible(true);
                campoDespesas.setVisible(true);

                //apresenta a data de inicio e fim no campo de periodo
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String textoInicio = dataInicio.format(formatter);
                String textoFim = dataFim.format(formatter);
                campoPeriodo.setText(textoInicio + " até " + textoFim);
            }

        };

        //mostra os icones de carregamento enquanto a tarefa está rodando em segundo plano
        carregamentoSaldoTotal.visibleProperty().bind(task.runningProperty());
        carregamentoDespesas.visibleProperty().bind(task.runningProperty());
        carregamentoReceitas.visibleProperty().bind(task.runningProperty());
        carregamentoFechamento.visibleProperty().bind(task.runningProperty());
        carregamentoSociosAtivos.visibleProperty().bind(task.runningProperty());
        carregamentoInadimplentes.visibleProperty().bind(task.runningProperty());
        carregamentoTotalSocios.visibleProperty().bind(task.runningProperty());
        carregamentoDependentes.visibleProperty().bind(task.runningProperty());
        carregamentoSocios.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico1.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico2.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico3.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico4.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico5.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico6.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico7.visibleProperty().bind(task.runningProperty());


        //mostra os labels com as informações assim que a tarefa parar de rodar em segundo plano
        campoSaldoTotal.visibleProperty().bind(task.runningProperty().not());
        campoSociosAtivos.visibleProperty().bind(task.runningProperty().not());
        campoInadimplentes.visibleProperty().bind(task.runningProperty().not());
        campoTotalSocios.visibleProperty().bind(task.runningProperty().not());
        campoTotalDependentes.visibleProperty().bind(task.runningProperty().not());
        campoTotalSociosDependentes.visibleProperty().bind(task.runningProperty().not());

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
}