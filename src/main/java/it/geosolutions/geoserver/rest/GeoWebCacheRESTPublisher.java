package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.gwc.GWCSeedTask;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTAbstractManager;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * @author Arjen10
 * @date 2023/7/9 17:02
 */
@Slf4j
public class GeoWebCacheRESTPublisher extends GeoServerRESTAbstractManager {

    public GeoWebCacheRESTPublisher(URL restURL, String username, String password) {
        super(restURL, username, password);
    }

    /**
     *
     * <a href="https://www.geowebcache.org/docs/current/rest/seed.html#seeding-and-truncating-through-the-rest-api">
     *     send task to GeoWebCache</a>
     *
     * @param workspace workSpace
     * @param layerName layerName
     * @return whether the task has been accepted
     * @since 1.8.0
     */
    public boolean gwcSendTask(String workspace, String layerName) {
        GWCSeedTask gwcSeedTask = new GWCSeedTask(workspace, layerName);
        return gwcSendTask(gwcSeedTask);
    }

    /**
     *
     * <a href="https://www.geowebcache.org/docs/current/rest/seed.html#seeding-and-truncating-through-the-rest-api">
     *     send task to GeoWebCache</a>
     *
     * @param gwcSeedTask send task param
     * @return whether the task has been accepted
     * @since 1.8.0
     */
    public boolean gwcSendTask(GWCSeedTask gwcSeedTask) {
        String jsonString = JacksonUtil.toJSONString(gwcSeedTask);
        String url = gsBaseUrl + "/gwc/rest/seed/" + Util.encodeUrl(gwcSeedTask.getSeedRequest().getName()) + ".json";
        HTTPUtils.post(url, jsonString, "application/json", gsuser, gspass);
        return true;
    }

}
