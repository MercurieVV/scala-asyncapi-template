const path = require('path');
const Generator = require('@asyncapi/generator');
const { readFile } = require('fs').promises;

const MAIN_TEST_RESULT_PATH = path.join('tests', 'temp', 'integrationTestResult');

const generateFolderName = () => {
    // you always want to generate to new directory to make sure test runs in clear environment
    return path.resolve(MAIN_TEST_RESULT_PATH, Date.now().toString());
};

describe('template integration tests for generated files using the generator and mqtt example', () => {

    jest.setTimeout(30000);

    it('should generate proper config, services and DTOs files for provided kafka', async() => {
        const outputDir = generateFolderName();
        const params = {};
        const kafkaExamplePath = './mocks/kafka.yml';

        const generator = new Generator(path.normalize('./'), outputDir, { forceWrite: true, templateParams: params });
        await generator.generateFromFile(path.resolve('tests', kafkaExamplePath));

        const expectedFiles = [
            '/src/main/scala/com/asyncapi/infrastructure/Config.scala',
            '/src/main/scala/com/asyncapi/service/PublisherService.scala',
            '/src/main/scala/com/asyncapi/service/MessageHandlerService.scala',
            '/src/main/scala/com/asyncapi/model/LightMeasuredPayload.scala',
            '/src/main/scala/com/asyncapi/model/LightMeasured.scala',
            '/src/test/scala/com/asyncapi/TestcontainerKafkaTest.scala',
            '/build.gradle',
            '/gradle.properties'
        ];
        for (const index in expectedFiles) {
            const file = await readFile(path.join(outputDir, expectedFiles[index]), 'utf8');
            expect(file).toMatchSnapshot();
        }
    });
});
