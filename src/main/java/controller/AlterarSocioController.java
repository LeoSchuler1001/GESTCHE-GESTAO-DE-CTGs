package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import dao.ConexaoBanco;
import dao.EnderecoDAO;
import dao.SocioDAO;
import dao.Socio_DepartamentoDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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

    @FXML
    private Button botaoAdicionarDepartamento;

    @FXML
    private Button botaoExcluirDepartamento;

    @FXML
    private ListView<String> campoDepartamentosSocio;

    @FXML
    private ComboBox<String> listaDeDepartamentos;

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
    private ComboBox<String> campoEstadoSocio;

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
    void adicionarDepartamentoAction(ActionEvent event) {

    }

    @FXML
    void excluirDepartamentoAction(ActionEvent event) {

    }

    @FXML
    void alterarAction(ActionEvent event) {
        
    }

    //FUNÇÕES
    //configura os elementos da tela
    public void initialize() {
        //tira o foco dos campos, para o cursos não ficar em nenhum deles
        Platform.runLater(() -> painelFundo.requestFocus());

        //configura para que a data do seletor de datas fique em português
        Locale.setDefault(new Locale("pt", "BR"));
        campoNascimentoSocio.setConverter(new StringConverter<LocalDate>() {
            public String toString(LocalDate date) {
                return (date != null) ? formatoData.format(date) : "";
            }

            public LocalDate fromString(String string) {
                throw new UnsupportedOperationException("Unimplemented method 'fromString'");
            }
        });
    }

    //define qual é o sócio que foi selecionado
    public void setIdSocioSelecionado(int idSocio) throws SQLException {
        this.idSocioSelecionado = idSocio;
        
        // Carrega o sócio e preenche a tela SOMENTE AGORA
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
        campoEstadoSocio.setValue(String.valueOf(enderecoSocioSelecionado.getEstado()));
    }
}
