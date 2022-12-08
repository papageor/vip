package gr.compiler.vip.app;

import com.influxdb.query.FluxTable;
import gr.compiler.vip.entity.Alert;
import gr.compiler.vip.entity.DataQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("VIP_InfluxLogService")
public class InfluxLogService {
    public void registerQuery(DataQuery dataQuery, String org, String token, String query, List<FluxTable> fluxTables, String error) {

    }

    public void registerQueryAlert(Alert dataQuery, String org, String token, String query, List<FluxTable> fluxTables, String error) {

    }
}