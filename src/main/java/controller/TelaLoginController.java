package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.Usuario;

public class TelaLoginController {
    //ATRIBUTOS
    @FXML
    private Button botaoLogin;

    @FXML
    private TextField cpfUsuarioLogin;

    @FXML
    private PasswordField senhaUsuarioLogin;

    //BOTÕES
    @FXML
    void fazerLoginAction(ActionEvent event) {
        //verifica se os campos foram preenchidos
        if(validaFormulario() == false) {
            return;
        }

        //instancia o objeto usuario
        Usuario usuario = new Usuario();
        
    }

    //FUNÇÕES
    //valida o formulário 
    private boolean validaFormulario() {
        //verifica o campo login
        if(cpfUsuarioLogin.getText().isEmpty()) {
            //emite um alerta
            emitirAlerta("Preencha o campo Login!", AlertType.ERROR);
            //coloca o cursor no campo login após a confirmação do erro
            cpfUsuarioLogin.requestFocus();
            return false;
        }

        //verifica o campo senha
        if(senhaUsuarioLogin.getText().isEmpty()) {
            emitirAlerta("Preencha o campo senha!", AlertType.ERROR);
            //coloca o cursor no campo senha após a confirmação do erro
            senhaUsuarioLogin.requestFocus();
            return false;
        }

        return true;
    }

    //emite um alerta
    private void emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Login");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
