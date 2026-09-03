package controller;

import java.io.IOException;
import java.sql.SQLException;

import app.App;
import dao.UsuarioDAO;
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

    @FXML
    private Button botaoRecupararSenha;

    //BOTÕES
    @FXML
    void fazerLoginAction(ActionEvent event) throws SQLException, IOException {
        //verifica se os campos foram preenchidos
        if(validaFormulario() == false) {
            return;
        }

        //pega a senha e login que o usuario digitou
        String cpfDigitado = cpfUsuarioLogin.getText();
        String senhaDigitada = senhaUsuarioLogin.getText();

        //autentica o usuario
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuarioLogin = usuarioDAO.autenticarUsuario(cpfDigitado, senhaDigitada);

        //verifica se ele achou algum usuario
        if(usuarioLogin != null) {
            //atribui o usuario ao atributo estatico usuario logado
            App.usuarioLogado = usuarioLogin;

            //verifica o cargo do usuário
            if(App.usuarioLogado.getCargoUsuario().equals("Secretário")) {
                App.trocarTela("TelaInicialSecretario");
            }
        } else {
            //emite alerta de senha ou login errados
            emitirAlerta("Usuário e/ou senha inválidos!", AlertType.ERROR);
        }
    }

    @FXML
    void recuperarSenhaAction(ActionEvent event) throws IOException {
        App.trocarTela("TelaRecuperacaoSenha");
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
