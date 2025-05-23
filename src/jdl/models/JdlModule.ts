import { JdlModuleConfig } from './JdlModuleConfig.js'

export interface JdlModuleOptions {
    dto?: Record<string, string>
    service?: Record<string, string>
    paginate?: Record<string, string>
    other?: Record<string, string[]>
}

export interface JdlEntities {
    entityList?: string[]
    excluded?: string[]
}

export class JdlModule {
    name: string
    config: JdlModuleConfig
    entities: JdlEntities
    options: JdlModuleOptions

    constructor(raw: any) {
        this.name = raw.config.baseName
        this.config = {
            baseName: raw.config.baseName,
            packageName: raw.config.packageName,
            applicationType: raw.config.applicationType
        }

        this.entities = raw.entities ?? { entityList: [] }
        this.options = {
            dto: raw.dto ?? {},
            service: raw.service ?? {},
            paginate: raw.paginate ?? {},
            other: raw.other ?? {}
        }
    }
}
