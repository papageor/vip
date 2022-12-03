package gr.compiler.vip.screen.vesseltype;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.VesselType;

@UiController("VIP_VesselType.edit")
@UiDescriptor("vessel-type-edit.xml")
@EditedEntityContainer("vesselTypeDc")
public class VesselTypeEdit extends StandardEditor<VesselType> {
}