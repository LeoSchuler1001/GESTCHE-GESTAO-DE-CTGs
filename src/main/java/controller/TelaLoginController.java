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
    void fazerLoginAction(ActionEvent event) {
        System.out.println("TESTE: ENTROU NO METODO FAZER LOGIN");

        if (!validaFormulario()) {
            return;
        }

        if (!validaFormulario()) {
            return;
        }

        String cpfDigitado = cpfUsuarioLogin.getText();
        String senhaDigitada = senhaUsuarioLogin.getText();

        try {
            System.out.println("1 - Iniciando autenticação...");

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuarioLogin = usuarioDAO.autenticarUsuario(cpfDigitado, senhaDigitada);

            System.out.println("2 - Retorno da autenticação: "
                    + (usuarioLogin == null ? "NENHUM USUÁRIO" : "USUÁRIO ENCONTRADO"));

            if (usuarioLogin != null) {

                App.usuarioLogado = usuarioLogin;

                String cargo = App.usuarioLogado.getCargoUsuario();

                System.out.println("3 - Usuário logado: " + App.usuarioLogado.getIdUsuario());
                System.out.println("4 - Cargo encontrado: [" + cargo + "]");

                if ("Secretário".equals(cargo)) {
                    System.out.println("5 - Abrindo tela do secretário...");
                    App.trocarTela("TelaInicialSecretario");

                } else if ("Tesoureiro".equals(cargo)) {
                    System.out.println("5 - Abrindo tela do tesoureiro...");
                    App.trocarTela("TelaInTesoureiroMes");

                } else if ("Diretor".equals(cargo)) {
                    System.out.println("5 - Abrindo tela do diretor...");
                    App.trocarTela("TelaInDiretor");

                } else {
                    System.err.println("Cargo não reconhecido: [" + cargo + "]");
                    emitirAlerta("Cargo de usuário não reconhecido: " + cargo, AlertType.ERROR);
                }

            } else {
                System.out.println("3 - Usuário não encontrado ou senha inválida.");
                emitirAlerta("Usuário e/ou senha inválidos!", AlertType.ERROR);
            }

        } catch (SQLException e) {
            System.err.println("ERRO NA CONEXÃO OU CONSULTA AO BANCO:");
            e.printStackTrace();
            emitirAlerta("Não foi possível consultar o banco de dados. Verifique a conexão.", AlertType.ERROR);

        } catch (IOException e) {
            System.err.println("ERRO AO ABRIR A PRÓXIMA TELA:");
            e.printStackTrace();
            emitirAlerta("Ocorreu um erro ao abrir a próxima tela.", AlertType.ERROR);
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