package gr.compiler.vip.screen.fragments.vesselfragment;

import gr.compiler.vip.entity.Vessel;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("VIP_VesselFragment")
@UiDescriptor("vessel-fragment.xml")
public class VesselFragment extends ScreenFragment {
    @Autowired
    private CollectionLoader<Vessel> vesselsDl;

    @Subscribe(target = Target.PARENT_CONTROLLER)
    public void onBeforeShow(Screen.BeforeShowEvent event) {
        vesselsDl.load();
    }

}