package util;

import model.Debito;
import model.Endereco;
import model.Socio;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GerarPdfFichaSocio {

    private static final PDType1Font fonteNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font fonteNegrito = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    
    private static final float margem = 50;
    private static final float larguraPagina = PDRectangle.A4.getWidth();
    private static final float alturaPagina = PDRectangle.A4.getHeight();
    private static final float MARGEM_INFERIOR = 75;

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

    public static void gerar(RelatorioFichaSocioProvider provider, File arquivoDestino) throws IOException {
        Socio socio = provider.getSocio();
        Endereco end = provider.getEndereco();

        try (PDDocument documento = new PDDocument()) {
            PDPage paginaAtual = new PDPage(PDRectangle.A4);
            documento.addPage(paginaAtual);

            PDPageContentStream content = new PDPageContentStream(documento, paginaAtual);
            float eixoY = alturaPagina - 50;

            ContextoPagina ctx = new ContextoPagina(documento, content, eixoY);

            // Cabeçalho e Logo
            adicionarLogo(ctx.documento, ctx.content, ctx.eixoY);
            float xTitulo = 175;
            float yTitulo = ctx.eixoY - 35;
            adicionarTexto(ctx.content, "Ficha Cadastral do Sócio", xTitulo, yTitulo, fonteNegrito, 14);
            adicionarTexto(ctx.content, "Sistema de Gestão - CTG Chaleira Preta", xTitulo, yTitulo - 16, fonteNormal, 10);
            
            adicionarLinha(ctx.content, ctx.eixoY - 95);
            ctx.eixoY -= 115;

            // 1. Dados Pessoais
            ctx = verificarEQuebrar(ctx, 110);
            adicionarTexto(ctx.content, "1. Dados Pessoais:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 18;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String nascStr = (socio.getDataNascSocio() != null) ? sdf.format(socio.getDataNascSocio()) : "";

            adicionarTexto(ctx.content, "• Nome: " + (socio.getNomeSocio() != null ? socio.getNomeSocio() : ""), margem + 10, ctx.eixoY, fonteNormal, 10);
            ctx.eixoY -= 15;
            adicionarTexto(ctx.content, "• CPF: " + (socio.getCpfSocio() != null ? socio.getCpfSocio() : "") + " | Sexo: " + (socio.getSexoSocio() != null ? socio.getSexoSocio() : ""), margem + 10, ctx.eixoY, fonteNormal, 10);
            ctx.eixoY -= 15;
            adicionarTexto(ctx.content, "• Data de Nascimento: " + nascStr + " | Cor/Raça: " + (socio.getCorSocio() != null ? socio.getCorSocio() : ""), margem + 10, ctx.eixoY, fonteNormal, 10);
            ctx.eixoY -= 15;
            adicionarTexto(ctx.content, "• E-mail: " + (socio.getEmailSocio() != null ? socio.getEmailSocio() : ""), margem + 10, ctx.eixoY, fonteNormal, 10);
            ctx.eixoY -= 15;
            adicionarTexto(ctx.content, "• Telefone: " + (socio.getTelefoneSocio() != null ? socio.getTelefoneSocio() : ""), margem + 10, ctx.eixoY, fonteNormal, 10);

            // 2. Endereço
            ctx.eixoY -= 25;
            ctx = verificarEQuebrar(ctx, 75);
            adicionarTexto(ctx.content, "2. Endereço:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 18;

            if (end != null) {
                adicionarTexto(ctx.content, "• Logradouro: " + end.getRua() + ", Nº " + end.getNumero() + " - " + end.getBairro(), margem + 10, ctx.eixoY, fonteNormal, 10);
                ctx.eixoY -= 15;
                adicionarTexto(ctx.content, "• Cidade/UF: " + end.getCidade() + " / " + end.getEstado() + " | CEP: " + end.getCep(), margem + 10, ctx.eixoY, fonteNormal, 10);
            } else {
                adicionarTexto(ctx.content, "• Nenhum endereço cadastrado.", margem + 10, ctx.eixoY, fonteNormal, 10);
            }

            // 3. Departamentos
            ctx.eixoY -= 25;
            ctx = verificarEQuebrar(ctx, 40);
            adicionarTexto(ctx.content, "3. Departamentos Vinculados:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 18;

            List<String> deptos = provider.getListaDepartamentos();
            if (deptos == null || deptos.isEmpty()) {
                adicionarTexto(ctx.content, "• Nenhum departamento vinculado.", margem + 10, ctx.eixoY, fonteNormal, 10);
                ctx.eixoY -= 15;
            } else {
                for (String d : deptos) {
                    ctx = verificarEQuebrar(ctx, 18);
                    adicionarTexto(ctx.content, "• " + d, margem + 10, ctx.eixoY, fonteNormal, 10);
                    ctx.eixoY -= 15;
                }
            }

            // 4. Dependentes
            ctx.eixoY -= 10;
            ctx = verificarEQuebrar(ctx, 40);
            adicionarTexto(ctx.content, "4. Dependentes:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 18;

            List<String> dependentes = provider.getListaDependentes();
            if (dependentes == null || dependentes.isEmpty()) {
                adicionarTexto(ctx.content, "• Nenhum dependente cadastrado.", margem + 10, ctx.eixoY, fonteNormal, 10);
                ctx.eixoY -= 15;
            } else {
                for (String dep : dependentes) {
                    ctx = verificarEQuebrar(ctx, 18);
                    adicionarTexto(ctx.content, "• " + dep, margem + 10, ctx.eixoY, fonteNormal, 10);
                    ctx.eixoY -= 15;
                }
            }

            // 5. Débitos em Aberto
            ctx.eixoY -= 10;
            ctx = verificarEQuebrar(ctx, 40);
            adicionarTexto(ctx.content, "5. Débitos em Aberto:", margem, ctx.eixoY, fonteNegrito, 11);
            ctx.eixoY -= 18;

            List<Debito> debitos = provider.getListaDebitos();
            if (debitos == null || debitos.isEmpty()) {
                adicionarTexto(ctx.content, "• Nenhum débito em aberto.", margem + 10, ctx.eixoY, fonteNormal, 10);
                ctx.eixoY -= 15;
            } else {
                for (Debito deb : debitos) {
                    ctx = verificarEQuebrar(ctx, 18);
                    
                    String dataVencString = "";
                    if (deb.getVencimentoDebito() != null) {
                        dataVencString = " - Vencimento: " + sdf.format(deb.getVencimentoDebito());
                    }

                    String infoDebito = "• " + deb.getTipoDebito() + " - R$ " + String.format("%.2f", deb.getValorDebito()) + dataVencString;
                    adicionarTexto(ctx.content, recortarTexto(infoDebito, 90), margem + 10, ctx.eixoY, fonteNormal, 10);
                    ctx.eixoY -= 15;
                }
            }

            // Rodapé final e salvamento
            adicionarRodape(ctx.content);
            ctx.content.close();
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
        String texto = "Ficha Cadastral emitida pelo sistema Gestchê em " + data;
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

    private static void adicionarLogo(PDDocument documento, PDPageContentStream content, float eixoY) throws IOException {
        PDImageXObject logo = LogoPdf.carregar(documento);
        content.drawImage(logo, margem, eixoY - 80, 110, 80);
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

    private static String recortarTexto(String texto, int maxTamanho) {
        if (texto == null) return "";
        if (texto.length() <= maxTamanho) return texto;
        return texto.substring(0, maxTamanho) + "...";
    }
}