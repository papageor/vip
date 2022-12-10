package gr.compiler.vip.screen.dataqstore;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.DataQStore;

@UiController("VIP_DataQStore.browse")
@UiDescriptor("data-q-store-browse.xml")
@LookupComponent("dataQStoresTable")
public class DataQStoreBrowse extends StandardLookup<DataQStore> {
}