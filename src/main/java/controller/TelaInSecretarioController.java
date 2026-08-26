package controller;

import java.sql.SQLException;

import dao.ConexaoBanco;
import dao.SocioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

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
    private TableColumn<?, ?> colunaSociosEmDia;

    @FXML
    private TableColumn<?, ?> colunaSociosPendentes;

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

    //FUNÇÕES
    public void initialize() throws SQLException {
        ConexaoBanco conexao = new ConexaoBanco();
        SocioDAO socioDAO = new SocioDAO(conexao);
        campoTotalSocios.setText(String.valueOf(socioDAO.contarSocios()));
        campoSociosAtivos.setText(String.valueOf(socioDAO.contarSociosAtivos()));
        campoSociosInadimplentes.setText(String.valueOf(socioDAO.contarSociosInadimplentes()));
        campoSociosInativos.setText(String.valueOf(socioDAO.contarSociosInativos()));

    }

}
