import path from 'path'
import { resolveFromProjectRoot } from '../utils/resolveFromProjectRoot.js'

// templatebase is relative to project root
const kotlinTemplateBase = resolveFromProjectRoot('src/generators/kotlin/templates')

export const CodegenConfig = {
    kotlin: {
        templateBase: kotlinTemplateBase,
        dbTemplatePath: `${kotlinTemplateBase}/db`,
        modelTemplatePath: `${kotlinTemplateBase}/db`,
        defaultJdlPath: path.resolve(process.cwd(), './jdl'),
        defaultOutputDir: path.resolve(process.cwd(), './output'),
    },
}
