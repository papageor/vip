package gr.compiler.vip.screen.vessel;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.Vessel;

@UiController("VIP_Vessel.edit")
@UiDescriptor("vessel-edit.xml")
@EditedEntityContainer("vesselDc")
public class VesselEdit extends StandardEditor<Vessel> {
}