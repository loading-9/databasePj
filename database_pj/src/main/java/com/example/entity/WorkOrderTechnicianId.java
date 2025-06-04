package com.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class WorkOrderTechnicianId implements Serializable {
    private Long workOrderId;
    private Long technicianId;

    // 必须要有无参构造函数
    public WorkOrderTechnicianId() {}

    public WorkOrderTechnicianId(Long workOrderId, Long technicianId) {
        this.workOrderId = workOrderId;
        this.technicianId = technicianId;
    }

    // 重写 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkOrderTechnicianId)) return false;
        WorkOrderTechnicianId that = (WorkOrderTechnicianId) o;
        return Objects.equals(workOrderId, that.workOrderId) &&
                Objects.equals(technicianId, that.technicianId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workOrderId, technicianId);
    }
}
