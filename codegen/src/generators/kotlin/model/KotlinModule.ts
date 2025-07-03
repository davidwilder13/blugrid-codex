export enum KotlinModuleType {
    Model = 'model',
    Db = 'db',
    Client = 'client',
    GraphQL = 'graphql',
    GrpcClient = 'grpc-client',
    Api = 'api',
}

export class KotlinModule {
    constructor(
        readonly domain: string,
        readonly moduleType: KotlinModuleType,
        readonly baseName: string,
        readonly packageName: string,
        readonly mainClassName: string = `${packageName}.ApplicationKt`,
        readonly group: string = 'net.blugrid.api',
        readonly version: string = '0.1.0',
        readonly coreDependencies?: {
            name: string;
            type: KotlinModuleType
        }[],
        readonly includeDb: boolean = false,
        readonly includeWebService: boolean = false,
        readonly includeSecurity: boolean = false,
        readonly includeTest: boolean = false,
    ) {
    }

    get name(): string {
        return `${this.baseName}-${this.moduleType}`
    }

    get gradleBuildFile(): string {
        return 'build.gradle.kts'
    }

    get gradlePropsFile(): string {
        return 'gradle.properties'
    }

    get gradlewFiles(): string {
        return 'gradlew'
    }

    get gradlewBatFile(): string {
        return 'gradlew.bat'
    }
}
