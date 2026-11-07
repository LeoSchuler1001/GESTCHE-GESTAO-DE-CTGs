package controller;

import java.util.List;
import java.util.Optional;
import dao.ConexaoBanco;
import dao.DebitoDAO;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Debito;
import model.Socio;

public class HistPagamentosController {
    //ATRIBUTOS
    Socio socioSelecionado;
    ConexaoBanco conexao = new ConexaoBanco();
    DebitoDAO debitoDAO = new DebitoDAO(conexao);
    
    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private TableColumn<Debito, String> colunaDescricao;

    @FXML
    private TableColumn<Debito, String> colunaPagamento;

    @FXML
    private TableColumn<Debito, String> colunaValor;

    @FXML
    private TableColumn<Debito, String> colunaVencimento;

    @FXML
    private VBox painelFundo;

    @FXML
    private TableView<Debito> tabelaPagamentos;
    
    //BOTÕES
    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //MÉTODOS
    public void initialize() {
        //configura as colunas das tabelas
        this.colunaDescricao.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTipoDebito())
        );

        this.colunaVencimento.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getVencimentoDebitoFormatado())
        );

        this.colunaPagamento.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDtPgmtDebitoFormatada())
        );

        this.colunaValor.setCellValueFactory(cellData -> {
            double valor = cellData.getValue().getValorDebito();
            String formatado = String.format("R$ %.2f", valor);
            return new SimpleStringProperty(formatado);
        });
    }

    public void carregarDadosEmSegundoPlano(Socio socioSelecionado) {
        this.socioSelecionado = socioSelecionado;

        tabelaPagamentos.getItems().clear();

        tabelaPagamentos.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Debito> debitosPagosSocio = debitoDAO.listarDebitosPagos(socioSelecionado.getIdSocio());

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaPagamentos.setItems(FXCollections.observableArrayList(debitosPagosSocio));

                    //mensagem para se a tabela estiver vazia
                    if (debitosPagosSocio.isEmpty()) {
                        tabelaPagamentos.setPlaceholder(new javafx.scene.control.Label("Nenhum débito pago registrado."));
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
