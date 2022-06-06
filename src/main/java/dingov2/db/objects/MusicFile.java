package dingov2.db.objects;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity("musicFile")
public class MusicFile {
    @Id
    private ObjectId id;
    private String name;
    private String baseName;
    private long plays;

    public MusicFile(ObjectId id, String name, String baseName, long plays){

        this.id = id;
        this.name = name;
        this.baseName = baseName;
        this.plays = plays;
    }

    public MusicFile(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPlays() {
        return plays;
    }

    public void setPlays(long plays) {
        this.plays = plays;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public void increasePlayCount(){
        setPlays(getPlays() + 1);
    }

    public String toString(){
        return getId() + ", " + getBaseName() + ", " + getPlays();
    }
}
