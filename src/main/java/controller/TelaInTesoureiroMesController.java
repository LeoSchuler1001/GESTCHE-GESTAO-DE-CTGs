package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class TelaInTesoureiroMesController {
    //ATRIBUTOS
    @FXML
    private Button botaoAdicionarDespesa;

    @FXML
    private Button botaoAdicionarReceita;

    @FXML
    private Button botaoDetalharDespesa;

    @FXML
    private Button botaoDetalharReceita;

    @FXML
    private Button botaoOutrasContas;

    @FXML
    private Label campoSaldoTotal;

    @FXML
    private ProgressIndicator carregamentoConta1;

    @FXML
    private ProgressIndicator carregamentoConta2;

    @FXML
    private ProgressIndicator carregamentoTotal;

    @FXML
    private TableColumn<?, ?> colunaDataDespesa;

    @FXML
    private TableColumn<?, ?> colunaDataReceita;

    @FXML
    private TableColumn<?, ?> colunaDescricaoDespesa;

    @FXML
    private TableColumn<?, ?> colunaDescricaoReceita;

    @FXML
    private TableColumn<?, ?> colunaValorReceita;

    @FXML
    private TableColumn<?, ?> colunavalorDespesa;

    @FXML
    private ImageView iconeConta1;

    @FXML
    private ImageView iconeConta2;

    @FXML
    private Hyperlink linkAno;

    @FXML
    private Hyperlink linkCategorias;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkContas;

    @FXML
    private Hyperlink linkDebitosSocios;

    @FXML
    private Hyperlink linkDia;

    @FXML
    private Hyperlink linkGraficosRelatorios;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkPeriodo;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private Hyperlink linkSemana;

    @FXML
    private Label nomeConta1;

    @FXML
    private Label nomeConta2;

    @FXML
    private TableColumn<?, ?> nomeLembrete;

    @FXML
    private Label saldoConta1;

    @FXML
    private Label saldoConta2;

    @FXML
    private TableView<?> tabelaDespesas;

    @FXML
    private TableView<?> tabelaLembretes;

    @FXML
    private TableView<?> tabelaReceitas;

    @FXML
    private TableColumn<?, ?> valorLembrete;

    //BOTÕES
    @FXML
    void adicionarDespesaAction(ActionEvent event) {

    }

    @FXML
    void adicionarReceitaAction(ActionEvent event) {

    }

    @FXML
    void anoAction(ActionEvent event) {

    }

    @FXML
    void categoriasAction(ActionEvent event) {

    }

    @FXML
    void configuracoesAction(ActionEvent event) {

    }

    @FXML
    void contasAction(ActionEvent event) {

    }

    @FXML
    void debitosSociosAction(ActionEvent event) {

    }

    @FXML
    void detalharDespesaAction(ActionEvent event) {

    }

    @FXML
    void detalharReceitaAction(ActionEvent event) {

    }

    @FXML
    void diaAction(ActionEvent event) {

    }

    @FXML
    void graficosRelatoriosAction(ActionEvent event) {

    }

    @FXML
    void lembretesAction(ActionEvent event) {

    }

    @FXML
    void outrasContasAction(ActionEvent event) {

    }

    @FXML
    void periodoAction(ActionEvent event) {

    }

    @FXML
    void sairAction(ActionEvent event) {

    }

    @FXML
    void semanaAction(ActionEvent event) {

    }

    //MÉTODOS
}