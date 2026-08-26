package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import app.App;
import dao.ConexaoBanco;
import dao.SocioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Socio;

public class TelaInSecretarioController {
    //ATRIBUTOS
    @FXML
    private Label campoSociosAtivos;

    @FXML
    private Label campoSociosInadimplentes;

    @FXML
    private Label campoSociosInativos;

    @FXML
    private Label campoTotalSocios;

    @FXML
    private Hyperlink linkConfiguracoes;

    @FXML
    private Hyperlink linkDepartamentos;

    @FXML
    private Hyperlink linkGraficosRelat;

    @FXML
    private Hyperlink linkInicio;

    @FXML
    private Hyperlink linkLembretes;

    @FXML
    private Hyperlink linkSociosDepend;

    @FXML
    private Hyperlink linkSair;

    @FXML
    private TableColumn<Socio, String> sociosEmDia;

    @FXML
    private TableColumn<Socio, String> sociosInadimplentes;

    @FXML
    private TableView<Socio> tabelaSociosEmdia;

    @FXML
    private TableView<Socio> tabelaSociosInadimplentes;

    //BOTÕES
    @FXML
    void sairAction(ActionEvent event) throws IOException {
        boolean confirmaSaida = emitirAlerta("Deseja realmente sair?", AlertType.CONFIRMATION);

        if (confirmaSaida) {
            App.trocarTela("TelaLogin");
        } else {
            System.out.println("Ação cancelada pelo usuário.");
        }
    }

    //FUNÇÕES
    public void initialize() throws SQLException {
        ConexaoBanco conexao = new ConexaoBanco();
        SocioDAO socioDAO = new SocioDAO(conexao);

        //preenchimento dos mostradores 
        campoTotalSocios.setText(String.valueOf(socioDAO.contarSocios()));
        campoSociosAtivos.setText(String.valueOf(socioDAO.contarSociosAtivos()));
        campoSociosInadimplentes.setText(String.valueOf(socioDAO.contarSociosInadimplentes()));
        campoSociosInativos.setText(String.valueOf(socioDAO.contarSociosInativos()));

        //preenchimento das tabelas de sócios
        //configura as colunas das tabelas para receber os nomes dos sócios
        this.sociosEmDia.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeSocio())
        );

        this.sociosInadimplentes.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getNomeSocio())
        );

        //busca os dados no banco
        List<Socio> listaSociosEmDia = socioDAO.listarSociosEmDia();
        List<Socio> listaSociosInadimplentes = socioDAO.listarSociosPendentes();

        //atribui os valores da lista nas tabelas
        tabelaSociosEmdia.setItems(FXCollections.observableArrayList(listaSociosEmDia));
        tabelaSociosInadimplentes.setItems(FXCollections.observableArrayList(listaSociosInadimplentes));
    }

   private boolean emitirAlerta(String mensagem, AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle("Confirmação");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);

        Optional<ButtonType> resultado = alerta.showAndWait();

        // Verifica se o usuário clicou no botão OK
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
