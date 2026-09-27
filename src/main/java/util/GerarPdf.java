package util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GerarPdf {
    // ATRIBUTOS
    private File arquivoLogo = new File("src/main/resources/img/LOGO DO PROJETO.png");

    //armazena os títulos dos gréficos
    private final String[] titulosGraficos = {
        "1. Sócios em Dia e Inadimplentes",
        "2. Distribuição por Gênero (Homens e Mulheres)",
        "3. Distribuição por Etnias",
        "4. Sócios Ativos e Inativos",
        "5. Distribuição por Faixa Etária"
    };

    //método auxiliar para incluir o rodapé do sistema Gestchê
    private void adicionarRodape(PDPageContentStream contentStream, float larguraPagina) throws IOException {
        String dataFormatada = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String textoRodape = "Relatório emitido pelo sistema Gestchê em " + dataFormatada;
        
        PDType1Font fonteRodape = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        float fontSize = 9;
        float larguraTexto = fonteRodape.getStringWidth(textoRodape) / 1000 * fontSize;
        float posicaoX = (larguraPagina - larguraTexto) / 2;
        float posicaoY = 30; // 30 pontos a partir da borda inferior

        //adiciona uma linha em cima do escrito de rodapé
        contentStream.setStrokingColor(220 / 255f, 220 / 255f, 220 / 255f);
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(50, posicaoY + 15);
        contentStream.lineTo(larguraPagina - 50, posicaoY + 15);
        contentStream.stroke();

        //adiciona o texto do rodapé
        contentStream.beginText();
        contentStream.setFont(fonteRodape, fontSize);
        contentStream.setNonStrokingColor(120 / 255f, 120 / 255f, 120 / 255f);
        contentStream.newLineAtOffset(posicaoX, posicaoY);
        contentStream.showText(textoRodape);
        contentStream.endText();
        contentStream.setNonStrokingColor(0, 0, 0); // Reseta para preto
    }

    //emitir relatório geral de sócios
    public void gerarRelatorioGeralSocios(List<File> imagensGraficos, File arquivoDestino, int qtdSocios, int qtdDependentes) {
        try (PDDocument document = new PDDocument()) {
            
            float margemEsquerda = 50;
            float larguraPagina = PDRectangle.A4.getWidth();
            float alturaPagina = PDRectangle.A4.getHeight();
            
            PDPage paginaAtual = new PDPage(PDRectangle.A4);
            document.addPage(paginaAtual);
            PDPageContentStream contentStream = new PDPageContentStream(document, paginaAtual);

            float eixoY = alturaPagina - 50;

            //adiciona o cabeçalho com a logo e o título do relatório
            float larguraLogo = 110;
            float alturaLogo = 80;
            float posicaoLogoY = eixoY - alturaLogo;

            if (arquivoLogo != null && arquivoLogo.exists()) {
                PDImageXObject imagemLogo = PDImageXObject.createFromFile(arquivoLogo.getAbsolutePath(), document);
                contentStream.drawImage(imagemLogo, margemEsquerda, posicaoLogoY, larguraLogo, alturaLogo);
            }

            //adiciona o titulo principal
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
            String tituloPrincipal = "Relatório Geral de Sócios";
            float larguraTextoTitulo = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD).getStringWidth(tituloPrincipal) / 1000 * 18;
            float posXTitulo = (larguraPagina - larguraTextoTitulo) / 2;
            
            contentStream.newLineAtOffset(posXTitulo, eixoY - 35);
            contentStream.showText(tituloPrincipal);
            contentStream.endText();

            //adiciona uma linha divisória 
            contentStream.setStrokingColor(200 / 255f, 200 / 255f, 200 / 255f);
            contentStream.setLineWidth(1f);
            contentStream.moveTo(margemEsquerda, eixoY - 95);
            contentStream.lineTo(larguraPagina - margemEsquerda, eixoY - 95);
            contentStream.stroke();

            //adiciona o resumo de indicadores de sócios
            eixoY -= 115;
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
            contentStream.newLineAtOffset(margemEsquerda, eixoY);
            contentStream.showText("Resumo de Indicadores:");
            contentStream.endText();

            eixoY -= 20;
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
            contentStream.setLeading(16f);
            contentStream.newLineAtOffset(margemEsquerda + 10, eixoY);
            
            int totalGeral = qtdSocios + qtdDependentes;
            contentStream.showText("• Quantidade de Sócios: " + qtdSocios);
            contentStream.newLine();
            contentStream.showText("• Quantidade de Dependentes: " + qtdDependentes);
            contentStream.newLine();
            contentStream.showText("• Total Geral (Sócios + Dependentes): " + totalGeral);
            contentStream.endText();

            eixoY -= 75; // Espaço antes dos gráficos

            //adiciona os gráficos um após o outro
            float larguraImg = 325; 
            float alturaImg = 190;  
            float posXImg = (larguraPagina - larguraImg) / 2;

            for (int i = 0; i < imagensGraficos.size(); i++) {
                //verifica se há espaço para um novo gráfico, senão pula para a próxima página
                if (eixoY < (alturaImg + 80)) {
                    //adiciona o rodapé antes de fechar a página
                    adicionarRodape(contentStream, larguraPagina);
                    contentStream.close();
                    
                    //cria uma nova página
                    paginaAtual = new PDPage(PDRectangle.A4);
                    document.addPage(paginaAtual);
                    contentStream = new PDPageContentStream(document, paginaAtual);
                    eixoY = alturaPagina - 50; // Reinicia perto do topo
                }

                //adiciona o título do gráfico
                String tituloGrafico = (i < titulosGraficos.length) ? titulosGraficos[i] : "Gráfico " + (i + 1);
                
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.newLineAtOffset(margemEsquerda, eixoY);
                contentStream.showText(tituloGrafico);
                contentStream.endText();

                //adiciona a imagem do gráfico
                eixoY -= (alturaImg + 15);

                PDImageXObject graficoImg = PDImageXObject.createFromFile(imagensGraficos.get(i).getAbsolutePath(), document);
                contentStream.drawImage(graficoImg, posXImg, eixoY, larguraImg, alturaImg);

                eixoY -= 45; // Espaço para o próximo item
            }

            //adiciona o rodapé na ultima página
            adicionarRodape(contentStream, larguraPagina);
            contentStream.close();

            //salva o documento no destino escolhido
            document.save(arquivoDestino);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}