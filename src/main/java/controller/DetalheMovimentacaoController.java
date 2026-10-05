package controller;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import app.App;
import dao.ConexaoBanco;
import dao.ContaDAO;
import dao.LogAuditoriaDAO;
import dao.MovimentacaoDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Conta;
import model.LogAuditoria;
import model.Movimentacao;

public class DetalheMovimentacaoController {
    //ATRIBUTOS
    private Movimentacao movimentacaoSelecionada;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Locale localBrasil = Locale.of("pt", "BR");
    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(localBrasil);
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO(conexaoBanco);
    LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexaoBanco);
    ContaDAO contaDAO = new ContaDAO(conexaoBanco);

    @FXML
    private Button botaoExcluir;

    @FXML
    private Button botaoFechar;

    @FXML
    private HBox botaoRegistrarPagamento;

    @FXML
    private TextField campoContaMovimentacao;

    @FXML
    private TextField campoDataMovimentacao;

    @FXML
    private TextField campoDescricaoMovimentacao;

    @FXML
    private TextField campoTipoMovimentacao;

    @FXML
    private TextField campoValorMovimentacao;

    @FXML
    private TextField campoCategoriaMovimentacao;

    @FXML
    private VBox painelFundo;

    //BOTÕES
    @FXML
    void excluirAction(ActionEvent event) throws SQLException {
        //faz a confirmação com o usuário
        boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja realmente excluir?", AlertType.CONFIRMATION);
        Conta conta = movimentacaoSelecionada.getConta();

        if(confirmaExclusao) {
            if(movimentacaoSelecionada.getTipoMovimentacao().equals("Receita")) {
                Double saldoAtual = conta.getSaldo();

                conta.setSaldo(saldoAtual - movimentacaoSelecionada.getValorMovimentacao());

                contaDAO.atualizarConta(conta);
            } else {
                Double saldoAtual = conta.getSaldo();

                conta.setSaldo(saldoAtual + movimentacaoSelecionada.getValorMovimentacao());

                contaDAO.atualizarConta(conta);
            }
            
            movimentacaoDAO.excluirMovimentacao(movimentacaoSelecionada);

            //cadastra um log de auditoria no banco de dados
            String descricaoLog = "Exlusão da movimentação (" + movimentacaoSelecionada.getTipoMovimentacao() + ") " + movimentacaoSelecionada.getComentarioMovimentacao() + ", da conta " + conta.getNomeConta();
            LogAuditoria logAuditoria = new LogAuditoria(descricaoLog, App.usuarioLogado, App.usuarioLogado.getNomeUsuario());
            logAuditoriaDAO.cadastrarLog(logAuditoria, App.usuarioLogado);

            emitirAlerta("Movimentação excluída com sucesso!", AlertType.INFORMATION);
            //fecha a tela após a eclusão
            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();

        } else { 
            emitirAlerta("Exclusão cancelada!", AlertType.INFORMATION);
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

        campoDescricaoMovimentacao.setEditable(false);
        campoValorMovimentacao.setEditable(false);
        campoDataMovimentacao.setEditable(false);
        campoTipoMovimentacao.setEditable(false);
        campoContaMovimentacao.setEditable(false);
        campoCategoriaMovimentacao.setEditable(false);
    }

    public void carregarDadosEmSegundoPlano(Movimentacao movimentacaoSelecionada) {
        this.movimentacaoSelecionada = movimentacaoSelecionada;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                buscarDadosMovimentacao();
                return null;
            }
        };

        //mostra um aviso caso os dados não possam ser carregados
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() -> emitirAlerta("Erro ao carregar os dados.", AlertType.ERROR));
        });

        //cria uma nova Thread para rodar a tarefa de carregamento em segundo plano
        new Thread(task).start();
    }

    //preenche os dados nos labels
    private void buscarDadosMovimentacao() throws SQLException {
        //preenche os campos com os dados do departamento
        campoDescricaoMovimentacao.setText(movimentacaoSelecionada.getComentarioMovimentacao());
        
        //preenche o campo do valor do débito
        Double valorMovimentacao = movimentacaoSelecionada.getValorMovimentacao();
        String valor = formatoMoeda.format(valorMovimentacao);
        campoValorMovimentacao.setText(valor);

        //preenche o campo da data de vencimento do debito
        LocalDate localDate = ((java.sql.Date) movimentacaoSelecionada.getDataMovimentacao()).toLocalDate();
        campoDataMovimentacao.setText(localDate.format(formatoData));

        campoTipoMovimentacao.setText(movimentacaoSelecionada.getTipoMovimentacao());
        campoContaMovimentacao.setText(movimentacaoSelecionada.getConta().getNomeConta());
        campoCategoriaMovimentacao.setText(movimentacaoSelecionada.getCategoria().getNomeCategoria());
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
