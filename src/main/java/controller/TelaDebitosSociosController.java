package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.SocioDAO;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TelaDebitosSociosController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    SocioDAO socioDAO = new SocioDAO(conexao);
    
    @FXML
    private Button botaoAprovar;

    @FXML
    private Button botaoCadastrarDebito;

    @FXML
    private Button botaoRecusar;

    @FXML
    private Button botaoVerDados;

    @FXML
    private TableColumn<?, ?> colunaData;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaDepartamento;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaDependentes;

    @FXML
    private TableColumn<?, ?> colunaDescricao;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaNomeSocio;

    @FXML
    private TableColumn<?, ?> colunaResponsavel;

    @FXML
    private TableColumn<SocioResumoDTO, String> colunaSituacao;

    @FXML
    private TableColumn<?, ?> colunaSocio;

    
    @FXML
    private TableView<SocioResumoDTO> tabelaResumoSocios;

    @FXML
    private TableView<?> tabelaAguardandoAprovacao;

    @FXML
    private Hyperlink linkCategorias;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkContas;

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
    private TableColumn<Lembrete, String> valorLembrete;

    //BOTÕES
    @FXML
    void aprovarAction(ActionEvent event) {

    }

    @FXML
    void cadastrarDebitoAction(ActionEvent event) throws IOException {
        //verifica qual foi o sócio selecionado
        SocioResumoDTO socioSelecionado = tabelaResumoSocios.getSelectionModel().getSelectedItem();

        //verifica se um sócio foi selecionado
        if (socioSelecionado != null) {
            //pega o id so sócio selecionado
            int idSocioSelecionado = socioSelecionado.getIdSocio();

            //abre a tela de exibição dos dados do sócio
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaCadastrarDebito.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            CadastrarDebitoController controller = fxmlLoader.getController();
            controller.setIdSocioSelecionado(idSocioSelecionado);

            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Cadastrar Débito");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(tabelaResumoSocios.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            //atualiza a tabela depois da alteração
            carregarDadosSegundoPlano();
        } else {
            emitirAlerta("Selecione um Sócio!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void recusarAction(ActionEvent event) {

    }

    @FXML
    void verDadosAction(ActionEvent event) throws SQLException, IOException {
        //verifica qual foi o sócio selecionado
        SocioResumoDTO socioSelecionado = tabelaResumoSocios.getSelectionModel().getSelectedItem();

        //verifica se um sócio foi selecionado
        if (socioSelecionado != null) {
            //pega o id so sócio selecionado
            int idSocioSelecionado = socioSelecionado.getIdSocio();

            //abre a tela de exibição dos dados do sócio
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaVerDadosSocio.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            VerDadosSocioController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(idSocioSelecionado);

            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Dados do Sócio");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(tabelaResumoSocios.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            //atualiza a tabela depois da alteração
            carregarDadosSegundoPlano();
        } else {
            emitirAlerta("Selecione um Sócio!", AlertType.ERROR);
            return;
        }
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
            new SimpleStringProperty(cellData.getValue().getDepartamentosFormatado())
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
        tabelaLembretes.getItems().clear();
        tabelaResumoSocios.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaLembretes.setPlaceholder(criarIndicator());
        tabelaResumoSocios.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<SocioResumoDTO> listalistaResumoSocios = socioDAO.listarResumoSocios();
                List<Lembrete> listaLembretesHoje = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaResumoSocios.setItems(FXCollections.observableArrayList(listalistaResumoSocios));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretesHoje));

                    if (listaLembretesHoje.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }

                    if (listalistaResumoSocios.isEmpty()) {
                        tabelaResumoSocios.setPlaceholder(new javafx.scene.control.Label("Sem sócios cadastrados."));
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
}