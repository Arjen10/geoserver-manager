package it.geosolutions.geoserver.rest.gwc.tile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Arjen10
 * @date 2023/7/6 22:29
 * @since 1.8.0
 */
public class GWCExecutingTasks {

    @Getter
    @JsonProperty("long-array-array")
    private List<TaskStatus> longArrayArray;


    public void setLongArrayArray(List<List<Integer>> taskStatusList) {
        longArrayArray = taskStatusList.stream()
                .map(TaskStatus::new)
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    public static class TaskStatus {

        private long tilesProcessed;

        private long totalTilesToProcess;

        /**
         * Unit seconds
         */
        private long expectedRemainingTime;

        private TaskStatusEnum status;

        private long id;

        /**
         * [已处理的瓦片，要处理的瓦片总数，预计剩余时间（秒），任务ID，任务状态]
         */
        public TaskStatus(List<Integer> array) {
            if (array.size() != 5) {
                throw new IllegalArgumentException("abnormal task status , array = " + array);
            }
            tilesProcessed = array.get(0);
            totalTilesToProcess = array.get(1);
            expectedRemainingTime = array.get(2);
            id = array.get(3);
            status = TaskStatusEnum.getByStatusCode(array.get(4));
        }

    }

    @AllArgsConstructor
    public enum TaskStatusEnum {

        ABORTED(-1),

        PENDING(0),

        RUNNING(1),

        DONE(2);

        @Getter
        @JsonValue
        private final int statusCode;

        public static TaskStatusEnum getByStatusCode(Integer statusCode) {
            for (TaskStatusEnum value : TaskStatusEnum.values()) {
                if (value.statusCode == statusCode) {
                    return value;
                }
            }
            throw new IllegalArgumentException("status code not found. status code = " + statusCode);
        }

    }

}
