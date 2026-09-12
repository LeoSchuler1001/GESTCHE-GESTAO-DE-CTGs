package controller;

import java.sql.SQLException;
import java.util.Optional;

import dao.ConexaoBanco;
import dao.DepartamentoDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Departamento;

public class CadastrarDepartamentoController {
    //ATRIBUTOS
    //conexão com o banco
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexaoBanco);

    @FXML
    private Button botaoFechar;

    @FXML
    private Button botaoSalvarDepartamento;

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

    @FXML
    void salvarDepartamentoAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return; }

        //instancia um novo departamento
        Departamento departamento = new Departamento();

        //atribui os valores ao objeto
        departamento.setNomeDepartamento(campoNomeDepartamento.getText());
        departamento.setDescricaoDepartamento(campoDescricaoDepartamento.getText());

        //cria o departamento no banco de dados
        departamentoDAO.cadastrarDepartamento(departamento);

        //emite um aleta
        emitirAlerta("Departamento cadastrado com sucesso!", AlertType.INFORMATION);

        //fecha a tela
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //MÉTODOS
    private boolean verificaFormulario() {
        if (campoNomeDepartamento.getText() == null || campoNomeDepartamento.getText().trim().isEmpty()) {
            emitirAlerta("O nome do departamento não pode estar vazio.", AlertType.WARNING);
            campoNomeDepartamento.requestFocus();
            return false;
        }

        if (campoDescricaoDepartamento.getText() == null || campoDescricaoDepartamento.getText().trim().isEmpty()) {
            emitirAlerta("Preencha a descrição do departamento.", AlertType.WARNING);
            campoDescricaoDepartamento.requestFocus();
            return false;
        }
        return true;
    }

    //método auxiliar para emitir alertas
    private boolean emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Atenção");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);

        Optional<ButtonType> resultado = alerta.showAndWait();

        // Verifica se o usuário clicou no botão OK
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
