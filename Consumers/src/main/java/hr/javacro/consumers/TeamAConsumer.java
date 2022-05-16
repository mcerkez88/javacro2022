package hr.javacro.consumers;

import io.strimzi.kafka.oauth.client.ClientConfig;
import io.strimzi.kafka.oauth.common.ConfigProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

public class TeamAConsumer {
    private final String BOOSTRAP_SERVER = "javacro-cluster-oauth-kafka-bootstrap-javacro.apps-crc.testing:443";


    private KafkaConsumer<String, String> consumer;

    public void consume() {

        consumer.subscribe(Collections.singletonList("javacro"));



        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                final ConsumerRecords<String, String> records = consumer.poll(Duration.of(10, ChronoUnit.SECONDS));

                if (records.count() > 0) {
                    records.forEach(record -> {
                        System.out.println("Key: " + record.key() + ", Value:" + record.value());
                        record.headers().forEach(header -> {
                            System.out.println("Header Key: " + header.key() + ", Header value:" + new String(header.value()));
                        });
                    });
                } else {
                    System.out.println(Thread.currentThread().getName() + " - No messages!");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }


    }

    public TeamAConsumer() {
        Properties props = new Properties();

        // Configure kafka settings
        props.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        props.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, "a_Consumer-1");
        props.putIfAbsent(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "Instance-1");
        props.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.putIfAbsent(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        props.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());


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

        // Create the Kafka Consumer
        consumer = new KafkaConsumer<String, String>(props);
    }
}
