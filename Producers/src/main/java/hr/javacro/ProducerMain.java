package hr.javacro;

public class ProducerMain {

    public static void main(String[] args) {
        TeamAProducer teamAProducer = new TeamAProducer();
        for (int i = 0; i <= 10000; i++) {
            teamAProducer.sendMessage();

        }
    }
}
