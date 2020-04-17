import db.JDBC;
import db.LimitsService;
import model.Limit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Application {
    private String user = "", url = "", pass = "";
    public static final String TOPIC_NAME = "alerts";
    public static final int READ_TIMEOUT = 300000;
    public static final int SNAPSHOT_LENGTH = 65536;
    private static PcapNetworkInterface nif = null;

    public Application(String user, String url, String pass) {
        this.user = user;
        this.url = url;
        this.pass = pass;
    }

    public void service(){
        int minutes = 20;
        long limit_max = 0, limit_min = 0;
        LimitsService srv = new LimitsService(new JDBC(url, user, pass));
        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer(props);
        SparkConf conf = new SparkConf().setAppName("Application");
        JavaSparkContext sc = new JavaSparkContext(conf);

        try{
            nif = new NifSelector().selectNetworkInterface();
            PcapHandle handle = nif.openLive(SNAPSHOT_LENGTH, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            while (true){
                if(minutes == 20) {
                    List<Limit> list = srv.getMaxDateLimits();
                    if (list.size() < 1) {
                        limit_max = 1073741824;
                        limit_min = 1024;
                    } else {
                        for (Limit value : list) {
                            if (value.getName().equals("max")) {
                                limit_max = value.getValue();
                            }
                            if (value.getName().equals("min")) {
                                limit_min = value.getValue();
                            }
                        }
                    }
                    minutes = 0;
                }
                final List <Integer> lengths = new LinkedList<Integer>();
                PacketListener listener = new PacketListener() {
                    public void gotPacket(Packet packet) {
                        lengths.add(packet.length());
                    }
                };
                handle.loop(-1, listener);
                JavaRDD<Integer> RDD = sc.parallelize(lengths);
                Integer count = RDD.reduce((a,b) -> a+b);
                if(count<limit_min || count>limit_max){
                    producer.send(new ProducerRecord<String, String>(TOPIC_NAME, "Exceeding the limit"+count.toString()));
                }
                minutes+=5;

            }
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }catch (PcapNativeException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }catch (InterruptedException ex){
            System.out.println(ex.getMessage());
        }catch (NotOpenException ex){
            ex.printStackTrace();
        }

    }
}
