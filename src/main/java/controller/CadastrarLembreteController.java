package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CadastrarLembreteController {

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private Button botaoSalvar;

    @FXML
    private TextField campoDescricaoLembrete;

    @FXML
    private DatePicker campoFimLembrete;

    @FXML
    private DatePicker campoInicioLembrete;

    @FXML
    private TextField campoNomeLembrete;

    @FXML
    private ComboBox<?> campoPeriodicidadeLembrete;

    @FXML
    private VBox painelFundo;

    @FXML
    void fecharAction(ActionEvent event) {

    }

    @FXML
    void salvarAction(ActionEvent event) {

    }

}
