package controller;

import java.sql.SQLException;
import java.util.Optional;
import dao.CategoriaDAO;
import dao.ConexaoBanco;
import enums.TiposMovimentacoes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Categoria;

public class CadastrarCategoriaController {
    //ATRIBUTOS
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    CategoriaDAO categoriaDAO = new CategoriaDAO(conexaoBanco);

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private Button botaoSalvar;

    @FXML
    private TextField campoNomeConta;

    @FXML
    private VBox painelFundo;

    @FXML
    private ComboBox<TiposMovimentacoes> campoTipoCategoria;

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
            Categoria categoria = new Categoria();

            categoria.setNomeCategoria(campoNomeConta.getText());

            String tipoCategoria = campoTipoCategoria.getValue().getDescricao();
            categoria.setTipoCategoria(tipoCategoria);

            categoriaDAO.cadastrarCategoria(categoria);

            emitirAlerta("Cadastro realizado com sucesso!", AlertType.INFORMATION);

            //fecha a tela após a eclusão
            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();
        }
    }

    //MÉTODOS
    //MÉTODOS
    public void initialize() {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //preenche o combobox de tipos de movimentações
        campoTipoCategoria.getItems().setAll(TiposMovimentacoes.values());
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
        if (campoNomeConta.getText() == null || campoNomeConta.getText().trim().isEmpty()) {
            
            emitirAlerta("Preencha todos os campos!", AlertType.ERROR);
            return false;
        }

        if (campoTipoCategoria.getValue() == null) {
            emitirAlerta("Preencha todos os campos!", AlertType.ERROR);
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
