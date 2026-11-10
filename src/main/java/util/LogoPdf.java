package util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Carrega a logo do GesTchê a partir dos recursos internos da aplicação.
 */
public final class LogoPdf {

    private static final String CAMINHO_LOGO = "/img/LOGO DO PROJETO.png";

    private LogoPdf() {
        // Classe utilitária: não deve ser instanciada.
    }

    public static PDImageXObject carregar(PDDocument documento) throws IOException {
        try (InputStream input = LogoPdf.class.getResourceAsStream(CAMINHO_LOGO)) {
            if (input == null) {
                throw new IOException("Logo do GesTchê não encontrada nos recursos: " + CAMINHO_LOGO);
            }

            return PDImageXObject.createFromByteArray(
                documento,
                input.readAllBytes(),
                "logo-gestche"
            );
        }
    }
}
