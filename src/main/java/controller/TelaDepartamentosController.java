package controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import app.App;
import dao.ConexaoBanco;
import dao.DepartamentoDAO;
import dao.LembreteDAO;
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
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Departamento;
import model.Lembrete;

public class TelaDepartamentosController {
    //ATRIBUTOS
    @FXML
    private Button botaoAlterar;

    @FXML
    private Button botaoCriarNovo;

    @FXML
    private Button botaoExcluir;

    @FXML
    private TableView<Departamento> tabelaDepartamento;

    @FXML
    private TableColumn<Departamento, String> colunaDescricaoDepartamento;

    @FXML
    private TableColumn<Departamento, String> colunaNomeDepartamento;

    @FXML
    private TableColumn<Lembrete, String> lembretes;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkGraficosRelatorios;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSociosDependentes;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    //BOTÕES
    @FXML
    void alterarDepartamentoAction(ActionEvent event) throws IOException {
        //verifica qual foi o departamento selecionado
        Departamento departamentoSelecionado = tabelaDepartamento.getSelectionModel().getSelectedItem();

        //verifica se um departamento foi selecionado
        if(departamentoSelecionado != null) {
            //abre a tela de alteração de departamento
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaAlteracaoDepartamento.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            AlterarDepartamentoController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(departamentoSelecionado);

            //cria e exibe a tela de alteração
            Stage telaAlteracao = new Stage();
            telaAlteracao.setTitle("Alterar Departamento");
            telaAlteracao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaAlteracao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaAlteracao.initModality(Modality.WINDOW_MODAL);
            telaAlteracao.initOwner(tabelaDepartamento.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaAlteracao.showAndWait();

            //atualiza a tabela depois da alteração
            carregarDadosSegundoPlano();
        } else {
            emitirAlerta("Selecione um departamento!", AlertType.ERROR);
        }
    }

    @FXML
    void criarDepartamentoAction(ActionEvent event) {

    }

    @FXML
    void excluirDepartamentoAction(ActionEvent event) {

    }

    @FXML
    void configuracoesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaConfiguracoes");
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
    public void initialize() {
        //configura as colunas das tabelas para receber os nomes dos sócios e lembretes
        this.colunaNomeDepartamento.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeDepartamento())
        );

        this.colunaDescricaoDepartamento.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescricaoDepartamento())
        );

        this.lembretes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        //chama a função que irá carregar os dados das tabelas e dos mostradores
        carregarDadosSegundoPlano();
    }

    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        tabelaDepartamento.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaDepartamento.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexao);
                LembreteDAO lembreteDAO = new LembreteDAO(conexao);

                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Departamento> listaDepartamentos = departamentoDAO.listarTodos();
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje();

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaDepartamento.setItems(FXCollections.observableArrayList(listaDepartamentos));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));
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
