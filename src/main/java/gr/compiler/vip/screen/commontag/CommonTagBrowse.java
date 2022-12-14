package gr.compiler.vip.screen.commontag;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.CommonTag;

@UiController("VIP_CommonTag.browse")
@UiDescriptor("common-tag-browse.xml")
@LookupComponent("commonTagsTable")
public class CommonTagBrowse extends StandardLookup<CommonTag> {
}