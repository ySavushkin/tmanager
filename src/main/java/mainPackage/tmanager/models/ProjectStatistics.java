//package mainPackage.tmanager.models;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.checkerframework.checker.units.qual.C;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Entity
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//@Table(name = "project_statistics")
//public class ProjectStatistics {
//    @Id
//    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//    @Column(name = "project_name")
//
//    @OneToOne
//    @JoinColumn(name = "project_id",referencedColumnName = "id")
//    private Project project;
//    @Column(name = "start_date")
//    private LocalDateTime startDate;
//    @Column(name = "end_date")
//    private LocalDateTime endDate;
//    @Column(name = "achieved_goals")
//    private String achievedGoals;
//    @Column(name = "retrospective")
//    private String retrospectiveNotes;
//}
