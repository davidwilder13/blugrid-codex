import { requireCjs } from '../utils/commonjs-loader.js'
import { JdlEntity } from './models/JdlEntity.js'

const { parseFromFiles } = requireCjs('jhipster-core')

export function loadJdlEntities(jdlPath: string): JdlEntity[] {
    const jdl = parseFromFiles([jdlPath])

    if (!jdl.entities || Object.keys(jdl.entities).length === 0) {
        throw new Error('❌ No entities found in JDL.')
    }

    return Object.values(jdl.entities)
        .map(raw => JdlEntity.fromRaw(raw))
}
