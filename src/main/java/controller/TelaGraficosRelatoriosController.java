package controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.SocioDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Lembrete;
import util.GerarPdf;

public class TelaGraficosRelatoriosController {
    //ATRIBUTOS
    GerarPdf gerarPdf = new GerarPdf();
    //cria as variáveis que vão armazenar as quantidades de sócios e dependentes
    int quantidadeSociosAtivos;
    int quantidadeDependentesAtivos;

    @FXML
    private Button botaoGerarPdf;

    @FXML
    private Label campoNumeroDependentes;

    @FXML
    private Label campoNumeroSocios;

    @FXML
    private Label campoNumeroTotal;

    @FXML
    private ProgressIndicator carregamentoDependentes;

    @FXML
    private ProgressIndicator carregamentoSocios;

    @FXML
    private ProgressIndicator carregamentoTotal;
    
    @FXML
    private ProgressIndicator carregamentoGrafico1;

    @FXML
    private ProgressIndicator carregamentoGrafico2;

    @FXML
    private ProgressIndicator carregamentoGrafico3;

    @FXML
    private ProgressIndicator carregamentoGrafico4;

    @FXML
    private ProgressIndicator carregamentoGrafico5;

    @FXML
    private PieChart graficoAtivosInativos;

    @FXML
    private PieChart graficoEmdiaInadimplentes;

    @FXML
    private PieChart graficoEtnias;

    @FXML
    private PieChart graficoFaixaEtaria;

    @FXML
    private PieChart graficoHomensMulheres;

    @FXML
    private TableColumn<Lembrete, String> lembretes;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkDepartamentos;

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
    void gerarPdfAction(ActionEvent event) {
        //abre uma janela para o usuário escolher onde salvar o arquivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório de Gráficos");
        fileChooser.setInitialFileName("relatorio_graficos.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        Stage stage = (Stage) tabelaLembretes.getScene().getWindow(); 
        File arquivoDestino = fileChooser.showSaveDialog(stage);

        //verifica se foi selecionado um local válido
        if (arquivoDestino != null) {
            try {
                List<File> imagensGraficos = new ArrayList<>();

                //seleciona os gráficos que vão ser incluídos no pdf
                PieChart[] meusGraficos = { 
                    graficoEmdiaInadimplentes, 
                    graficoHomensMulheres, 
                    graficoEtnias, 
                    graficoAtivosInativos, 
                    graficoFaixaEtaria 
                };

                //captura a imagem de todos os gráficos
                for (int i = 0; i < meusGraficos.length; i++) {
                    if (meusGraficos[i] != null) {
                        WritableImage writableImage = meusGraficos[i].snapshot(new SnapshotParameters(), null);
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        
                        File imagem = File.createTempFile("grafico_" + i, ".png");
                        ImageIO.write(bufferedImage, "png", imagem);
                        imagem.deleteOnExit();
                        
                        imagensGraficos.add(imagem);
                    }
                }

                //chama o método de gerar pdfs
                gerarPdf.gerarRelatorioGeralSocios(imagensGraficos, arquivoDestino, quantidadeSociosAtivos, quantidadeDependentesAtivos);

            } catch (IOException e) {
                emitirAlerta("Selecione um local válido para salvar o pdf", AlertType.ERROR);
            }
        }
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
    public void initialize() throws SQLException {
        //desativa a seleção na tabela de lembretes, mas mantêm o scroll
        Callback<TableView<Lembrete>, TableRow<Lembrete>> desativarSelecaoLembrete = tv -> {
            TableRow<Lembrete> row = new TableRow<>();
            
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            row.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return row;
        };

        tabelaLembretes.setRowFactory(desativarSelecaoLembrete);
        tabelaLembretes.setFocusTraversable(false);
        
        this.lembretes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeLembrete())
        );

        botaoGerarPdf.setDisable(true);

        //chama a função que irá carregar os dados das tabelas e dos mostradores
        carregarDadosSegundoPlano();        
    }

    //carrega os dados em segundo plano
    private void carregarDadosSegundoPlano() {
        //coloca os ícones de carregamento nas tabelas enquanto os dados não são carregados
        tabelaLembretes.setPlaceholder(criarIndicator());
        botaoGerarPdf.setDisable(true);

        //cria uma tarefa que irá carregar os dados em segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //cria a conexão com o banco de dados
                ConexaoBanco conexao = new ConexaoBanco();
                LembreteDAO lembreteDAO = new LembreteDAO(conexao);
                SocioDAO socioDAO = new SocioDAO(conexao);

                //cria a lista que vai armazenar os lembretes
                List<Lembrete> listaLembretes = lembreteDAO.listarLembretesHoje(App.usuarioLogado.getIdUsuario());

                //cria as listas de dados que vão armazenar as informações dos gráficos
                ObservableList<PieChart.Data> dadosAtivosInativos = socioDAO.buscarPorcentagemAtivosInativos();
                ObservableList<PieChart.Data> dadosEmdiaInadimplentes = socioDAO.buscarPorcentagemEmdiaInadimplentes();
                ObservableList<PieChart.Data> dadosHomensMulheres = socioDAO.buscarPorcentagemHomensMulheres();
                ObservableList<PieChart.Data> dadosEtnias = socioDAO.buscarPorcentagemEtnias();
                ObservableList<PieChart.Data> dadosFaixaEtaria = socioDAO.buscarPorcentagemFaixaEtaria();              

                //cria as variáveis que vão armazenar as quantidades de sócios e dependentes
                quantidadeSociosAtivos = socioDAO.contarSociosAtivos();
                quantidadeDependentesAtivos = socioDAO.contarDependentesAtivos();
                int totalAtivos = quantidadeDependentesAtivos + quantidadeSociosAtivos;

                // Atualiza as tabelas e os gráficos
                Platform.runLater(() -> {
                    tabelaLembretes.setItems(FXCollections.observableArrayList(listaLembretes));

                    graficoAtivosInativos.setData(dadosAtivosInativos);
                    graficoEmdiaInadimplentes.setData(dadosEmdiaInadimplentes);
                    graficoHomensMulheres.setData(dadosHomensMulheres);
                    graficoEtnias.setData(dadosEtnias);
                    graficoFaixaEtaria.setData(dadosFaixaEtaria);

                    campoNumeroSocios.setText(String.valueOf(quantidadeSociosAtivos));
                    campoNumeroDependentes.setText(String.valueOf(quantidadeDependentesAtivos));
                    campoNumeroTotal.setText(String.valueOf(totalAtivos));

                    if (listaLembretes.isEmpty()) {
                        tabelaLembretes.setPlaceholder(new javafx.scene.control.Label("Sem lembretes."));
                    }

                    botaoGerarPdf.setDisable(false);
                });

                return null;
            }
        };

        //mostra os icones de carregamento enquanto a tarefa está rodando em segundo plano
        carregamentoTotal.visibleProperty().bind(task.runningProperty());
        carregamentoDependentes.visibleProperty().bind(task.runningProperty());
        carregamentoSocios.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico1.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico2.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico3.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico4.visibleProperty().bind(task.runningProperty());
        carregamentoGrafico5.visibleProperty().bind(task.runningProperty());

        //mostra os labels com as informações assim que a tarefa parar de rodar em segundo plano
        campoNumeroSocios.visibleProperty().bind(task.runningProperty().not());
        campoNumeroDependentes.visibleProperty().bind(task.runningProperty().not());
        campoNumeroTotal.visibleProperty().bind(task.runningProperty().not());

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
