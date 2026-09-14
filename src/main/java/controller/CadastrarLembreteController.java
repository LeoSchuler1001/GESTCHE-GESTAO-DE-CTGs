package controller;

import java.sql.Date;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import app.App;
import dao.ConexaoBanco;
import dao.LembreteDAO;
import dao.SocioDAO;
import enums.PeriodicidadeLembretes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Lembrete;

public class CadastrarLembreteController {
    //ATRIBUTOS
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Locale localBrasil = new Locale("pt", "BR");    
    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(localBrasil);
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    LembreteDAO lembreteDAO = new LembreteDAO(conexaoBanco);
    SocioDAO socioDAO = new SocioDAO(conexaoBanco);

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private Button botaoSalvar;

    @FXML
    private TextField campoDescricaoLembrete;

    @FXML
    private DatePicker campoInicioLembrete;

    @FXML
    private TextField campoNomeLembrete;

    @FXML
    private ComboBox<String> campoPeriodicidadeLembrete;

    @FXML
    private VBox painelFundo;

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
            //cria um objeto lembrete
            Lembrete lembrete = new Lembrete();

            //atribui as informações ao objeto lembrete
            lembrete.setNomeLembrete(campoNomeLembrete.getText());
            lembrete.setDescricaoLembrete(campoDescricaoLembrete.getText());
            
            lembrete.setDataInicioLembrete(Date.valueOf(campoInicioLembrete.getValue()));
            lembrete.setUsuario(App.usuarioLogado);

            //recupera a hora do sistema
            LocalTime horarioAtual = LocalTime.now();
            lembrete.setHorarioLembrete(java.sql.Time.valueOf(horarioAtual));

            lembrete.setPeriodicidadeLembrete(campoPeriodicidadeLembrete.getValue());

            lembreteDAO.cadastrarLembrete(lembrete);

            emitirAlerta("Débito cadastrado com sucesso", AlertType.INFORMATION);
            
            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
    }

    //MÉTODOS
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //configura para que a data do seletor de datas fique em português
        campoInicioLembrete.setConverter(new StringConverter<LocalDate>() {
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
        campoInicioLembrete.setEditable(false);

        //preenche o combobox das periodicidades
        campoPeriodicidadeLembrete.getItems().clear();
        for (PeriodicidadeLembretes periodicidade : PeriodicidadeLembretes.values()) {
            campoPeriodicidadeLembrete.getItems().add(periodicidade.getDescricao());
        }
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

    public boolean verificaFormulario() {
        //verifica os campos de texto
        if (campoNomeLembrete.getText() == null || campoNomeLembrete.getText().trim().isEmpty() ||
            campoDescricaoLembrete.getText() == null || campoDescricaoLembrete.getText().trim().isEmpty()) {
            
            emitirAlertaSimples("Preencha todos os campos!");
            return false;
        }

        // Verifica o campo de vencimento
        LocalDate inicio = campoInicioLembrete.getValue();
        if (inicio == null) {
            emitirAlertaSimples("Selecione a data de início do lembrete!");
            return false;
        }

        //verifica se foi selecionada a periodicidade
        if (campoPeriodicidadeLembrete.getValue() == null) {
            emitirAlerta("Selecione a periodicidade!", AlertType.ERROR);
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
