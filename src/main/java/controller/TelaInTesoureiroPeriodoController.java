package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Conta;
import model.Lembrete;
import model.Movimentacao;
import model.dto.DatasTelaPeriodoDTO;
import java.io.IOException;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.ContaDAO;
import dao.LembreteDAO;
import dao.MovimentacaoDAO;

public class TelaInTesoureiroPeriodoController {
    //ATRIBUTOS
    Double saldoTotal = 0.0;
    List<Conta> listaContas;
    Date dataInicio;
    Date dataFim;
    DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @FXML
    private Button botaoAdicionarDespesa;

    @FXML
    private Button botaoAdicionarReceita;

    @FXML
    private Button botaoDetalharDespesa;

    @FXML
    private Button botaoDetalharReceita;

    @FXML
    private Button botaoOutrasContas;

    @FXML
    private Label campoSaldoTotal;

    @FXML
    private ProgressIndicator carregamentoConta1;

    @FXML
    private ProgressIndicator carregamentoConta2;

    @FXML
    private ProgressIndicator carregamentoTotal;

    @FXML
    private TableColumn<Movimentacao, String> colunaDataDespesa;

    @FXML
    private TableColumn<Movimentacao, String> colunaDataReceita;

    @FXML
    private TableColumn<Movimentacao, String> colunaDescricaoDespesa;

    @FXML
    private TableColumn<Movimentacao, String> colunaDescricaoReceita;

    @FXML
    private TableColumn<Movimentacao, String> colunaValorReceita;

    @FXML
    private TableColumn<Movimentacao, String> colunavalorDespesa;

    @FXML
    private ImageView iconeConta1;

    @FXML
    private ImageView iconeConta2;

    @FXML
    private Hyperlink linkAno;

    @FXML
    private Hyperlink linkCategorias;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkContas;

    @FXML
    private Hyperlink linkDebitosSocios;

    @FXML
    private Hyperlink linkDia;

    @FXML
    private Hyperlink linkGraficosRelatorios;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkMes;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private Hyperlink linkSemana;

    @FXML
    private Hyperlink linkPeriodo;

    @FXML
    private Label nomeConta1;

    @FXML
    private Label nomeConta2;

    @FXML
    private TextField periodoSelecionado;

    @FXML
    private TableColumn<Lembrete, String> nomeLembrete;

    @FXML
    private Label saldoConta1;

    @FXML
    private Label saldoConta2;

    @FXML
    private TableView<Movimentacao> tabelaDespesas;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableView<Movimentacao> tabelaReceitas;

    @FXML
    private TableColumn<Lembrete, String> valorLembrete;

