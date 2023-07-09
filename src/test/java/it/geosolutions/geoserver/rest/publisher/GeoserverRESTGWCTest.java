package it.geosolutions.geoserver.rest.publisher;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Arjen10
 * @date 2023/7/6 22:29
 */
@Slf4j
public class GeoserverRESTGWCTest extends GeoserverRESTTest {

    private final String testWorkspace = "nurc";

    private final String testLayerName = "Pk50095";

    @Test
    public void sendTask() {
        //nurc:Pk50095
        boolean b = gwcPublisher.gwcSendTask(testWorkspace, testLayerName);
        Assert.assertTrue(b);
    }

    @Test
    public void gwcGetTasksPercentage() {
        double v = gwcReader.gwcRunningTasksPercentage(testWorkspace, testLayerName);
        System.out.println(v);
    }

}
