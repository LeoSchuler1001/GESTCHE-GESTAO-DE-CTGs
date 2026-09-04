package controller;

import java.io.IOException;
import java.sql.SQLException;

import app.App;
import dao.ConexaoBanco;
import dao.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Usuario;
import util.Criptografia;

public class TelaRecuperacaoSenhaController {
    //ATRIBUTOS
    @FXML
    private Button botaoRedefinir;

    @FXML
    private TextField campoCpfUsuario;

    @FXML
    private PasswordField campoConfirmacaoSenha;

    @FXML
    private PasswordField campoNovaSenha;

    @FXML
    private TextField campoRespostaSeguranca;

    //BOTÕES
    @FXML
    void redefirnirAction(ActionEvent event) throws SQLException, IOException {
        if(validaFormulario()) {
            ConexaoBanco conexao = new ConexaoBanco();
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
            Usuario usuario = usuarioDAO.buscaUsuarioPorCpf(campoCpfUsuario.getText());

            //verifica se foi encontrado um usuário
            if(usuario != null) {
                //verifica se a resposta de segurança está correta
                if(Criptografia.verificar(campoRespostaSeguranca.getText(), usuario.getRespostaSeguranca().trim())) {
                    //atualiza a senha
                    usuario.setSenhaHash(Criptografia.gerarHash(campoNovaSenha.getText()));
                    usuarioDAO.atualizarUsuario(usuario);

                    emitirAlerta("Senha alterada com sucesso", AlertType.INFORMATION);
                    App.trocarTela("TelaLogin");
                } else {
                    emitirAlerta("A resposta de segurança está incorreta!", AlertType.ERROR);
                }
            } else {
                emitirAlerta("Usuário não encontrado!", AlertType.ERROR);
            }
        }
    }

    //FUNÇÕES
    //valida o formulário 
    private boolean validaFormulario() {
        //verifica o campo login
        if(campoCpfUsuario.getText().isEmpty()) {
            //emite um alerta
            emitirAlerta("Preencha o campo CPF!", AlertType.ERROR);
            //coloca o cursor no campo cpf após a confirmação do erro
            campoCpfUsuario.requestFocus();
            return false;
        }

        //verifica o campo senha
        if(campoNovaSenha.getText().isEmpty()) {
            emitirAlerta("Preencha o campo NOVA SENHA!", AlertType.ERROR);
            //coloca o cursor no campo nova senha após a confirmação do erro
            campoNovaSenha.requestFocus();
            return false;
        }

        //verifica o campo senha
        if(campoConfirmacaoSenha.getText().isEmpty()) {
            emitirAlerta("Preencha o campo Confirmação de senha!", AlertType.ERROR);
            //coloca o cursor no campo confirmação de senha
            campoConfirmacaoSenha.requestFocus();
            return false;
        }

        //verifica o campo senha
        if(campoRespostaSeguranca.getText().isEmpty()) {
            emitirAlerta("Preencha o campo RESPOSTA DE SEGURANÇA!", AlertType.ERROR);
            //coloca o cursor no campo resposta de segurança
            campoRespostaSeguranca.requestFocus();
            return false;
        }

        //verifica se as senhas coincidem
        if(!campoNovaSenha.getText().equals(campoConfirmacaoSenha.getText())) {
            emitirAlerta("As senhas não coincidem!", AlertType.ERROR);
            //coloca o cursor no campo confirmação de senha
            campoConfirmacaoSenha.requestFocus();
            return false;
        }

        return true;
    }

    //emite um alerta
    private void emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Redefinição de senha");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

}
