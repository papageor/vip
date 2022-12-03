package gr.compiler.vip.screen.vesseltype;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.VesselType;

@UiController("VIP_VesselType.browse")
@UiDescriptor("vessel-type-browse.xml")
@LookupComponent("vesselTypesTable")
public class VesselTypeBrowse extends StandardLookup<VesselType> {
}