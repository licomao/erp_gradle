package com.daqula.carmore.util;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public class FreeMarkerUtil {

    public static TemplateHashModel getAuthorityModel() {
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        TemplateHashModel fileStatics = null;
        try {
            fileStatics = (TemplateHashModel) staticModels.get("com.daqula.carmore.AuthorityConst");
        } catch (TemplateModelException e) {
            throw new RuntimeException("AuthorityConst is not found in classpath");
        }
        return fileStatics;
    }
}
