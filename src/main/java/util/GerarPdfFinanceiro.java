package util;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class GerarPdfFinanceiro {
    // ATRIBUTOS
    //imagem da logo do sistema
    private final File arquivoLogo = new File("src/main/resources/img/LOGO DO PROJETO.png");

    //fontes para o relatório
    private final PDType1Font fonteNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDType1Font fonteNegrito = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    //medidas da página
    private final float margem = 50;
    private final float larguraPagina = PDRectangle.A4.getWidth();
    private final float alturaPagina = PDRectangle.A4.getHeight();

    //formato dos valores monetários
    private final NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    //formato das datas
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //MÉTODOS
    public void gerarRelatorioFinanceiro(
        PieChart graficoReceitas,
        PieChart graficoDespesas,
        File arquivoDestino,
        LocalDate dataInicio,
        LocalDate dataFim,
        double totalReceitas,
        double totalDespesas
    ) throws IOException {
        //imagens dos gráficos
        File imagemReceitas = File.createTempFile("grafico_receitas", ".png");
        File imagemDespesas = File.createTempFile("grafico_despesas", ".png");

        try {
            //captura a imagem dos gráficos
            salvarGrafico(graficoReceitas, imagemReceitas);
            salvarGrafico(graficoDespesas, imagemDespesas);

            //calcula o valor do fechamento
            double fechamento = totalReceitas - totalDespesas;

            try (PDDocument documento = new PDDocument()) {
                //cria uma página e adiciona ao documento
                PDPage pagina = new PDPage(PDRectangle.A4);
                documento.addPage(pagina);

                try (PDPageContentStream content = new PDPageContentStream(documento, pagina)) {

                    float eixoY = alturaPagina - 50;

                    //cabeçalho
                    adicionarLogo(documento, content, eixoY);

                    adicionarTextoCentralizado(
                        content,
                        "Relatório Financeiro",
                        fonteNegrito,
                        18,
                        eixoY - 35
                    );

                    ///adiciona linha divisória
                    adicionarLinha(content, eixoY - 95);

                    //poríodo selecionado
                    eixoY -= 125;

                    adicionarTexto(
                        content,
                        "Período analisado:",
                        margem,
                        eixoY,
                        fonteNegrito,
                        12
                    );

                    eixoY -= 20;

                    adicionarTexto(
                        content,
                        dataInicio.format(formatoData) + " até " + dataFim.format(formatoData),
                        margem + 10,
                        eixoY,
                        fonteNormal,
                        11
                    );

                    //resumo
                    eixoY -= 45;

                    adicionarTexto(
                        content,
                        "Resumo Financeiro:",
                        margem,
                        eixoY,
                        fonteNegrito,
                        13
                    );

                    eixoY -= 25;

                    adicionarTexto(
                        content,
                        "Total de Receitas: " + formatoMoeda.format(totalReceitas),
                        margem + 10,
                        eixoY,
                        fonteNormal,
                        11
                    );

                    eixoY -= 20;

                    adicionarTexto(
                        content,
                        "Total de Despesas: " + formatoMoeda.format(totalDespesas),
                        margem + 10,
                        eixoY,
                        fonteNormal,
                        11
                    );

                    eixoY -= 20;

                    adicionarTexto(
                        content,
                        "Fechamento: " + formatoMoeda.format(fechamento),
                        margem + 10,
                        eixoY,
                        fonteNegrito,
                        11
                    );

                    //gráfico de receitas
                    eixoY -= 45;

                    adicionarTextoCentralizado(
                        content,
                        "Receitas por Categorias",
                        fonteNegrito,
                        12,
                        eixoY
                    );

                    eixoY -= 15;

                    adicionarImagemGrafico(
                        documento,
                        content,
                        imagemReceitas,
                        margem,
                        eixoY - 175,
                        larguraPagina - (margem * 2),
                        175
                    );

                    //gráfico de despesas
                    eixoY -= 210;

                    adicionarTextoCentralizado(
                        content,
                        "Despesas por Categorias",
                        fonteNegrito,
                        12,
                        eixoY
                    );

                    eixoY -= 15;

                    adicionarImagemGrafico(
                        documento,
                        content,
                        imagemDespesas,
                        margem,
                        eixoY - 175,
                        larguraPagina - (margem * 2),
                        175
                    );

                    //rodapé
                    adicionarRodape(content);

                }

                //salva o documento
                documento.save(arquivoDestino);
            }

        } finally {
                //exclui as imagens dos gráficos
                imagemReceitas.delete();
                imagemDespesas.delete();
        }
    }

    //captuda o gráfico e salva como um png
    private void salvarGrafico(
        PieChart grafico,
        File destino
    ) throws IOException {
        WritableImage imagem = grafico.snapshot(null, null);

        ImageIO.write(
                SwingFXUtils.fromFXImage(imagem, null),
                "png",
                destino
        );
    }

    private void adicionarLogo(
        PDDocument documento,
        PDPageContentStream content,
        float eixoY
    ) throws IOException {
        if (arquivoLogo.exists()) {
            PDImageXObject logo = PDImageXObject.createFromFile(
                arquivoLogo.getAbsolutePath(),
                documento
            );

            content.drawImage(
                logo,
                margem,
                eixoY - 80,
                110,
                80
            );
        }
    }

    private void adicionarTexto(
        PDPageContentStream content,
        String texto,
        float x,
        float y,
        PDType1Font fonte,
        float tamanho
    ) throws IOException {
        content.beginText();
        content.setFont(fonte, tamanho);
        content.setNonStrokingColor(0, 0, 0);
        content.newLineAtOffset(x, y);
        content.showText(texto);
        content.endText();
    }

    private void adicionarTextoCentralizado(
        PDPageContentStream content,
        String texto,
        PDType1Font fonte,
        float tamanho,
        float y
    ) throws IOException {
        float larguraTexto = fonte.getStringWidth(texto) / 1000 * tamanho;
        float x = (larguraPagina - larguraTexto) / 2;

        adicionarTexto(
                content,
                texto,
                x,
                y,
                fonte,
                tamanho
        );
    }

    private void adicionarLinha(
                PDPageContentStream content,
                float y
        ) throws IOException {

        content.setStrokingColor(
                200 / 255f,
                200 / 255f,
                200 / 255f
        );

        content.setLineWidth(1);

        content.moveTo(margem, y);
        content.lineTo(larguraPagina - margem, y);

        content.stroke();
        }

    private void adicionarImagemGrafico(
        PDDocument documento,
        PDPageContentStream content,
        File arquivo,
        float x,
        float y,
        float larguraMaxima,
        float alturaMaxima
    ) throws IOException {

        PDImageXObject imagem = PDImageXObject.createFromFile(
                arquivo.getAbsolutePath(),
                documento
        );

        float proporcao = Math.min(
                larguraMaxima / imagem.getWidth(),
                alturaMaxima / imagem.getHeight()
        );

        float largura = imagem.getWidth() * proporcao;
        float altura = imagem.getHeight() * proporcao;

        float posicaoX = x + (larguraMaxima - largura) / 2;

        content.drawImage(
                imagem,
                posicaoX,
                y,
                largura,
                altura
        );
    }

    private void adicionarRodape(
        PDPageContentStream content
    ) throws IOException {

        String data = LocalDate.now().format(formatoData);

        String texto = "Relatório emitido pelo sistema Gestchê em " + data;

        float tamanho = 9;

        float larguraTexto = fonteNormal.getStringWidth(texto) / 1000 * tamanho;

        float x = (larguraPagina - larguraTexto) / 2;

        //linha do rodapé
        content.setStrokingColor(
                220 / 255f,
                220 / 255f,
                220 / 255f
        );
        content.setLineWidth(0.5f);

        content.moveTo(margem, 45);
        content.lineTo(larguraPagina - margem, 45);

        content.stroke();

        adicionarTexto(
                content,
                texto,
                x,
                30,
                fonteNormal,
                tamanho
        );
    }
}