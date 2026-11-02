package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TelaInDiretorController {
    //ATRIBUTOS
    @FXML
    private Button botaoAcessarSecretaria;

    @FXML
    private Button botaoAcessarTesouraria;

    @FXML
    private Button botaoSelecionarPeriodo;

    @FXML
    private Label campoDespesas;

    @FXML
    private Label campoFechamento;

    @FXML
    private Label campoInadimplentes;

    @FXML
    private TextField campoPeriodo;

    @FXML
    private Label campoReceitas;

    @FXML
    private Label campoSaldoTotal;

    @FXML
    private Label campoSociosAtivos;

    @FXML
    private Label campoTotalDependentes;

    @FXML
    private Label campoTotalSocios;

    @FXML
    private Label campoTotalSociosDependentes;

    @FXML
    private ProgressIndicator carregamentoDependentes;

    @FXML
    private ProgressIndicator carregamentoDespesas;

    @FXML
    private ProgressIndicator carregamentoFechamento;

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
    private ProgressIndicator carregamentoGrafico6;

    @FXML
    private ProgressIndicator carregamentoGrafico7;

    @FXML
    private ProgressIndicator carregamentoInadimplentes;

    @FXML
    private ProgressIndicator carregamentoReceitas;

    @FXML
    private ProgressIndicator carregamentoSaldoTotal;

    @FXML
    private ProgressIndicator carregamentoSocios;

    @FXML
    private ProgressIndicator carregamentoSociosAtivos;

    @FXML
    private ProgressIndicator carregamentoTotalSocios;

    @FXML
    private TableColumn<?, ?> colunaLembretes;

    @FXML
    private PieChart graficoAtivosInativos;

    @FXML
    private PieChart graficoDespesaCategorias;

    @FXML
    private PieChart graficoEmdiaInadimplentes;

    @FXML
    private PieChart graficoEtnias;

    @FXML
    private PieChart graficoFaixaEtaria;

    @FXML
    private PieChart graficoHomensMulheres;

    @FXML
    private PieChart graficoReceitaCategorias;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkGestaoAcesso;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableView<?> tabelaLembretes;

    //BOTÕES
    @FXML
    void acessarSecretariaAction(ActionEvent event) {

    }

    @FXML
    void acessarTesourariaAction(ActionEvent event) {

    }

    @FXML
    void configuracoesAction(ActionEvent event) {

    }

    @FXML
    void gestaoAcessoAction(ActionEvent event) {

    }

    @FXML
    void sairAction(ActionEvent event) {

    }

    @FXML
    void selecionarPeriodoAction(ActionEvent event) {

    }

    //FUNÇÕES
}