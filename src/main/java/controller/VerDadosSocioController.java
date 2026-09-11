package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.ConexaoBanco;
import dao.DebitoDAO;
import dao.DepartamentoDAO;
import dao.DependenteDAO;
import dao.EnderecoDAO;
import dao.SocioDAO;
import dao.Socio_DepartamentoDAO;
import javafx.application.Platform;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Debito;
import model.Departamento;
import model.Dependente;
import model.Endereco;
import model.Socio;

public class VerDadosSocioController {
    //ATRIBUTOS
    List<String> listaDepartamentosSocio;
    List<String> listaDependentesSocio;
    List<Debito> listaDebitosSocio;
    Endereco enderecoSocioSelecionado;
    Socio socioSelecionado;
    private int idSocioSelecionado;
    private Debito debitoSelecionado;
    private Dependente dependenteSelecionado;
    private Departamento departamentoSelecionado;
    //conexões com o banco de dados
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    SocioDAO socioDAO = new SocioDAO(conexaoBanco);
    EnderecoDAO enderecoDAO = new EnderecoDAO(conexaoBanco);
    Socio_DepartamentoDAO socio_DepartamentoDAO = new Socio_DepartamentoDAO(conexaoBanco);
    DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexaoBanco);
    DependenteDAO dependenteDAO = new DependenteDAO(conexaoBanco);
    DebitoDAO debitoDAO = new DebitoDAO(conexaoBanco);
    
    @FXML
    private Button botaoFechar;

    @FXML
    private TextField campoBairroSocio;

    @FXML
    private TextField campoCepSocio;

    @FXML
    private TextField campoCidadeSocio;

    @FXML
    private TextField campoCpfSocio;

    @FXML
    private ListView<String> campoDebitosSocio;

    @FXML
    private ListView<String> campoDepartamentosSocio;

    @FXML
    private ListView<String> campoDependentesSocio;

    @FXML
    private TextField campoEmailSocio;

    @FXML
    private TextField campoEstadoSocio;

    @FXML
    private TextField campoNascimentoSocio;

    @FXML
    private TextField campoNomeSocio;

    @FXML
    private TextField campoNumeroSocio;

    @FXML
    private TextField campoRuaSocio;

    @FXML
    private TextField campoTelefoneSocio;

    @FXML
    private Button detalharDebito;

    @FXML
    private Button detalharDepartamento;

    @FXML
    private Button detalharDependente;

    @FXML
    private VBox painelFundo;

    //BOTÕES
    @FXML
    void detalharDebitoAction(ActionEvent event) throws SQLException, IOException {
        debitoSelecionado = debitoDAO.buscarPorTipo(campoDebitosSocio.getSelectionModel().getSelectedItem());

        //verifica se um debito foi selecionado
        if (debitoSelecionado != null) {
            //abre a tela de exibição dos dados do debito
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaDetalheDebito.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            DetalheDebitoController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(debitoSelecionado);
            
            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Dados do Debito");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(campoNomeSocio.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            //recarrega as informações
            buscarDadosSocio();
        } else {
            emitirAlerta("Selecione um Debito!", AlertType.ERROR);
            return;
        }
    }   

    @FXML
    void detalharDepartamentoAction(ActionEvent event) throws SQLException, IOException {
        departamentoSelecionado = departamentoDAO.buscarPorNome(campoDepartamentosSocio.getSelectionModel().getSelectedItem());

        //verifica se um departamento foi selecionado
        if (departamentoSelecionado != null) {
            //abre a tela de exibição dos dados do departamento
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaDetalheDepartamento.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            DetalheDepartamentoController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(departamentoSelecionado);
            
            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Dados do Departamento");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(campoNomeSocio.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

        } else {
            emitirAlerta("Selecione um Departamento!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void detalharDependenteAction(ActionEvent event) throws SQLException, IOException {
        dependenteSelecionado = dependenteDAO.buscarPorNome(campoDependentesSocio.getSelectionModel().getSelectedItem());

        //verifica se um dependente foi selecionado
        if (dependenteSelecionado != null) {
            //abre a tela de exibição dos dados do dependente
            //carregamento do fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaDetalheDependente.fxml"));
            Parent root = fxmlLoader.load();

            //obtem o controller da tela de alteração
            DetalheDependenteController controller = fxmlLoader.getController();
            controller.carregarDadosEmSegundoPlano(dependenteSelecionado);
            
            //cria e exibe a tela de alteração
            Stage telaExibicao = new Stage();
            telaExibicao.setTitle("Dados do Dependente");
            telaExibicao.setScene(new Scene(root));

            //proibe que o usuario possa alterar o tamanho da tela
            telaExibicao.setResizable(false);

            //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
            telaExibicao.initModality(Modality.WINDOW_MODAL);
            telaExibicao.initOwner(campoNomeSocio.getScene().getWindow());

            //abre a tela e aguarda o usuário fechar
            telaExibicao.showAndWait();

            //recarrega as informações
            buscarDadosSocio();
        } else {
            emitirAlerta("Selecione um Dependente!", AlertType.ERROR);
            return;
        }
    }

    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //FUNÇÕES
    //configura os elementos da tela
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //faz com que o usuário não possa mexer nos campos
        campoNomeSocio.setMouseTransparent(true);
        campoNomeSocio.setFocusTraversable(false);
        campoCpfSocio.setMouseTransparent(true);
        campoCpfSocio.setFocusTraversable(false);
        campoTelefoneSocio.setMouseTransparent(true);
        campoTelefoneSocio.setFocusTraversable(false);
        campoNascimentoSocio.setMouseTransparent(true);
        campoNascimentoSocio.setFocusTraversable(false);
        campoEmailSocio.setMouseTransparent(true);
        campoEmailSocio.setFocusTraversable(false);
        campoRuaSocio.setMouseTransparent(true);
        campoRuaSocio.setFocusTraversable(false);
        campoNumeroSocio.setMouseTransparent(true);
        campoNumeroSocio.setFocusTraversable(false);
        campoBairroSocio.setMouseTransparent(true);
        campoBairroSocio.setFocusTraversable(false);
        campoCepSocio.setMouseTransparent(true);
        campoCepSocio.setFocusTraversable(false);
        campoCidadeSocio.setMouseTransparent(true);
        campoCidadeSocio.setFocusTraversable(false);
        campoEstadoSocio.setMouseTransparent(true);
        campoEstadoSocio.setFocusTraversable(false);
    }
    
    //define qual é o sócio que foi selecionado
    public void carregarDadosEmSegundoPlano(int idSocio) throws SQLException {
        this.idSocioSelecionado = idSocio;

        // Define Placeholders com o indicador de carregamento
        campoDebitosSocio.setPlaceholder(criarIndicator());
        campoDependentesSocio.setPlaceholder(criarIndicator());
        campoDepartamentosSocio.setPlaceholder(criarIndicator());
        
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                buscarDadosSocio();
                return null;
            }
        };

        //atualiza os dados caso a busca tenha ocorrido bem
        task.setOnSucceeded(e -> {
            atualizarComponentesTela();
        });

        //mostra um aviso caso os dados não possam ser carregados
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() -> emitirAlerta("Erro ao carregar os dados.", AlertType.ERROR));
        });

        new Thread(task).start(); 
    }

    //preenche os dados nos labels
    private void buscarDadosSocio() throws SQLException {
        //busca os dados do sócio selecionado
        socioSelecionado = socioDAO.buscarPorId(idSocioSelecionado);
        enderecoSocioSelecionado = enderecoDAO.buscarPorId(socioSelecionado.getEndereco().getIdEndereco());
        listaDepartamentosSocio = socio_DepartamentoDAO.buscarDepartamentosSocio(idSocioSelecionado);
        listaDependentesSocio = dependenteDAO.buscarDependentesSocio(idSocioSelecionado);
        listaDebitosSocio = debitoDAO.listarDebitosAbertosSocio(idSocioSelecionado);
        
    }

    //atualiza os componentes visuais da tela após serem carregadas as informações
    private void atualizarComponentesTela() {
        //cria uma lista somente com o nome dos débitos
        List<String> listaNomeDebitos = new ArrayList<>();

        for (Debito debito : listaDebitosSocio) {
            listaNomeDebitos.add(debito.getTipoDebito());
        }


        //preenche os campos com os dados do sócio 
        campoNomeSocio.setText(socioSelecionado.getNomeSocio());
        campoCpfSocio.setText(socioSelecionado.getCpfSocio());
        campoEmailSocio.setText(socioSelecionado.getEmailSocio());
        campoTelefoneSocio.setText(socioSelecionado.getTelefoneSocio());

        //preenche o campo da data de nascimento do socio
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = formatoData.format(socioSelecionado.getDataNascSocio());
        campoNascimentoSocio.setText(dataFormatada);

        //preenche a lista de departamentos daquele sócio
        campoDepartamentosSocio.setItems(FXCollections.observableArrayList(listaDepartamentosSocio));

        //preenche a lista de dependentes daquele sócio
        campoDependentesSocio.setItems(FXCollections.observableArrayList(listaDependentesSocio));

        //preenche a lista de débitos daquele sócio
        campoDebitosSocio.setItems(FXCollections.observableArrayList(listaNomeDebitos));

        //preenche os dados do endereço do sócio
        campoRuaSocio.setText(enderecoSocioSelecionado.getRua());
        campoNumeroSocio.setText(String.valueOf(enderecoSocioSelecionado.getNumero()));
        campoBairroSocio.setText(enderecoSocioSelecionado.getBairro());
        campoCepSocio.setText(enderecoSocioSelecionado.getCep());
        campoCidadeSocio.setText(enderecoSocioSelecionado.getCidade());
        campoEstadoSocio.setText(enderecoSocioSelecionado.getEstado());


        if (listaDepartamentosSocio == null || listaDepartamentosSocio.isEmpty()) {
            campoDepartamentosSocio.setPlaceholder(new Label("Nenhum departamento vinculado."));
        }

        if (listaDependentesSocio == null || listaDependentesSocio.isEmpty()) {
            campoDependentesSocio.setPlaceholder(new Label("Nenhum dependente cadastrado."));
        }

        if (listaNomeDebitos.isEmpty()) {
            campoDebitosSocio.setPlaceholder(new Label("Nenhum débito em aberto."));
        }
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
