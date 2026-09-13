package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import model.Lembrete;

public class TelaLembretesController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    
    @FXML
    private Button botaoDetalhar;
    
    @FXML
    private Button botaoAlterar;

    @FXML
    private Button botaoConcluido;

    @FXML
    private Button botaoCriarNovo;

    @FXML
    private Button botaoExcluir;

    @FXML
    private TableColumn<Lembrete, Boolean> colunaConcluido;

    @FXML
    private TableColumn<Lembrete, String> colunaDescricao;

    @FXML
    private TableColumn<Lembrete, String> colunaFim;

    @FXML
    private TableColumn<Lembrete, String> colunaInicio;

    @FXML
    private TableColumn<Lembrete, String> colunaNome;

    @FXML
    private TableColumn<Lembrete, String> colunaPeriodicidade;

    @FXML
    private TableColumn<Lembrete, String> lembretes;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkDepartamentos;

    @FXML
    private Hyperlink linkGraficosRelatorios;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkSociosDependentes;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableView<Lembrete> tabelaInformacoesLembretes;

    //BOTÕES
    @FXML
    void configuracoesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaConfiguracoes");
    }

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
    void sociosDependentesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaSociosDependentes");
    }

    @FXML
    void sairAction(ActionEvent event) throws Exception {
        boolean confirmaSaida = emitirAlerta("Deseja realmente sair?", AlertType.CONFIRMATION);

        if (confirmaSaida) {
            App.trocarTela("TelaLogin");
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
    }

    @FXML
    void detalharAction(ActionEvent event) {

    }

    @FXML
    void alterarAction(ActionEvent event) {

    }

    @FXML
    void concluidoAction(ActionEvent event) throws SQLException {
        //verifica qual foi o lembrete selecionado
        Lembrete lembreteSelecionado = tabelaInformacoesLembretes.getSelectionModel().getSelectedItem();

        //verifica se um lembrete foi selecionado
        if(lembreteSelecionado != null) {
            //verifica se o sócio já está ativo
            if(lembreteSelecionado.isPagoLembrete()) {
                emitirAlerta("Este lembrete já está conluído!", AlertType.ERROR);
                return;
            }

            Boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja marcar como concluído?", AlertType.CONFIRMATION);

            if(confirmaExclusao) {
                lembreteDAO.marcarConcluido(lembreteSelecionado);

                //atualiza a tabela depois da alteração
                carregarDadosSegundoPlano();

                emitirAlerta("Lembrete concluído!", AlertType.INFORMATION);
            }
        } else {
            emitirAlerta("Selecione um departamento!", AlertType.ERROR);
        }
    }

    @FXML
    void criarNovoAction(ActionEvent event) {

    }

    @FXML
    void excluirAction(ActionEvent event) throws SQLException {
        //verifica qual foi o lembrete selecionado
        Lembrete lembreteSelecionado = tabelaInformacoesLembretes.getSelectionModel().getSelectedItem();

        //verifica se um lembrete foi selecionado
        if(lembreteSelecionado != null) {
            Boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja prosseguir com a exclusão?", AlertType.CONFIRMATION);

            if(confirmaExclusao) {
                lembreteDAO.excluirLembrete(lembreteSelecionado);

                //atualiza a tabela depois da alteração
                carregarDadosSegundoPlano();

                emitirAlerta("Lembrete excluído!", AlertType.INFORMATION);
            }
        } else {
            emitirAlerta("Selecione um departamento!", AlertType.ERROR);
        }
    }

    //MÉTODOS
    //inicializa a tela
    public void initialize() {
        //configura a coluna conclluido com um checkbox
        colunaConcluido.setCellValueFactory(cellData -> cellData.getValue().pagoLembreteProperty());
        colunaConcluido.setCellFactory(CheckBoxTableCell.forTableColumn(colunaConcluido));

        //configura as colunas das tabelas
        this.colunaNome.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        this.colunaInicio.setCellValueFactory(cellData -> cellData.getValue().dataInicioFormatada());

        this.colunaFim.setCellValueFactory(cellData -> cellData.getValue().dataFimFormatada());

        this.colunaPeriodicidade.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPeriodicidadeLembrete())
        );

        this.colunaDescricao.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescricaoLembrete())
        );

        this.lembretes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        //chama a função que irá carregar os dados das tabelas e dos mostradores
        carregarDadosSegundoPlano();
    }

    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        tabelaInformacoesLembretes.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaInformacoesLembretes.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Lembrete> listaTodosLembretes = lembreteDAO.listarLembretesUsuario(App.usuarioLogado);
                List<Lembrete> listaLembretesHoje = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaInformacoesLembretes.setItems(FXCollections.observableArrayList(listaTodosLembretes));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretesHoje));

                    //mensagem para se a tabela estiver vazia
                    if (listaTodosLembretes.isEmpty()) {
                        tabelaInformacoesLembretes.setPlaceholder(new javafx.scene.control.Label("Nenhum lembrete cadastrado."));
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
