export interface JdlModuleConfig {
    baseName: string
    packageName: string
    applicationType: 'microservice' | 'gateway' | 'monolith' | string
}
