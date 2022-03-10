const fs = require('fs-extra');
const tmp = require('tmp');

module.exports = {
    /**
     * Move directory with source code to specified.
     *
     * @param generator
     */
    'generate:after': generator => {
        const sourcePath = generator.targetDir + '/src/main/scala/';
        const testPath = generator.targetDir + '/src/test/scala/';
        let scalaPackage = generator.templateParams['userScalaPackage'];

        scalaPackage = scalaPackage.replace(/\./g, '/');

        if (scalaPackage !== 'com/asyncapi') {
            const tmpSrc = tmp.dirSync();
            const tmpTest = tmp.dirSync();
            fs.copySync(sourcePath + 'com/asyncapi', tmpSrc.name);
            fs.copySync(testPath + 'com/asyncapi', tmpTest.name);
            fs.removeSync(sourcePath + 'com');
            fs.removeSync(testPath + 'com');
            fs.copySync(tmpSrc.name, sourcePath + scalaPackage);
            fs.copySync(tmpTest.name, testPath + scalaPackage);
            tmp.setGracefulCleanup();
        }
    },

    /**
     * If parameters wasn't pass, but extension is used, then set extension to param value.
     * Since template params are not modifiable, another param used to store updated value.
     *
     * @param generator
     */
    'generate:before': generator => {
        const extensions = generator.asyncapi.info().extensions();
        let scalaPackage = generator.templateParams['scalaPackage'];

        if (scalaPackage === 'com.asyncapi' && extensions && extensions['x-scala-package']) {
            scalaPackage = extensions['x-scala-package'];
        }

        Object.defineProperty(generator.templateParams, 'userScalaPackage', {
            enumerable: true,
            get() {
                return scalaPackage;
            }
        });
    }
};