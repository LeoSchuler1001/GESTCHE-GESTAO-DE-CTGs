package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.ContaDAO;
import dao.LembreteDAO;
import dao.MovimentacaoDAO;
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

public class TelaContasController {
    //ATRIBUTOS
    List<Conta> listaContas = new ArrayList<>();
    Double saldoTotal = 0.0;
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    ContaDAO contaDAO = new ContaDAO(conexao);
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexao);

    @FXML
    private Button botaoAlterar;

    @FXML
    private Button botaoCriarNovo;

    @FXML
    private Button botaoExcluir;

    @FXML
    private TextField campoSaldoTotal;

    @FXML
    private TableColumn<Conta, String> colunaNomeConta;

    @FXML
    private TableColumn<Conta, String> colunaSaldoConta;

    @FXML
    private Hyperlink linkCategorias;

    @FXML
    private Hyperlink linkConfiguracoes;

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
    private TableView<Conta> tabelaContas;


    @FXML
    private TableColumn<Lembrete, String> valorLembrete;

    //BOTÕES
    @FXML
    void alterarAction(ActionEvent event) {

    }

    @FXML
    void excluirAction(ActionEvent event) throws SQLException {
        //verifica qual foi a conta selecionado
        Conta contaSelecionada = tabelaContas.getSelectionModel().getSelectedItem();

        //verifica se uma conta foi selecionada
        if(contaSelecionada != null) {
            //verifica se a conta pode ser excluída
            if(contaDAO.contaPossuiMovimentacao(contaSelecionada.getIdConta())) {
                emitirAlerta("A conta não pode ser excluída, pois possui movimentações cadastradas!", AlertType.ERROR);
                return;
            }
            
            Boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja realmente excluir?", AlertType.CONFIRMATION);
            if(confirmaExclusao) {
                contaDAO.excluirConta(contaSelecionada);

                emitirAlerta("Conta excluída com sucesso!", AlertType.INFORMATION);
            
                carregarDadosSegundoPlano();
            }
        } else {
            emitirAlerta("Selecione uma conta!", AlertType.ERROR);
        }
    }

    @FXML
    void criarNovoAction(ActionEvent event) throws IOException {
        //abre a tela de cadastro de contas
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaCadastrarConta.fxml"));
        Parent root = fxmlLoader.load();

        //cria e exibe a tela de alteração
        Stage telaExibicao = new Stage();
        telaExibicao.setTitle("Cadastrar Conta");
        telaExibicao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaExibicao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaExibicao.initModality(Modality.WINDOW_MODAL);
        telaExibicao.initOwner(tabelaContas.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaExibicao.showAndWait();

        //atualiza a tabela depois do cadastro
        carregarDadosSegundoPlano();
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
        
        //faz com que o usuário não possa mexer nos campos
        campoSaldoTotal.setMouseTransparent(true);
        campoSaldoTotal.setFocusTraversable(false);

        //configura as colunas das tabelas
        this.colunaNomeConta.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeConta())
        );

        this.colunaSaldoConta.setCellValueFactory(cellData -> 
            new SimpleStringProperty("R$" + cellData.getValue().getSaldo())
        );

        this.nomeLembrete.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        this.valorLembrete.setCellValueFactory(cellData -> {
            double valor = cellData.getValue().getValorLembrete();
            String formatado = String.format("R$ %.2f", valor);
            return new SimpleStringProperty(formatado);
        });

        carregarDadosSegundoPlano();
    }

    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        tabelaContas.getItems().clear();
        tabelaLembretes.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaContas.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //armazena as contas cadastradas
                listaContas = contaDAO.listarContas();
                List<Lembrete> listaLembretesHoje = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                saldoTotal = 0.0;
                
                //calcula o total de saldo em todas as contas
                for (Conta conta : listaContas) {
                    saldoTotal += conta.getSaldo();
                }

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaContas.setItems(FXCollections.observableArrayList(listaContas));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretesHoje));

                    campoSaldoTotal.setText("R$" + saldoTotal);

                    if (listaContas.isEmpty()) {
                        tabelaContas.setPlaceholder(new javafx.scene.control.Label("Sem Contas cadastradas."));
                    }

                    if (listaLembretesHoje.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
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