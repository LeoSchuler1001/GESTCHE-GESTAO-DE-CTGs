package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import dao.ConexaoBanco;
import dao.DependenteDAO;
import dao.LogAuditoriaDAO;
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
import model.Dependente;

public class DetalheDependenteTesoureiroController {
    //ATRIBUTOS
    private Dependente dependenteSelecionado;
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    DependenteDAO dependenteDAO = new DependenteDAO(conexaoBanco);
    LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexaoBanco);

    @FXML
    private Button botaoFechar;

    @FXML
    private TextField campoCorDependente;

    @FXML
    private TextField campoCpfDependente;

    @FXML
    private TextField campoNascimentoDependente;

    @FXML
    private TextField campoNomeDependente;

    @FXML
    private TextField campoSexoDependente;

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
        campoCorDependente.setMouseTransparent(true);
        campoCorDependente.setFocusTraversable(false);
        campoCpfDependente.setMouseTransparent(true);
        campoCpfDependente.setFocusTraversable(false);
        campoNascimentoDependente.setMouseTransparent(true);
        campoNascimentoDependente.setFocusTraversable(false);
        campoNomeDependente.setMouseTransparent(true);
        campoNomeDependente.setFocusTraversable(false);
        campoSexoDependente.setMouseTransparent(true);
        campoSexoDependente.setFocusTraversable(false);
    }

    //diz qual que foi o dependente selecionado
    public void carregarDadosEmSegundoPlano(Dependente dependenteSelecionado) {
        this.dependenteSelecionado = dependenteSelecionado;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                buscarDadosDependente();
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
    private void buscarDadosDependente() throws SQLException {
        //preenche os campos com os dados do departamento
        campoNomeDependente.setText(dependenteSelecionado.getNomeDependente());
        campoCpfDependente.setText(dependenteSelecionado.getCpfDependente());
        
        campoSexoDependente.setText(dependenteSelecionado.getSexoDependente());
        campoCorDependente.setText(dependenteSelecionado.getCorDependente());

        //preenche o campo da data de nascimento do socio
        LocalDate localDate = ((java.sql.Date) dependenteSelecionado.getDataNascDependente()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        campoNascimentoDependente.setText(localDate.format(formatter));
    }
}