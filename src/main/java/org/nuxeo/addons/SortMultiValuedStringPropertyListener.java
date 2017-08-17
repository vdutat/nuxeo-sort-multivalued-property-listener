package org.nuxeo.addons;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class SortMultiValuedStringPropertyListener implements EventListener {

    protected static final String PROPERTY_NAME = "supnxp-20933:stringlist";

    private static final Log LOGGER = LogFactory.getLog(SortMultiValuedStringPropertyListener.class);

    @Override
    public void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        DocumentEventContext docCtx = (DocumentEventContext) ctx;
        DocumentModel doc = docCtx.getSourceDocument();
        DocumentModel previousDoc = getBeforeUpdateDocument(docCtx);
        if (DocumentEventTypes.ABOUT_TO_CREATE.equals(event.getName()) || propertyChanged(PROPERTY_NAME, previousDoc, doc)) {
            List<String> list = Arrays.asList((String[]) doc.getPropertyValue(PROPERTY_NAME));
            Collections.sort(list);
            doc.setPropertyValue(PROPERTY_NAME, (Serializable) list);
        }

        // Add some logic starting from here.
    }

    protected boolean propertyChanged(String string, DocumentModel previousDoc, DocumentModel doc) {
        List<String> list = Arrays.asList((String[]) doc.getPropertyValue(PROPERTY_NAME));
        List<String> previousList = Arrays.asList((String[]) previousDoc.getPropertyValue(PROPERTY_NAME));
        if (list.size() != previousList.size()) {
            return true;
        } else {
            list.removeAll(previousList);
            if (list.size() > 0) {
                return true;
            }
        }
        return false;
    }

    protected DocumentModel getBeforeUpdateDocument(DocumentEventContext context) throws ClientException {
        if (LOGGER.isDebugEnabled()) {
            Map<String, Serializable> properties = context.getProperties();
            for (Entry<String,Serializable> entry : properties.entrySet()) {
                LOGGER.debug("Event context data:" + entry.getKey());
            }
        }
        return (DocumentModel) context.getProperty(CoreEventConstants.PREVIOUS_DOCUMENT_MODEL);
    }
}
