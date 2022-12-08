package gr.compiler.vip.screen.chartwidget;

import gr.compiler.vip.entity.CommonTag;
import gr.compiler.vip.entity.DateValueVolume;
import gr.compiler.vip.entity.Vessel;
import io.jmix.core.Metadata;
import io.jmix.core.TimeSource;
import io.jmix.dashboards.model.Widget;
import io.jmix.dashboardsui.annotation.DashboardWidget;
import io.jmix.dashboardsui.annotation.WidgetParam;
import io.jmix.ui.WindowParam;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@UiController("VIP_ChartWidget")
@UiDescriptor("chart-widget.xml")
@DashboardWidget(name = "LineChart",editFragmentId = "VIP_VesselWidgetEditor")
public class ChartWidget extends ScreenFragment {
    private static final int DAYS_COUNT = 20;

    @Autowired
    private CollectionContainer<DateValueVolume> lineChartDc;
    @Autowired
    private Metadata metadata;
    @Autowired
    private TimeSource timeSource;

    @WindowParam
    @WidgetParam
    protected Vessel vessel;

    @WindowParam
    @WidgetParam
    protected CommonTag commonTag;


    @WindowParam
    protected Widget widget;

    private Random random = new Random();

    @Subscribe
    public void onAfterInit(AfterInitEvent event) {
        List<DateValueVolume> items = new ArrayList<>();
        Date startDate = DateUtils.addDays(timeSource.currentTimestamp(), -DAYS_COUNT);
        for (int i = 0; i < DAYS_COUNT; i++) {
            items.add(generateDateValueVolume(DateUtils.addDays(startDate, i), i));
        }
        lineChartDc.setItems(items);
    }

    private DateValueVolume generateDateValueVolume(Date date, int i) {
        Long value = Math.round(random.nextDouble() * (20 + i)) + 20 + i;
        Long volume = Math.round(random.nextDouble() * (20 + i)) + i;
        return dateValueVolume(date, value, volume);
    }

    private DateValueVolume dateValueVolume(Date date, Long value, Long volume) {
        DateValueVolume dateValueVolume = metadata.create(DateValueVolume.class);
        dateValueVolume.setId(UUID.randomUUID());
        dateValueVolume.setDate(date);
        dateValueVolume.setValue(value);
        dateValueVolume.setVolume(volume);
        return dateValueVolume;
    }
    
}