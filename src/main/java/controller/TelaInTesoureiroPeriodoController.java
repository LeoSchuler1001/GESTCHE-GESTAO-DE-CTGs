package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.Optional;
import app.App;

public class TelaInTesoureiroPeriodoController {
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
    private Hyperlink linkMes;

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
    void anoAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroAno");
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
    void diaAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroDia");
    }

    @FXML
    void graficosRelatoriosAction(ActionEvent event) {

    }

    @FXML
    void lembretesAction(ActionEvent event) {

    }

    @FXML
    void mesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroMes");
    }

    @FXML
    void outrasContasAction(ActionEvent event) {

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
    void semanaAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaInTesoureiroSemana");
    }

    //MÉTODOS
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