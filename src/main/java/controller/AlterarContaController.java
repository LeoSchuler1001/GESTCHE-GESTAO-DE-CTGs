package controller;

import java.sql.SQLException;
import java.util.Optional;

import dao.ConexaoBanco;
import dao.ContaDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Conta;
import model.Departamento;

public class AlterarContaController {
    //ATRIBUTOS
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    ContaDAO contaDAO = new ContaDAO(conexaoBanco);
    Conta contaSelecionada;

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private Button botaoSalvar;

    @FXML
    private TextField campoNomeConta;

    @FXML
    private TextField campoSaldoConta;

    @FXML
    private VBox painelFundo;

    //BOTÕES
    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    @FXML
    void salvarAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return; }

        //faz a confirmação com o usuário
        boolean confirmaCadastro = emitirAlertaConfirmacao("Deseja realmente alterar?", AlertType.CONFIRMATION);

        if(confirmaCadastro) {
            contaSelecionada.setNomeConta(campoNomeConta.getText());

            //salva o valor do débito
            String valorDigitado = campoSaldoConta.getText();
            //ajusta a string para salvar em um double
            String valorPuro = valorDigitado.replaceAll("[^0-9.,]", "");
            valorPuro = valorPuro.replace(",", ".");
            //converte para double
            try {
                contaSelecionada.setSaldo(Double.parseDouble(valorPuro));
            } catch (NumberFormatException e) {
                emitirAlerta("O campo valor está incorreto!", AlertType.ERROR);
                return;
            }

            contaDAO.atualizarConta(contaSelecionada);

            emitirAlerta("Atualização realizada com sucesso!", AlertType.INFORMATION);

            //fecha a tela após a eclusão
            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();
        }
    }

    //MÉTODOS
    public void initialize() {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());
    }

    public void carregarDadosEmSegundoPlano(Conta contaSelecionada) {
        this.contaSelecionada = contaSelecionada;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                buscarDadosConta();
                return null;
            }
        };

        //mostra um aviso caso os dados não possam ser carregados
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() -> emitirAlerta("Erro ao carregar os dados.", AlertType.ERROR));
        });

        new Thread(task).start(); 
    }

    //preenche os dados nos labels
    private void buscarDadosConta() throws SQLException {
        //preenche os campos com os dados do departamento
        campoNomeConta.setText(contaSelecionada.getNomeConta());
        campoSaldoConta.setText("R$" + contaSelecionada.getSaldo());
    }

    //método auxiliar para emitir alertas
    private boolean emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);

        Optional<ButtonType> resultado = alerta.showAndWait();

        // Verifica se o usuário clicou no botão OK
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    public boolean verificaFormulario() {
        //verifica os campos de texto
        if (campoNomeConta.getText() == null || campoNomeConta.getText().trim().isEmpty() ||
            campoSaldoConta.getText() == null || campoSaldoConta.getText().trim().isEmpty()) {
            
            emitirAlerta("Preencha todos os campos!", AlertType.ERROR);
            return false;
        }

        //valida se o valor do débito é válido
        String valorDigitado = campoSaldoConta.getText();
        String valorPuro = valorDigitado.replaceAll("[^0-9.,]", "").replace(",", ".");
        try {
            double valor = Double.parseDouble(valorPuro);
            if(valor < 0) {

            }
        } catch (NumberFormatException e) {
            emitirAlerta("O campo valor está incorreto! Insira um número válido.", AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean emitirAlertaConfirmacao(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);

        Optional<ButtonType> resultado = alerta.showAndWait();

        // Verifica se o usuário clicou no botão OK
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}