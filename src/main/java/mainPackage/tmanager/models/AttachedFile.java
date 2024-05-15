package mainPackage.tmanager.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attached_file")
public class AttachedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "file_type")
    private String fileType;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}



