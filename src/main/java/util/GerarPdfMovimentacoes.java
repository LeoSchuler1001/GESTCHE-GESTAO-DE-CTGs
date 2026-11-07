package util;

import model.Conta;
import model.Movimentacao;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class GerarPdfMovimentacoes {

    private static final File arquivoLogo = new File("src/main/resources/img/LOGO DO PROJETO.png");
    private static final PDType1Font fonteNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font fonteNegrito = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    
    private static final float margem = 50;
    private static final float larguraPagina = PDRectangle.A4.getWidth();
    private static final float alturaPagina = PDRectangle.A4.getHeight();
    private static final float MARGEM_INFERIOR = 75; // Um pouco mais de espaço para garantir que o rodapé não encoste no texto

    private static final NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
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

    public static void gerar(RelatorioFinanceiroProvider provider, File arquivoDestino) throws IOException {
        double totalReceitas = provider.getListaReceitas().stream().mapToDouble(Movimentacao::getValorMovimentacao).sum();
        double totalDespesas = provider.getListaDespesas().stream().mapToDouble(Movimentacao::getValorMovimentacao).sum();
        double fechamento = totalReceitas - totalDespesas;

        try (PDDocument documento = new PDDocument()) {
            PDPage paginaAtual = new PDPage(PDRectangle.A4);
            documento.addPage(paginaAtual);

            PDPageContentStream content = new PDPageContentStream(documento, paginaAtual);
            float eixoY = alturaPagina - 50;

            ContextoPagina ctx = new ContextoPagina(documento, content, eixoY);

            // Cabeçalho e Logo na Primeira Página
            adicionarLogo(ctx.documento, ctx.content, ctx.eixoY);
            
            float xTitulo = 175; 
            float yTitulo = ctx.eixoY - 30;

            adicionarTexto(ctx.content, "Relatório Financeiro", xTitulo, yTitulo, fonteNegrito, 14);
            adicionarTexto(ctx.content, provider.getTituloPeriodo(), xTitulo, yTitulo - 16, fonteNormal, 10);
            adicionarLinha(ctx.content, ctx.eixoY - 95);

            ctx.eixoY -= 115;

            // 1. Período e Resumo Financeiro Geral
            ctx = verificarEQuebrar(ctx, 70);
            adicionarTexto(ctx.content, "Período analisado: " + provider.getDataInicio().format(formatoData) + " até " + provider.getDataFim().format(formatoData), margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 20;
            adicionarTexto(ctx.content, "Total de Receitas: " + formatoMoeda.format(totalReceitas), margem, ctx.eixoY, fonteNormal, 10);
            adicionarTexto(ctx.content, "Total de Despesas: " + formatoMoeda.format(totalDespesas), margem + 200, ctx.eixoY, fonteNormal, 10);
            ctx.eixoY -= 18;
            adicionarTexto(ctx.content, "Fechamento do Período: " + formatoMoeda.format(fechamento), margem, ctx.eixoY, fonteNegrito, 10);

            // 2. Saldo das Contas no Momento da Emissão
            ctx.eixoY -= 30;
            ctx = verificarEQuebrar(ctx, 50);
            adicionarTexto(ctx.content, "Saldo das Contas (Emissão em " + LocalDate.now().format(formatoData) + "):", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 15;
            
            for (Conta conta : provider.getListaContas()) {
                ctx = verificarEQuebrar(ctx, 20);
                adicionarTexto(ctx.content, "• " + conta.getNomeConta() + ": " + formatoMoeda.format(conta.getSaldo()), margem + 10, ctx.eixoY, fonteNormal, 10);
                ctx.eixoY -= 15;
            }
            ctx = verificarEQuebrar(ctx, 20);
            adicionarTexto(ctx.content, "Saldo Total Geral: " + formatoMoeda.format(provider.getSaldoTotal()), margem + 10, ctx.eixoY, fonteNegrito, 10);

            // 3. Listagem de Receitas
            ctx.eixoY -= 30;
            ctx = verificarEQuebrar(ctx, 30);
            adicionarTexto(ctx.content, "Detalhamento de Receitas:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 15;

            List<Movimentacao> receitas = provider.getListaReceitas();
            if (receitas.isEmpty()) {
                ctx = verificarEQuebrar(ctx, 20);
                adicionarTexto(ctx.content, "Nenhuma receita registrada no período.", margem + 10, ctx.eixoY, fonteNormal, 9);
                ctx.eixoY -= 15;
            } else {
                for (Movimentacao m : receitas) {
                    ctx = verificarEQuebrar(ctx, 18);

                    String dataStr = "";
                    if (m.getDataMovimentacao() != null) {
                        dataStr = m.getDataMovimentacao().toLocalDate().format(formatoData);
                    }
                    String desc = m.getComentarioMovimentacao() != null ? m.getComentarioMovimentacao() : "Sem descrição";
                    String linha = dataStr + " - " + desc + ": " + formatoMoeda.format(m.getValorMovimentacao());
                    
                    adicionarTexto(ctx.content, "• " + recortarTexto(linha, 90), margem + 10, ctx.eixoY, fonteNormal, 9);
                    ctx.eixoY -= 14;
                }
            }

            // 4. Listagem de Despesas
            ctx.eixoY -= 20;
            ctx = verificarEQuebrar(ctx, 30);
            adicionarTexto(ctx.content, "Detalhamento de Despesas:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 15;

            List<Movimentacao> despesas = provider.getListaDespesas();
            if (despesas.isEmpty()) {
                ctx = verificarEQuebrar(ctx, 20);
                adicionarTexto(ctx.content, "Nenhuma despesa registrada no período.", margem + 10, ctx.eixoY, fonteNormal, 9);
                ctx.eixoY -= 15;
            } else {
                for (Movimentacao m : despesas) {
                    ctx = verificarEQuebrar(ctx, 18);

                    String dataStr = "";
                    if (m.getDataMovimentacao() != null) {
                        dataStr = m.getDataMovimentacao().toLocalDate().format(formatoData);
                    }
                    String desc = m.getComentarioMovimentacao() != null ? m.getComentarioMovimentacao() : "Sem descrição";
                    String linha = dataStr + " - " + desc + ": " + formatoMoeda.format(m.getValorMovimentacao());
                    
                    adicionarTexto(ctx.content, "• " + recortarTexto(linha, 90), margem + 10, ctx.eixoY, fonteNormal, 9);
                    ctx.eixoY -= 14;
                }
            }

            // Adiciona o rodapé na última página ativa antes de fechar
            adicionarRodape(ctx.content);
            ctx.content.close();

            // Salva o documento no destino escolhido
            documento.save(arquivoDestino);
        }
    }

    private static ContextoPagina verificarEQuebrar(ContextoPagina ctx, float alturaNecessaria) throws IOException {
        if (ctx.eixoY - alturaNecessaria < MARGEM_INFERIOR) {
            // Adiciona o rodapé na página atual antes de fechá-la
            adicionarRodape(ctx.content);
            ctx.content.close(); 

            // Cria a nova página
            PDPage novaPagina = new PDPage(PDRectangle.A4);
            ctx.documento.addPage(novaPagina);

            PDPageContentStream novoContent = new PDPageContentStream(ctx.documento, novaPagina);
            float novoEixoY = alturaPagina - 50; // Reinicia perto do topo da nova página

            return new ContextoPagina(ctx.documento, novoContent, novoEixoY);
        }
        return ctx;
    }

    private static void adicionarRodape(PDPageContentStream content) throws IOException {
        String data = LocalDate.now().format(formatoData);
        String texto = "Relatório emitido pelo sistema Gestchê em " + data;
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