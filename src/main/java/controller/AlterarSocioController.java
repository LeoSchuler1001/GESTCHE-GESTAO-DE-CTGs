package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.sql.Date;

import dao.ConexaoBanco;
import dao.EnderecoDAO;
import dao.SocioDAO;
import dao.Socio_DepartamentoDAO;
import dao.DepartamentoDAO;
import enums.Departamentos;
import enums.EstadosBrasil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Endereco;
import model.Socio;

public class AlterarSocioController {
    //ATRIBUTOS
    private int idSocioSelecionado;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    List<String> listaDepartamentosSocio;
    Endereco enderecoSocioSelecionado;
    Socio socioSelecionado;

    //conexões com o banco de dados
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    SocioDAO socioDAO = new SocioDAO(conexaoBanco);
    EnderecoDAO enderecoDAO = new EnderecoDAO(conexaoBanco);
    Socio_DepartamentoDAO socio_DepartamentoDAO = new Socio_DepartamentoDAO(conexaoBanco);
    DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexaoBanco);

    @FXML
    private Button botaoAdicionarDepartamento;

    @FXML
    private Button botaoExcluirDepartamento;

    @FXML
    private ListView<String> campoDepartamentosSocio;

    @FXML
    private ComboBox<Departamentos> listaDeDepartamentos;

    @FXML
    private Button botaoAlterarSocio;

    @FXML
    private TextField campoBairroSocio;

    @FXML
    private TextField campoCepSocio;

    @FXML
    private TextField campoCidadeSocio;

    @FXML
    private TextField campoCpfSocio;

    @FXML
    private TextField campoEmailSocio;

    @FXML
    private ComboBox<EstadosBrasil> campoEstadoSocio;

    @FXML
    private DatePicker campoNascimentoSocio;

    @FXML
    private TextField campoNomeSocio;

    @FXML
    private TextField campoNumeroSocio;

    @FXML
    private TextField campoRuaSocio;

    @FXML
    private TextField campoTelefoneSocio;

    @FXML
    private VBox painelFundo;

    //BOTÕES
    @FXML
    void adicionarDepartamentoAction(ActionEvent event) throws SQLException {
        //recupera o valor da string selecionada
        Departamentos departamentoSelecionado = listaDeDepartamentos.getValue();

        //verifica se o usuário selecionou alguma opção
        if(departamentoSelecionado == null) {
            emitirAlerta("Selecione uma opção!", AlertType.ERROR);
            return;
        }

        //verifica se ela já está na lista de departamentos do usuário
        if(listaDepartamentosSocio.contains(departamentoSelecionado.name())) {
            emitirAlerta("Este sócio já pertence a este departamento!", AlertType.ERROR);
            return;
        }

        //adiciona o departamento selecionado à lista de departamentos daquele sócio
        listaDepartamentosSocio.add(departamentoSelecionado.name());

        //preenche a lista de departamentos daquele sócio
        campoDepartamentosSocio.setItems(FXCollections.observableArrayList(listaDepartamentosSocio));

        //cria o registro no banco de dados
        int idDepartamento = departamentoDAO.buscarIdDepartamento(departamentoSelecionado.name());
        socio_DepartamentoDAO.vincularSocioDepartamento(idSocioSelecionado, idDepartamento);
    }

    @FXML
    void excluirDepartamentoAction(ActionEvent event) throws SQLException {
        //recupera o departamento selecionado
        Departamentos departamentoSelecionado = listaDeDepartamentos.getValue();

        //verifica se o usuário selecionou alguma opção
        if(departamentoSelecionado == null) {
            emitirAlerta("Selecione uma opção!", AlertType.ERROR);
            return;
        }

        //verifica se ela já está na lista de departamentos do usuário
        if(!listaDepartamentosSocio.contains(departamentoSelecionado.name())) {
            emitirAlerta("Este sócio não pertence a este departamento!", AlertType.ERROR);
            return;
        }

        //faz a confirmação com o usuário
        boolean confirmaExclusao = emitirAlertaConfirmacao("Deseja realmente excluir?", AlertType.CONFIRMATION);

        if (confirmaExclusao) {
            //remove o departamento da lista de departamentos do sócio
            listaDepartamentosSocio.remove(departamentoSelecionado.name());
    
            //preenche a lista de departamentos daquele sócio
            campoDepartamentosSocio.setItems(FXCollections.observableArrayList(listaDepartamentosSocio));
    
            //exclui o registro no banco de dados
            int idDepartamento = departamentoDAO.buscarIdDepartamento(departamentoSelecionado.name());
            socio_DepartamentoDAO.desvincularSocioDepartamento(idSocioSelecionado, idDepartamento);
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
        
    }

    @FXML
    void alterarAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return ;}

        //altera as informações no objeto sócio
        String cpfLimpo = campoCpfSocio.getText().replaceAll("[^0-9]", "");
        socioSelecionado.setCpfSocio(cpfLimpo);
        socioSelecionado.setNomeSocio(campoNomeSocio.getText());
        socioSelecionado.setEmailSocio(campoEmailSocio.getText());
        socioSelecionado.setDataNascSocio(Date.valueOf(campoNascimentoSocio.getValue()));

        //verifica se o telefone esta preenchido
        if(!campoTelefoneSocio.getText().isEmpty()) {  
            socioSelecionado.setTelefoneSocio(campoTelefoneSocio.getText());
        } else {
            socioSelecionado.setTelefoneSocio("");
        }

        //altera od dados do endereço
        enderecoSocioSelecionado.setRua(campoRuaSocio.getText());
        enderecoSocioSelecionado.setNumero(Integer.parseInt(campoNumeroSocio.getText()));
        enderecoSocioSelecionado.setBairro(campoBairroSocio.getText());
        enderecoSocioSelecionado.setCidade(campoCidadeSocio.getText());
        enderecoSocioSelecionado.setEstado(campoEstadoSocio.getValue().name());
        String cepLimpo = campoCepSocio.getText().replaceAll("[^0-9]", "");
        enderecoSocioSelecionado.setCep(cepLimpo);

        emitirAlerta("Sócio alterado com sucesso", AlertType.INFORMATION);

        socioDAO.atualizarSocio(socioSelecionado);
        enderecoDAO.alterarEndereco(enderecoSocioSelecionado);

        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //FUNÇÕES
    //configura os elementos da tela
    public void initialize() {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //faz com que o usuário não possa mexerno campo de departamentos
        campoDepartamentosSocio.setMouseTransparent(true);
        campoDepartamentosSocio.setFocusTraversable(false);

        //configura para que a data do seletor de datas fique em português
        Locale.setDefault(new Locale("pt", "BR"));
        campoNascimentoSocio.setConverter(new StringConverter<LocalDate>() {
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

        //preenche o comboBox dos departamentos
        listaDeDepartamentos.getItems().setAll(Departamentos.values());

        //preenche a lista de estados
        campoEstadoSocio.getItems().setAll(EstadosBrasil.values());

        //impede do usuario digitar no campo da data
        campoNascimentoSocio.setEditable(false);
    }

    //define qual é o sócio que foi selecionado
    public void setIdSocioSelecionado(int idSocio) throws SQLException {
        this.idSocioSelecionado = idSocio;
        
        // Carrega o sócio e preenche a tela
        buscarDadosSocio(); 
    }

    //preenche os dados nos labels
    private void buscarDadosSocio() throws SQLException {
        //busca os dados do sócio selecionado
        socioSelecionado = socioDAO.buscarPorId(idSocioSelecionado);
        enderecoSocioSelecionado = enderecoDAO.buscarPorId(socioSelecionado.getEndereco().getIdEndereco());
        listaDepartamentosSocio = socio_DepartamentoDAO.buscarDepartamentosSocio(idSocioSelecionado);

        //preenche os campos com os dados do sócio 
        campoNomeSocio.setText(socioSelecionado.getNomeSocio());
        campoCpfSocio.setText(socioSelecionado.getCpfSocio());
        campoEmailSocio.setText(socioSelecionado.getEmailSocio());
        campoTelefoneSocio.setText(socioSelecionado.getTelefoneSocio());

        //preenche o campo da data de nascimento do socio
        LocalDate localDate = ((java.sql.Date) socioSelecionado.getDataNascSocio()).toLocalDate();        
        campoNascimentoSocio.setValue(localDate);

        //preenche a lista de departamentos daquele sócio
        campoDepartamentosSocio.setItems(FXCollections.observableArrayList(listaDepartamentosSocio));

        //preenche os dados do endereço do sócio
        campoRuaSocio.setText(enderecoSocioSelecionado.getRua());
        campoNumeroSocio.setText(String.valueOf(enderecoSocioSelecionado.getNumero()));
        campoBairroSocio.setText(enderecoSocioSelecionado.getBairro());
        campoCepSocio.setText(enderecoSocioSelecionado.getCep());
        campoCidadeSocio.setText(enderecoSocioSelecionado.getCidade());
        campoEstadoSocio.setValue(EstadosBrasil.valueOf(enderecoSocioSelecionado.getEstado()));
    }

    //emite um alerta
    private void emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
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
        //verifica campos de texto
        if (campoNomeSocio.getText() == null || campoNomeSocio.getText().trim().isEmpty() ||
            campoCpfSocio.getText() == null || campoCpfSocio.getText().trim().isEmpty() ||
            campoEmailSocio.getText() == null || campoEmailSocio.getText().trim().isEmpty() ||
            campoRuaSocio.getText() == null || campoRuaSocio.getText().trim().isEmpty() ||
            campoNumeroSocio.getText() == null || campoNumeroSocio.getText().trim().isEmpty() ||
            campoBairroSocio.getText() == null || campoBairroSocio.getText().trim().isEmpty() ||
            campoCidadeSocio.getText() == null || campoCidadeSocio.getText().trim().isEmpty() ||
            campoCepSocio.getText() == null || campoCepSocio.getText().trim().isEmpty()) {
            
            emitirAlerta("Preencha todos os campos!", AlertType.ERROR);
            return false;
        }

        //verifica se o cpf está correto
        String cpfLimpo = campoCpfSocio.getText().replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            emitirAlerta("O CPF deve conter exatamente 11 dígitos!", AlertType.ERROR);
            return false;
        }

        //verifica se o cep está correto
        String cepLimpo = campoCepSocio.getText().replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            emitirAlerta("O CEP deve conter exatamente 8 dígitos!", AlertType.ERROR);
            return false;
        }

        //verificação do telefone
        String telefoneTexto = campoTelefoneSocio.getText();
        if (telefoneTexto != null && !telefoneTexto.trim().isEmpty() && !telefoneTexto.equals("Telefone não cadastrado!")) {
            String telefoneLimpo = telefoneTexto.replaceAll("[^0-9]", "");
            if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
                emitirAlerta("O telefone deve conter 10 ou 11 dígitos (com DDD)!", AlertType.ERROR);
                return false;
            }
        }

        //valida o preenchimento da data de nascimento
        LocalDate dataNascimento = campoNascimentoSocio.getValue();
        if (dataNascimento == null) {
            emitirAlerta("Selecione a data de nascimento!", AlertType.ERROR);
            return false;
        }
        
        //valida a data de nascimento
        LocalDate hoje = LocalDate.now();
        if (dataNascimento.isAfter(hoje)) {
            emitirAlerta("A data de nascimento é inválida!", AlertType.ERROR);
            return false;
        }
        if (dataNascimento.isBefore(hoje.minusYears(120))) {
            emitirAlerta("A data de nascimento é inválida!", AlertType.ERROR);
            return false;
        }

        //verifica o estado
        if (campoEstadoSocio.getValue() == null) {
            emitirAlerta("Selecione o estado!", AlertType.ERROR);
            return false;
        }
        return true;
    }
}
