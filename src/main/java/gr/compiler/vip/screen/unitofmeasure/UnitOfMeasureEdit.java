package gr.compiler.vip.screen.unitofmeasure;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.UnitOfMeasure;

@UiController("VIP_UnitOfMeasure.edit")
@UiDescriptor("unit-of-measure-edit.xml")
@EditedEntityContainer("unitOfMeasureDc")
public class UnitOfMeasureEdit extends StandardEditor<UnitOfMeasure> {
}