package it.geosolutions.geoserver.rest.gwc;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.geosolutions.geoserver.rest.gwc.tile.format.GWCTileFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 切片任务的封装类
 *
 * @author Arjen10
 * @since 1.8.0
 */
@Data
public class GWCSeedTask {

    private TaskFromDto seedRequest = new TaskFromDto();

    public GWCSeedTask(String workSpace, String layerName) {
        seedRequest.setName(workSpace, layerName);
    }

    public GWCSeedTask(String workSpace, String layerName, double[] bounds) {
        seedRequest.setName(workSpace, layerName);
        seedRequest.bounds = new Bounds(bounds);
    }

    public GWCSeedTask(String workSpace, String layerName, GWCTileFormat format, double[] bounds) {
        seedRequest.setName(workSpace, layerName);
        seedRequest.bounds = new Bounds(bounds);
        seedRequest.format = format;
    }

    /**
     * 参数封装对象
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static public class TaskFromDto {

        /**
         * name = workspace:layerName
         */
        private String name;

        /**
         * EPSG CODE
         */
        private SRS srs = new SRS(4326);

        /**
         * 这个字段可以不要，要的话需要自己通过构造器传参设置
         */
        private Bounds bounds;

        /**
         * 从第几层开始 默认从第0层开始切
         */
        private int zoomStart = 0;

        /**
         * 切到第几层 默认切到15层
         */
        private int zoomStop = 15;

        /**
         * 切出来的格式，默认png。
         */
        @Getter(AccessLevel.NONE)
        private GWCTileFormat format = new GWCTileFormat.PNGTileFormat();

        /**
         * 切片的策略，这里默认是补充缺少的瓦片
         */
        private GWCTileTypeEnum type = GWCTileTypeEnum.SEED;

        /**
         * 切片时候的线程，默认1个线程
         */
        private int threadCount = 1;

        @JsonGetter
        public String getFormat() {
            return format.tileFormat();
        }

        public void setName(String workSpace, String layerName) {
            this.name = String.format("%s:%s", workSpace, layerName);
        }

    }

    /**
     * 参考坐标系
     */
    @Data
    @AllArgsConstructor
    static public class SRS {

        /**
         * 默认参考坐标系 4326
         */
        private int number;

    }

    /**
     * 边界
     */
    @Data
    static public class Bounds {

        /**
         * 数据在参考坐标系中的位置
         */
        private Map<String, double[]> coords;

        //示例 "double":["-124.0","22.0","66.0","72.0"]
        //bbox  [Xmin, Ymin, Xmax, Ymax]
        public Bounds(double[] bbox) {
            Map<String, double[]> map = new HashMap<>(1);
            map.put("double", bbox);
            this.coords = map;
        }

    }

    /**
     * 切片策略枚举
     *
     * @author Arjen10
     * @since 1.8.0
     */
    public enum GWCTileTypeEnum {

        /**
         * 补充缺少的瓦片
         */
        SEED,

        /**
         * 全部从新开始切
         */
        RESEED,

        /**
         * 删掉所有的瓦片
         */
        TRUNCATE;

    }

}

