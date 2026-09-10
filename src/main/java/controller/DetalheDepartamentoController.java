package controller;

import java.sql.SQLException;
import java.util.Optional;

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
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //faz com que o usuário não possa mexer nos campos
        campoDescricaoDepartamento.setMouseTransparent(true);
        campoDescricaoDepartamento.setFocusTraversable(false);
        campoNomeDepartamento.setMouseTransparent(true);
        campoNomeDepartamento.setFocusTraversable(false);
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
        alerta.setTitle("Confirmação");
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
}
