package gr.compiler.vip.screen.dataquery;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.DataQuery;

@UiController("VIP_DataQuery.edit")
@UiDescriptor("data-query-edit.xml")
@EditedEntityContainer("dataQueryDc")
public class DataQueryEdit extends StandardEditor<DataQuery> {
}