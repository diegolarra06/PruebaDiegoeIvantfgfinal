package com.daw.adoptauncompanero.servicio.impl;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daw.adoptauncompanero.entities.SolicitudAdopcionEntity;
import com.daw.adoptauncompanero.repositorios.SolicitudAdopcionRepository;
import com.daw.adoptauncompanero.servicio.interfaces.PdfService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private SolicitudAdopcionRepository solicitudRepository;

    @Override
    public byte[] generarPdfSolicitud(Integer idSolicitud) {

        try {

            SolicitudAdopcionEntity solicitud = solicitudRepository
                    .findById(idSolicitud)
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, baos);

            document.open();

            Font tituloFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            20);

            Font textoFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            12);

            Paragraph titulo =
                    new Paragraph(
                            "SOLICITUD DE ADOPCIÓN",
                            tituloFont
                    );

            titulo.setSpacingAfter(20);

            document.add(titulo);

            // =====================================================
            // TABLA PDF
            // =====================================================

            PdfPTable tabla = new PdfPTable(2);

            tabla.setWidthPercentage(100);

            agregarFila(
                    tabla,
                    "ID Solicitud",
                    solicitud.getIdSolicitud().toString()
            );

            agregarFila(
                    tabla,
                    "Fecha Solicitud",
                    solicitud.getFechaSolicitud()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy HH:mm"
                                    )
                            )
            );

            agregarFila(
                    tabla,
                    "Usuario",
                    solicitud.getUsuario().getNombre()
            );

            agregarFila(
                    tabla,
                    "Correo",
                    solicitud.getUsuario().getEmail()
            );

            agregarFila(
                    tabla,
                    "Teléfono",
                    solicitud.getUsuario().getTelefono()
            );

            agregarFila(
                    tabla,
                    "Dirección",
                    solicitud.getUsuario().getDireccion()
            );

            agregarFila(
                    tabla,
                    "Animal",
                    solicitud.getAnimal().getNombre()
            );

            agregarFila(
                    tabla,
                    "Especie",
                    solicitud.getAnimal().getEspecie()
            );

            agregarFila(
                    tabla,
                    "Estado Solicitud",
                    solicitud.getEstado().getNombre()
            );

            agregarFila(
                    tabla,
                    "Comentarios",
                    solicitud.getComentarios() != null
                            ? solicitud.getComentarios()
                            : "Sin comentarios"
            );

            document.add(tabla);

            // =====================================================
            // TEXTO FINAL
            // =====================================================

            Paragraph finalTexto =
                    new Paragraph(
                            "\nDocumento generado automáticamente por AdoptaUnCompañero.",
                            textoFont
                    );

            finalTexto.setSpacingBefore(20);

            document.add(finalTexto);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generando PDF",
                    e
            );
        }
    }

    // =====================================================
    // MÉTODO AUXILIAR
    // =====================================================

    private void agregarFila(
            PdfPTable tabla,
            String campo,
            String valor) {

        PdfPCell celdaCampo =
                new PdfPCell(new Phrase(campo));

        PdfPCell celdaValor =
                new PdfPCell(new Phrase(valor));

        tabla.addCell(celdaCampo);
        tabla.addCell(celdaValor);
    }
}