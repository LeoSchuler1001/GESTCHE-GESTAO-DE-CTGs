package controller;

import java.sql.Date;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import dao.ConexaoBanco;
import dao.DebitoDAO;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Debito;

public class CadastrarDebitoController {
    //ATRIBUTOS
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Locale localBrasil = new Locale("pt", "BR");    
    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(localBrasil);
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    DebitoDAO debitoDAO = new DebitoDAO(conexaoBanco);
    SocioDAO socioDAO = new SocioDAO(conexaoBanco);
    private int idSocioSelecionado;

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private Button botaoSalvar;

    @FXML
    private TextField campoTipoDebito;

    @FXML
    private TextField campoValorDebito;

    @FXML
    private DatePicker campoVencimentoDebito;

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
            //cria um objeto débito
            Debito debito = new Debito();

            //atribui as informações ao objeto debito
            debito.setTipoDebito(campoTipoDebito.getText());

            //salva o valor do débito
            String valorDigitado = campoValorDebito.getText();
            //ajusta a string para salvar em um double
            String valorPuro = valorDigitado.replaceAll("[^0-9.,]", "");
            valorPuro = valorPuro.replace(",", ".");
            //converte para double
            try {
                debito.setValorDebito(Double.parseDouble(valorPuro));
            } catch (NumberFormatException e) {
                emitirAlerta("O campo valor está incorreto!", AlertType.ERROR);
                return;
            }

            debito.setVencimentoDebito(Date.valueOf(campoVencimentoDebito.getValue()));
            
            debito.setSocio(socioDAO.buscarPorId(idSocioSelecionado));

            debitoDAO.cadastrarDebito(debito);

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
        Locale.setDefault(new Locale("pt", "BR"));
        campoVencimentoDebito.setConverter(new StringConverter<LocalDate>() {
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
        campoVencimentoDebito.setEditable(false);
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
        //verifica os campos de texto
        if (campoTipoDebito.getText() == null || campoTipoDebito.getText().trim().isEmpty() ||
            campoValorDebito.getText() == null || campoValorDebito.getText().trim().isEmpty()) {
            
            emitirAlertaSimples("Preencha todos os campos!");
            return false;
        }

        //valida se o valor do débito é válido
        String valorDigitado = campoValorDebito.getText();
        String valorPuro = valorDigitado.replaceAll("[^0-9.,]", "").replace(",", ".");
        try {
            double valor = Double.parseDouble(valorPuro);
            if (valor < 0) {
                emitirAlertaSimples("O valor do débito não pode ser negativo!");
                return false;
            }
        } catch (NumberFormatException e) {
            emitirAlertaSimples("O campo valor está incorreto! Insira um número válido.");
            return false;
        }

        // Verifica o campo de vencimento
        LocalDate vencimento = campoVencimentoDebito.getValue();
        if (vencimento == null) {
            emitirAlertaSimples("Selecione a data de vencimento!");
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