    //BOTÕES
    @FXML
    void adicionarDespesaAction(ActionEvent event) throws IOException {
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaCadastroMovimentacao.fxml"));
        Parent root = fxmlLoader.load();

        //cria e exibe a tela de alteração
        Stage telaExibicao = new Stage();
        telaExibicao.setTitle("Cadastrar Movimentação");
        telaExibicao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaExibicao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaExibicao.initModality(Modality.WINDOW_MODAL);
        telaExibicao.initOwner(tabelaDespesas.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaExibicao.showAndWait();

        //atualiza a tabela depois da alteração
        carregarTabelas();
    }

    @FXML
    void adicionarReceitaAction(ActionEvent event) throws IOException {
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaCadastroMovimentacao.fxml"));
        Parent root = fxmlLoader.load();

        //cria e exibe a tela de alteração
        Stage telaExibicao = new Stage();
        telaExibicao.setTitle("Cadastrar Movimentação");
        telaExibicao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaExibicao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaExibicao.initModality(Modality.WINDOW_MODAL);
        telaExibicao.initOwner(tabelaDespesas.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaExibicao.showAndWait();

        //atualiza a tabela depois da alteração
        carregarTabelas();
    }

    @FXML
    void anoAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroAno");
    }

    @FXML
    void categoriasAction(ActionEvent event) {

    }

    @FXML
    void configuracoesAction(ActionEvent event) {

    }

    @FXML
    void contasAction(ActionEvent event) {

    }

    @FXML
    void debitosSociosAction(ActionEvent event) {

    }

    @FXML
    void detalharDespesaAction(ActionEvent event) throws IOException {
        //verifica qual foi o sócio selecionado
        Movimentacao movimentacaoSelecionada = tabelaDespesas.getSelectionModel().getSelectedItem();
        
        //verifica se uma movimentação foi selecionada
        if(movimentacaoSelecionada != null) {
            //abre a tela de exibição dos dados da movimentação
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaDetalheMovimentacao.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela
            DetalheMovimentacaoController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(movimentacaoSelecionada);

            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Dados da movimentação");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(tabelaDespesas.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            carregarTabelas();
        } else {
            emitirAlerta("Selecione uma Despesa!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void detalharReceitaAction(ActionEvent event) throws IOException {
        //verifica qual foi o sócio selecionado
        Movimentacao movimentacaoSelecionada = tabelaReceitas.getSelectionModel().getSelectedItem();
        
        //verifica se uma movimentação foi selecionada
        if(movimentacaoSelecionada != null) {
            //abre a tela de exibição dos dados da movimentação
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaDetalheMovimentacao.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela
            DetalheMovimentacaoController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(movimentacaoSelecionada);

            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Dados da movimentação");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(tabelaDespesas.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            carregarTabelas();
        } else {
            emitirAlerta("Selecione uma Receita!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void diaAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroDia");
    }

    @FXML
    void graficosRelatoriosAction(ActionEvent event) {

    }

    @FXML
    void lembretesAction(ActionEvent event) {

    }

    @FXML
    void mesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroMes");
    }

    @FXML
    void outrasContasAction(ActionEvent event) throws IOException {
        //abre a tela de contas
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaSaldoContas.fxml"));
        Parent root = fxmlLoader.load();

        //obtem o controller da tela de alteração
        SaldoContasController controller = fxmlLoader.getController();
        controller.setListaContas(listaContas);

        //cria e exibe a tela
        Stage telaAlteracao = new Stage();
        telaAlteracao.setTitle("Saldo Contas");
        telaAlteracao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaAlteracao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaAlteracao.initModality(Modality.WINDOW_MODAL);
        telaAlteracao.initOwner(tabelaDespesas.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaAlteracao.showAndWait();
    }

    @FXML
    void periodoAction(ActionEvent event) throws IOException {
        //abre a tela de seleção do período
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaSelecionarPeriodo.fxml"));
        Parent root = fxmlLoader.load();

        SelecionarPeriodoController controller = fxmlLoader.getController();

        Stage telaExibicao = new Stage();
        telaExibicao.setTitle("Selecionar Período");
        telaExibicao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaExibicao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaExibicao.initModality(Modality.WINDOW_MODAL);
        telaExibicao.initOwner(tabelaDespesas.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaExibicao.showAndWait();
        
        //verifica se o usuário apertou em confirmar
        if(controller.confirmaConsulta == true) {
            App.trocarTela("TelaInTesoureiroPeriodo");
        }
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
    void semanaAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroSemana");
    }

    //MÉTODOS
    //inicializa a tela
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

        //formata uma string com o periodo selecionado
        String dataInicioFormatada = (DatasTelaPeriodoDTO.getDataInicial() != null) 
            ? DatasTelaPeriodoDTO.getDataInicial().toLocalDate().format(formatoData) 
            : "";

        String dataFimFormatada = (DatasTelaPeriodoDTO.getDataFinal() != null) 
            ? DatasTelaPeriodoDTO.getDataFinal().toLocalDate().format(formatoData) 
            : "";
            
        String periodo = dataInicioFormatada + " até " + dataFimFormatada;
        periodoSelecionado.setText(periodo);
        
        //configura as colunas das tabelas
        this.colunaDescricaoDespesa.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getComentarioMovimentacao())
        );

        this.colunaDescricaoReceita.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getComentarioMovimentacao())
        );

        this.colunaDataDespesa.setCellValueFactory(cellData -> cellData.getValue().dataMovimentFormatada());

        this.colunaDataReceita.setCellValueFactory(cellData -> cellData.getValue().dataMovimentFormatada());

        this.colunavalorDespesa.setCellValueFactory(cellData -> {
            double valor = cellData.getValue().getValorMovimentacao();
            String formatado = String.format("R$ %.2f", valor);
            return new SimpleStringProperty(formatado);
        });

        this.colunaValorReceita.setCellValueFactory(cellData -> {
            double valor = cellData.getValue().getValorMovimentacao();
            String formatado = String.format("R$ %.2f", valor);
            return new SimpleStringProperty(formatado);
        });

        this.nomeLembrete.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        this.valorLembrete.setCellValueFactory(cellData -> {
            double valor = cellData.getValue().getValorLembrete();
            String formatado = String.format("R$ %.2f", valor);
            return new SimpleStringProperty(formatado);
        });

        //chama a função que irá carregar os dados das tabelas e dos mostradores
        carregarDadosSegundoPlano();
    }
    
    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        tabelaReceitas.getItems().clear();
        tabelaDespesas.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaReceitas.setPlaceholder(criarIndicator());
        tabelaDespesas.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexao);
                LembreteDAO lembreteDAO = new LembreteDAO(conexao);
                ContaDAO contaDAO = new ContaDAO(conexao);

                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Movimentacao> listaReceitas = movimentacaoDAO.buscarReceitasPeriodo(periodoSelecionado.getText());
                List<Movimentacao> listaDespesas = movimentacaoDAO.buscarDespesasPeriodo(periodoSelecionado.getText());
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                //armazena as contas cadastradas
                listaContas = contaDAO.listarContas();

                //calcula o total de saldo em todas as contas
                for (Conta conta : listaContas) {
                    saldoTotal += conta.getSaldo();
                }

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaReceitas.setItems(FXCollections.observableArrayList(listaReceitas));
                    tabelaDespesas.setItems(FXCollections.observableArrayList(listaDespesas));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));

                    campoSaldoTotal.setText("R$" + saldoTotal);

                    if(listaContas.size() >= 1) {
                        nomeConta1.setText(listaContas.get(0).getNomeConta());
                        saldoConta1.setText("R$" + listaContas.get(0).getSaldo());
                    }

                    if (listaContas.size() >= 2) {
                        nomeConta2.setText(listaContas.get(1).getNomeConta());
                        saldoConta2.setText("R$" + listaContas.get(1).getSaldo());
                    }

                    if (listaLembretes.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }

                    if (listaReceitas.isEmpty()) {
                        tabelaReceitas.setPlaceholder(new javafx.scene.control.Label("Sem movimentações no período."));
                    }

                    if (listaDespesas.isEmpty()) {
                        tabelaDespesas.setPlaceholder(new javafx.scene.control.Label("Sem movimentações no período."));
                    }
                });

                return null;
            }
        };

        //mostra os icones de carregamento enquanto a tarefa está rodando em segundo plano
        carregamentoTotal.visibleProperty().bind(task.runningProperty());
        carregamentoConta1.visibleProperty().bind(task.runningProperty());
        carregamentoConta2.visibleProperty().bind(task.runningProperty());

        //mostra os labels com as informações assim que a tarefa parar de rodar em segundo plano
        campoSaldoTotal.visibleProperty().bind(task.runningProperty().not());
        saldoConta1.visibleProperty().bind(task.runningProperty().not());
        saldoConta2.visibleProperty().bind(task.runningProperty().not());
        nomeConta1.visibleProperty().bind(task.runningProperty().not());
        nomeConta2.visibleProperty().bind(task.runningProperty().not());

        //mostra um aviso caso os dados não possam ser carregados
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() -> emitirAlerta("Erro ao carregar os dados.", AlertType.ERROR));
        });

        //cria uma nova Thread para rodar a tarefa de carregamento em segundo plano
        new Thread(task).start();
    }

    private void carregarTabelas() {
        tabelaReceitas.getItems().clear();
        tabelaDespesas.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaReceitas.setPlaceholder(criarIndicator());
        tabelaDespesas.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexao);
                ContaDAO contaDAO = new ContaDAO(conexao);

                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Movimentacao> listaReceitas = movimentacaoDAO.buscarReceitasPeriodo(periodoSelecionado.getText());
                List<Movimentacao> listaDespesas = movimentacaoDAO.buscarDespesasPeriodo(periodoSelecionado.getText());

                //armazena as contas cadastradas
                listaContas = contaDAO.listarContas();

                saldoTotal = 0.0;
                //calcula o total de saldo em todas as contas
                for (Conta conta : listaContas) {
                    saldoTotal += conta.getSaldo();
                }

                // Atualiza as tabelas
                Platform.runLater(() -> {
                    tabelaReceitas.setItems(FXCollections.observableArrayList(listaReceitas));
                    tabelaDespesas.setItems(FXCollections.observableArrayList(listaDespesas));

                    campoSaldoTotal.setText("R$" + saldoTotal);

                    if(listaContas.size() >= 1) {
                        nomeConta1.setText(listaContas.get(0).getNomeConta());
                        saldoConta1.setText("R$" + listaContas.get(0).getSaldo());
                    }

                    if (listaContas.size() >= 2) {
                        nomeConta2.setText(listaContas.get(1).getNomeConta());
                        saldoConta2.setText("R$" + listaContas.get(1).getSaldo());
                    }

                    if (listaReceitas.isEmpty()) {
                        tabelaReceitas.setPlaceholder(new javafx.scene.control.Label("Sem movimentações no período."));
                    }

                    if (listaDespesas.isEmpty()) {
                        tabelaDespesas.setPlaceholder(new javafx.scene.control.Label("Sem movimentações no período."));
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
}