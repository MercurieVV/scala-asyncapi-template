const fs = require('fs');
const path = require('path');

module.exports = {
    'generate:after': generator => {
        let hasMqtt, hasAmqp, hasKafka;
        const asyncapi = generator.asyncapi;
        for (let server of Object.values(asyncapi.servers())) {
            hasAmqp = hasAmqp || server.protocol() === 'amqp';
            hasMqtt = hasMqtt || server.protocol() === 'mqtt';
            hasKafka = hasKafka || server.protocol() === 'kafka' || server.protocol() === 'kafka-secure';
        }
        if (!hasKafka) {
            // remove filers from template related only to Kafka
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/scala/com/asyncapi/SimpleKafkaTest.scala'));
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/scala/com/asyncapi/TestcontainerKafkaTest.scala'));
        }
        if (!hasAmqp) {
            // remove filers from template related only to amqp
        }
        if (!hasMqtt) {
            // remove filers from template related only to mqtt
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/scala/com/asyncapi/TestcontainerMqttTest.scala'));
        }
    }
};