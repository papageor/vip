package gr.compiler.vip.app;

import com.google.common.collect.Multimap;
import com.influxdb.client.*;
import com.influxdb.client.domain.*;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import gr.compiler.vip.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.Metadata;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.math.NumberUtils;
import org.h2.util.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("VIP_InfluxConnectorServiceBean")
public class InfluxConnectorService {

    private static final Logger log = LoggerFactory.getLogger(InfluxConnectorService.class);

    @Inject
    private DataManager dataManager;

    @Inject
    private InfluxConnectorV2Service influxConnectorV2Service;

    @Inject
    private Metadata metadata;

    @Inject
    private AppSetupService appSetupService;
    @Inject
    private InfluxLogService influxLogService;

    private final OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS);

    public List<TimePoint> getQueryResults(String influxServer, String databaseName, String userName, String password, String measurement, String fieldName, Date startDate, Date endDate) {
        List<TimePoint> timePoints = new ArrayList<>();

        try {
            final InfluxDB influxDB = InfluxDBFactory.connect(influxServer, userName, password);

            String startDate_rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(startDate);
            String endDate_rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(endDate);

            String influxQuery = String.format("SELECT \"%s\" FROM %s WHERE time >= '%s' AND time <= '%s' ORDER BY time ASC", fieldName, measurement, startDate_rfc3339, endDate_rfc3339);
            QueryResult queryResult = influxDB.query(new Query(influxQuery, databaseName));

            List<QueryResult.Result> resultList = queryResult.getResults();

            if (!resultList.isEmpty()) {
                List<QueryResult.Series> seriesList = resultList.get(0).getSeries();
                if (!seriesList.isEmpty()) {
                    QueryResult.Series series = resultList.get(0).getSeries().get(0);

                    for (List<Object> valueList : series.getValues()) {

                        String date_rfc3339 = (String) valueList.get(0);
                        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(date_rfc3339);

                        TimePoint timePoint = new TimePoint();
                        timePoint.setDate(date);
                        timePoint.setValue((double) valueList.get(1));

                        timePoints.add(timePoint);
                    }
                }
            }

            // Close it if your application is terminating or you are not using it anymore.
            influxDB.close();

        } catch (Exception e) {
            log.error(e.getMessage());
        }


        return timePoints;
    }

    public List<VesselPoint> getQueryResultsExt(String influxServer, String databaseName, String userName, String password, String measurement,
                                                String fieldName, String fieldName2, String fieldName3,
                                                String fieldName4, String fieldName5, String fieldName6, String fieldName7,
                                                Date startDate, Date endDate, int interval) {
        List<VesselPoint> vesselPointList = null;

        try {
            final InfluxDB influxDB = InfluxDBFactory.connect(influxServer, userName, password);

            String startDate_rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(startDate);
            String endDate_rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(endDate);

            String influxQuery = String.format("SELECT " +
                            "mean(\"%s\") as %s," +
                            "mean(\"%s\") as %s," +
                            "mean(\"%s\") as %s," +
                            "mean(\"%s\") as %s," +
                            "mean(\"%s\") as %s," +
                            "mean(\"%s\") as %s," +
                            "difference(mean(\"%s\"))/1000 as %s" +
                            " FROM %s WHERE time >= $start AND time <= $end AND \"sid\"='1' GROUP BY time(%dm) FILL(0)",
                    fieldName, fieldName.replace("#", ""), fieldName2, fieldName2.replace("#", ""), fieldName3, fieldName3.replace("#", ""),
                    fieldName4, fieldName4.replace("#", ""), fieldName5, fieldName5.replace("#", ""),
                    fieldName6, fieldName6.replace("#", ""), fieldName7, fieldName7.replace("#", ""), measurement, interval);
            Query query = BoundParameterQuery.QueryBuilder.newQuery(influxQuery)
                    .forDatabase(databaseName)
                    .bind("start", startDate_rfc3339)
                    .bind("end", endDate_rfc3339)
                    .create();


            QueryResult queryResult = influxDB.query(query);

            InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
            vesselPointList = resultMapper
                    .toPOJO(queryResult, VesselPoint.class);

            if (!vesselPointList.isEmpty()) {
                System.out.println(vesselPointList.size());
            }

            // Close it if your application is terminating or you are not using it anymore.
            influxDB.close();

        } catch (Exception e) {
            log.error(e.getMessage());
        }


        return vesselPointList;
    }

    public List<VesselPoint> getQueryResultsV2(DataQuery dataQuery, HashMap<String, String> params) {
        List<VesselPoint> vesselPointList = new ArrayList<>();

        try {
            Pair<HashMap<Integer, String>, Multimap<Object, Object>> tuples;
            tuples = influxConnectorV2Service.executeQueryEx(dataQuery, params);

            if (tuples != null) {
                HashMap<Integer, String> columns = tuples.getValue0();

                Multimap<Object, Object> records = tuples.getValue1();

                for (Object recordTimeKey : records.keySet().stream().sorted().collect(Collectors.toList())) {
                    Collection<Object> singleRecord = records.get(recordTimeKey);
                    if (!singleRecord.isEmpty()) {
                        VesselPoint vesselPoint = new VesselPoint();
                        Date date = Date.from((Instant) recordTimeKey);
                        vesselPoint.setTime(date);
                        vesselPoint.setSpeedOverGround(new Double(0.0));
                        vesselPoint.setTotalConsumption(new Double(0.0));
                        vesselPoint.setWindSpeed(new Double(0.0));
                        vesselPoint.setLatitude(new Double(0.0));
                        vesselPoint.setLongitude(new Double(0.0));
                        vesselPoint.setShaftPower(new Double(0.0));
                        vesselPoint.setRpm(new Double(0.0));

                        int columnIndex = 0;
                        for (Object fieldValue : singleRecord) //Add value fields
                        {
                            columnIndex++;
                            String columnName = columns.get(columnIndex);
                            String fieldValueText = fieldValue.toString();
                            Double fieldValueDouble = Double.valueOf(0.0);
                            try {
                                fieldValueDouble = NumberUtils.createDouble(fieldValueText);
                            } catch (Exception e) {
                                fieldValueDouble = Double.valueOf(0.0);
                            }

                            if (columnName.equals("00020102"))
                                vesselPoint.setSpeedOverGround(fieldValueDouble);

                            if (columnName.equals("01010305"))
                                vesselPoint.setTotalConsumption(fieldValueDouble / 1000.0);

                            if (columnName.equals("00030103"))
                                vesselPoint.setWindSpeed(fieldValueDouble);

                            if (columnName.equals("00010101"))
                                vesselPoint.setLatitude(fieldValueDouble);

                            if (columnName.equals("00010102"))
                                vesselPoint.setLongitude(fieldValueDouble);

                            if (columnName.equals("01010203"))
                                vesselPoint.setShaftPower(fieldValueDouble);

                            if (columnName.equals("01010201"))
                                vesselPoint.setRpm(fieldValueDouble);
                        }

                        vesselPointList.add(vesselPoint);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return vesselPointList;
    }

    public String createOrUpdateEvent(Event event) {
        String result = "";

        try {
            //dataManager.reload(event,"event-view");

            String token = event.getDatastore().getToken();
            String org = event.getDatastore().getOrganization();
            String url = event.getDatastore().getDatabaseURL();

            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org);

            //Get Organization ID
            String orgID = "";
            List<Organization> organizations = influxDBClient.getOrganizationsApi().findOrganizations();
            Optional<Organization> organization = organizations.stream().filter(p -> p.getName().equals(org)).findFirst();
            if (organization.isPresent()) {
                orgID = organization.get().getId();
            }

            //Get HTTP EndPoint ID
            String endpointID = "";
            List<NotificationEndpoint> notificationEndPoints = influxDBClient.getNotificationEndpointsApi().findNotificationEndpoints(orgID);
            Optional<NotificationEndpoint> notificationEndpoint = notificationEndPoints.stream().filter(f -> f.getName().equals("CUBA SERVICES")).findFirst();
            if (notificationEndpoint.isPresent()) {
                endpointID = notificationEndpoint.get().getId();
            }

            //Remove existing Threshold & Threshold Rule
            List<NotificationRule> notificationRules = influxDBClient.getNotificationRulesApi().findNotificationRules(orgID);
            Optional<NotificationRule> notificationRule = notificationRules.stream().filter(f -> f.getName().equals(event.getNotificationRuleName())).findFirst();
            if (notificationRule.isPresent()) {
                influxDBClient.getNotificationRulesApi().deleteNotificationRule(notificationRule.get().getId());
            }

            List<Check> checks = influxDBClient.getChecksApi().findChecks(orgID);
            Optional<Check> check = checks.stream().filter(f -> f.getName().equals(event.getName())).findFirst();
            if (check.isPresent()) {
                influxDBClient.getChecksApi().deleteCheck(check.get().getId());
            }

            //Create Threshold
            LesserThreshold threshold = new LesserThreshold();
            threshold.setLevel(CheckStatusLevel.CRIT);
            threshold.setValue(event.getThresholdValue().floatValue());

            List<LesserThreshold> thresholds = new ArrayList<>();
            thresholds.add(threshold);

            //Create Threshold Check
            influxDBClient.getChecksApi().createThresholdCheck(event.getName(), event.getEventQuery(), event.getEvery(), event.getMessage(), threshold, orgID);

            //Get HTTP Endpoint
            HTTPNotificationEndpoint httpNotificatioNEndPoint =
                    (HTTPNotificationEndpoint) influxDBClient.getNotificationEndpointsApi().findNotificationEndpointByID(endpointID);


            //Create Notification Rule
            /*
            influxDBClient
                    .getNotificationRulesApi()
                    .createHTTPRule(event.getNotificationRuleName(), event.getEvery(), RuleStatusLevel.CRIT,null,httpNotificatioNEndPoint, orgID);
            */
        } catch (Exception e) {
            log.error("Influx event definition failed: ", e);
            result = e.getMessage();
        }

        return result;
    }

    public void storeDataPoint(DataQuery dataQuery, Date date, String sid, String fieldName, Double fieldValue) {

        String token = appSetupService.GetAppSetup().getToken();
        String org = appSetupService.GetAppSetup().getOrganization();
        String databaseURL = dataQuery.getDataStore().getDatabaseURL();

        String bucket = "B_" + sid;


        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), org, bucket);

        try (WriteApi writeApi = influxDBClient.getWriteApi()) {

            //
            // Write by Data Point
            //
            Point point = Point.measurement("iot_data")
                    //.addTag("sid", sid)
                    .addField(fieldName, fieldValue)
                    .time(date.toInstant().toEpochMilli(), WritePrecision.MS);

            writeApi.writePoint(point);


        }

        influxDBClient.close();


    }


    public void storeDataPoints(DataQuery dataQuery, Date date, String sid, HashMap<String, Double> fieldvalues) {

        String bucket = "B_" + sid;

        try {
            HashMap<String, Setup> bucketNameAndSetup = getBucketName(bucket);

            String token = bucketNameAndSetup.get(bucket).getToken();
            String org = bucketNameAndSetup.get(bucket).getOrganization();
            String databaseURL = dataQuery.getDataStore().getDatabaseURL();


            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(databaseURL, token.toCharArray(), org, bucket);

            try (WriteApi writeApi = influxDBClient.getWriteApi()) {
                for (Map.Entry<String, Double> fieldEntry : fieldvalues.entrySet()) {
                    Point point = Point.measurement("iot_data")
                            //.addTag("sid", sid)
                            .addField(fieldEntry.getKey(), fieldEntry.getValue())
                            .time(date.toInstant().toEpochMilli(), WritePrecision.MS);

                    writeApi.writePoint(point);
                }
            } catch (Exception e) {
                log.error("Cannot store data points in influx:", fieldvalues);
                log.error(e.getMessage());
            }
            influxDBClient.close();
        } catch (Exception e) {
            log.error("Cannot store data points in influx:", fieldvalues);
            log.error(e.getMessage());
        }

    }

    public List<FluxTable> getFluxTables(DataQuery dataQuery, HashMap<String, String> params, boolean ignoreDemoMode) {
        List<FluxTable> fluxTables = null;
        String token = "";
        String org = "";
        String query = "";
        String error = "";

        if (params.containsKey("sid")) {
            String bucket = "DSB_" + params.get("sid");

            InfluxObject influxObject = getInfluxDetailsForVessel(params.get("sid"));
            if (influxObject != null) {
                if (!StringUtils.isNullOrEmpty(influxObject.getOrganization()))
                    params.put("ORG", influxObject.getOrganization());

                if (!StringUtils.isNullOrEmpty(influxObject.getToken()))
                    params.put("TOKEN", influxObject.getToken());
            } else {
                HashMap<String, Setup> bucketNameAndSetup = getBucketName(bucket);
                params.put("TOKEN", bucketNameAndSetup.get(bucket).getToken());
                params.put("ORG", bucketNameAndSetup.get(bucket).getOrganization());
            }

        }

        try {
            String databaseURL = dataQuery.getDataStore().getDatabaseURL();

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

            InfluxDBClientOptions options = InfluxDBClientOptions.builder()
                    .url(databaseURL)
                    .authenticateToken(token.toCharArray())
                    .org(org)
                    .okHttpClient(builder)
                    .build();

            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(options);


            query = dataQuery.getSelectQuery();

            //  query = query.replaceAll("__bucket__",bucketName);

            query = commentsExtractor(query);
            query = query.replaceAll("(?:\\n|\\r|\\t)", "");

            //Modify Period Range for demo purposes
            String sid = "";
            if (params.containsKey("sid"))
                sid = params.get("sid");

            if ((!ignoreDemoMode) && (appSetupService.GetAppSetupBySID(sid).getDemoMode() != null) && (appSetupService.GetAppSetupBySID(sid).getDemoMode())) {
                LocalDateTime demoEndDate = appSetupService.GetAppSetup().getDemoWorkingDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                long daysOffset = 0;
                LocalDateTime startDate = null;
                LocalDateTime endDate = null;

                if (params.containsKey("startDate")) {
                    startDate = LocalDateTime.parse(params.get("startDate"), formatter);
                }

                if (params.containsKey("endDate")) {
                    endDate = LocalDateTime.parse(params.get("endDate"), formatter);
                }

                if ((startDate != null) && (endDate != null)) {
                    java.time.Duration duration = Duration.between(startDate, endDate);
                    daysOffset = duration.toDays();

                    LocalDateTime demoStartDate = demoEndDate.minusDays(daysOffset);

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
            e.printStackTrace();
            //fluxTables = null;
        } finally {
            influxLogService.registerQuery(dataQuery, org, token, query, fluxTables, error);
        }

        return fluxTables;
    }

    public HashMap<Integer, Coordinates> getVesselCoordinates() {
        HashMap<Integer, Coordinates> vesselPositions = new HashMap<>();
        HashMap<Date, VesselPoint> hashVesselPoints = new HashMap<>();

        try {
            DataQuery dataQuery = getDataQueryOfVesselPosition();
            if (dataQuery == null)
                return null;

            LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
            LocalDateTime before30Days = today.minusDays(30);

            HashMap<String, String> params = new HashMap<>();
            String key = "";
            String value = "";
            params.clear();

            Date fromDate = Date.from(before30Days.atZone(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());

            if (fromDate != null) {
                key = "startDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);
                params.put(key, value);
            }

            if (toDate != null) {

                key = "endDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
                params.put(key, value);
            }

            key = "windowPeriod";
            value = "1d";
            params.put(key, value);

            List<Vessel> vessels = dataManager.load(Vessel.class)
                    .query("select v from VIP_Vessel v")
                    .list();

            if (vessels != null) {
                for (Vessel vessel : vessels) {
                    hashVesselPoints.clear();

                    if (!StringUtils.isNullOrEmpty(vessel.getReferenceId().toString())) {
                        key = "vesselid";
                        value = vessel.getReferenceId().toString();
                        params.put(key, value);

                        List<FluxTable> fluxTables = getFluxTables(dataQuery, params, false);
                        if ((fluxTables != null) & (!fluxTables.isEmpty())) {
                            for (FluxTable fluxTable : fluxTables) {
                                List<FluxRecord> records = fluxTable.getRecords();
                                for (FluxRecord fluxRecord : records) {
                                    Date timeStamp = Date.from(fluxRecord.getTime());
                                    double fluxRecValue = getfluxRecordValueByKey(fluxRecord, "_value");

                                    if (hashVesselPoints.containsKey(timeStamp)) {
                                        VesselPoint vesselPoint = hashVesselPoints.get(timeStamp);
                                        switch (fluxRecord.getField()) {
                                            case "00010101":
                                                if (fluxRecValue != 0) {
                                                    vesselPoint.setLatitude(fluxRecValue);
                                                }
                                                break;

                                            case "00010102":
                                                if (fluxRecValue != 0) {
                                                    vesselPoint.setLongitude(fluxRecValue);
                                                }
                                                break;
                                        }
                                    } else {
                                        VesselPoint vesselPoint = new VesselPoint();
                                        vesselPoint.setTime(timeStamp);
                                        vesselPoint.setLongitude(0.0);
                                        vesselPoint.setLatitude(0.0);
                                        vesselPoint.setRpm(0.0);
                                        vesselPoint.setShaftPower(0.0);
                                        vesselPoint.setSpeedOverGround(0.0);
                                        vesselPoint.setTotalConsumption(0.0);
                                        vesselPoint.setWindSpeed(0.0);

                                        switch (fluxRecord.getField()) {
                                            case "00010101":
                                                vesselPoint.setLatitude(fluxRecValue);
                                                break;

                                            case "00010102":
                                                vesselPoint.setLongitude(fluxRecValue);
                                                break;
                                        }

                                        hashVesselPoints.put(timeStamp, vesselPoint);
                                    }
                                }
                            }
                        }
                        if (hashVesselPoints.size() > 0) {
                            List<VesselPoint> timePointList = hashVesselPoints.values().stream().sorted(Comparator.comparing(VesselPoint::getEntryDate)).collect(Collectors.toList());
                            if ((timePointList != null) && (!timePointList.isEmpty())) {
                                int loopIndex = 0;
                                Date lastDate = new Date();
                                Coordinates coordinates = metadata.create(Coordinates.class);
                                for (VesselPoint vpoint : timePointList) {
                                    if (loopIndex == 0) {
                                        lastDate = vpoint.getTime();
                                        coordinates.setLatitude(vpoint.getLatitude());
                                        coordinates.setLongitude(vpoint.getLongitude());
                                    } else {
                                        if (vpoint.getTime().after(lastDate)) {
                                            coordinates.setLatitude(vpoint.getLatitude());
                                            coordinates.setLongitude(vpoint.getLongitude());
                                        }
                                    }
                                    loopIndex++;
                                }
                                vesselPositions.put(vessel.getId().intValue(), coordinates);

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            vesselPositions = null;
            log.error("Vessels current position cannot be fetched", e);
        }

        return vesselPositions;
    }


    public HashMap<String, Coordinates> getFleetCoordinates() {
        HashMap<String, Coordinates> fleetCoordinates = new HashMap<>();

        HashMap<String, Setup> bucketsNames = getBucketsNames();

        for (Map.Entry<String, Setup> entry : bucketsNames.entrySet()) {

            try {
                DataQuery dataQuery = getDataQueryOfFleetPosition();
                if (dataQuery == null)
                    return null;

                LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
                LocalDateTime before10Days = today.minusDays(30);

                HashMap<String, String> params = new HashMap<>();
                String key = "";
                String value = "";
                params.clear();

                Date fromDate = Date.from(before10Days.atZone(ZoneId.systemDefault()).toInstant());
                Date toDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());

                if (fromDate != null) {
                    key = "startDate";
                    value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);
                    params.put(key, value);
                }

                if (toDate != null) {

                    key = "endDate";
                    value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
                    params.put(key, value);
                }

                params.put("ORG", entry.getValue().getOrganization());
                params.put("TOKEN", entry.getValue().getToken());

                key = "windowPeriod";
                value = "1d";
                params.put(key, value);
                params.put("bucket", entry.getKey());

                List<FluxTable> fluxTables = getFluxTables(dataQuery, params, true);
                if ((fluxTables != null) & (!fluxTables.isEmpty())) {
                    for (FluxTable fluxTable : fluxTables) {
                        List<FluxRecord> records = fluxTable.getRecords();
                        for (FluxRecord fluxRecord : records) {
                            String sid = "";
                            double latitude = 0.0;
                            double longtitude = 0.0;
                            double heading = 0.0;

                            int underscoreCharIndex = 0;
                            underscoreCharIndex = entry.getKey().indexOf('_'); //Bucket names contain the reference id for each vessel by convention
                            if (underscoreCharIndex > 0)
                                sid = entry.getKey().substring(underscoreCharIndex + 1);
                            else
                                sid = entry.getKey();

                            //sid = getfluxRecordStringValueByKey(fluxRecord, "sid");
                            latitude = getfluxRecordValueByKey(fluxRecord, "00010101");
                            longtitude = getfluxRecordValueByKey(fluxRecord, "00010102");
                            heading = getfluxRecordValueByKey(fluxRecord, "00020103");

                            if (!fleetCoordinates.containsKey(sid)) {
                                Coordinates coordinates = metadata.create(Coordinates.class);
                                coordinates.setLatitude(latitude);
                                coordinates.setLongitude(longtitude);
                                coordinates.setHeading(heading);

                                fleetCoordinates.put(sid, coordinates);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                fleetCoordinates = null;
                log.error("Failed to get Fleet coordinates", e);
            }


        }
        return fleetCoordinates;
    }

    public HashMap<String, Setup> getBucketsNames() {
        List<FluxTable> tables = null;
        HashMap<String, Setup> bucketsNames = new HashMap<>();
        List<Setup> appSetup = appSetupService.GetAppSetupList();
        DataQuery dataQueryBuckets = getDataQueryOfBucketsNames();
        String URL = dataQueryBuckets.getDataStore().getDatabaseURL();

        String token = "";
        String org = "";
        String query = "";
        String error = "";

        for (Setup s : appSetup) {

            try {

                token = s.getToken();
                org = s.getOrganization();

                query = dataQueryBuckets.getSelectQuery();

                InfluxDBClient influxDBClient = InfluxDBClientFactory.create(URL, token.toCharArray(), org);

                QueryApi queryApi = influxDBClient.getQueryApi();

                String bucket;
                query = commentsExtractor(query);
                query = query.replaceAll("(?:\\n|\\r|\\t)", "");

                tables = queryApi.query(query);
                for (FluxTable fluxTable : tables) {
                    List<FluxRecord> records = fluxTable.getRecords();
                    for (FluxRecord fluxRecord : records) {
                        bucket = (String) fluxRecord.getValueByKey("name");

                        //bucketsNames.add(bucket);
                        bucketsNames.putIfAbsent(bucket, s);

                    }
                }

                influxDBClient.close();


            } catch (Exception e) {
                log.error("Failed to get vessels names ", e);
            } finally {
                influxLogService.registerQuery(dataQueryBuckets, org, token, query, tables, error);
            }
        }

        return bucketsNames;
    }

    private HashMap<String, Setup> getBucketName(String bucket) {
        HashMap<String, Setup> bucketsName = new HashMap<>();
        DataQuery dataQuery = getDataQueryOfBucketName();
        List<Setup> appSetup = appSetupService.GetAppSetupList();

        HashMap<String, String> params = new HashMap<>();
        params.put("getBucketName", bucket);

        for (Setup s : appSetup) {
            try {

                String token = s.getToken();
                String org = s.getOrganization();
                params.put("TOKEN", token);
                params.put("ORG", org);

                List<FluxTable> fluxTables = getFluxTables(dataQuery, params, false);
                if ((fluxTables != null) & (!fluxTables.isEmpty())) {
                    for (FluxTable fluxTable : fluxTables) {
                        List<FluxRecord> records = fluxTable.getRecords();
                        for (FluxRecord fluxRecord : records) {

                            if (fluxRecord.getValueByKey("name") != null) {
                                bucketsName.put(bucket, s);
                            }
                        }
                    }

                }


            } catch (Exception e) {
                log.error("Failed to get vessels names ", e);
            }
        }

        return bucketsName;
    }

    private DataQuery getDataQueryOfBucketName() {
        DataQuery dataQuery = null;

        String queryName = "FIND_SHIP_NAME";
        Optional<DataQuery> query = dataManager.load(DataQuery.class)
                .query("select d from VIP_DataQuery d where d.name = :name")
                .parameter("name", queryName)
                .fetchPlan("dataQuery-view")
                .optional();
        if (query.isPresent()) {
            dataQuery = query.get();
        }

        return dataQuery;
    }


    public List<String> getImos() {
        List<String> bucketsNames = new ArrayList<>();
        DataQuery dataQueryBuckets = null;
        List<FluxTable> tables = null;
        String token = "";
        String org = "";
        String URL = "";
        String query = "";
        String error = "";

        try {

            dataQueryBuckets = getDataQueryOfBucketsNames();
            token = appSetupService.GetAppSetup().getToken();
            org = appSetupService.GetAppSetup().getOrganization();
            URL = dataQueryBuckets.getDataStore().getDatabaseURL();

            query = dataQueryBuckets.getSelectQuery();

            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(URL, token.toCharArray(), org);

            QueryApi queryApi = influxDBClient.getQueryApi();

            String bucket;
            query = commentsExtractor(query);
            query = query.replaceAll("(?:\\n|\\r|\\t)", "");

            tables = queryApi.query(query);
            for (FluxTable fluxTable : tables) {
                List<FluxRecord> records = fluxTable.getRecords();
                for (FluxRecord fluxRecord : records) {
                    bucket = (String) fluxRecord.getValueByKey("name");

                    bucketsNames.add(bucket);
                }
            }

            influxDBClient.close();
        } catch (Exception e) {
            error = e.getMessage();
            log.error("Failed to get Fleet coordinates", e);
        } finally {
            influxLogService.registerQuery(dataQueryBuckets, org, token, query, tables, error);
        }

        return bucketsNames;
    }

    public HashMap<String, Coordinates> getFleetCoordinatesInPeriod(Date fromDate, Date toDate) {
        HashMap<String, Coordinates> fleetCoordinates = new HashMap<>();

        try {
            DataQuery dataQuery = getDataQueryOfFleetPosition();
            if (dataQuery == null)
                return null;

            LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
            LocalDateTime before10Days = today.minusDays(30);

            HashMap<String, String> params = new HashMap<>();
            String key = "";
            String value = "";
            params.clear();

            if (fromDate != null) {
                key = "startDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);
                params.put(key, value);
            }

            if (toDate != null) {

                key = "endDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
                params.put(key, value);
            }

            key = "windowPeriod";
            value = "1d";
            params.put(key, value);

            List<FluxTable> fluxTables = getFluxTables(dataQuery, params, true);
            if ((fluxTables != null) & (!fluxTables.isEmpty())) {
                for (FluxTable fluxTable : fluxTables) {
                    List<FluxRecord> records = fluxTable.getRecords();
                    for (FluxRecord fluxRecord : records) {
                        String sid = "";
                        double latitude = 0.0;
                        double longtitude = 0.0;
                        double heading = 0.0;

                        sid = getfluxRecordStringValueByKey(fluxRecord, "sid");
                        latitude = getfluxRecordValueByKey(fluxRecord, "00010101");
                        longtitude = getfluxRecordValueByKey(fluxRecord, "00010102");
                        heading = getfluxRecordValueByKey(fluxRecord, "00020103");

                        if (!fleetCoordinates.containsKey(sid)) {
                            Coordinates coordinates = metadata.create(Coordinates.class);
                            coordinates.setLatitude(latitude);
                            coordinates.setLongitude(longtitude);
                            coordinates.setHeading(heading);

                            fleetCoordinates.put(sid, coordinates);
                        }
                    }
                }
            }
        } catch (Exception e) {
            fleetCoordinates = null;
            log.error("Failed to get Fleet coordinates", e);
        }

        return fleetCoordinates;
    }

    public Coordinates getSingleVesselCoordinates(String sid) {
        Coordinates coordinates = null;

        try {
            DataQuery dataQuery = getDataQueryOfVesselPosition();
            if (dataQuery == null)
                return null;

            LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
            LocalDateTime before10Days = today.minusDays(10);

            HashMap<String, String> params = new HashMap<>();
            String key = "";
            String value = "";
            params.clear();

            Date fromDate = Date.from(before10Days.atZone(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());

            if (fromDate != null) {
                key = "startDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);
                params.put(key, value);
            }

            if (toDate != null) {

                key = "endDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
                params.put(key, value);
            }

            key = "windowPeriod";
            value = "1d";
            params.put(key, value);

            key = "sid";
            value = sid;
            params.put(key, value);

            List<FluxTable> fluxTables = getFluxTables(dataQuery, params, false);
            if ((fluxTables != null) & (!fluxTables.isEmpty())) {
                for (FluxTable fluxTable : fluxTables) {
                    List<FluxRecord> records = fluxTable.getRecords();
                    for (FluxRecord fluxRecord : records) {
                        double latitude = 0.0;
                        double longtitude = 0.0;
                        double heading = 0.0;

                        latitude = getfluxRecordValueByKey(fluxRecord, "00010101");
                        longtitude = getfluxRecordValueByKey(fluxRecord, "00010102");
                        heading = getfluxRecordValueByKey(fluxRecord, "00020103");

                        coordinates = metadata.create(Coordinates.class);
                        coordinates.setLatitude(latitude);
                        coordinates.setLongitude(longtitude);
                        coordinates.setHeading(heading);
                    }
                }
            }
        } catch (Exception e) {
            coordinates = null;
            log.error("Failed to get Single Vessel coordinates", e);
        }

        return coordinates;
    }

    public Coordinates getSingleVesselCoordinatesInPeriod(String sid, Date fromDate, Date toDate) {
        Coordinates coordinates = null;

        try {
            DataQuery dataQuery = getDataQueryOfVesselPosition();
            if (dataQuery == null)
                return null;

            LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
            LocalDateTime before10Days = today.minusDays(10);

            HashMap<String, String> params = new HashMap<>();
            String key = "";
            String value = "";
            params.clear();

            if (fromDate != null) {
                key = "startDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);
                params.put(key, value);
            }

            if (toDate != null) {

                key = "endDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
                params.put(key, value);
            }

            key = "windowPeriod";
            value = "1d";
            params.put(key, value);

            key = "sid";
            value = sid;
            params.put(key, value);

            List<FluxTable> fluxTables = getFluxTables(dataQuery, params, false);
            if ((fluxTables != null) & (!fluxTables.isEmpty())) {
                for (FluxTable fluxTable : fluxTables) {
                    List<FluxRecord> records = fluxTable.getRecords();
                    for (FluxRecord fluxRecord : records) {
                        double latitude = 0.0;
                        double longtitude = 0.0;
                        double heading = 0.0;

                        latitude = getfluxRecordValueByKey(fluxRecord, "00010101");
                        longtitude = getfluxRecordValueByKey(fluxRecord, "00010102");
                        heading = getfluxRecordValueByKey(fluxRecord, "00020103");

                        coordinates = metadata.create(Coordinates.class);
                        coordinates.setLatitude(latitude);
                        coordinates.setLongitude(longtitude);
                        coordinates.setHeading(heading);
                    }
                }
            }
        } catch (Exception e) {
            coordinates = null;
            log.error("Failed to get Single Vessel coordinates", e);
        }

        return coordinates;
    }

    public HashMap<String, Coordinates> getTenantFleetCoordinatesInPeriod(Date fromDate, Date toDate) {
        HashMap<String, Coordinates> fleetCoordinates = new HashMap<>();

        List<Vessel> vessels = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v")
                .list();
        if (vessels != null) {
            for (Vessel vessel : vessels) {
                Coordinates vesselCoordinates = getSingleVesselCoordinatesInPeriod(vessel.getReferenceId().toString(), fromDate, toDate);
                if (vesselCoordinates != null) {
                    fleetCoordinates.put(vessel.getReferenceId().toString(), vesselCoordinates);
                }
            }
        }

        return fleetCoordinates;
    }

    public List<Coordinates> getGeoPoints(String sid, Integer timeRange) {
        List<Coordinates> coordinatesList = new ArrayList<>();


        try {
            DataQuery dataQuery = getDataQueryPortCalls();
            if (dataQuery == null)
                return null;

            LocalDateTime today = LocalDateTime.now(ZoneId.systemDefault());
            LocalDateTime before10Days;


            switch (timeRange) {

                case -2:
                    before10Days = today.minusDays(60);
                    break;

                case -3:
                    before10Days = today.minusDays(90);
                    break;

                default:
                    before10Days = today.minusDays(30);
            }


            HashMap<String, String> params = new HashMap<>();
            String key = "";
            String value = "";
            params.clear();

            Date fromDate = Date.from(before10Days.atZone(ZoneId.systemDefault()).toInstant());
            Date toDate = Date.from(today.atZone(ZoneId.systemDefault()).toInstant());

            if (fromDate != null) {
                key = "startDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(fromDate);
                params.put(key, value);
            }

            if (toDate != null) {

                key = "endDate";
                value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(toDate);
                params.put(key, value);
            }

            key = "windowPeriod";
            value = "1d";
            params.put(key, value);

            key = "sid";
            value = sid;
            params.put(key, value);

            List<FluxTable> fluxTables = getFluxTables(dataQuery, params, false);
            if ((fluxTables != null) & (!fluxTables.isEmpty())) {
                for (FluxTable fluxTable : fluxTables) {
                    List<FluxRecord> records = fluxTable.getRecords();
                    for (FluxRecord fluxRecord : records) {
                        double latitude = 0.0;
                        double longtitude = 0.0;


                        latitude = getfluxRecordValueByKey(fluxRecord, "00010101");
                        longtitude = getfluxRecordValueByKey(fluxRecord, "00010102");

                        coordinatesList.add(new Coordinates(latitude, longtitude));
                    }
                }
            }
        } catch (Exception e) {
            coordinatesList = null;
            log.error("Failed to get Single Vessel coordinates", e);
        }

        return coordinatesList;

    }


    public void createSFOC_ISO_EXP_Task() {

        InfluxDBClient influxDBClient = null;//InfluxDBClientFactory.create(URL, token, org);

        List<Task> tasks = influxDBClient.getTasksApi().findTasks();
        for (Task t : tasks) {
            if (t.getName().equals("SFOC_ISO_EXP")) {
                return;
            }
        }
        String flux = "option task = {name: \"SFOC_ISO_EXP\", every: 1h}\n" +
                "\n" +
                "c0 = 17.848201447\n" +
                "c1 = 0.000235059358185\n" +
                "c2 = -0.0000004794141527\n" +
                "c3 = 0.0000000000337463277\n" +
                "\n" +
                "from(bucket: \"B_8479059\")\n" +
                "\t|> range(start: -2h, stop: now())\n" +
                "\t|> filter(fn: (r) =>\n" +
                "\t\t(r[\"_measurement\"] == \"iot_data\"))\n" +
                "\t|> filter(fn: (r) =>\n" +
                "\t\t(r[\"01010101\"] == \"1\"))\n" +
                "\t|> filter(fn: (r) =>\n" +
                "\t\t(r[\"_field\"] == \"01010203\" or r[\"_field\"] == \"01010302\"))\n" +
                "\t|> drop(columns: [\"02010101\", \"02020101\", \"02030101\", \"02040101\"])\n" +
                "\t|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")\n" +
                "\t|> filter(fn: (r) =>\n" +
                "\t\t(r[\"01010203\"] >= 500))\n" +
                "\t|> map(fn: (r) =>\n" +
                "\t\t({r with _value: c3 * r[\"01010203\"] * r[\"01010203\"] * r[\"01010203\"] + c2 * r[\"01010203\"] * r[\"01010203\"] + c1 * r[\"01010203\"] + c0, _field: \"SFOC_ISO_EXP\"}))\n" +
                "\t|> drop(columns: [\"01010203\", \"01010302\", \"_start\", \"_stop\"])\n" +
                "\t|> group(columns: [\"_measurement\", \"sid\", \"01010101\", \"_field\"])\n" +
                "\t|> aggregateWindow(every: 1h, fn: mean, createEmpty: false)\n" +
                "\t|> to(bucket: \"DS_B_8479059\", org: \"ORG3\")";
        Task t = new Task();
        t.setFlux(flux);
        t.setOrgID("2e0099eee9af9a85");
        influxDBClient.getTasksApi().createTask(t);
        influxDBClient.close();

    }

    public boolean getTaskStatus(String taskId, String vesselName) {
        boolean checked;

        Vessel vessel = dataManager.load(Vessel.class)
                .query("select v from VIP_Vessel v where v.name = :name")
                .parameter("name", vesselName)
                .one();
        String bucket = "B_" + vessel.getReferenceId();
        HashMap<String, Setup> setup = getBucketName(bucket);

        DataQuery dataQueryBuckets = getDataQueryOfBucketsNames();
        String token = setup.get(bucket).getToken();
        String org = setup.get(bucket).getOrganization();
        String URL = dataQueryBuckets.getDataStore().getDatabaseURL();
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(URL, token.toCharArray(), org);

        Task task = influxDBClient.getTasksApi().findTaskByID(taskId);

        if (task.getStatus().equals(TaskStatusType.ACTIVE)) {
            checked = true;
        } else {
            checked = false;
        }

        influxDBClient.close();

        return checked;

    }

    public void deleteTaskIfExist(UserEventConf userEventConf, InfluxDBClient influxDBClient) {

        try {
            UserEventConf u = dataManager.load(UserEventConf.class)
                    .query("select t from VIP_UserEventConf t where t.taskId = :taskId")
                    .parameter("taskId", userEventConf.getTaskId())
                    .one();
            Task taskById = influxDBClient.getTasksApi().findTaskByID(u.getTaskId());

            dataManager.remove(u);
            influxDBClient.getTasksApi().deleteTask(taskById);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteTaskById(String taskId) {
        if (taskId != null) {
            DataQuery dataQueryBuckets = getDataQueryOfBucketsNames();
            String token = appSetupService.GetAppSetup().getToken();
            String org = appSetupService.GetAppSetup().getOrganization();
            String URL = dataQueryBuckets.getDataStore().getDatabaseURL();
            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(URL, token.toCharArray(), org);

            UserEventConf u = dataManager.load(UserEventConf.class)
                    .query("select t from VIP_UserEventConf t where t.taskId = :taskId")
                    .parameter("taskId", taskId)
                    .one();
            dataManager.remove(u);

            Task taskById = influxDBClient.getTasksApi().findTaskByID(taskId);

            influxDBClient.getTasksApi().deleteTask(taskById);

            influxDBClient.close();
        }
    }


    public void createEventTask(UserEventConf userEventConf,
                                String name,
                                Vessel v,
                                UserEventOptions options1,
                                UserEventOptions options2,
                                UUID confId,
                                boolean checked) {
        Setup appSetup = appSetupService.GetAppSetupBySID(v.getReferenceId().toString());
        if (appSetup == null)
            return;

        DataQuery dataQueryBuckets = getDataQueryOfBucketsNames();
        String token = appSetup.getToken();
        String org = appSetup.getOrganization();
        String orgID = appSetup.getOrganizationID();
        String URL = dataQueryBuckets.getDataStore().getDatabaseURL();
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(URL, token.toCharArray(), org);

        String startRangeTime = userEventConf.getIfEventPersistsAtLeast().toString();
        String idleTimeAfterAlerting = String.valueOf((int) (userEventConf.getIdleTimeAfterAlerting()));

        String offset = userEventConf.getOffset();


        List<Organization> organizations = influxDBClient.getOrganizationsApi().findOrganizations();
        Optional<Organization> organization = organizations.stream().filter(p -> p.getName().equals(org)).findFirst();
        if (organization.isPresent()) {
            orgID = organization.get().getId();
        }

        deleteTaskIfExist(userEventConf, influxDBClient);

        List<String> vals = new ArrayList<>();
        List<String> valsOption1 = new ArrayList<>();
        List<String> valsOption2 = new ArrayList<>();

        String sid = v.getReferenceId().toString();

//        String first ="import \"influxdata/influxdb/monitor\"\n" +
//                "import \"influxdata/influxdb/schema\"\n" +
//                "import \"contrib/tomhollingworth/events\"\n" +
//                "import \"math\"\n" +
//                "\n" +
//                "option v = {bucket: \"\", timeRangeStop: now()}\n" +
//                "option task = {name: \""+name+"\", every: 2m, offset:"+offset+"m}\n" +
//                "\n" +
//                "RPM_MCR = 105.0\n" +
//                "Power_MCR = 13560.0\n";

        String first2 = "import \"influxdata/influxdb/monitor\"\n"
                + "import \"math\"\n" +
                "\n" +
                "option task = {\n" +
                "    name: \"" + name + "\",\n" +
                "    every: 10m, offset:" + offset + "m\n" +
                "}\n" +
                "\n" +
                "consecutive_events = " + startRangeTime + "\n" +
                "\n";


        if (options1.getLine1() != null) {

            String val1 = buildEventTaskValForFlux("_l1", startRangeTime, sid,
                    options1.getLine1().get(0),
                    options1.getLine1().get(1),
                    options1.getLine1().get(2));

            vals.add(val1);
            valsOption1.add("val_l1");
        }

        if (options1.getLine2() != null) {

            String val2 = buildEventTaskValForFlux("_l2", startRangeTime, sid,
                    options1.getLine2().get(0),
                    options1.getLine2().get(1),
                    options1.getLine2().get(2));
            vals.add(val2);
            valsOption1.add("val_l2");
        }

        if (options1.getLine3() != null) {
            String val3 = buildEventTaskValForFlux("_l3", startRangeTime, sid,
                    options1.getLine3().get(0),
                    options1.getLine3().get(1),
                    options1.getLine3().get(2));
            vals.add(val3);
            valsOption1.add("val_l3");

        }

        if (options1.getLine4() != null) {
            String val4 = buildEventTaskValForFlux("_l4", startRangeTime, sid,
                    options1.getLine4().get(0),
                    options1.getLine4().get(1),
                    options1.getLine4().get(2));
            vals.add(val4);
            valsOption1.add("val_l4");

        }


        if (options2.getLine1() != null) {
            String val7 = buildEventTaskValForFlux("_r1", startRangeTime, sid,
                    options2.getLine1().get(0),
                    options2.getLine1().get(1),
                    options2.getLine1().get(2));

            vals.add(val7);
            valsOption2.add("val_r1");

        }

        if (options2.getLine2() != null) {
            String val8 = buildEventTaskValForFlux("_r2", startRangeTime, sid,
                    options2.getLine2().get(0),
                    options2.getLine2().get(1),
                    options2.getLine2().get(2));

            vals.add(val8);
            valsOption2.add("val_r2");

        }

        if (options2.getLine3() != null) {
            String val9 = buildEventTaskValForFlux("_r3", startRangeTime, sid,
                    options2.getLine3().get(0),
                    options2.getLine3().get(1),
                    options2.getLine3().get(2));
            vals.add(val9);
            valsOption2.add("val_r3");

        }

        if (options2.getLine4() != null) {
            String val10 = buildEventTaskValForFlux("_r4", startRangeTime, sid,
                    options2.getLine4().get(0),
                    options2.getLine4().get(1),
                    options2.getLine4().get(2));
            vals.add(val10);
            valsOption2.add("val_r4");

        }

        String Left = "";
        String Right = "";
        String values2 = "";
        if (options1.getCondition() != null) {

            if (options1.getCondition().equals(1)) {
                int size = valsOption1.size();
                if (size == 1) {
                    Left = "Ljoin = val_l1\n" +
                            "   |> rename(columns: {_field: \"_field_l1\", _value: \"_value_l1\"})\n\n";
                    values2 += "r[\"_value_l1\"] == 1";
                } else {
                    String tables = "";
                    for (int i = 0; i < size; i++) {
                        if (i == 0) values2 += "(";
                        if (i == size - 1) {
                            tables = tables + valsOption1.get(i);
                            values2 = values2 + "r[\"_value_" + String.format("l%d", i + 1) + "\"] == 1)";

                            if (i % 2 != 0) {
                                Left += "L" + (i) + (i + 1) + "= join(tables: {l" + (i) + ": " + valsOption1.get(i - 1) + ", l" + (i + 1) + ": " + valsOption1.get(i) + "}, on: [\"_time\"])\n\n";
                            } else {
                                Left += "L" + (i + 1) + (i + 2) + "= " + valsOption1.get(i) + "\n\n";
                            }

                            switch (i + 1) {
                                case 2:
                                    Left += "Ljoin = L12\n\n";
                                    break;
                                case 3:
                                    Left += "Ljoin = join(tables: {L12, L34}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_l3\", _value: \"_value_l3\"})\n\n";
                                    break;
                                case 4:
                                    Left += "Ljoin = join(tables: {L12, L34}, on: [\"_time\"])\n\n";
                                    break;
                                case 5:
                                    Left += "Ljoin = join(tables: {L12, L34, L56}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_l5\", _value: \"_value_l5\"})\n\n";
                                    break;
                                case 6:
                                    Left += "Ljoin = join(tables: {L12, L34, L56}, on: [\"_time\"])\n\n";
                                    break;
                            }
                        } else {
                            tables = tables + valsOption1.get(i) + ",";
                            values2 = values2 + "r[\"_value_" + String.format("l%d", i + 1) + "\"] == 1 and ";

                            if (i % 2 != 0) {
                                Left += "L" + (i) + (i + 1) + "= join(tables: {l" + (i) + ": " + valsOption1.get(i - 1) + ", l" + (i + 1) + ": " + valsOption1.get(i) + "}, on: [\"_time\"])\n\n";
                            }
                        }


                    }

                }

            }
            if (options1.getCondition().equals(2)) {
                int size = valsOption1.size();

                if (size == 1) {
                    Left = "Ljoin = val_l1\n" +
                            "   |> rename(columns: {_field: \"_field_l1\", _value: \"_value_l1\"})\n\n";
                    values2 += "r[\"_value_l1\"] == 1";
                } else {

                    String tables = "";
                    String tables2 = "";
                    for (int i = 0; i < size; i++) {
                        if (i == 0) values2 += "(";
                        if (i == size - 1) {
                            tables = tables + valsOption1.get(i);
                            tables2 = tables2 + String.format("d%d:", i) + " " + valsOption1.get(i);
                            values2 = values2 + "r[\"_value_" + String.format("l%d", i + 1) + "\"] == 1)";

                            if (i % 2 != 0) {
                                Left += "L" + (i) + (i + 1) + "= join(tables: {l" + (i) + ": " + valsOption1.get(i - 1) + ", l" + (i + 1) + ": " + valsOption1.get(i) + "}, on: [\"_time\"])\n\n";
                            } else {
                                Left += "L" + (i + 1) + (i + 2) + "= " + valsOption1.get(i) + "\n\n";
                            }

                            switch (i + 1) {
                                case 2:
                                    Left += "Ljoin = L12\n\n";
                                    break;
                                case 3:
                                    Left += "Ljoin = join(tables: {L12, L34}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_l3\", _value: \"_value_l3\"})\n\n";
                                    break;
                                case 4:
                                    Left += "Ljoin = join(tables: {L12, L34}, on: [\"_time\"])\n\n";
                                    break;
                                case 5:
                                    Left += "Ljoin = join(tables: {L12, L34, L56}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_l5\", _value: \"_value_l5\"})\n\n";
                                    break;
                                case 6:
                                    Left += "Ljoin = join(tables: {L12, L34, L56}, on: [\"_time\"])\n\n";
                                    break;
                            }
                        } else {
                            tables = tables + valsOption1.get(i) + ",";
                            tables2 = tables2 + String.format("d%d:", i) + " " + valsOption1.get(i) + ",";
                            values2 = values2 + "r[\"_value_" + String.format("l%d", i + 1) + "\"] == 1 or ";

                            if (i % 2 != 0) {
                                Left += "L" + (i) + (i + 1) + "= join(tables: {l" + (i) + ": " + valsOption1.get(i - 1) + ", l" + (i + 1) + ": " + valsOption1.get(i) + "}, on: [\"_time\"])\n\n";
                            }
                        }


                    }

                }

            }

        }

        if (options2.getCondition() != null) {


            if (options2.getCondition().equals(1)) {
                int size = valsOption2.size();
                if (size > 0) values2 += " and ";
                if (size == 1) {
                    Right = "Rjoin = val_r1\n" +
                            "   |> rename(columns: {_field: \"_field_r1\", _value: \"_value_r1\"})\n\n";
                    values2 += "r[\"_value_r1\"] == 1";
                } else {

                    String tables = "";
                    for (int i = 0; i < size; i++) {
                        if (i == 0) values2 += "(";
                        if (i == size - 1) {
                            tables = tables + valsOption2.get(i);
                            values2 = values2 + "r[\"_value_" + String.format("r%d", i + 1) + "\"] == 1)";

                            if (i % 2 != 0) {
                                Right += "R" + (i) + (i + 1) + "= join(tables: {r" + (i) + ": " + valsOption2.get(i - 1) + ", r" + (i + 1) + ": " + valsOption2.get(i) + "}, on: [\"_time\"])\n\n";
                            } else {
                                Right += "R" + (i + 1) + (i + 2) + "= " + valsOption2.get(i);
                            }

                            switch (i + 1) {
                                case 2:
                                    Right += "Rjoin = R12\n\n";
                                    break;
                                case 3:
                                    Right += "Rjoin = join(tables: {R12, R34}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_r3\", _value: \"_value_r3\"})\n\n";
                                    break;
                                case 4:
                                    Right += "Rjoin = join(tables: {R12, R34}, on: [\"_time\"])\n\n";
                                    break;
                                case 5:
                                    Right += "Rjoin = join(tables: {R12, R34, R56}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_r5\", _value: \"_value_r5\"})\n\n";
                                    break;
                                case 6:
                                    Right += "Rjoin = join(tables: {R12, R34, R56}, on: [\"_time\"])\n\n";
                                    break;
                            }
                        } else {
                            tables = tables + valsOption2.get(i) + ",";
                            values2 = values2 + "r[\"_value_" + String.format("r%d", i + 1) + "\"] == 1 and ";

                            if (i % 2 != 0) {
                                Right += "R" + (i) + (i + 1) + "= join(tables: {r" + (i) + ": " + valsOption2.get(i - 1) + ", r" + (i + 1) + ": " + valsOption2.get(i) + "}, on: [\"_time\"])\n\n";
                            }
                        }


                    }

                }

            }
            if (options2.getCondition().equals(2)) {
                int size = valsOption2.size();
                if (size > 0) values2 += " and ";
                if (size == 1) {
                    Right = "Rjoin = val_r1\n" +
                            "   |> rename(columns: {_field: \"_field_r1\", _value: \"_value_r1\"})\n\n";
                    values2 += "r[\"_value_r1\"] == 1";
                } else {

                    String tables = "";
                    for (int i = 0; i < size; i++) {
                        if (i == 0) values2 += "(";
                        if (i == size - 1) {
                            tables = tables + valsOption2.get(i);
                            values2 = values2 + "r[\"_value_" + String.format("r%d", i + 1) + "\"] == 1)";

                            if (i % 2 != 0) {
                                Right += "R" + (i) + (i + 1) + "= join(tables: {r" + (i) + ": " + valsOption2.get(i - 1) + ", r" + (i + 1) + ": " + valsOption2.get(i) + "}, on: [\"_time\"])\n\n";
                            } else {
                                Right += "R" + (i + 1) + (i + 2) + "= " + valsOption2.get(i);
                            }

                            switch (i + 1) {
                                case 2:
                                    Right += "Rjoin = R12\n\n";
                                    break;
                                case 3:
                                    Right += "Rjoin = join(tables: {R12, R34}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_r3\", _value: \"_value_r3\"})\n\n";
                                    break;
                                case 4:
                                    Right += "Rjoin = join(tables: {R12, R34}, on: [\"_time\"])\n\n";
                                    break;
                                case 5:
                                    Right += "Rjoin = join(tables: {R12, R34, R56}, on: [\"_time\"])\n   |> rename(columns: {_field: \"_field_r5\", _value: \"_value_r5\"})\n\n";
                                    break;
                                case 6:
                                    Right += "Rjoin = join(tables: {R12, R34, R56}, on: [\"_time\"])\n\n";
                                    break;
                            }
                        } else {
                            tables = tables + valsOption2.get(i) + ",";
                            values2 = values2 + "r[\"_value_" + String.format("r%d", i + 1) + "\"] == 1 or ";

                            if (i % 2 != 0) {
                                Right += "R" + (i) + (i + 1) + "= join(tables: {r" + (i) + ": " + valsOption2.get(i - 1) + ", r" + (i + 1) + ": " + valsOption2.get(i) + "}, on: [\"_time\"])\n\n";
                            }
                        }


                    }

                }

            }
        }

        String valuesQuery = " ";
        String unionVals = "";
        int i = 0;
        for (String s : vals) {
            i++;
            valuesQuery = valuesQuery + s;
            unionVals += "val" + i + ", ";
        }
        String userConfId = confId.toString();

        String checkId = userConfId.substring(0, 16);

        String join;
        if (Right.isEmpty()) {
            join = "Ljoin\n";
        } else {
            join = "join(tables: {Ljoin, Rjoin}, on: [\"_time\"])\n";
        }

        String flux2 = first2 + valuesQuery +
                "prev_alert = from(bucket: \"_monitoring\")\n"+
                "    |> range(start: -"+idleTimeAfterAlerting+"m, stop: now())\n"+
                "    |> filter(fn: (r) => r[\"_measurement\"] == \"statuses\")\n" +
                "    |> filter(fn: (r) => r[\"_source_measurement\"] == \""+v.getReferenceId().toString()+"_Events\")\n"+
                "    |> filter(fn: (r) => r[\"_check_name\"] == \""+name+"\")\n" +
                "    |> filter(fn: (r) => r[\"_field\"] == \"_status_value\")\n"+
                "    |> group()\n" +
                "    |> max()\n"+
                "    |> map(fn: (r) => ({r with _value: if r[\"_value\"] == 2.0 then 0.0 else 1.0, \"_measurement\": \""+v.getReferenceId().toString()+"_Events\", \"_field\": \""+name+"\"}))\n"+
                "    |> keep(columns: [\"_field\", \"_measurement\", \"_value\"])\n"+
                "\n"+
                Left + Right +
                "LR_join = "+join+"" +
                "    |> stateCount(fn: (r) => "+values2+", column: \"on\")\n" +
                "    |> filter(fn: (r) => r[\"on\"] <= consecutive_events)\n" +
                "    |> max(column: \"on\")\n" +
                "    |> map(fn: (r) => ({r with \"_value\": if r[\"on\"] == consecutive_events then 2.0 else 0.0,  \"_measurement\": \""+v.getReferenceId().toString()+"_Events\", \"_field\": \""+name+"\"}))\n" +
                "    |> keep(columns: [\"_time\",\"_field\",\"_measurement\",\"_value\"])\n\n" +
                "prev_alert_union = union(tables: [prev_alert, LR_join])\n"+
                "    |> group(columns: [\"_measurement\", \"_field\"])\n"+
                "    |> reduce(fn: (r, accumulator) => ({prod: r._value * accumulator.prod}), identity: {prod: 1.0})\n"+
                "\n"+
                "final_join = join(tables: {LR_join, prev_alert_union}, on: [\"_measurement\"])\n"+
                "    |> map(fn: (r) => ({r with\n" +
                "       \"_value\": r.prod,\n" +
                "       \"_time\": r._time,\n" +
                "       \"_field\": \"_status_value\"\n" +
//                "       \"_event_time\": r._time\n" +
                "    }))\n"+
                "    |> keep(columns: [\"_value\",\n" +
                "       \"_field\",\n" +
                "       \"_measurement\",\n" +
                "       \"_time\"\n" +
                "    ])\n"+

                "check = {\n" +
                "\t_check_id: \""+checkId+"\",\n" +
                "\t_check_name: \""+name+"\",\n" +
                "\t_type: \"custom\",\n" +
                "\ttags: {sid:\"" + v.getReferenceId() + "\",grp: \""+ userEventConf.getTargetGroup() +"\", nty:\" "+ 0 +" \"},\n" +
                "}\n" +
                "ok = (r) =>\n" +
                "\t(r[\"_status_value\"] == 0)\n" + // value
                "crit = (r) =>\n" +
                "\t(r[\"_status_value\"] == 2)\n" +
                "messageFn = (r) =>\n" +
                "\t(\""+userEventConf.getNotificationMessage()+ "\")\n" +
                "\n" +
                "final_join\n" +
                "\t|> v1[\"fieldsAsCols\"]()\n" +
                "\t|> monitor[\"check\"](\n" +
                "\t\tdata: check,\n" +
                "\t\tmessageFn: messageFn,\n" +
                "\t\tok: ok,\n" +
                "\t\tcrit: crit,\n" +
                "\t)";

        log.info(flux2);
        Task t = new Task();
        t.setFlux(flux2);
        t.setOrgID(orgID);
        t.setStatus(TaskStatusType.ACTIVE);


        Task task = influxDBClient.getTasksApi().createTask(t);

        userEventConf.setTaskId(task.getId());
        dataManager.save(userEventConf);

        influxDBClient.close();

    }

    public HashMap<String, Double> getEngineInfo(String sid) {


        HashMap<String, Double> engineInfo = new HashMap<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", sid);
        DataQuery dataQuery = getDataQueryOfMEMonitoring();
        try {
            String field;
            Double value;

            List<FluxTable> fluxTables = getFluxTables(dataQuery, params, false);
            if ((fluxTables != null) & (!fluxTables.isEmpty()))
                for (FluxTable fluxTable : fluxTables) {
                    List<FluxRecord> records = fluxTable.getRecords();
                    for (FluxRecord fluxRecord : records) {
                        field = (String) fluxRecord.getValueByKey("_field");
                        value = (Double) fluxRecord.getValueByKey("_value");
                        engineInfo.put(field, value);
                    }
                }

        } catch (Exception e) {
            log.error("Failed to get data", e);
        }

        return engineInfo;

    }

    public boolean testConnection(DataQStore dataStore) {

        boolean result = false;

        if (dataStore == null)
            return false;


        try {
            if (dataStore.getDatabaseType() == DatabaseType.INFLUXDB.toString()) {
                String token = appSetupService.GetAppSetup().getToken();
                String org = appSetupService.GetAppSetup().getOrganization();
                String databaseURL = dataStore.getDatabaseURL();

                InfluxDBClient influxDBClient = InfluxDBClientFactory.create(dataStore.getDatabaseURL(), token.toCharArray(), org);
                if (influxDBClient != null) {
                    HealthCheck healthCheck = influxDBClient.health();
                    if (healthCheck.getStatus() == HealthCheck.StatusEnum.PASS) {
                        result = true;
                    }
                }

                influxDBClient.close();
            }
        } catch (Exception e) {
            result = false;

            log.error(e.getMessage());
        }

        return result;
    }

    private double getfluxRecordValueByKey(FluxRecord fluxRecord, String key) {
        double value = 0.0;
        try {
            if (fluxRecord.getValueByKey(key) != null) {
                value = (double) fluxRecord.getValueByKey(key);
                value = new BigDecimal(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
            }
        } catch (Exception e) {
            value = 0.0;
        }

        return value;
    }

    private String getfluxRecordStringValueByKey(FluxRecord fluxRecord, String key) {
        String value = "";
        try {
            if (fluxRecord.getValueByKey(key) != null) {
                value = fluxRecord.getValueByKey(key).toString();
            }
        } catch (Exception e) {
            value = "";
        }

        return value;
    }


    private DataQuery getDataQueryOfVesselPosition() {
        DataQuery dataQuery = null;

        String queryName = "VESSEL_POSITION";
        Optional<DataQuery> query = dataManager.load(DataQuery.class)
                .query("select d from VIP_DataQuery d where d.name = :name")
                .parameter("name", queryName)
                .fetchPlan("dataQuery-view")
                .optional();
        if (query.isPresent()) {
            dataQuery = query.get();
        }

        return dataQuery;
    }

    private DataQuery getDataQueryOfFleetPosition() {
        DataQuery dataQuery = null;

        String queryName = "FLEET_POSITION";
        Optional<DataQuery> query = dataManager.load(DataQuery.class)
                .query("select d from VIP_DataQuery d where d.name = :name")
                .parameter("name", queryName)
                .fetchPlan("dataQuery-view")
                .optional();
        if (query.isPresent()) {
            dataQuery = query.get();
        }

        return dataQuery;
    }

    private DataQuery getDataQueryPortCalls() {
        DataQuery dataQuery = null;

        String queryName = "PORT_CALLS";
        Optional<DataQuery> query = dataManager.load(DataQuery.class)
                .query("select d from VIP_DataQuery d where d.name = :name")
                .parameter("name", queryName)
                .fetchPlan("dataQuery-view")
                .optional();
        if (query.isPresent()) {
            dataQuery = query.get();
        }

        return dataQuery;
    }


    private DataQuery getDataQueryOfBucketsNames() {
        DataQuery dataQuery = null;

        String queryName = "FIND_SHIPS_NAME";
        Optional<DataQuery> query = dataManager.load(DataQuery.class)
                .query("select d from VIP_DataQuery d where d.name = :name")
                .parameter("name", queryName)
                .fetchPlan("dataQuery-view")
                .optional();
        if (query.isPresent()) {
            dataQuery = query.get();
        }

        return dataQuery;
    }


    private DataQuery getDataQueryOfMEMonitoring() {
        DataQuery dataQuery = null;

        String queryName = "ME_MONITORING";
        Optional<DataQuery> query = dataManager.load(DataQuery.class)
                .query("select d from VIP_DataQuery d where d.name = :name")
                .parameter("name", queryName)
                .fetchPlan("dataQuery-view")
                .optional();
        if (query.isPresent()) {
            dataQuery = query.get();
        }

        return dataQuery;
    }

    private InfluxObject getInfluxDetailsForVessel(String sid) {
        InfluxObject influxObject = metadata.create(InfluxObject.class);

        try {
            Optional<Vessel> vesselOptional = dataManager.load(Vessel.class)
                    .query("select v from VIP_Vessel v where v.imo = :imo")
                    .parameter("imo", sid)
                    .optional();
            if (vesselOptional.isPresent()) {
                if (!StringUtils.isNullOrEmpty(vesselOptional.get().getOrganization())) {
                    influxObject.setOrganization(vesselOptional.get().getOrganization());
                }

                if (!StringUtils.isNullOrEmpty(vesselOptional.get().getToken())) {
                    influxObject.setToken(vesselOptional.get().getToken());
                }
            }
        } catch (Exception e) {
            log.error("Cannot fetch vessel's influx data from database");
        }


        return influxObject;
    }

    private String buildEventTaskValForFlux(String number, String startRangeTime, String sid,
                                            String field, String condition, String value) {
        String derivative = "";

        Optional<CommonTag> commonTagOptional = dataManager.load(CommonTag.class)
                .query("select ct from VIP_CommonTag ct where ct.tag = :tag")
                .parameter("tag", field)
                .optional();

        if (commonTagOptional.isPresent()) {
            if (commonTagOptional.get().getTotalizer().equals("t")) derivative = "    |> derivative(unit: 1h)\n";
        }

        String val2 = "val" + number + " = from(bucket: \"B_" + sid + "\")\n" +
                "    |> range(start: -10m, stop: now())\n" +
                "    |> filter(fn: (r) => r[\"_measurement\"] == \"iot_data\")\n" +
                "    |> drop(columns: [\"01010101\",\"02010101\",\"02020101\",\"02030101\",\"02040101\",\"03010101\",\"03020101\"])\n" +
                "    |> filter(fn: (r) => r[\"_field\"] == \"" + field + "\")\n" +
                "    |> keep(columns: [\"_time\", \"_field\", \"_value\"])\n" +
//                "    |> derivative(unit: 1h)\n"+
                derivative +
                "    |> map(fn: (r) => ({r with _value: if r[\"_value\"] " + condition + " " + value + " then 1 else 0}))\n" +
                "    |> toFloat()\n" +
                "\n";

        return val2;
    }

    private String commentsExtractor(String expression) {
        String result = "";

        //result = expression.replaceAll("//\\w+$[\\n\\r\\t]*?",System.lineSeparator());
        result = expression.replaceAll("//(.)*?//", System.lineSeparator());

        return result;
    }


}
