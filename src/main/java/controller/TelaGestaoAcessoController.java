package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.UsuarioDAO;
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
import model.Lembrete;
import model.Usuario;

public class TelaGestaoAcessoController {
    //ATRIBUTOS
    //cria a conexão com o banco de dados
    ConexaoBanco conexao = new ConexaoBanco();
    UsuarioDAO UsuarioDAO = new UsuarioDAO(conexao);
    LembreteDAO lembreteDAO = new LembreteDAO(conexao);
    UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);

    @FXML
    private Button botaoAlterarDados;

    @FXML
    private Button botaoAtivarUsuario;

    @FXML
    private Button botaoCadastrarUsuario;

    @FXML
    private Button botaoInativarUsuario;

    @FXML
    private Button botaoVerDados;

    @FXML
    private TableColumn<Usuario, String> colunaCargoUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaEnderecoUsuarios;

    @FXML
    private TableColumn<Lembrete, String> colunaLembretes;

    @FXML
    private TableColumn<Usuario, String> colunaNomeUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaSituacaoUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaTelefoneUsuarios;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableView<Lembrete> tabelaLembretes;

    @FXML
    private TableView<Usuario> tabelaUsuarios;

    @FXML
    void alterarDadosAction(ActionEvent event) throws IOException, SQLException{
        //verifica qual foi o usuario selecionado
        Usuario usuarioSelecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();

        //verifica se um usuario foi selecionado
        if (usuarioSelecionado != null) {
            //pega o id so usuario selecionado
            int idUsuarioSelecionado = usuarioSelecionado.getIdUsuario();

            //abre a tela de exibição dos dados do usuario
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaAlteracaoUsuario.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            AlteracaoUsuarioController controller = fxmlLoader.getController();
            controller.setIdUsuarioSelecionado(idUsuarioSelecionado);

            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Alterar dados do Usuário");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(tabelaUsuarios.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            //atualiza a tabela depois da alteração
            carregarDadosSegundoPlano();
        } else {
            emitirAlerta("Selecione um usuario!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void ativarUsuarioAction(ActionEvent event) throws SQLException {
        //verifica qual foi o sócio selecionado
        Usuario usuarioSelecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();

        //verifica se um sócio foi selecionado
        if (usuarioSelecionado != null) {
            //verifica se o sócio já está ativo
            if(usuarioSelecionado.isAtivoUsuario()) {
                emitirAlerta("Este usuário já está ativo!", AlertType.ERROR);
                return;
            }

            //faz a confirmação com o usuário
            boolean confirmaAtivacao = emitirAlertaConfirmacao("Deseja prosseguir com a ativação?", AlertType.CONFIRMATION);
            if(confirmaAtivacao) {
                //pega o id so sócio selecionado
                int idUsuarioSelecionado = usuarioSelecionado.getIdUsuario();

                //ativa o socio
                usuarioDAO.ativarUsuario(idUsuarioSelecionado);

                //cadastra um log de auditoria
                String descricao = "Usuario " + usuarioSelecionado.getNomeUsuario() + " ativado com o cargo de " + usuarioSelecionado.getCargoUsuario();
                usuarioDAO.cadastrarLogUsuario(descricao, App.usuarioLogado);
                
                //atualiza a tabela depois da alteração
                carregarDadosSegundoPlano();
            }
        } else {
            emitirAlerta("Selecione um usuário!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void cadastrarUsuarioAction(ActionEvent event) throws IOException {
        //abre a tela de cadastro de usuarios
        //carregamento do fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaCadastrarUsuario.fxml"));
        Parent root = fxmlLoader.load();

        //cria e exibe a tela de alteração
        Stage telaExibicao = new Stage();
        telaExibicao.setTitle("Cadastrar Usuário");
        telaExibicao.setScene(new Scene(root));

        //proibe que o usuario possa alterar o tamanho da tela
        telaExibicao.setResizable(false);

        //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
        telaExibicao.initModality(Modality.WINDOW_MODAL);
        telaExibicao.initOwner(tabelaUsuarios.getScene().getWindow());

        //abre a tela e aguarda o usuário fechar
        telaExibicao.showAndWait();

        //atualiza a tabela depois da alteração
        carregarDadosSegundoPlano();
    }

    @FXML
    void configuracoesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaConfiguracoesDiretor");
    }

    @FXML
    void inativarUsuarioAction(ActionEvent event) throws SQLException {
        //verifica qual foi o sócio selecionado
        Usuario usuarioSelecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();

        //verifica se um sócio foi selecionado
        if (usuarioSelecionado != null) {
            //verifica se o sócio já está ativo
            if(!usuarioSelecionado.isAtivoUsuario()) {
                emitirAlerta("Este usuário já está ativo!", AlertType.ERROR);
                return;
            }

            //faz a confirmação com o usuário
            boolean confirmaAtivacao = emitirAlertaConfirmacao("Deseja prosseguir com a inativação?", AlertType.CONFIRMATION);
            if(confirmaAtivacao) {
                //pega o id so usuario selecionado
                int idUsuarioSelecionado = usuarioSelecionado.getIdUsuario();

                //ativa o socio
                usuarioDAO.desativarUsuario(idUsuarioSelecionado);

                //cadastra um log de auditoria
                String descricao = "Usuario " + usuarioSelecionado.getNomeUsuario() + " inativado";
                usuarioDAO.cadastrarLogUsuario(descricao, App.usuarioLogado);
                
                //atualiza a tabela depois da alteração
                carregarDadosSegundoPlano();
            }
        } else {
            emitirAlerta("Selecione um usuário!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void inicioAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInDiretor");
    }

    @FXML
    void lembretesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaLembretesDiretor");
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
    void verDadosAction(ActionEvent event) throws IOException {
        //verifica qual foi o usuario selecionado
        Usuario usuarioSelecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();

        //verifica se um usuario foi selecionado
        if (usuarioSelecionado != null) {
            //pega o id so usuario selecionado
            int idUsuarioSelecionado = usuarioSelecionado.getIdUsuario();

            //abre a tela de exibição dos dados do usuario
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaDetalheUsuario.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            DetalheUsuarioController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(idUsuarioSelecionado);

            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Dados do Usuário");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(tabelaUsuarios.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            //atualiza a tabela depois da alteração
            carregarDadosSegundoPlano();
        } else {
            emitirAlerta("Selecione um usuario!", AlertType.ERROR);
            return;
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
        this.colunaNomeUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeUsuario())
        );

        this.colunaTelefoneUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getTelefoneUsuario())
        );

        this.colunaCargoUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCargoUsuario())
        );

        this.colunaSituacaoUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getSituacaoUsuario())
        );

        this.colunaEnderecoUsuarios.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEnderecoString())
        );

        //configura a coluna da tabela de lembretes
        this.colunaLembretes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        //chama a função que irá carregar os dados das tabelas e dos mostradores
        carregarDadosSegundoPlano();
    }

    private void carregarDadosSegundoPlano() {
        tabelaLembretes.getItems().clear();
        tabelaUsuarios.getItems().clear();

        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaUsuarios.setPlaceholder(criarIndicator());
        tabelaLembretes.setPlaceholder(criarIndicator());

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria as listas que irão armazenar os dados para preencher as tabelas
                List<Usuario> listaUsuarios = UsuarioDAO.listarTodos();
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                // Atualiza as tabelas e os mostradores
                Platform.runLater(() -> {
                    tabelaUsuarios.setItems(FXCollections.observableArrayList(listaUsuarios));
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));

                    if (listaLembretes.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }

                    if (listaUsuarios.isEmpty()) {
                        tabelaUsuarios.setPlaceholder(new javafx.scene.control.Label("Sem sócios cadastrados."));
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