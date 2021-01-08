package com.meli.project.meliproject.service.impl;

import com.meli.project.meliproject.model.Execution;
import com.meli.project.meliproject.service.IMongoDBService;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Service("mongoDBService")
public class MongoDBServiceImpl implements IMongoDBService {

    @Value("${mongodb.url.connection}")
    private String mongoDBUrlConnection;

    private Logger logger;
    private static final String COUNTRY_FIELD = "country";
    private static final String DISTANCE_FIELD = "distance";
    private static final String TOTAL_FIELD = "total";
    private static final String INVOCATIONS_FIELD = "invocations";
    private static final String EXECUTIONS_COLLECTION_NAME = "executions";

    @PostConstruct
    public void init() {

        this.logger = LoggerFactory.getLogger(this.getClass());
        startConnection();
    }

    private MongoClient mongoClient;

    private void startConnection() {

        ConnectionString connectionString = new ConnectionString(mongoDBUrlConnection);
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .build();

        try {
            this.mongoClient = MongoClients.create(settings);
        } catch (Exception e) {
            logger.error("MELI-PROJECT : No se ha podido establecer conexion con la BD. ");
        }
    }

    @Override
    public Execution getAverageExecutions() {

        return getExecutionsCollection().aggregate(
                Arrays.asList(group(null, Accumulators.avg(TOTAL_FIELD, "$total")))).first();
    }

    @Override
    public void saveOrUpdateExecution(Execution execution) {

        Bson filter = eq(COUNTRY_FIELD, execution.getCountry());
        Bson update1 = inc(INVOCATIONS_FIELD, 1);
        Bson update2 = set(DISTANCE_FIELD, execution.getDistance());
        Bson update3 = inc(TOTAL_FIELD, execution.getDistance());
        Bson updates = combine(update1, update2, update3);
        UpdateOptions options = new UpdateOptions().upsert(true);
        getExecutionsCollection().updateOne(filter, updates, options);
    }

    @Override
    public Execution getClosestDistance() {

        Bson project = project(Projections.fields(Projections.excludeId(),
                Projections.include(COUNTRY_FIELD, DISTANCE_FIELD)));
        Execution execution = getExecutionsCollection().aggregate(
                Arrays.asList(group(null, Accumulators.min(DISTANCE_FIELD, "$distance")))).first();

        return getExecutionsCollection().aggregate(Arrays.asList(project,
                match(Filters.eq(DISTANCE_FIELD, execution.getDistance())))).first();
    }

    @Override
    public Execution getFarthestDistance() {

        Bson project = project(Projections.fields(Projections.excludeId(),
                Projections.include(COUNTRY_FIELD, DISTANCE_FIELD)));
        Execution execution = getExecutionsCollection().aggregate(
                Arrays.asList(group(null, Accumulators.max(DISTANCE_FIELD, "$distance")))).first();

        return getExecutionsCollection().aggregate(Arrays.asList(project,
                match(Filters.eq(DISTANCE_FIELD, execution.getDistance())))).first();
    }

    private MongoCollection<Execution> getExecutionsCollection() {
        return this.mongoClient.getDatabase("stats").getCollection(EXECUTIONS_COLLECTION_NAME, Execution.class);
    }

}
