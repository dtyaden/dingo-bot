package dingov2.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dingov2.db.objects.MusicFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

public class DingoDB {

    public static void main(String args[]){
        MongoClient client = null;
        try {
            client = new MongoClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (client == null){
            System.out.println("failed to connect");
            return;
    }

        MongoDatabase database = client.getDatabase("dingo");
        MongoCollection collection = database.getCollection("musicFile");

        System.out.println(collection.getNamespace());

        Datastore dataStore = buildMorphia(client);

        MusicFile file = getTestMusicFile(dataStore);
        File trackFile = FileUtils.getFile("/src/resources/sounds/" + file.getBaseName());
        System.out.println("before insertion and increment: " + file);
        System.out.println(trackFile.getAbsolutePath());

        file.increasePlayCount();
        dataStore.save(file);

        file = getTestMusicFile(dataStore);

        System.out.println("after save and reloaded: " + file);
    }

    public static Datastore buildMorphia(MongoClient client){
        Morphia morphia = new Morphia();
        morphia.mapPackage("dingov2.db.objects");
        Datastore dataStore = morphia.createDatastore(client, "dingo");
        dataStore.ensureIndexes();
        return dataStore;
    }

    public static MusicFile getTestMusicFile(Datastore dataStore){
        Query<MusicFile> query = dataStore.createQuery(MusicFile.class);

        System.out.println(query.find().toList());

        List<MusicFile> files = query.field("name").contains("test").find().toList();
        MusicFile file = files.get(0);
        return file;
    }
}
