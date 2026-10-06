package controller;

import java.io.IOException;
import java.util.Optional;
import app.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TelaContasController {
    //ATRIBUTOS
    @FXML
    private Hyperlink linkCategorias;

    @FXML
    private Hyperlink linkConfiguracoes;

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
    private TableColumn<?, ?> nomeLembrete;

    @FXML
    private TableView<?> tabelaLembretes;

    @FXML
    private TableColumn<?, ?> valorLembrete;

    //BOTÕES
    @FXML
    void categoriasAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaCategorias");
    }

    @FXML
    void configuracoesAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaConfiguracoesTesoureiro");
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