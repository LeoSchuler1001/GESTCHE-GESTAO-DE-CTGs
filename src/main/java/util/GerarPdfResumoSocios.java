package util;

import model.dto.SocioResumoDTO;
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

public class GerarPdfResumoSocios {

    private static final File arquivoLogo = new File("src/main/resources/img/LOGO DO PROJETO.png");
    private static final PDType1Font fonteNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font fonteNegrito = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    
    private static final float margem = 50;
    private static final float larguraPagina = PDRectangle.A4.getWidth();
    private static final float alturaPagina = PDRectangle.A4.getHeight();
    private static final float MARGEM_INFERIOR = 75; // Espaço reservado para o rodapé

    private static final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static class ContextoPagina {
        PDDocument documento;
        PDPageContentStream content;
        float eixoY;

        public ContextoPagina(PDDocument documento, PDPageContentStream content, float eixoY) {
            this.documento = documento;
            this.content = content;
            this.eixoY = eixoY;
        }
    }

    public static void gerar(RelatorioSociosResumoProvider provider, File arquivoDestino) throws IOException {
        try (PDDocument documento = new PDDocument()) {
            PDPage paginaAtual = new PDPage(PDRectangle.A4);
            documento.addPage(paginaAtual);

            PDPageContentStream content = new PDPageContentStream(documento, paginaAtual);
            float eixoY = alturaPagina - 50;

            ContextoPagina ctx = new ContextoPagina(documento, content, eixoY);

            // Cabeçalho e Logo na Primeira Página (Alinhado à direita da logo)
            adicionarLogo(ctx.documento, ctx.content, ctx.eixoY);
            float xTitulo = 175;
            float yTitulo = ctx.eixoY - 35;
            adicionarTexto(ctx.content, "Relatório de Sócios", xTitulo, yTitulo, fonteNegrito, 14);
            adicionarTexto(ctx.content, provider.getTituloRelatorio(), xTitulo, yTitulo - 16, fonteNormal, 10);
            
            adicionarLinha(ctx.content, ctx.eixoY - 95);
            ctx.eixoY -= 115;

            // Listagem dos Sócios
            ctx = verificarEQuebrar(ctx, 30);
            adicionarTexto(ctx.content, "Relação de Sócios e Situação Cadastral:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 20;

            List<SocioResumoDTO> socios = provider.getListaSociosResumo();
            if (socios.isEmpty()) {
                ctx = verificarEQuebrar(ctx, 20);
                adicionarTexto(ctx.content, "Nenhum sócio cadastrado.", margem + 10, ctx.eixoY, fonteNormal, 9);
            } else {
                for (SocioResumoDTO socio : socios) {
                    ctx = verificarEQuebrar(ctx, 35); // Cada sócio ocupa cerca de 2 a 3 linhas

                    String nome = socio.getNomeSocio() != null ? socio.getNomeSocio() : "Sem nome";
                    String situacao = socio.getSituacaoTexto() != null ? socio.getSituacaoTexto() : "Indefinida";
                    String dep = socio.getDependentesFormatado() != null ? socio.getDependentesFormatado() : "Nenhum";
                    String deptos = socio.getDepartamentosFormatado() != null ? socio.getDepartamentosFormatado() : "Nenhum";

                    adicionarTexto(ctx.content, "• Sócio: " + recortarTexto(nome, 65), margem + 10, ctx.eixoY, fonteNegrito, 9);
                    ctx.eixoY -= 14;
                    adicionarTexto(ctx.content, "  Situação: " + situacao + " | Dependentes: " + recortarTexto(dep, 50), margem + 10, ctx.eixoY, fonteNormal, 9);
                    ctx.eixoY -= 14;
                    adicionarTexto(ctx.content, "  Departamentos: " + recortarTexto(deptos, 70), margem + 10, ctx.eixoY, fonteNormal, 9);
                    ctx.eixoY -= 18; // Espaçamento entre sócios
                }
            }

            // Adiciona o rodapé na última página ativa antes de fechar
            adicionarRodape(ctx.content);
            ctx.content.close();

            // Salva o documento PDF
            documento.save(arquivoDestino);
        }
    }

    private static ContextoPagina verificarEQuebrar(ContextoPagina ctx, float alturaNecessaria) throws IOException {
        if (ctx.eixoY - alturaNecessaria < MARGEM_INFERIOR) {
            adicionarRodape(ctx.content);
            ctx.content.close(); 

            PDPage novaPagina = new PDPage(PDRectangle.A4);
            ctx.documento.addPage(novaPagina);

            PDPageContentStream novoContent = new PDPageContentStream(ctx.documento, novaPagina);
            float novoEixoY = alturaPagina - 50;

            return new ContextoPagina(ctx.documento, novoContent, novoEixoY);
        }
        return ctx;
    }

    private static void adicionarRodape(PDPageContentStream content) throws IOException {
        String data = LocalDate.now().format(formatoData);
        String texto = "Relatório de Sócios emitido pelo sistema Gestchê em " + data;
        float tamanho = 9;
        float larguraTexto = fonteNormal.getStringWidth(texto) / 1000 * tamanho;
        float x = (larguraPagina - larguraTexto) / 2;

        content.setStrokingColor(220 / 255f, 220 / 255f, 220 / 255f);
        content.setLineWidth(0.5f);
        content.moveTo(margem, 45);
        content.lineTo(larguraPagina - margem, 45);
        content.stroke();

        adicionarTexto(content, texto, x, 30, fonteNormal, tamanho);
    }

    private static String recortarTexto(String texto, int maxTamanho) {
        if (texto.length() <= maxTamanho) return texto;
        return texto.substring(0, maxTamanho) + "...";
    }

    private static void adicionarLogo(PDDocument documento, PDPageContentStream content, float eixoY) throws IOException {
        if (arquivoLogo.exists()) {
            PDImageXObject logo = PDImageXObject.createFromFile(arquivoLogo.getAbsolutePath(), documento);
            content.drawImage(logo, margem, eixoY - 80, 110, 80);
        }
    }

    private static void adicionarTexto(PDPageContentStream content, String texto, float x, float y, PDType1Font fonte, float tamanho) throws IOException {
        content.beginText();
        content.setFont(fonte, tamanho);
        content.setNonStrokingColor(0, 0, 0);
        content.newLineAtOffset(x, y);
        content.showText(texto != null ? texto : "");
        content.endText();
    }

    private static void adicionarLinha(PDPageContentStream content, float y) throws IOException {
        content.setStrokingColor(200 / 255f, 200 / 255f, 200 / 255f);
        content.setLineWidth(1);
        content.moveTo(margem, y);
        content.lineTo(larguraPagina - margem, y);
        content.stroke();
    }
}