package controller;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.dto.DatasTelaPeriodoDTO;

public class SelecionarPeriodoController {
    //ATRIBUTOS
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static DatasTelaPeriodoDTO datasTelaPeriodoDTO;
    boolean confirmaConsulta = false;

    @FXML
    private Button botaoConsultar;

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private DatePicker dataFinal;

    @FXML
    private DatePicker dataInicio;

    @FXML
    private VBox painelFundo;
    
    //BOTÕES
    @FXML
    void consultarAction(ActionEvent event) {
        if(!verificaFormulario()) { return; }

        datasTelaPeriodoDTO = new DatasTelaPeriodoDTO();
        datasTelaPeriodoDTO.setDataInicial(Date.valueOf(dataInicio.getValue()));
        datasTelaPeriodoDTO.setDataFinal(Date.valueOf(dataFinal.getValue()));

        this.confirmaConsulta = true;
        
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

        //configura para que a data do seletor de datas fique em português
        Locale.setDefault(Locale.of("pt", "BR"));
        dataInicio.setConverter(new StringConverter<LocalDate>() {
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

        Locale.setDefault(Locale.of("pt", "BR"));
        dataFinal.setConverter(new StringConverter<LocalDate>() {
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
        dataFinal.setEditable(false);
        dataInicio.setEditable(false);
    }

    public boolean verificaFormulario() {
        // Verifica o campo de inicio
        LocalDate inicio = dataInicio.getValue();
        if (inicio == null) {
            emitirAlertaSimples("Selecione a data de início!");
            return false;
        }

        // Verifica o campo de final
        LocalDate dtFinal = dataFinal.getValue();
        if (dtFinal == null) {
            emitirAlertaSimples("Selecione a data de fim!");
            return false;
        }

        if(dtFinal.isAfter(inicio)) {
            emitirAlertaSimples("A data final não pode ser anterios à data inicial!");
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