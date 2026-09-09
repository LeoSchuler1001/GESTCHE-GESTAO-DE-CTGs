package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Dependente;

public class DetalheDependenteController {
    //ATRIBUTOS
    private Dependente dependenteSelecionado;

    @FXML
    private Button botaoAlterar;

    @FXML
    private Button botaoFechar;

    @FXML
    private TextField campoCpfDependente;

    @FXML
    private DatePicker campoNascimentoDependente;

    @FXML
    private TextField campoNomeDependente;

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

    //MÉTODOS
    //diz qual que foi o dependente selecionado
    public void setDependenteSelecionado(Dependente dependenteSelecionado) {
        this.dependenteSelecionado = dependenteSelecionado;
    }
    
    
}
