package gr.compiler.vip.screen.unitofmeasure;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.UnitOfMeasure;

@UiController("VIP_UnitOfMeasure.browse")
@UiDescriptor("unit-of-measure-browse.xml")
@LookupComponent("unitOfMeasuresTable")
public class UnitOfMeasureBrowse extends StandardLookup<UnitOfMeasure> {
}