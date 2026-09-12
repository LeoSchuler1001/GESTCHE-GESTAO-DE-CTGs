package controller;

import java.sql.SQLException;
import java.util.Optional;

import dao.ConexaoBanco;
import dao.DepartamentoDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

public class AlterarDepartamentoController {
    //ATRIBUTOS
    private Departamento departamentoSelecionado;
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexaoBanco);

    @FXML
    private Button botaoAlterarDepartamento;

    @FXML
    private Button botaoFechar;

    @FXML
    private TextField campoDescricaoDepartamento;

    @FXML
    private TextField campoNomeDepartamento;

    @FXML
    private VBox painelFundo;

    @FXML
    void alterarDepartamentoAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return; }
        //recupera os dados informados pelo usuário
        departamentoSelecionado.setNomeDepartamento(campoNomeDepartamento.getText());
        departamentoSelecionado.setDescricaoDepartamento(campoDescricaoDepartamento.getText());

        //altera os dados do departamento
        departamentoDAO.atualizarDepartamento(departamentoSelecionado);
        
        //emite um aviso 
        emitirAlerta("Departamento alterado com sucesso!", AlertType.INFORMATION);

        //fecha a tela de alteração
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    @FXML
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //MÉTODOS
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());
    }

    public void carregarDadosEmSegundoPlano(Departamento departamentoSelecionado) {
        this.departamentoSelecionado = departamentoSelecionado;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                buscarDadosDepartamento();
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

    //preenche os dados nos labels
    private void buscarDadosDepartamento() throws SQLException {
        //preenche os campos com os dados do departamento
        campoDescricaoDepartamento.setText(departamentoSelecionado.getDescricaoDepartamento());
        campoNomeDepartamento.setText(departamentoSelecionado.getNomeDepartamento());
    }

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
}
