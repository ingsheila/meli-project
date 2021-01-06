package com.meli.project.meliproject.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Connection {

    public static void main(String[] args) {

        String connectionString = "mongodb+srv://mongodbss:a5B6kRL16tB9LkOD@cluster0.hrc0b.mongodb.net/stats?retryWrites=true&w=majority";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
            databases.forEach(db -> System.out.println(db.toJson()));
            MongoDatabase statsDB = mongoClient.getDatabase("stats");
            MongoCollection<Document> executionsCollection = statsDB.getCollection("executions");

            Bson filter = eq("country", "PROMEDIO");
            Document executionTotal = executionsCollection.find(filter).first();

            int invocations = 2;
            int distance = 500;
            int total = invocations*distance;
            Document execution = new Document("_id", new ObjectId());
            execution.append("total", total)
                    .append("country", "Brasil")
                    .append("distance", distance)
                    .append("invocations",invocations);
            executionsCollection.insertOne(execution);

            int x = executionTotal.getInteger("invocations")+invocations;
            int totalDistance =(executionTotal.getInteger("total")+total) / x;
            Bson update1 = inc("invocations", invocations);
            Bson update2 = set("distance", totalDistance );
            Bson update3 = inc("total", total);
            Bson updates = combine(update1,update2,update3);
            FindOneAndUpdateOptions optionAfter = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
            executionTotal = executionsCollection.findOneAndUpdate(filter, updates,optionAfter);

           /* Document student1 = executionsCollection.find(new Document("country", "Argentina")).first();
            Bson filter = eq("country", "Brasil");
            Bson update = inc("invocations", 1);
            // si no lo encuentra lo crea
            UpdateOptions options = new UpdateOptions().upsert(true);
           executionsCollection.updateOne(filter,update,options);

            MongoCollection<Document> statsCollection = statsDB.getCollection("stats");
            FindOneAndUpdateOptions optionAfter = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
            Bson filter1 = eq("identifier", 1);
            Bson update1 = set("closest_distance", 2);
            Bson update2 = set("farthest_distance", 2);
            Bson updates = combine(update1, update2);
        // returns the old version of the document before the update.
            Document newVersion = statsCollection.findOneAndUpdate(filter1, updates,optionAfter);*/
        }
    }
}
