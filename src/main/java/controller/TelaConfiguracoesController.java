package controller;

import java.io.IOException;

import app.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TelaConfiguracoesController {

    @FXML
    private TableColumn<?, ?> lembretes;

    @FXML
    private Hyperlink linkDepartamentos;

    @FXML
    private Hyperlink linkGraficosRelatorios;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSociosDependentes;

    @FXML
    private TableView<?> tabelaLembretes;

    @FXML
    void departamentosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaDepartamentos");
    }

    @FXML
    void graficosRelatoriosAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaGraficosRelatorios");
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

}