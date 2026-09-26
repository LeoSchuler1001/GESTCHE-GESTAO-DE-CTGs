package util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GerarPdf {
    //ATRIBUTOS
    private File arquivoLogo = new File("src/main/resources/img/LOGO DO PROJETO.png");

    //método para gerar relatórios geral de sócios
    public void gerarRelatorio(List<File> imagensGraficos, File arquivoDestino) {
        try (PDDocument document = new PDDocument()) {
            //cria uma nova página e adiciona ao documento
            PDPage pagina = new PDPage();
            document.addPage(pagina);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, pagina)) {
                //define as coordenadas iniciais da folha
                float margemEsquerda = 50;
                float eixoYLogo = 730;

                //adiciona o logotipo do gestchê no topo da página
                if (arquivoLogo != null && arquivoLogo.exists()) {
                    //carrega a imagem para o pdf
                    PDImageXObject imagemLogo = PDImageXObject.createFromFile(arquivoLogo.getAbsolutePath(), document);
                    
                    //define a largura e altura da imagem
                    float larguraImg = 200;
                    float alturaImg = 150;
                    
                    //desenha a imagem na página
                    contentStream.drawImage(imagemLogo, margemEsquerda, eixoYLogo, larguraImg, alturaImg);
                    
                    //escrever o título ao lado da imagem
                    float posicaoXTexto = margemEsquerda + larguraImg + 15;
                    
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                    //alinha o texto com a imagem
                    contentStream.newLineAtOffset(posicaoXTexto, eixoYLogo + 18); 
                    contentStream.showText("Relatório geral de Sócios");
                    contentStream.endText();
                } else {
                    //se não tiver imagem, escreve somente o título
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                    contentStream.newLineAtOffset(margemEsquerda, eixoYLogo + 18);
                    contentStream.showText("Relatório geral de Sócios");
                    contentStream.endText();
                }

                //desloca o eixo y para começar a escrever o conteúdo
                float eixoYCorpo = eixoYLogo - 50;
                
                //configurações do texto
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.setLeading(18.0f); //espaçamento entre linhas
                contentStream.newLineAtOffset(margemEsquerda, eixoYCorpo);
                
                //marca do sistema
                contentStream.showText("Gerado pelo sistema Gestchê em " + java.time.LocalDate.now());
                contentStream.newLine();
                contentStream.newLine();

                //início do conteúdo
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.showText("Resumo de Indicadores:");
                contentStream.newLine();
                
                //conteúdo
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);

                float eixoX = 150; //centralizado horixontalmente
                float larguraImg = 300; 
                float alturaImg = 140;
                float eixoY = 560;

                //gráfico em dia e inadimplentes
                contentStream.showText("SÓCIOS EM DIA E INADIMPLENTES");
                contentStream.newLine();
                contentStream.endText();

                //carrega a imagem para o pdf
                PDImageXObject grafico1 = PDImageXObject.createFromFile(imagensGraficos.get(0).getAbsolutePath(), document);
                //desenha a imagem na página
                contentStream.drawImage(grafico1, eixoX, eixoY, larguraImg, alturaImg);
                
                
            }

            //salva o documento no local escolhido
            document.save(arquivoDestino);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}