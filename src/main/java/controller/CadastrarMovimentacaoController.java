package controller;

import java.sql.Date;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import dao.CategoriaDAO;
import dao.ConexaoBanco;
import dao.ContaDAO;
import dao.MovimentacaoDAO;
import enums.TiposMovimentacoes;
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
import model.Categoria;
import model.Conta;
import model.Movimentacao;

public class CadastrarMovimentacaoController {
    //ATRIBUTOS
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Locale localBrasil = Locale.of("pt", "BR");  
    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(localBrasil);
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexaoBanco);
    CategoriaDAO categoriaDAO = new CategoriaDAO(conexaoBanco);
    ContaDAO contaDAO = new ContaDAO(conexaoBanco);

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private Button botaoSalvar;

    @FXML
    private ComboBox<String> campoCategoriaMovimentacao;

    @FXML
    private ComboBox<String> campoContaMovimentacao;

    @FXML
    private DatePicker campoDataMovimentacao;

    @FXML
    private TextField campoDescricaoMovimentacao;

    @FXML
    private ComboBox<TiposMovimentacoes> campoTipoMovimentacao;

    @FXML
    private TextField campoValorMovimentacao;

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
            //cria um objeto movimentação
            Movimentacao movimentacao = new Movimentacao();

            //atribui as informações ao objeto
            movimentacao.setComentarioMovimentacao(campoDescricaoMovimentacao.getText());

            //salva o valor do débito
            String valorDigitado = campoValorMovimentacao.getText();
            //ajusta a string para salvar em um double
            String valorPuro = valorDigitado.replaceAll("[^0-9.,]", "");
            valorPuro = valorPuro.replace(",", ".");
            //converte para double
            Double valorMovimentacao;
            try {
                valorMovimentacao = Double.parseDouble(valorPuro);
                movimentacao.setValorMovimentacao(valorMovimentacao);
            } catch (NumberFormatException e) {
                emitirAlerta("O campo valor está incorreto!", AlertType.ERROR);
                return;
            }

            movimentacao.setDataMovimentacao(Date.valueOf(campoDataMovimentacao.getValue()));
            
            String tipoMovimentacao = campoTipoMovimentacao.getValue().getDescricao();
            movimentacao.setTipoMovimentacao(tipoMovimentacao);

            movimentacao.setCategoria(categoriaDAO.buscarPorNome(campoCategoriaMovimentacao.getValue()));
            
            Conta conta = contaDAO.buscarPorNome(campoContaMovimentacao.getValue());
            movimentacao.setConta(conta);

            //cadastra a movimentação
            movimentacaoDAO.cadastrarMovimentacao(movimentacao);

            //atualiza o saldo da conta
            if(tipoMovimentacao.equals("Receita")) {
                conta.setSaldo(conta.getSaldo() + valorMovimentacao);
            } else {
                conta.setSaldo(conta.getSaldo() - valorMovimentacao);
            }
            
            //atualiza a conta no banco de dados
            contaDAO.atualizarConta(conta);

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
        Locale.setDefault(Locale.of("pt", "BR"));
        campoDataMovimentacao.setConverter(new StringConverter<LocalDate>() {
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
        campoDataMovimentacao.setEditable(false);

        //preenche o combobox de tipos de movimentações
        campoTipoMovimentacao.getItems().setAll(TiposMovimentacoes.values());

        //preenche o combobox das contas cadastradas
        List<Conta> listaContas = contaDAO.listarContas();
        List<String> nomeContas = new ArrayList<>();
        for (Conta conta : listaContas) {
            nomeContas.add(conta.getNomeConta());
        }
        campoContaMovimentacao.getItems().setAll(nomeContas);

        //preenche o combobox das categorias cadastradas
        List<Categoria> listaCategorias = categoriaDAO.listarCategorias();
        List<String> nomeCategorias = new ArrayList<>();
        for (Categoria categoria : listaCategorias) {
            nomeCategorias.add(categoria.getNomeCategoria());
        }
        campoCategoriaMovimentacao.getItems().setAll(nomeCategorias);
    }

    public boolean verificaFormulario() {
        //verifica os campos de texto
        if (campoDescricaoMovimentacao.getText() == null || campoDescricaoMovimentacao.getText().trim().isEmpty() ||
            campoValorMovimentacao.getText() == null || campoValorMovimentacao.getText().trim().isEmpty()) {
            
            emitirAlertaSimples("Preencha todos os campos!");
            return false;
        }

        //valida se o valor da movimentação é válido
        String valorDigitado = campoValorMovimentacao.getText();
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
        LocalDate vencimento = campoDataMovimentacao.getValue();
        if (vencimento == null) {
            emitirAlertaSimples("Selecione a data de vencimento!");
            return false;
        }

        //verifica a seleção do tipo
        if(campoTipoMovimentacao.getValue() == null) {
            emitirAlertaSimples("Selecione o tipo da movimentação!");
            return false;
        }

        //verifica a seleção da categoria
        if(campoCategoriaMovimentacao.getValue() == null) {
            emitirAlertaSimples("Selecione a categoria da movimentação!");
            return false;
        }

        //verifica a seleção da conta
        if(campoContaMovimentacao.getValue() == null) {
            emitirAlertaSimples("Selecione a conta da movimentação!");
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
}