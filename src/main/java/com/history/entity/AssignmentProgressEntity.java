package com.history.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 班级任务进度实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "classroom_progress", indexes = {
    @Index(name = "idx_progress_assignment", columnList = "assignment_id"),
    @Index(name = "idx_progress_student", columnList = "student_name")
})
public class AssignmentProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long assignmentId;

    @Column(nullable = false, length = 100)
    private String studentName;

    @ElementCollection
    @CollectionTable(name = "progress_completed_nodes", joinColumns = @JoinColumn(name = "progress_id"))
    @Column(name = "node_id", length = 100)
    @Builder.Default
    private List<String> completedNodes = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime lastActiveAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
