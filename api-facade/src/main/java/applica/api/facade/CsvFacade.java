package applica.api.facade;

import applica.framework.fileserver.viewmodel.UIFileUpload;
import applica.api.domain.model.csv.CsvInfo;
import applica.api.domain.model.csv.CsvReader;
import applica.api.domain.model.csv.RowData;
import applica.api.domain.model.csv.csvRowValidator.GeoCityRowValidator;
import applica.api.domain.model.csv.csvRowValidator.GeoProvinceRowValidator;
import applica.api.domain.model.geo.GeoCity;
import applica.api.domain.model.geo.GeoProvince;
import applica.api.services.GeoCityService;
import applica.api.services.GeoProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class CsvFacade {

    private final GeoCityService geoCityService;
    private final GeoProvinceService geoProvinceService;

    @Autowired
    public CsvFacade(GeoCityService geoCityService, GeoProvinceService geoProvinceService) {
        this.geoCityService = geoCityService;
        this.geoProvinceService = geoProvinceService;
    }

    public String importGeoCity(UIFileUpload csvFile) throws Exception {

        /*
        Sposto il csv in una cartella temporanea (dato che mi serve un percorso assoluto per utilizzare il csvImporter)
         */
        Integer count = 0;

        //salvo il file nella directory temporanea del file server
        String csvPath = csvFile.getPath();
        GeoCityRowValidator geoCityRowValidator = new GeoCityRowValidator();

        CsvReader reader = new CsvReader(csvPath, ";", geoCityRowValidator);
        CsvInfo csvReadOutput = reader.readFile();
        if (StringUtils.hasLength(csvReadOutput.getError())) {
            //errori di parse del file
            throw new Exception(csvReadOutput.getError());
        } else if (csvReadOutput.getNonValidatedRowIndexes() != null & (csvReadOutput.getNonValidatedRowIndexes().size() >= csvReadOutput.getImportedTableRows().size())) {
            //non vi è alcuna riga valida nel file
            throw new Exception("Il csv non presenta alcun elemento valido per l'importazione!");
        }

        //dopo aver validato le righe del CSV, creo le entità a partire da esse
        List<RowData> rows = csvReadOutput.getImportedTableRows();

        for (RowData rowData : rows) {

            String name = rowData.getData().get(GeoCityRowValidator.NAME);
            String cap = rowData.getData().get(GeoCityRowValidator.POSTAL_CODE);
            String province = rowData.getData().get(GeoCityRowValidator.PROVINCE_CODE);

            GeoCity geoCity = geoCityService.getByPostalCode(cap);

            if (geoCity == null) {
                geoCity = new GeoCity();
            }

            geoCity.setDescription(name);
            geoCity.setCap(cap);
            geoCity.setProvince(geoProvinceService.getByCode(province));

            geoCityService.save(geoCity);

            count += 1;

        }

        return count.toString();
    }

    public String importGeoProvince(UIFileUpload csvFile) throws Exception {

        /*
        Sposto il csv in una cartella temporanea (dato che mi serve un percorso assoluto per utilizzare il csvImporter)
         */
        Integer count = 0;

        //salvo il file nella directory temporanea del file server
        String csvPath = csvFile.getPath();
        GeoProvinceRowValidator geoProvinceRowValidator = new GeoProvinceRowValidator();

        CsvReader reader = new CsvReader(csvPath, ";", geoProvinceRowValidator);
        CsvInfo csvReadOutput = reader.readFile();
        if (StringUtils.hasLength(csvReadOutput.getError())) {
            //errori di parse del file
            throw new Exception(csvReadOutput.getError());
        } else if (csvReadOutput.getNonValidatedRowIndexes() != null & (csvReadOutput.getNonValidatedRowIndexes().size() >= csvReadOutput.getImportedTableRows().size())) {
            //non vi è alcuna riga valida nel file
            throw new Exception("Il csv non presenta alcun elemento valido per l'importazione!");
        }

        //dopo aver validato le righe del CSV, creo le entità a partire da esse
        List<RowData> rows = csvReadOutput.getImportedTableRows();

        for (RowData rowData : rows) {

            String code = rowData.getData().get(GeoProvinceRowValidator.CODE);
            String name = rowData.getData().get(GeoProvinceRowValidator.NAME);

            GeoProvince geoProvince = geoProvinceService.getByCode(code);

            if (geoProvince == null) {
                geoProvince = new GeoProvince();
            }

            geoProvince.setDescription(name);
            geoProvince.setCode(code);

            geoProvinceService.save(geoProvince);

            count += 1;

        }

        return count.toString();
    }

}
