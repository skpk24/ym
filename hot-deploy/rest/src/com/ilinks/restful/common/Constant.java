package com.ilinks.restful.common;

import org.ofbiz.entity.util.EntityFindOptions;


public class Constant {
    static public String PROPERTY_FILE_NAME="restful.properties";
    public static final EntityFindOptions readonly = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
}