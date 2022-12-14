package gr.compiler.vip.screen.commontag;

import io.jmix.ui.screen.*;
import gr.compiler.vip.entity.CommonTag;

@UiController("VIP_CommonTag.edit")
@UiDescriptor("common-tag-edit.xml")
@EditedEntityContainer("commonTagDc")
public class CommonTagEdit extends StandardEditor<CommonTag> {
}