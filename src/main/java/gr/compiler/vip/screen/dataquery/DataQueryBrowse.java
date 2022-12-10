package gr.compiler.vip.screen.dataquery;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.DataQuery;

@UiController("VIP_DataQuery.browse")
@UiDescriptor("data-query-browse.xml")
@LookupComponent("dataQueriesTable")
public class DataQueryBrowse extends StandardLookup<DataQuery> {
}