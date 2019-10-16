package applica.api.services;

import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.io.InputStream;
import java.util.HashMap;

public interface ReportsService {

    InputStream createReport(String filename, HashMap<String, Object> fields, FieldsMetadata metadata, String outputType, InputStream in) throws Exception;

}
