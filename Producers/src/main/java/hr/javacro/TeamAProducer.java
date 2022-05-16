package hr.javacro;

import io.strimzi.kafka.oauth.client.ClientConfig;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class TeamAProducer {

    private final String BOOSTRAP_SERVER = "javacro-cluster-oauth-kafka-bootstrap-javacro.apps-crc.testing:443";

    private static final Logger LOG = LogManager.getLogger(TeamAProducer.class);



    private KafkaProducer<String, String> producer;

    public void sendMessage() {
        String key = UUID.randomUUID().toString();
        List<Header> headers = new ArrayList<>();

        headers.add(new RecordHeader("Header_key", "Header_value".getBytes()));

        ProducerRecord<String, String> pr = new ProducerRecord<String, String>("javacro", null, key, "value" , headers);

        try {
            String topic = producer.send(pr).get().topic();
            //producer.flush();
            //producer.close();
            LOG.info("Sample message sent " + topic + " with key " + key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public TeamAProducer () {
        Properties props = new Properties();

        // Configure kafka settings
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        props.putIfAbsent(ProducerConfig.CLIENT_ID_CONFIG, "producer-user");
        props.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        props.putIfAbsent(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Use the Apicurio Registry provided Kafka Serializer for Avro
        props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //props.putIfAbsent(ProducerConfig.RETRIES_CONFIG, 3);
        props.putIfAbsent(ProducerConfig.LINGER_MS_CONFIG, 2*1000);
        props.putIfAbsent(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2*1000);
        props.putIfAbsent(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 5*1000);

        //configure the following three settings for SSL Encryption
        props.putIfAbsent(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        props.setProperty("sasl.mechanism", "OAUTHBEARER");
        props.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
        props.setProperty("sasl.login.callback.handler.class", "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
        props.putIfAbsent(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "C:\\Users\\mcerkez\\git\\javacro\\02_kafka_broker\\oauth_auth\\kafka_cluster_ca.jks");
        props.setProperty("ssl.keystore.type", "jks");
        props.putIfAbsent(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "X0zuGhejaQYK");



        Properties defaults = new Properties();
        defaults.setProperty(ClientConfig.OAUTH_TOKEN_ENDPOINT_URI, "https://keycloak-javacro.apps-crc.testing/auth/realms/kafka-authz/protocol/openid-connect/token");
        defaults.setProperty(ClientConfig.OAUTH_CLIENT_ID, "team-a-client");
        defaults.setProperty(ClientConfig.OAUTH_CLIENT_SECRET, "1dfk2aOz2jnj3fY6SbhW5EdviANYPd1V");
        defaults.setProperty(ClientConfig.OAUTH_USERNAME_CLAIM, "preferred_username");
        defaults.setProperty(ClientConfig.OAUTH_SSL_TRUSTSTORE_PASSWORD, "password");
        defaults.setProperty(ClientConfig.OAUTH_SSL_TRUSTSTORE_LOCATION, "C:\\Users\\mcerkez\\git\\javacro\\02_kafka_broker\\oauth_auth\\keycloak_trustsore.jks");
        defaults.setProperty(ClientConfig.OAUTH_SSL_TRUSTSTORE_TYPE, "jks");
        ConfigProperties.resolveAndExportToSystemProperties(defaults);

        // Create the Kafka producer
        producer = new KafkaProducer<String, String>(props);


    }
}
