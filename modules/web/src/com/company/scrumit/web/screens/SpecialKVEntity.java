package com.company.scrumit.web.screens;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.data.impl.AbstractCollectionDatasource;

@com.haulmont.chile.core.annotations.MetaClass(name = "scrumit$SpecialKVEntity")
public class SpecialKVEntity extends KeyValueEntity {

    private AbstractCollectionDatasource datasource;

    SpecialKVEntity(AbstractCollectionDatasource ds) {
        datasource = ds;
    }

    /**
     * Sets a meta-class for this entity instance.
     *
     * @param metaClass - игнорируется
     */
    @Override
    public void setMetaClass(MetaClass metaClass) {
    }

    @Override
    public MetaClass getMetaClass() {
        MetaClass metaClass = datasource.getMetaClass();
        metaClass.getProperties().clear();
        return metaClass;
    }

}
