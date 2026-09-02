package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.SocioDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Lembrete;

public class TelaInSecretarioController {
    //ATRIBUTOS
    @FXML
    private Label campoSociosAtivos;

    @FXML
    private Label campoSociosInadimplentes;

    @FXML
    private Label campoSociosInativos;

    @FXML
    private Label campoTotalSocios;

    @FXML
    private ProgressIndicator carregamentoAtivos;

    @FXML
    private ProgressIndicator carregamentoInadimplentes;

    @FXML
    private ProgressIndicator carregamentoInativos;

    @FXML
    private ProgressIndicator carregamentoTotal;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkDepartamentos;

    @FXML
    private Hyperlink linkGraficosRelat;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSociosDepend;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableColumn<String, String> sociosEmDia;

    @FXML
    private TableColumn<String, String> sociosInadimplentes;

    @FXML
    private TableView<String> tabelaSociosEmdia;

    @FXML
    private TableView<String> tabelaSociosInadimplentes;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableColumn<Lembrete, String> lembretes;

    //BOTÕES
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
    void sociosDependenAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaSociosDependentes");
    }

    @FXML
    void graficosRelatoriosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaGraficosRelatorios");
    }

    @FXML
    void departamentosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaDepartamentos");
    }

    @FXML
    void lembretesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaLembretes");
    }

    @FXML
    void configuracoesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaConfiguracoes");
    }

    //FUNÇÕES
    //inicializa a tela
    public void initialize() throws SQLException {
        //configura as colunas das tabelas para receber os nomes dos sócios e lembretes
        this.sociosEmDia.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue())
        );

        this.sociosInadimplentes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue())
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
        tabelaSociosEmdia.setPlaceholder(criarIndicator());
        tabelaSociosInadimplentes.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                SocioDAO socioDAO = new SocioDAO(conexao);
                LembreteDAO lembreteDAO = new LembreteDAO(conexao);

                // consulta os dados no banco de dados
                int total = socioDAO.contarSocios();
                int ativos = socioDAO.contarSociosAtivos();
                int inadimplentes = socioDAO.contarSociosInadimplentes();
                int inativos = socioDAO.contarSociosInativos();

                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<String> listaEmDia = socioDAO.listarSociosEmDia();
                List<String> listaPendentes = socioDAO.listarSociosPendentes();
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje();

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    campoTotalSocios.setText(String.valueOf(total));
                    campoSociosAtivos.setText(String.valueOf(ativos));
                    campoSociosInadimplentes.setText(String.valueOf(inadimplentes));
                    campoSociosInativos.setText(String.valueOf(inativos));

                    tabelaSociosEmdia.setItems(FXCollections.observableArrayList(listaEmDia));
                    tabelaSociosInadimplentes.setItems(FXCollections.observableArrayList(listaPendentes));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));
                });

                return null;
            }
        };
        
        //mostra os icones de carregamento enquanto a tarefa está rodando em segundo plano
        carregamentoTotal.visibleProperty().bind(task.runningProperty());
        carregamentoAtivos.visibleProperty().bind(task.runningProperty());
        carregamentoInadimplentes.visibleProperty().bind(task.runningProperty());
        carregamentoInativos.visibleProperty().bind(task.runningProperty());

        //mostra os labels com as informações assim que a tarefa parar de rodar em segundo plano
        campoTotalSocios.visibleProperty().bind(task.runningProperty().not());
        campoSociosAtivos.visibleProperty().bind(task.runningProperty().not());
        campoSociosInadimplentes.visibleProperty().bind(task.runningProperty().not());
        campoSociosInativos.visibleProperty().bind(task.runningProperty().not());

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
        ProgressIndicator pi = new ProgressIndicator();
        pi.setMaxSize(40, 40);
        return pi;
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
