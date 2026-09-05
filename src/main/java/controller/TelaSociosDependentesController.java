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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Lembrete;
import model.dto.SocioResumoDTO;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TelaSociosDependentesController {
    //ATRIBUTOS
    @FXML
    private Button botaoAlterarDados;

    @FXML
    private Button botaoCadastrarDebito;

    @FXML
    private Button botaoCadastrarDependente;

    @FXML
    private Button botaoCadastrarSocio;

    @FXML
    private Button botaoExcluirSocio;

    @FXML
    private Button botaoInativarSocio;

    @FXML
    private Button botaoVerDados;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaDepartamento;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaDependentes;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaNomeSocio;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaSituacao;

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
    private Hyperlink linkLembretes;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableView<SocioResumoDTO> tabelaResumoSocios;

    @FXML
    private Hyperlink linkSair;

    //BOTÕES
    @FXML
    void alterarDadosAction(ActionEvent event) throws IOException, SQLException {
        //verifica qual foi o sócio selecionado
        SocioResumoDTO socioSelecionado = tabelaResumoSocios.getSelectionModel().getSelectedItem();

        //verifica se um sócio foi selecionado
        if (socioSelecionado != null) {
            //pega o id so sócio selecionado
            int idSocioSelecionado = socioSelecionado.getIdSocio();

            //abre a tela de alteração de sócio
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaAlteracaoSocio.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            AlterarSocioController controller = fxmlLoader.getController();
            controller.setIdSocioSelecionado(idSocioSelecionado);

            //cria e exibe a tela de alteração
            Stage telaAlteracao = new Stage();
            telaAlteracao.setTitle("Alterar Sócio");
            telaAlteracao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaAlteracao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaAlteracao.initModality(Modality.WINDOW_MODAL);
            telaAlteracao.initOwner(tabelaResumoSocios.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaAlteracao.showAndWait();

        } else {
            emitirAlerta("Selecione um Sócio!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void cadastrarDebitoAction(ActionEvent event) {

    }

    @FXML
    void cadastrarDependenteSocio(ActionEvent event) {

    }

    @FXML
    void cadastrarSocioAction(ActionEvent event) {

    }

    @FXML
    void excluirSocioAction(ActionEvent event) {

    }

    @FXML
    void inativarSocioAction(ActionEvent event) {

    }

    @FXML
    void verDadosAction(ActionEvent event) {

    }

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
    void lembretesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaLembretes");
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
        this.colunaNomeSocio.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeSocio())
        );

        this.colunaSituacao.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSituacaoTexto())
        );

        this.colunaDependentes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDependentesFormatado())
        );

        this.colunaDepartamento.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDependentesFormatado())
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
        tabelaResumoSocios.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                SocioDAO socioDAO = new SocioDAO(conexao);
                LembreteDAO lembreteDAO = new LembreteDAO(conexao);

                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<SocioResumoDTO> listalistaResumoSocios = socioDAO.listarResumoSocios();
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje();

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaResumoSocios.setItems(FXCollections.observableArrayList(listalistaResumoSocios));
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
