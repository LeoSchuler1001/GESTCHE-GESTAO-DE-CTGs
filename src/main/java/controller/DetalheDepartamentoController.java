package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Departamento;

public class DetalheDepartamentoController {
    //ATRIBUTOS
    private Departamento departamentoSelecionado;

    @FXML
    private Button botaoFechar;

    @FXML
    private TextField campoDescricaoDepartamento;

    @FXML
    private TextField campoNomeDepartamento;

    @FXML
    private VBox painelFundo;

    //BOTÕES
    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //MÉTODOS
    public void setDepartamentoSelecionado(Departamento departamentoSelecionado) {
        this.departamentoSelecionado = departamentoSelecionado;
    }

    
}
