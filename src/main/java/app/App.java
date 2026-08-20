package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Usuario;

public class App extends Application {
    //ATRIBUTOS
    public static Usuario usuarioLogado; //armazena o usuário logado, para todo o sistema
    public static Scene telaAtual; //guarda a tela que está aberta

    //MÉTODOS
    //ponto de partida do javaFx, que abre a primeira tela
    public void start(Stage janelaPrincipal) throws IOException {
        telaAtual = new Scene(carregarFXML("TelaLogin"));
        janelaPrincipal.setTitle("Gestchê");
        janelaPrincipal.setScene(telaAtual);
        janelaPrincipal.show();
    }

    //faz a busca e leitura de um arquivo fxml
    public static Parent carregarFXML(String FXML) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/" + FXML + ".fxml"));
        return fxmlLoader.load();
    }

    //faz a troca de telas
    public static void trocarTela(String FXML) throws IOException {
        telaAtual.setRoot(carregarFXML(FXML));
    }

    //ponto de partida do Java
    public static void main(String[] args) {
        launch(); //roda o método start, que vai iniciar o JavaFx
    }
}
