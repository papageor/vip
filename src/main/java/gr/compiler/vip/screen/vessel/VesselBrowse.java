package gr.compiler.vip.screen.vessel;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.Vessel;

@UiController("VIP_Vessel.browse")
@UiDescriptor("vessel-browse.xml")
@LookupComponent("vesselsTable")
public class VesselBrowse extends StandardLookup<Vessel> {
}