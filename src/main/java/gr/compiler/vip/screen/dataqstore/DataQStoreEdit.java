package gr.compiler.vip.screen.dataqstore;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.DataQStore;

@UiController("VIP_DataQStore.edit")
@UiDescriptor("data-q-store-edit.xml")
@EditedEntityContainer("dataQStoreDc")
public class DataQStoreEdit extends StandardEditor<DataQStore> {
}