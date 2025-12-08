// parsers/jdl-parser.js
const { parseFromFiles } = require('jhipster-core');
const fs = require('fs');
const path = require('path');

const args = process.argv.slice(2);
const inputIndex = args.indexOf('--input');
const outputIndex = args.indexOf('--output');

if (inputIndex === -1) {
    console.error('Usage: node jdl-parser.js --input <jdl-file> [--output <json-file>]');
    process.exit(1);
}

const inputFile = args[inputIndex + 1];
if (!inputFile || inputFile.startsWith('--')) {
    console.error('Error: --input requires a value');
    console.error('Usage: node jdl-parser.js --input <jdl-file> [--output <json-file>]');
    process.exit(1);
}

const outputFile = outputIndex !== -1 ? args[outputIndex + 1] : null;

try {
    const jdl = parseFromFiles([path.resolve(inputFile)]);

    const result = {
        entities: Object.values(jdl.entities).map((entity) => ({
            name: entity.name,
            tableName: entity.tableName,
            javadoc: entity.javadoc,
            fields: (entity.body || []).map((field) => ({
                name: field.name,
                type: field.type,
                javadoc: field.javadoc,
                validations: field.validations || [],
            })),
            annotations: entity.annotations || [],
        })),
        applications: Object.values(jdl.applications || {}).map((app) => ({
            name: app.config?.baseName || 'app',
            config: app.config || {},
            entities: app.entities?.entityList || [],
        })),
    };

    const jsonOutput = JSON.stringify(result, null, 2);

    if (outputFile) {
        fs.writeFileSync(outputFile, jsonOutput);
        console.log(`Parsed JDL written to: ${outputFile}`);
    } else {
        console.log(jsonOutput);
    }
} catch (error) {
    console.error('Error parsing JDL:', error.message);
    process.exit(1);
}
