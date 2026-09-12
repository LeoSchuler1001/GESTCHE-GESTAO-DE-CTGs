package controller;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import dao.ConexaoBanco;
import dao.DependenteDAO;
import dao.SocioDAO;
import javafx.application.Platform;
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
import javafx.util.StringConverter;
import model.Dependente;

public class CadastrarDependenteController {
    //ATRIBUTOS
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    SocioDAO socioDAO = new SocioDAO(conexaoBanco);
    DependenteDAO dependenteDAO = new DependenteDAO(conexaoBanco);
    private int idSocioSelecionado;

    @FXML
    private Button botaoFechar;

    @FXML
    private Button botaoSalvar;

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
    void fecharAction(ActionEvent event) {
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    @FXML
    void salvarAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return; }

        //faz a confirmação com o usuário
        boolean confirmaCadastro = emitirAlertaConfirmacao("Deseja realmente cadastrar?", AlertType.CONFIRMATION);
        
        if(confirmaCadastro) {
            //cria um objeto Dependente
            Dependente dependente = new Dependente();

            //atribui as informações ao objeto dependente
            String cpfLimpo = campoCpfDependente.getText().replaceAll("[^0-9]", "");
            dependente.setCpfDependente(cpfLimpo);
            dependente.setNomeDependente(campoNomeDependente.getText());
            dependente.setDataNascDependente(Date.valueOf(campoNascimentoDependente.getValue()));
            dependente.setSocio(socioDAO.buscarPorId(idSocioSelecionado));

            //cadastra o dependente
            dependenteDAO.cadastrarDependente(dependente);

            emitirAlerta("Dependente cadastrado com sucesso", AlertType.INFORMATION);

            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();
        }
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

    public void setIdSocioSelecionado(int idSocioSelecionado) {
        this.idSocioSelecionado = idSocioSelecionado;
    }

    public boolean verificaFormulario() {
        //verifica os campos de textos
        if (campoNomeDependente.getText() == null || campoNomeDependente.getText().trim().isEmpty() ||
            campoCpfDependente.getText() == null || campoCpfDependente.getText().trim().isEmpty() ||
            campoNascimentoDependente.getValue() == null) {
            
            emitirAlerta("Preencha todos os campos!", AlertType.ERROR);
            return false;
        }

        //verifica se o cpf possui a quantidade certa de dígitos
        String cpfLimpo = campoCpfDependente.getText().replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            emitirAlerta("O CPF deve conter exatamente 11 dígitos!", AlertType.ERROR);
            return false;
        }

        //valida a data de nascimento
        LocalDate dataNascimento = campoNascimentoDependente.getValue();
        LocalDate hoje = LocalDate.now();
        
        if (dataNascimento.isAfter(hoje) || dataNascimento.isBefore(hoje.minusYears(120))) {
            emitirAlerta("A data de nascimento é inválida!", AlertType.ERROR);
            return false;
        }

        return true;
    }

}
