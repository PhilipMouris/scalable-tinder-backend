package Models;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "blocks")
@IdClass(BlocksData.class)
public class BlocksData implements Serializable{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    @Column
    private int source_user_id;
    @Column
    private int target_user_id;
    @Column
    private Timestamp created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSource_user_id() {
        return source_user_id;
    }

    public void setSource_user_id(int source_user_id) {
        this.source_user_id = source_user_id;
    }

    public int getTarget_user_id() {
        return target_user_id;
    }

    public void setTarget_user_id(int target_user_id) {
        this.target_user_id = target_user_id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
