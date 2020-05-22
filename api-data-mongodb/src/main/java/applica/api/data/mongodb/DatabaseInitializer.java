package applica.api.data.mongodb;

import applica.framework.data.mongodb.MongoHelper;
import applica.framework.library.utils.Tuple;
import com.mongodb.DB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DatabaseInitializer implements InitializingBean {

    private Log logger = LogFactory.getLog(getClass());

    private final MongoHelper mongoHelper;

    @Autowired
    public DatabaseInitializer(MongoHelper mongoHelper) {
        this.mongoHelper = mongoHelper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        var indexes = new ArrayList<Tuple<String, String>>();
        //indexes.add(new Tuple<>("collection", "property"));

        DB client = mongoHelper.getDB("default");

        for (var index : indexes) {
            client.getCollection(index.getV1()).createIndex(index.getV2());

            logger.info(String.format("Created db index at %s.%s", index.getV1(), index.getV2()));
        }
    }
}
