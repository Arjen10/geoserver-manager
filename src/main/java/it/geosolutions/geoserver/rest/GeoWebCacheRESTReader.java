package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.gwc.tile.GWCExecutingTasks;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTAbstractManager;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.List;

/**
 * @author Arjen10
 * @date 2023/7/9 17:11
 */
@Slf4j
public class GeoWebCacheRESTReader extends GeoServerRESTAbstractManager {

    /**
     * Default constructor.
     * <p>
     * Indicates connection parameters to remote GeoServer instance.
     *
     * @param restURL  GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     */
    public GeoWebCacheRESTReader(URL restURL, String username, String password) throws IllegalArgumentException {
        super(restURL, username, password);
    }

    /**
     * <a href="https://www.geowebcache.org/docs/current/rest/seed.html#querying-the-running-tasks">api explain</a>
     *
     * @param workspace workSpace
     * @param layerName layerName
     * @return this layer list of currently executing tasks
     * @since 1.8.0
     */
    public GWCExecutingTasks gwcQueryTheRunningTasks(String workspace, String layerName) {
        String url = gsBaseUrl + "/gwc/rest/seed/" + Util.encodeUrl(String.format("%s:%s", workspace, layerName)) + ".json";
        String resJson = HTTPUtils.get(url, gsuser, gspass);
        return JacksonUtil.parseObject(resJson, GWCExecutingTasks.class);
    }

    /**
     *
     * get gwc run task percentage by layerName
     *
     * @param workspace workSpace
     * @param layerName layerName
     * @return tasks percentage, -1 is ended
     * @since 1.8.0
     */
    public double gwcRunningTasksPercentage(String workspace, String layerName) {
        GWCExecutingTasks gwcExecutingTasks = gwcQueryTheRunningTasks(workspace, layerName);
        List<GWCExecutingTasks.TaskStatus> longArrayArray = gwcExecutingTasks.getLongArrayArray();
        double total = longArrayArray.stream()
                .mapToDouble(GWCExecutingTasks.TaskStatus::getTotalTilesToProcess)
                .sum();
        double totalTilesProcessed = longArrayArray.stream()
                .mapToDouble(GWCExecutingTasks.TaskStatus::getTilesProcessed)
                .sum();
        double percentage = totalTilesProcessed / total;
        return Double.isNaN(percentage) ? -1 : percentage * 100;
    }

}
