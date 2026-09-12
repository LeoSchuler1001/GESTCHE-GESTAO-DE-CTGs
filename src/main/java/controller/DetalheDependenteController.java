package controller;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import app.App;
import dao.ConexaoBanco;
import dao.DependenteDAO;
import dao.LogAuditoriaDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Dependente;
import model.LogAuditoria;

public class DetalheDependenteController {
    //ATRIBUTOS
    private Dependente dependenteSelecionado;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    DependenteDAO dependenteDAO = new DependenteDAO(conexaoBanco);
    LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexaoBanco);

    @FXML
    private Button botaoAlterar;

    @FXML
    private Button excluirDependente;

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
    void alterarAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return; }
        
        //faz a confirmação com o usuário
        boolean confirmaAlteracao = emitirAlertaConfirmacao("Deseja realmente alterar?", AlertType.CONFIRMATION);

        if(confirmaAlteracao) {
            //altera as informações no objeto dependente
            dependenteSelecionado.setNomeDependente(campoNomeDependente.getText());
            String cpfLimpo = campoCpfDependente.getText().replaceAll("[^0-9]", "");
            dependenteSelecionado.setCpfDependente(cpfLimpo);
            dependenteSelecionado.setDataNascDependente(Date.valueOf(campoNascimentoDependente.getValue()));
            
            dependenteDAO.atualizarDependente(dependenteSelecionado);

            emitirAlerta("Sócio alterado com sucesso", AlertType.INFORMATION);
            
            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
    }
    
    @FXML
    void excluirDependenteAction(ActionEvent event) throws SQLException {
        //faz a confirmação com o usuário
        boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja realmente excluir?", AlertType.CONFIRMATION);

        if(confirmaExclusao) {
            dependenteDAO.excluirDependente(dependenteSelecionado);
            
            //cadastra um log de auditoria no banco de dados
            String descricaoLog = "Exlusão do dependente " + dependenteSelecionado.getNomeDependente() + ", CPF: " + dependenteSelecionado.getCpfDependente();
            LogAuditoria logAuditoria = new LogAuditoria(descricaoLog, App.usuarioLogado, App.usuarioLogado.getNomeUsuario());
            logAuditoriaDAO.cadastrarLog(logAuditoria, App.usuarioLogado);

            //fecha a tela após a eclusão
            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
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

        //configura para que a data do seletor de datas fique em português
        Locale.setDefault(new Locale("pt", "BR"));
        campoNascimentoDependente.setConverter(new StringConverter<LocalDate>() {
            public String toString(LocalDate date) {
                return (date != null) ? formatoData.format(date) : "";
            }

            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    return LocalDate.parse(string, formatoData);
                } else {
                    return null;
                }
            }
        });

        //impede do usuario digitar no campo da data
        campoNascimentoDependente.setEditable(false);
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

    private boolean emitirAlertaConfirmacao(String mensagem, AlertType tipoAlerta) {
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

        //preenche o campo da data de nascimento do socio
        LocalDate localDate = ((java.sql.Date) dependenteSelecionado.getDataNascDependente()).toLocalDate();        
        campoNascimentoDependente.setValue(localDate);
    }

    public boolean verificaFormulario() {
        //verifica os campos de textos
        if (campoNomeDependente.getText() == null || campoNomeDependente.getText().trim().isEmpty() ||
            campoCpfDependente.getText() == null || campoCpfDependente.getText().trim().isEmpty()) {
            
            emitirAlertaSimples("Preencha todos os campos obrigatórios!");
            return false;
        }

        //valida o tamanho do cpf
        String cpfLimpo = campoCpfDependente.getText().replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            emitirAlertaSimples("O CPF deve conter exatamente 11 dígitos!");
            return false;
        }

        //valida se a data de nascimento está preenchida
        LocalDate dataNascimento = campoNascimentoDependente.getValue();
        if (dataNascimento == null) {
            emitirAlertaSimples("Selecione a data de nascimento!");
            return false;
        }
        
        //valida se a data faz sentido
        LocalDate hoje = LocalDate.now();
        if (dataNascimento.isAfter(hoje)) {
            emitirAlertaSimples("A data de nascimento é inválida!");
            return false;
        }
        if (dataNascimento.isBefore(hoje.minusYears(120))) {
            emitirAlertaSimples("A data de nascimento é inválida!");
            return false;
        }

        return true;
    }

    private void emitirAlertaSimples(String mensagem) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
