package applica.api.services.impl;

import applica.api.services.ReportsService;
import fr.opensagres.xdocreport.converter.*;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.DocumentKind;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;

@Service
public class ReportsServiceImpl implements ReportsService {

    public static final String REPORT_OUTPUT_TYPE_PDF = "pdf";
    public static final String REPORT_OUTPUT_TYPE_DOCX = "docx";


    @Override
    public InputStream createReport(String filename, HashMap<String, Object> fields, FieldsMetadata metadata, String outputType, InputStream in) throws Exception {
        IXDocReport report = null;
        try {
            report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);
        } catch (XDocReportException e) {
            e.printStackTrace();
        }

        if (report != null && metadata != null) {
            report.setFieldsMetadata(metadata);
        }

        // 3) Generate report by merging Java model with the ODT
        File file = new File(filename);
        OutputStream out = new FileOutputStream(file);
        try {
            report.process(fields, out);
        } catch (XDocReportException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Errore durante la generazione del report");
        }

        switch (outputType){
            case REPORT_OUTPUT_TYPE_DOCX:
                return  new FileInputStream(new File(filename));
            case REPORT_OUTPUT_TYPE_PDF:
                return convertToPDF(filename.replace( "." + REPORT_OUTPUT_TYPE_DOCX, ""));
        }

        return new FileInputStream(new File(filename));
    }

    private InputStream convertToPDF(String filename) throws FileNotFoundException {

        String pdfOutPath = String.format("%s.%s", filename, "pdf");
        String odtOutPath = String.format("%s.%s", filename, "docx");

        // 1) Create options ODT 2 PDF to select well converter form the registry
        Options options = Options.getFrom(DocumentKind.DOCX).to(ConverterTypeTo.PDF);

        // 2) Get the converter from the registry
        IConverter converter = ConverterRegistry.getRegistry().getConverter(options);

        // 3) Convert ODT 2 PDF
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(new File(odtOutPath));
            out = new FileOutputStream(new File(pdfOutPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            converter.convert(in, out, options);
        } catch (XDocConverterException e) {
            e.printStackTrace();
        }

        return new FileInputStream(new File(pdfOutPath));
    }
}
