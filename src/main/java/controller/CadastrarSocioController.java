package controller;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.App;
import dao.ConexaoBanco;
import dao.DepartamentoDAO;
import dao.EnderecoDAO;
import dao.SocioDAO;
import dao.Socio_DepartamentoDAO;
import enums.EstadosBrasil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Departamento;
import model.Endereco;
import model.Socio;

public class CadastrarSocioController {
    //ATRIBUTOS
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    List<String> listaDepartamentosSocio = new ArrayList<>();
    List<Departamento> listaTodosDepartamentos;


    //conexões com o banco de dados
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    SocioDAO socioDAO = new SocioDAO(conexaoBanco);
    EnderecoDAO enderecoDAO = new EnderecoDAO(conexaoBanco);
    Socio_DepartamentoDAO socio_DepartamentoDAO = new Socio_DepartamentoDAO(conexaoBanco);
    DepartamentoDAO departamentoDAO = new DepartamentoDAO(conexaoBanco);

    @FXML
    private Button botaoAdicionarDepartamento;

    @FXML
    private Button botaoSalvarSocio;

    @FXML
    private TextField campoBairroSocio;

    @FXML
    private TextField campoCepSocio;

    @FXML
    private TextField campoCidadeSocio;

    @FXML
    private TextField campoCpfSocio;

    @FXML
    private ListView<String> campoDepartamentosSocio;

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
    private ComboBox<String> listaDeDepartamentos;

    @FXML
    private VBox painelFundo;

    //BOTÕES
    @FXML
    void adicionarDepartamentoAction(ActionEvent event) {
        //recupera o valor da string selecionada
        String departamentoSelecionado = listaDeDepartamentos.getValue();

        //verifica se o usuário selecionou alguma opção
        if(departamentoSelecionado == null) {
            emitirAlerta("Selecione uma opção!", AlertType.ERROR);
            return;
        }

        //verifica se ela já está na lista de departamentos do usuário
        if(listaDepartamentosSocio.contains(departamentoSelecionado)) {
            emitirAlerta("Este sócio já pertence a este departamento!", AlertType.ERROR);
            return;
        }

        //adiciona o departamento selecionado à lista de departamentos daquele sócio
        listaDepartamentosSocio.add(departamentoSelecionado);

        //preenche a lista de departamentos daquele sócio
        campoDepartamentosSocio.setItems(FXCollections.observableArrayList(listaDepartamentosSocio));
    }

    @FXML
    void salvarAction(ActionEvent event) throws SQLException {
        if(!verificaFormulario()) { return ;}

        //cria um objeto para armazenar as informações
        Socio socioCadastro = new Socio();

        //altera as informações no objeto sócio
        String cpfLimpo = campoCpfSocio.getText().replaceAll("[^0-9]", "");
        socioCadastro.setCpfSocio(cpfLimpo);
        socioCadastro.setNomeSocio(campoNomeSocio.getText());
        socioCadastro.setEmailSocio(campoEmailSocio.getText());
        socioCadastro.setDataNascSocio(Date.valueOf(campoNascimentoSocio.getValue()));
        socioCadastro.setUsuario(App.usuarioLogado);

        //verifica se o telefone esta preenchido
        if(!campoTelefoneSocio.getText().isEmpty()) {  
            socioCadastro.setTelefoneSocio(campoTelefoneSocio.getText());
        } else {
            socioCadastro.setTelefoneSocio("");
        }

        //cria um objeto endereço para armazenar o endereço do sócio
        Endereco endereco = new Endereco();

        //altera od dados do endereço
        endereco.setRua(campoRuaSocio.getText());
        endereco.setNumero(Integer.parseInt(campoNumeroSocio.getText()));
        endereco.setBairro(campoBairroSocio.getText());
        endereco.setCidade(campoCidadeSocio.getText());
        endereco.setEstado(campoEstadoSocio.getValue().name());
        String cepLimpo = campoCepSocio.getText().replaceAll("[^0-9]", "");
        endereco.setCep(cepLimpo);

        //cadastra o endereço no banco de dados
        enderecoDAO.cadastrarEndereco(endereco);

        //atribui o endereço ao sócio
        socioCadastro.setEndereco(endereco);

        //cadastra o sócio no banco de dados
        socioDAO.cadastrarSocio(socioCadastro);

        //cadastra os departamentos que foram selecionados
        for (String departamento : listaDepartamentosSocio) {
            //cria o registro no banco de dados
            int idDepartamento = departamentoDAO.buscarIdDepartamento(departamento);
            socio_DepartamentoDAO.vincularSocioDepartamento(socioCadastro.getIdSocio(), idDepartamento);
        }

        emitirAlerta("Sócio cadastrado com sucesso", AlertType.INFORMATION);
        Stage stage = (Stage) painelFundo.getScene().getWindow();
        stage.close();
    }

    //MÉTODOS
    //configura os elementos da tela
    public void initialize() throws SQLException {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //configura o campo de departamentos para não receber cliques, apenas o scroll
        campoDepartamentosSocio.setCellFactory(listView -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            
            //bloqueia os cliques, mas não a rolagem
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> event.consume());
            
            return cell;
        });
        campoDepartamentosSocio.setMouseTransparent(false);
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
        listaTodosDepartamentos = departamentoDAO.listarTodos();
        List<String> nomeDepartamentos = new ArrayList<>();
        for (Departamento departamento : listaTodosDepartamentos) {
            nomeDepartamentos.add(departamento.getNomeDepartamento());
        }
        listaDeDepartamentos.getItems().setAll(nomeDepartamentos);

        //preenche a lista de estados
        campoEstadoSocio.getItems().setAll(EstadosBrasil.values());

        //impede do usuario digitar no campo da data
        campoNascimentoSocio.setEditable(false);
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

        //verifica se o número do endereço contém apenas números
        String numeroTexto = campoNumeroSocio.getText();
        if (numeroTexto != null && !numeroTexto.matches("\\d+")) {
            emitirAlerta("O número do endereço deve conter apenas dígitos!", AlertType.ERROR);
            return false;
        }
        
        return true;
    }

    //emite um alerta
    private void emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

}
