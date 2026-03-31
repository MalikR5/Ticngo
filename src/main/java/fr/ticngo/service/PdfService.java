package fr.ticngo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import fr.ticngo.model.Billet;


public class PdfService {

    private static final DeviceRgb PRIMARY = new DeviceRgb(102, 126, 234);
    private static final DeviceRgb DARK    = new DeviceRgb(26, 37, 53);
    private static final DeviceRgb LIGHT   = new DeviceRgb(247, 248, 252);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DF  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateBilletPdf(Billet billet) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4)) {

            doc.setMargins(40, 50, 40, 50);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Header
            Paragraph header = new Paragraph("🎭  TIC'N GO")
                    .setFont(bold).setFontSize(28).setFontColor(PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER);
            doc.add(header);

            doc.add(new Paragraph("Billet de spectacle")
                    .setFont(regular).setFontSize(14).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            // Billet card
            Table card = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBorder(new SolidBorder(PRIMARY, 2))
                    .setBackgroundColor(LIGHT)
                    .setMarginBottom(20);

            Cell titleCell = new Cell().add(
                new Paragraph(billet.getSeance().getSpectacle().getTitre())
                    .setFont(bold).setFontSize(20).setFontColor(DARK)
                    .setTextAlignment(TextAlignment.CENTER))
                .setBorder(null).setPadding(15).setBackgroundColor(PRIMARY);
            titleCell.setFontColor(ColorConstants.WHITE);
            card.addCell(titleCell);

            Cell infoCell = new Cell().setBorder(null).setPadding(15);
            infoCell.add(infoLine(bold, regular, "N° Billet :", billet.getNumeroBillet()));
            infoCell.add(infoLine(bold, regular, "Client :",
                billet.getClient().getPrenom() + " " + billet.getClient().getNom()));
            infoCell.add(infoLine(bold, regular, "Email :", billet.getClient().getEmail()));
            infoCell.add(infoLine(bold, regular, "Date :",
                billet.getSeance().getDateHeure().format(DTF)));
            infoCell.add(infoLine(bold, regular, "Lieu :",
                billet.getSeance().getSpectacle().getLieu() != null
                    ? billet.getSeance().getSpectacle().getLieu().getNom() : "N/A"));
            infoCell.add(infoLine(bold, regular, "Prix :", billet.getPrix() + " €"));
            infoCell.add(infoLine(bold, regular, "Statut :", billet.getStatut().name()));
            infoCell.add(infoLine(bold, regular, "Acheté le :",
                billet.getDateAchat() != null ? billet.getDateAchat().format(DTF) : "N/A"));
            card.addCell(infoCell);
            doc.add(card);

            // Footer
            doc.add(new Paragraph("Merci pour votre confiance — Tic'n Go")
                    .setFont(regular).setFontSize(10).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
        return baos.toByteArray();
    }

    public byte[] generateListeBilletsPdf(List<Billet> billets) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4.rotate())) {

            doc.setMargins(30, 40, 30, 40);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            doc.add(new Paragraph("Tic'n Go — Liste des Billets")
                    .setFont(bold).setFontSize(20).setFontColor(PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            Table table = new Table(UnitValue.createPercentArray(new float[]{2,3,4,3,2,2}))
                    .setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"N° Billet", "Client", "Spectacle", "Date séance", "Prix", "Statut"};
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold))
                        .setBackgroundColor(PRIMARY).setFontColor(ColorConstants.WHITE)
                        .setBorder(null).setPadding(6));
            }

            boolean alt = false;
            for (Billet b : billets) {
                DeviceRgb bg = alt ? LIGHT : new DeviceRgb(255,255,255);
                table.addCell(cell(b.getNumeroBillet(), regular, bg));
                table.addCell(cell(b.getClient().getPrenom() + " " + b.getClient().getNom(), regular, bg));
                table.addCell(cell(b.getSeance().getSpectacle().getTitre(), regular, bg));
                table.addCell(cell(b.getSeance().getDateHeure().format(DTF), regular, bg));
                table.addCell(cell(b.getPrix() + " €", regular, bg));
                table.addCell(cell(b.getStatut().name(), regular, bg));
                alt = !alt;
            }
            doc.add(table);
        }
        return baos.toByteArray();
    }

    private Paragraph infoLine(PdfFont bold, PdfFont regular, String label, String value) {
        return new Paragraph()
                .add(new com.itextpdf.layout.element.Text(label + "  ").setFont(bold).setFontColor(DARK))
                .add(new com.itextpdf.layout.element.Text(value).setFont(regular))
                .setMarginBottom(6);
    }

    private Cell cell(String text, PdfFont font, DeviceRgb bg) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9))
                .setBackgroundColor(bg).setBorder(null).setPadding(5);
    }
}
