package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import app.App;
import dao.CategoriaDAO;
import dao.ConexaoBanco;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Categoria;
import model.Lembrete;

public class TelaCategoriasController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexao);
    CategoriaDAO categoriaDAO = new CategoriaDAO(conexao);
    List<Categoria> listaCategorias = new ArrayList<>();

    @FXML
    private Button botaoAlterar;

    @FXML
    private Button botaoCriarNovo;

    @FXML
    private Button botaoExcluir;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkContas;

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
    private TableColumn<Categoria, String> colunaNomeCategorias;

    @FXML
    private TableView<Lembrete> tabelaLembretes;
    
    @FXML
    private TableView<Categoria> tabelaCategorias;

    @FXML
    private TableColumn<Lembrete, String> valorLembrete;

    //BOTÕES
    @FXML
    void alterarAction(ActionEvent event) throws IOException {
        //verifica qual foi a categoria selecionada
        Categoria categoriaSelecionada = tabelaCategorias.getSelectionModel().getSelectedItem();

        //verifica se uma categoria foi selecionada
        if(categoriaSelecionada != null) {
            //abre a tela de alteração de categoria
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaAlterarCategoria.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            AlterarCategoriaController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(categoriaSelecionada);

            //cria e exibe a tela de alteração
            Stage telaAlteracao = new Stage();
            telaAlteracao.setTitle("Alterar categoria");
            telaAlteracao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaAlteracao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaAlteracao.initModality(Modality.WINDOW_MODAL);
            telaAlteracao.initOwner(tabelaCategorias.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaAlteracao.showAndWait();

            //atualiza a tabela depois da alteração
            carregarDadosSegundoPlano();
        } else {
            emitirAlerta("Selecione uma categoria", AlertType.ERROR);
        }
    }

    @FXML
    void criarNovoAction(ActionEvent event) throws IOException {
        //abre a tela de cadastro de categorias
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaCadastrarCategoria.fxml"));
        Parent root = fxmlLoader.load();

        //cria e exibe a tela de alteração
        Stage telaExibicao = new Stage();
        telaExibicao.setTitle("Cadastrar categoria");
        telaExibicao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaExibicao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaExibicao.initModality(Modality.WINDOW_MODAL);
        telaExibicao.initOwner(tabelaCategorias.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaExibicao.showAndWait();

        //atualiza a tabela depois do cadastro
        carregarDadosSegundoPlano();
    }

    @FXML
    void excluirAction(ActionEvent event) throws SQLException {
        //verifica qual foi a categoria selecionado
        Categoria categoriaSelecionada = tabelaCategorias.getSelectionModel().getSelectedItem();

        //verifica se uma categoria foi selecionada
        if(categoriaSelecionada != null) {
            //verifica se a categoria pode ser excluída
            if(categoriaDAO.categoriaPossuiMovimentacao(categoriaSelecionada.getIdCategoria())) {
                emitirAlerta("A categoria não pode ser excluída, pois possui movimentações cadastradas!", AlertType.ERROR);
                return;
            }
            
            Boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja realmente excluir?", AlertType.CONFIRMATION);
            if(confirmaExclusao) {
                categoriaDAO.excluirCategoria(categoriaSelecionada);

                emitirAlerta("Categoria excluída com sucesso!", AlertType.INFORMATION);
            
                carregarDadosSegundoPlano();
            }
        } else {
            emitirAlerta("Selecione uma categoria!", AlertType.ERROR);
        }
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
        
        //configura as colunas das tabelas
        this.colunaNomeCategorias.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeCategoria())
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
        tabelaCategorias.getItems().clear();
        tabelaLembretes.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaCategorias.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //armazena as contas cadastradas
                listaCategorias = categoriaDAO.listarCategorias();
                List<Lembrete> listaLembretesHoje = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaCategorias.setItems(FXCollections.observableArrayList(listaCategorias));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretesHoje));

                    if (listaCategorias.isEmpty()) {
                        tabelaCategorias.setPlaceholder(new javafx.scene.control.Label("Sem Contas cadastradas."));
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