package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import dao.ConexaoBanco;
import dao.ContaDAO;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Conta;

public class SaldoContasController {
    //ATRIBUTOS
    List<Conta> listaContas = new ArrayList<>();
    Double saldoTotal = 0.0;
    
    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private TableColumn<Conta, String> colunaNomeConta;

    @FXML
    private TableColumn<Conta, String> colunaSaldoConta;

    @FXML
    private VBox painelFundo;

    @FXML
    private TextField campoSaldoTotal;

    @FXML
    private TableView<Conta> tabelaContas;

    //BOTÕES
    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //GETERS E SETERS
    public List<Conta> getListaContas() {
        return listaContas;
    }

    public void setListaContas(List<Conta> listaContas) {
        this.listaContas = listaContas;
    }

    //MÉTODOS
    //inicializa a tela
    public void initialize() {
        //desativa a seleção na tabela de contas, mas mantêm o scroll
        Callback<TableView<Conta>, TableRow<Conta>> desativarSelecaoLembrete = tv -> {
            TableRow<Conta> row = new TableRow<>();
            
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return row;
        };

        tabelaContas.setRowFactory(desativarSelecaoLembrete);
        tabelaContas.setFocusTraversable(false);

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

        carregarDadosSegundoPlano();
    }

    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaContas.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                ContaDAO contaDAO = new ContaDAO(conexao);

                //armazena as contas cadastradas
                listaContas = contaDAO.listarContas();

                //calcula o total de saldo em todas as contas
                for (Conta conta : listaContas) {
                    saldoTotal += conta.getSaldo();
                }

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaContas.setItems(FXCollections.observableArrayList(listaContas));

                    campoSaldoTotal.setText("R$" + saldoTotal);

                    if (listaContas.isEmpty()) {
                        tabelaContas.setPlaceholder(new javafx.scene.control.Label("Sem Contas cadastradas."));
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