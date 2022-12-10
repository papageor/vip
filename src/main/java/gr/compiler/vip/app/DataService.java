package gr.compiler.vip.app;

import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import gr.compiler.vip.entity.CommonTag;
import gr.compiler.vip.entity.Coordinates;
import gr.compiler.vip.entity.DataQuery;
import gr.compiler.vip.entity.Vessel;
import io.jmix.core.DataManager;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("VIP_DataService")
public class DataService {
    private static final Logger log = LoggerFactory.getLogger(DataService.class);
    @Autowired
    private DataManager dataManager;
    @Autowired
    private InfluxConnectorService influxConnectorService;
    @Autowired
    private AppSetupService appSetupService;

    List<Coordinates> getVesselRoute(Vessel vessel, Date startDate, Date endDate)
    {
        List<Coordinates> coordinates = new ArrayList<>();

        try{

        }
        catch (Exception e)
        {
            log.error("Cannot fetch vessel coordinates");
        }

        return coordinates;
    }

    HashMap<Date, Double>  getTagValues(Vessel vessel, CommonTag commonTag, Date fromDate, Date toDate)
    {
        HashMap<Date, Double> series = new HashMap<>();

        int offsetDays = appSetupService.getDemoModeOffsetDays();

        DataQuery dataQuery = null;
        dataQuery = getDataQueryOfSinglTag();

        HashMap<String, String> params = new HashMap<>();
        String key = "";
        String value = "";
        params.clear();

        if (fromDate != null){
            key = "startDate";
            value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);

            String utcValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(fromDate) + "+00:00";
            value = utcValue;

            //value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);
            params.put(key,value);
        }

        if (toDate != null){

            key = "endDate";
            value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
            String utcValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(toDate) + "+00:00";
            value = utcValue;
            //value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
            params.put(key,value);
        }

        key = "sid";
        value = vessel.getReferenceId().toString();
        params.put(key, value);

        key="tag";
        value = commonTag.getTag();
        params.put(key, value);

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
        decimalFormat.setGroupingUsed(false); // Do not use grouping symbol for thousands - influx cannot accept it.

        List<FluxTable> fluxTables = influxConnectorService.getFluxTables(dataQuery, params, false);
        if ((fluxTables != null) && (!fluxTables.isEmpty())) {
            for (FluxTable fluxTable : fluxTables)
            {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records)
                {
                    Date timeStamp = Date.from(fluxRecord.getTime());
                    if (offsetDays > 0) {
                        timeStamp = DateUtils.addDays(timeStamp, offsetDays);
                    }

                    Double tagValue = getFluxRecordValueByKey(fluxRecord,"_value",2);

                    series.put(timeStamp,tagValue);
                }
            }
        }



        return series;
    }

    private DataQuery getDataQueryOfSinglTag() {
        DataQuery dataQuery = null;

        String queryName = "FETCH_SINGLE_TAG";
        Optional<DataQuery> query = dataManager.load(DataQuery.class)
                .query("select d from vesselinsights_DataQuery d where d.name = :name")
                .parameter("name", queryName)
                .fetchPlan("dataQuery-view")
                .optional();
        if (query.isPresent()) {
            dataQuery = query.get();
        }

        return dataQuery;
    }

    private double getFluxRecordValueByKey(FluxRecord fluxRecord, String key, int scale) {
        double value = 0.0;
        try {
            if (fluxRecord.getValueByKey(key) != null) {
                value = (double) fluxRecord.getValueByKey(key);
                value = new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
            }
        } catch (Exception e) {
            value = 0.0;
        }

        return value;
    }
}