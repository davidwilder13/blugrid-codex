import path from 'path'
import { requireCjs } from '../utils/commonjs-loader.js'
const { parseFromFiles } = requireCjs('jhipster-core')
import { JdlModule } from './models/JdlModule.js'

export function loadJdlModules(jdlFilePath: string): JdlModule[] {
    const absPath = path.resolve(jdlFilePath)
    const jdl = parseFromFiles([absPath])

    if (!jdl.applications || Object.keys(jdl.applications).length === 0) {
        throw new Error('❌ No applications found in JDL.')
    }

    const modules: JdlModule[] = []

    for (const app of Object.values(jdl.applications)) {
        const module = new JdlModule(app)
        modules.push(module)
    }

    return modules
}
