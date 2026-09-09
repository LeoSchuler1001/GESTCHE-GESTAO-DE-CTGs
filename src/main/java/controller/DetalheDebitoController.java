package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Debito;

public class DetalheDebitoController {
    //ATRIBUTOS
    private Debito debitoSelecionado;

    @FXML
    private Button botaoAlterar;

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private TextField campoTipoDebito;

    @FXML
    private TextField campoValorDebito;

    @FXML
    private DatePicker campoVencimentoDebito;

    @FXML
    private VBox painelFundo;

    //BOTÕES
    @FXML
    void alterarAction(ActionEvent event) {

    }

    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    @FXML
    void registrarPagamentoAction(ActionEvent event) {

    }
    
    //FUNÇÕES
    public void setDebitoSelecionado(Debito debitoSelecionado) {
        this.debitoSelecionado = debitoSelecionado;
    }
}
