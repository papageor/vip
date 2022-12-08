package gr.compiler.vip.app;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxColumn;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import gr.compiler.vip.entity.Alert;
import gr.compiler.vip.entity.DataQuery;
import org.apache.commons.lang3.time.DateUtils;
import org.h2.util.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qdata.QColumn;
import qdata.QField;
import qdata.QTable;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service("VIP_InfluxConnectorV2Service")
public class InfluxConnectorV2Service {
    private static final Logger log = LoggerFactory.getLogger(InfluxConnectorV2Service.class);

    @Inject
    private AppSetupService appSetupService;
    @Inject
    private InfluxLogService influxLogService;

     public List<QTable> executeQuery(DataQuery dataQuery, HashMap<String, String> params) {
        List<QTable> qTables = new ArrayList<>();

        String token = dataQuery.getDataStore().getToken();
        if (StringUtils.isNullOrEmpty(token)) {
            token = "0J4MoHlEMTbIvMEaXLdnYHqZgYpECUZlC15nKvvuL2nz_u_CtYSxafKDo8x8dnZCGsbPJVyPWLQz1YksF08qUA==";
        }
        //String org = "MTIS";
        String org = dataQuery.getDataStore().getOrganization();

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(dataQuery.getDataStore().getDatabaseURL(), token.toCharArray(), org);

        String query = dataQuery.getSelectQuery();
        /*
        String query ="from(bucket: \"iplatform\")\n" +
                "  |> range(start: -30d)\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"go_threads\")\n" +
                "  |> aggregateWindow(every: 1h, fn: mean, createEmpty: false)\n" +
                "  |> yield(name: \"mean\")";
        */
        query = commentsExtractor(query);

        query = query.replaceAll("(?:\\n|\\r|\\t)", "");

        //Replace placeholders with user-defined values
        for (Map.Entry<String, String> param : params.entrySet()) {
            if ((!StringUtils.isNullOrEmpty(param.getKey())) && (!StringUtils.isNullOrEmpty(param.getValue()))) {
                String regex = String.format("__%s__", param.getKey());
                query = query.replaceAll(regex, param.getValue());
            }
        }

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(query);
        influxLogService.registerQuery(dataQuery, org, token, query, tables, "");

        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
            }
        }

        //For each separate field value a new table is returned
        if (!tables.isEmpty()) {
            for (FluxTable fluxTable : tables) {
                QTable qTable = new QTable();

                //Copy Column metadata
                for (FluxColumn fluxColumn : fluxTable.getColumns()) {
                    QColumn qColumn = new QColumn();
                    qColumn.setDataType(fluxColumn.getDataType());
                    qColumn.setDefaultValue(fluxColumn.getDefaultValue());
                    qColumn.setIndex(fluxColumn.getIndex());
                    qColumn.setLabel(fluxColumn.getLabel());

                    qTable.getColumns().add(qColumn);
                }

                List<FluxRecord> records = fluxTable.getRecords();
                HashSet<QField> qFields = qTable.getFields();
                records.stream().forEach(fluxRecord -> {
                    qTable.addRecord(fluxRecord.getValues());

                    if (fluxRecord.getValues().containsKey("_field")) {
                        QField qField = new QField();
                        qField.setLabel(fluxRecord.getValueByKey("_field").toString());
                        qFields.add(qField);
                    }
                });

                QField qField = new QField();
                qField.setLabel("_time");
                qFields.add(qField);

                qTables.add(qTable);
            }


        }

        influxDBClient.close();

        return qTables;
    }

    public List<QTable> executeQueryAlert(Alert dataQuery, HashMap<String, String> params) {
        List<QTable> qTables = new ArrayList<>();

        String token = dataQuery.getDataStore().getToken();
        if (StringUtils.isNullOrEmpty(token)) {
            token = "0J4MoHlEMTbIvMEaXLdnYHqZgYpECUZlC15nKvvuL2nz_u_CtYSxafKDo8x8dnZCGsbPJVyPWLQz1YksF08qUA==";
        }
        //String org = "MTIS";
        String org = dataQuery.getDataStore().getOrganization();

        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(dataQuery.getDataStore().getDatabaseURL(), token.toCharArray(), org);

        String query = dataQuery.getSelectQuery();
        /*
        String query ="from(bucket: \"iplatform\")\n" +
                "  |> range(start: -30d)\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"go_threads\")\n" +
                "  |> aggregateWindow(every: 1h, fn: mean, createEmpty: false)\n" +
                "  |> yield(name: \"mean\")";
        */
        query = commentsExtractor(query);
        query = query.replaceAll("(?:\\n|\\r|\\t)", "");

        //Replace placeholders with user-defined values
        for (Map.Entry<String, String> param : params.entrySet()) {
            if ((!StringUtils.isNullOrEmpty(param.getKey())) && (!StringUtils.isNullOrEmpty(param.getValue()))) {
                String regex = String.format("__%s__", param.getKey());
                query = query.replaceAll(regex, param.getValue());
            }
        }

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(query);
        influxLogService.registerQueryAlert(dataQuery, org, token, query, tables, "");

        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                System.out.println(fluxRecord.getTime() + ": " + fluxRecord.getValueByKey("_value"));
            }
        }

        //For each separate field value a new table is returned
        if (!tables.isEmpty()) {
            for (FluxTable fluxTable : tables) {
                QTable qTable = new QTable();

                //Copy Column metadata
                for (FluxColumn fluxColumn : fluxTable.getColumns()) {
                    QColumn qColumn = new QColumn();
                    qColumn.setDataType(fluxColumn.getDataType());
                    qColumn.setDefaultValue(fluxColumn.getDefaultValue());
                    qColumn.setIndex(fluxColumn.getIndex());
                    qColumn.setLabel(fluxColumn.getLabel());

                    qTable.getColumns().add(qColumn);
                }

                List<FluxRecord> records = fluxTable.getRecords();
                HashSet<QField> qFields = qTable.getFields();
                records.stream().forEach(fluxRecord -> {
                    qTable.addRecord(fluxRecord.getValues());

                    if (fluxRecord.getValues().containsKey("_field")) {
                        QField qField = new QField();
                        qField.setLabel(fluxRecord.getValueByKey("_field").toString());
                        qFields.add(qField);
                    }
                });

                QField qField = new QField();
                qField.setLabel("_time");
                qFields.add(qField);

                qTables.add(qTable);
            }


            /*
            for (FluxRecord fluxRecord : records) {
                qTable.addRecord(fluxRecord.getValues());

                if (fluxRecord.getValues().containsKey("_field")){
                    QField qField = new QField();
                    qField.setLabel(fluxRecord.getValueByKey("_field").toString());
                    qFields.add(qField);
                }
            }
            */

        }

        influxDBClient.close();

        return qTables;
    }


    public Pair<HashMap<Integer, String>, Multimap<Object, Object>> executeQueryEx(DataQuery dataQuery, HashMap<String, String> params) {
        //This method returns (a) List of Column Names , (b) Combined values per timestamp, etc. timestamp, column1, column2,...,colymnN

        Pair<HashMap<Integer, String>, Multimap<Object, Object>> tuples = null;

        Multimap<Object, Object> multimap = ArrayListMultimap.create();
        HashMap<Integer, String> fields = new HashMap<>();

        List<FluxTable> fluxTables = getFluxTables(dataQuery, params);
        if (fluxTables != null) {
            for (FluxTable fluxTable : fluxTables) {
                List<FluxRecord> records = fluxTable.getRecords();
                records.stream().forEach(fluxRecord -> {

                    Object timeField = null;
                    Object valueField = null;

                    if (fluxRecord.getValues().containsKey("_time")) {
                        timeField = fluxRecord.getValueByKey("_time");
                    }

                    if (fluxRecord.getValues().containsKey("_value")) {
                        valueField = fluxRecord.getValueByKey("_value");
                    }

                    if (timeField != null) {
                        //Date date = new Date(((Instant)timeField).getEpochSecond() * 1000L);
                        multimap.put(timeField, valueField);
                    }

                    if (fluxRecord.getValues().containsKey("_field")) {
                        if (!fields.containsValue(fluxRecord.getValueByKey("_field").toString())) {
                            int index = fields.size() + 1;
                            fields.put(index, fluxRecord.getValueByKey("_field").toString());
                        }
                    }

                });
            }
        }

        tuples = Pair.with(fields, multimap);

        return tuples;
    }

    public Pair<HashMap<Integer, String>, Multimap<Object, Object>> executeQueryEx2(DataQuery dataQuery, HashMap<String, String> params, String defaultSelectQuery) {
        //This method returns (a) List of Column Names , (b) Combined values per timestamp, etc. timestamp, column1, column2,...,colymnN

        Pair<HashMap<Integer,String>,Multimap<Object,Object>> tuples = null;

        Multimap<Object,Object> multimap = ArrayListMultimap.create();
        HashMap<Integer,String> fields = new HashMap<>();

        List<FluxTable> fluxTables = getFluxTablesEx2(dataQuery,params,defaultSelectQuery);
        if (fluxTables != null)
        {
            int loopFluxTablesIndex = 0;
            for (FluxTable fluxTable : fluxTables)
            {
                List<FluxRecord> records = fluxTable.getRecords();
                int finalLoopFluxTablesIndex = loopFluxTablesIndex;
                records.stream().forEach(fluxRecord->{

                    Object timeField = null;
                    Object valueField = null;

                    if (fluxRecord.getValues().containsKey("_time")){
                        timeField = fluxRecord.getValueByKey("_time");
                    }

                    if (fluxRecord.getValues().containsKey("_value"))
                    {
                        valueField = fluxRecord;
                    }

                    if (timeField != null)
                    {
                        //Date date = new Date(((Instant)timeField).getEpochSecond() * 1000L);
                        if (finalLoopFluxTablesIndex > 0)
                        {
                            int totalKeyValues = 0;
                            Collection<Object> keyValues = multimap.get(timeField);
                            if (keyValues != null)
                            {
                                //There are already values added for this key (timeField)
                                totalKeyValues = keyValues.size();

                            }

                            for (int j = totalKeyValues; j< finalLoopFluxTablesIndex; j++){
                                multimap.put(timeField,0.0);
                            }

                        }

                        multimap.put(timeField,valueField);
                    }

                    if (fluxRecord.getValues().containsKey("_field"))
                    {
                        if (!fields.containsValue(fluxRecord.getValueByKey("_field").toString()))
                        {
                            int index = fields.size() + 1;
                            fields.put(index,fluxRecord.getValueByKey("_field").toString());
                        }
                    }

                });

                loopFluxTablesIndex++;
            }
        }
        tuples = Pair.with(fields,multimap);

        return tuples;
    }

    public HashMap<Object, Map<String, Object>> getValuesOfFluxRecords(DataQuery dataQuery, HashMap<String, String> params, String defaultSelectQuery) {
        //This method returns all values of each FluxRecord associated with a specific timestamp

        HashMap<Object, Map<String, Object>> queryResults = new HashMap<>();

        List<FluxTable> fluxTables = getFluxTablesEx2(dataQuery, params, defaultSelectQuery);
        if (fluxTables != null) {
            for (FluxTable fluxTable : fluxTables) {
                List<FluxRecord> records = fluxTable.getRecords();

                records.stream().forEach(fluxRecord -> {

                    Object timeField = null;
                    Object valueField = null;

                    if (fluxRecord.getValues().containsKey("_time")) {
                        timeField = fluxRecord.getValueByKey("_time");
                    }

                    if (timeField != null) {
                        queryResults.put(timeField, fluxRecord.getValues());
                    }

                });

            }
        }


        return queryResults;
    }

    public HashMap<Object, Map<String, Object>> getValuesOfFluxRecordsWithDemoMode(DataQuery dataQuery, HashMap<String, String> params, String defaultSelectQuery) {
        //This method returns all values of each FluxRecord associated with a specific timestamp

        HashMap<Object, Map<String, Object>> queryResults = new HashMap<>();

        AtomicLong demoToActualStartDateOffset = new AtomicLong(0L);

        List<FluxTable> fluxTables = getFluxTablesWithDemoMode(dataQuery, params, defaultSelectQuery, demoToActualStartDateOffset);
        if (fluxTables != null) {
            for (FluxTable fluxTable : fluxTables) {
                List<FluxRecord> records = fluxTable.getRecords();

                records.stream().forEach(fluxRecord -> {

                    Object timeField = null;
                    Object valueField = null;

                    if (fluxRecord.getValues().containsKey("_time")) {
                        if (demoToActualStartDateOffset.get() > 0) {
                            Date timeStamp = Date.from((Instant) fluxRecord.getValueByKey("_time"));
                            Date demoTimeStamp = DateUtils.addDays(timeStamp, (int) demoToActualStartDateOffset.get());

                            timeField = demoTimeStamp.toInstant();
                        } else {
                            timeField = fluxRecord.getValueByKey("_time");
                        }

                    }

                    if (timeField != null) {
                        queryResults.put(timeField, fluxRecord.getValues());
                    }

                });

            }
        }


        return queryResults;
    }

    private List<FluxTable> getFluxTables(DataQuery dataQuery, HashMap<String, String> params) {
        List<FluxTable> fluxTables = null;
        String token = "";
        String org = "";
        String query = "";
        String error = "";

        try {
            token = dataQuery.getDataStore().getToken();

            //String org = "MTIS";
            org = dataQuery.getDataStore().getOrganization();

            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(dataQuery.getDataStore().getDatabaseURL(), token.toCharArray(), org);

            query = dataQuery.getSelectQuery();

            query = commentsExtractor(query);
            query = query.replaceAll("(?:\\n|\\r|\\t)", "");

            //Replace placeholders with user-defined values
            for (Map.Entry<String, String> param : params.entrySet()) {
                if ((!StringUtils.isNullOrEmpty(param.getKey())) && (!StringUtils.isNullOrEmpty(param.getValue()))) {
                    String regex = String.format("__%s__", param.getKey());
                    query = query.replaceAll(regex, param.getValue());
                }
            }

            QueryApi queryApi = influxDBClient.getQueryApi();

            fluxTables = queryApi.query(query);

            influxDBClient.close();

        } catch (Exception e) {
            error = e.getMessage();

            fluxTables = null;
        } finally {
            influxLogService.registerQuery(dataQuery, org, token, query, fluxTables, error);
        }


        return fluxTables;
    }

    private List<FluxTable> getFluxTablesEx2(DataQuery dataQuery, HashMap<String, String> params, String defaultSelectQuery) {
        List<FluxTable> fluxTables = null;
        String token = "";
        String org = "";
        String query = "";
        String error = "";

        try {

            if (params.containsKey("ORG") && params.containsKey("TOKEN")) {
                token = params.get("TOKEN");
                org = params.get("ORG");

            } else {
                if (params.containsKey("sid")) {
                    token = appSetupService.GetAppSetupBySID(params.get("sid")).getToken();

                    //String org = "MTIS";
                    org = appSetupService.GetAppSetupBySID(params.get("sid")).getOrganization();
                } else {
                    token = appSetupService.GetAppSetup().getToken();
                    org = appSetupService.GetAppSetup().getOrganization();
                }

            }

            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(dataQuery.getDataStore().getDatabaseURL(), token.toCharArray(), org);

            query = !defaultSelectQuery.isEmpty() ? defaultSelectQuery : dataQuery.getSelectQuery();

            query = commentsExtractor(query);
            query = query.replaceAll("(?:\\n|\\r|\\t)", "");


            //Replace placeholders with user-defined values
            for (Map.Entry<String, String> param : params.entrySet()) {
                if ((!StringUtils.isNullOrEmpty(param.getKey())) && (!StringUtils.isNullOrEmpty(param.getValue()))) {
                    String regex = String.format("__%s__", param.getKey());
                    query = query.replaceAll(regex, param.getValue());
                }
            }


            QueryApi queryApi = influxDBClient.getQueryApi();

            fluxTables = queryApi.query(query);

            influxDBClient.close();
        } catch (Exception e) {
            error = e.getMessage();
            fluxTables = null;
        } finally {
            influxLogService.registerQuery(dataQuery, org, token, query, fluxTables, error);
        }

        return fluxTables;
    }

    private List<FluxTable> getFluxTablesWithDemoMode(DataQuery dataQuery, HashMap<String, String> params, String defaultSelectQuery, AtomicLong demoToActualStartDateOffset) {
        List<FluxTable> fluxTables = null;
        String token = "";
        String org = "";
        String query = "";
        String error = "";

        LocalDateTime actualStartDate = null;

        try {

            if (params.containsKey("ORG") && params.containsKey("TOKEN")) {
                token = params.get("TOKEN");
                org = params.get("ORG");

            } else {
                if (params.containsKey("sid")) {
                    token = appSetupService.GetAppSetupBySID(params.get("sid")).getToken();

                    //String org = "MTIS";
                    org = appSetupService.GetAppSetupBySID(params.get("sid")).getOrganization();
                } else {
                    token = appSetupService.GetAppSetup().getToken();
                    org = appSetupService.GetAppSetup().getOrganization();
                }

            }

            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(dataQuery.getDataStore().getDatabaseURL(), token.toCharArray(), org);

            query = !defaultSelectQuery.isEmpty() ? defaultSelectQuery : dataQuery.getSelectQuery();

            query = commentsExtractor(query);
            query = query.replaceAll("(?:\\n|\\r|\\t)", "");


            if ((appSetupService.GetAppSetupBySID(params.get("sid")).getDemoMode() != null) && (appSetupService.GetAppSetupBySID(params.get("sid")).getDemoMode())) {
                LocalDateTime demoEndDate = appSetupService.GetAppSetup().getDemoWorkingDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                long daysOffset = 0;
                LocalDateTime startDate = null;
                LocalDateTime endDate = null;

                if (params.containsKey("startDate")) {
                    startDate = LocalDateTime.parse(params.get("startDate"), formatter);
                    actualStartDate = startDate;
                }

                if (params.containsKey("endDate")) {
                    endDate = LocalDateTime.parse(params.get("endDate"), formatter);
                }

                if ((startDate != null) && (endDate != null)) {
                    Duration duration = Duration.between(startDate, endDate);
                    daysOffset = duration.toDays();

                    LocalDateTime demoStartDate = demoEndDate.minusDays(daysOffset);

                    Duration durationStartDates = Duration.between(demoStartDate, actualStartDate);
                    demoToActualStartDateOffset.set(durationStartDates.toDays());

                    String value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date.from(demoStartDate.atZone(ZoneId.systemDefault()).toInstant()));
                    params.put("startDate", value);

                    String value2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Date.from(demoEndDate.atZone(ZoneId.systemDefault()).toInstant()));
                    params.put("endDate", value2);

                }

            }

            //Replace placeholders with user-defined values
            for (Map.Entry<String, String> param : params.entrySet()) {
                if ((!StringUtils.isNullOrEmpty(param.getKey())) && (!StringUtils.isNullOrEmpty(param.getValue()))) {
                    String regex = String.format("__%s__", param.getKey());
                    query = query.replaceAll(regex, param.getValue());
                }
            }


            QueryApi queryApi = influxDBClient.getQueryApi();

            fluxTables = queryApi.query(query);

            influxDBClient.close();
        } catch (Exception e) {
            error = e.getMessage();
            fluxTables = null;
        } finally {
            influxLogService.registerQuery(dataQuery, org, token, query, fluxTables, error);
        }

        return fluxTables;
    }

    private String commentsExtractor(String expression) {
        String result = "";

        //result = expression.replaceAll("//(.*?)\\r?\\n?\\t?",System.lineSeparator());
        result = expression.replaceAll("//(.)*?//", System.lineSeparator());

        return result;
    }
}