import { KotlinModule, KotlinModuleType } from '../generators/kotlin/model/KotlinModule.js'
import { JdlModule } from '../jdl/models/JdlModule.js'


export function mapJdlModuleToKotlinModule({
                                               jdlModule,
                                               moduleType,
                                               coreDependencies,
                                               includeDb = false,
                                               includeWebService = false,
                                               includeSecurity = false,
                                               includeTest = false,
                                           }: {
                                               jdlModule: JdlModule,
                                               moduleType: KotlinModuleType,
                                               coreDependencies?: {
                                                   name: string;
                                                   type: KotlinModuleType
                                               }[],
                                               includeDb?: boolean,
                                               includeWebService?: boolean,
                                               includeSecurity?: boolean,
                                               includeTest?: boolean
                                           },
): KotlinModule {
    const domain = jdlModule.name
    const baseName = `core-${domain}-api`
    const packageName = jdlModule.config.packageName
    const mainClassName = `${packageName}.ApplicationKt`
    const group = 'net.blugrid.api'
    const version = '0.1.0'

    return new KotlinModule(
        domain,
        moduleType,
        baseName,
        packageName,
        mainClassName,
        group,
        version,
        coreDependencies,
        includeDb,
        includeWebService,
        includeSecurity,
        includeTest,
    )
}
