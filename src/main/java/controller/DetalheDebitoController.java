package controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import dao.DebitoDAO;
import dao.ConexaoBanco;
import dao.LogAuditoriaDAO;
import dao.Recebimento_pendenteDAO;
import app.App;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Debito;
import model.Socio;
import model.LogAuditoria;

public class DetalheDebitoController {
    //ATRIBUTOS
    private Debito debitoSelecionado;
    private Socio socioSelecionado;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    Locale localBrasil = Locale.of("pt", "BR");
    NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(localBrasil);
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    DebitoDAO debitoDAO = new DebitoDAO(conexaoBanco);
    LogAuditoriaDAO logAuditoriaDAO = new LogAuditoriaDAO(conexaoBanco);
    Recebimento_pendenteDAO recebimento_pendenteDAO = new Recebimento_pendenteDAO(conexaoBanco);

    @FXML
    private Button botaoAlterar;

    @FXML
    private Button botaoFechar;

    @FXML
    private Button botaoExcluirDebito;

    @FXML
    private HBox botaoRegistrarPagamento;

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
    void alterarAction(ActionEvent event) throws SQLException {
        //verifica se já não foi passado para a tesouraria
        if (recebimento_pendenteDAO.debitoPossuiRecebimentoPendente(debitoSelecionado.getIdDebito())) {
            emitirAlerta("Este débito não pode mais ser alterado, pois está aguardando uma confirmação de recebimento da tesouraria", AlertType.ERROR);
            return;
        }

        if (!verificaFormulario()) { return; }
        
        //faz a confirmação com o usuário
        boolean confirmaAlteracao = emitirAlertaConfirmacao("Deseja realmente alterar?", AlertType.CONFIRMATION);

        if (confirmaAlteracao) {
            //salva os valores antigos para criar um log de auditoria
            String tipoAntigo = debitoSelecionado.getTipoDebito();
            double valorAntigo = debitoSelecionado.getValorDebito();
            LocalDate vencimentoAntigo = debitoSelecionado.getVencimentoDebito().toLocalDate();

            //altera as informações no objeto debito
            String novoTipo = campoTipoDebito.getText();
            debitoSelecionado.setTipoDebito(novoTipo);

            //ajusta a string digitada para salvar em um double
            String valorDigitado = campoValorDebito.getText();
            String valorPuro = valorDigitado.replaceAll("[^0-9.,]", "");
            valorPuro = valorPuro.replace(",", ".");
            
            double novoValor;
            try {
                novoValor = Double.parseDouble(valorPuro);
                debitoSelecionado.setValorDebito(novoValor);
            } catch (NumberFormatException e) {
                emitirAlerta("O campo valor está incorreto!", AlertType.ERROR);
                return;
            }

            LocalDate novoVencimento = campoVencimentoDebito.getValue();
            debitoSelecionado.setVencimentoDebito(Date.valueOf(novoVencimento));
            
            //atualiza o débito no banco de dados
            debitoDAO.atualizarDebito(debitoSelecionado);
            
            boolean houveMudanca = false;

            if (!tipoAntigo.equals(novoTipo)) {
                //constroi a string do log de auditoria
                StringBuilder descricaoLog = new StringBuilder();
                descricaoLog.append("Alteração no débito do sócio ").append(debitoSelecionado.getSocio().getNomeSocio()).append(". ");
                descricaoLog.append("Descrição: mudou de ").append(tipoAntigo).append(" para ").append(novoTipo.trim()).append(". ");
                houveMudanca = true;

                //verifica se houve alguma mudança na alteração
                if (houveMudanca) {
                    LogAuditoria log = new LogAuditoria();
                    log.setDescricaoLog(descricaoLog.toString());
                    log.setDataHoraLog(LocalDateTime.now());
                    log.setUsuario(App.usuarioLogado);
                    log.setNomeUsuario(App.usuarioLogado.getNomeUsuario());
                    
                    logAuditoriaDAO.cadastrarLog(log, App.usuarioLogado);
                }
            }

            if (valorAntigo != novoValor) {
                //constroi a string do log de auditoria
                StringBuilder descricaoLog = new StringBuilder();
                descricaoLog.append("Alteração no débito do sócio ").append(debitoSelecionado.getSocio().getNomeSocio()).append(". ");
                descricaoLog.append("Valor: mudou de ").append(valorAntigo).append(" para ").append(novoValor).append(". ");
                houveMudanca = true;

                //verifica se houve alguma mudança na alteração
                if (houveMudanca) {
                    LogAuditoria log = new LogAuditoria();
                    log.setDescricaoLog(descricaoLog.toString());
                    log.setDataHoraLog(LocalDateTime.now());
                    log.setUsuario(App.usuarioLogado);
                    log.setNomeUsuario(App.usuarioLogado.getNomeUsuario());
                    
                    logAuditoriaDAO.cadastrarLog(log, App.usuarioLogado);
                }
            }

            if (!vencimentoAntigo.equals(novoVencimento)) {
                //constroi a string do log de auditoria
                StringBuilder descricaoLog = new StringBuilder();
                descricaoLog.append("Alteração no débito do sócio ").append(debitoSelecionado.getSocio().getNomeSocio()).append(". ");
                descricaoLog.append("Vencimento: mudou de ").append(vencimentoAntigo).append(" para ").append(novoVencimento).append(". ");
                houveMudanca = true;

                //verifica se houve alguma mudança na alteração
                if (houveMudanca) {
                    LogAuditoria log = new LogAuditoria();
                    log.setDescricaoLog(descricaoLog.toString());
                    log.setDataHoraLog(LocalDateTime.now());
                    log.setUsuario(App.usuarioLogado);
                    log.setNomeUsuario(App.usuarioLogado.getNomeUsuario());
                    
                    logAuditoriaDAO.cadastrarLog(log, App.usuarioLogado);
                }
            }

            emitirAlerta("Débito alterado com sucesso", AlertType.INFORMATION);
            
            Stage stage = (Stage) painelFundo.getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
    }
    
    @FXML
    void excluirDebitoAction(ActionEvent event) throws SQLException {
        //verifica se já não foi passado para a resouraria
        if(recebimento_pendenteDAO.debitoPossuiRecebimentoPendente(debitoSelecionado.getIdDebito())) {
            emitirAlerta("Este débito não pode mais ser excluído, pois está aguardando uma confirmação de recebimento da tesouraria", AlertType.ERROR);
            return;
        }

        //faz a confirmação com o usuário
        boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja realmente excluir?", AlertType.CONFIRMATION);

        if(confirmaExclusao) {
            debitoDAO.excluirDebito(debitoSelecionado);
            
            //cadastra um log de auditoria no banco de dados
            String descricaoLog = "Exlusão do debito " + debitoSelecionado.getTipoDebito() + ", do sócio: " + socioSelecionado.getNomeSocio();
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

    @FXML
    void registrarPagamentoAction(ActionEvent event) throws SQLException, IOException {
        //verifica se já não foi passado para a tesouraria
        if(recebimento_pendenteDAO.debitoPossuiRecebimentoPendente(debitoSelecionado.getIdDebito())) {
            emitirAlerta("Este débito já possui um registo de pagamento que está aguardando a aprovação da tesouraria.", AlertType.ERROR);
            return;
        }
        
        if(App.usuarioLogado.getCargoUsuario().equals("Tesoureiro")) {
            //verifica se um debito foi selecionado
            if (debitoSelecionado != null) {
                //abre a tela de exibição dos dados do debito
                //carregamento do fxml
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TelaRecebimentoDebito.fxml"));
                Parent root = fxmlLoader.load();

                //obtem o controller da tela de alteração
                RecebimentoDebitoController controller = fxmlLoader.getController();
                controller.carregarDadosEmSegundoPlano(debitoSelecionado, socioSelecionado);
                
                //cria e exibe a tela de alteração
                Stage telaExibicao = new Stage();
                telaExibicao.setTitle("Cadastro de recebimento");
                telaExibicao.setScene(new Scene(root));

                //proibe que o usuario possa alterar o tamanho da tela
                telaExibicao.setResizable(false);

                //bloqueia interações com a tela principal enquanto a outra tela estiver aberta
                telaExibicao.initModality(Modality.WINDOW_MODAL);
                telaExibicao.initOwner(campoTipoDebito.getScene().getWindow());

                //abre a tela e aguarda o usuário fechar
                telaExibicao.showAndWait();

                emitirAlerta("Pagamento registrado.", AlertType.INFORMATION);

                Stage stage = (Stage) painelFundo.getScene().getWindow();
                stage.close();
            }
        } else {
            if(recebimento_pendenteDAO.registrarPagamentoPendente(debitoSelecionado.getIdDebito(), App.usuarioLogado.getIdUsuario())) {
                emitirAlerta("Pagamento aguardando confirmação da tesouraria!", AlertType.INFORMATION);

                debitoSelecionado.setTipoDebito(debitoSelecionado.getTipoDebito() + " (Aguardando confirmação)");
                debitoDAO.atualizarDebito(debitoSelecionado);

                Stage stage = (Stage) painelFundo.getScene().getWindow();
                stage.close();
            } else {
                emitirAlerta("Erro no registro do pagamento! Tente novamente mais tarde.", AlertType.ERROR);

                Stage stage = (Stage) painelFundo.getScene().getWindow();
                stage.close();
            }
        }
    }
    
    //FUNÇÕES
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //configura para que a data do seletor de datas fique em português
        Locale.setDefault(Locale.of("pt", "BR"));
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

    //diz qual que foi o dependente selecionado e carrega os dados em segundo plano
    public void carregarDadosEmSegundoPlano(Debito debitoSelecionado, Socio socioSelecionado) {
        this.debitoSelecionado = debitoSelecionado;
        this.socioSelecionado = socioSelecionado;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                buscarDadosDebito();
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
    private void buscarDadosDebito() throws SQLException {
        //preenche os campos com os dados do debito
        campoTipoDebito.setText(debitoSelecionado.getTipoDebito());

        //preenche o campo do valor do débito
        Double valorDebito = debitoSelecionado.getValorDebito();
        String valor = formatoMoeda.format(valorDebito);
        campoValorDebito.setText(valor);

        //preenche o campo da data de vencimento do debito
        LocalDate localDate = ((java.sql.Date) debitoSelecionado.getVencimentoDebito()).toLocalDate();        
        campoVencimentoDebito.setValue(localDate);
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