const fs = require('fs');
const path = require('path');

module.exports = {
    'generate:after': generator => {
        const asyncapi = generator.asyncapi;
        const schemas = asyncapi.allSchemas();

        for (let [key, value] of schemas) {
            if (value.type() !== 'object') {
                try {
                    fs.unlinkSync(path.resolve(generator.targetDir, `src/main/scala/com/asyncapi/model/${key}.scala`));
                } catch (e) {}
            }
        }
    }
};