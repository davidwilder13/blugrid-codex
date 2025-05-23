#!/usr/bin/env node
import { Command } from 'commander'
import path from 'path'
import { CodegenConfig } from './config/codegen-config.js'
import {
    generateCommonModuleFiles,
    generateKotlinDbMigrationFiles,
    generateKotlinEntityFile,
    generateKotlinGenericCrudRepositoryFile,
    generateKotlinResources,
    generateKotlinServiceInterfaceFile,
    generateKotlinStateServiceDbImplFile,
} from './generators/kotlin/file-generators/index.js'
import { KotlinModuleType } from './generators/kotlin/model/KotlinModule.js'
import { loadJdlEntities } from './jdl/load-entities.js'
import { loadJdlModules } from './jdl/load-modules.js'
import { mapJdlEntityToCodegenEntity } from './mapper/index.js'
import { mapJdlModuleToKotlinModule } from './mapper/JdlModuleToKotlinModule.js'

const program = new Command()

program
    .name('api-codegen')
    .description('🚀 Generate Kotlin backend modules from JDL files')
    .option('--jdl <path>', 'Path to JDL file', './jdl/core-organisation.jdl')
    .option('--out-dir <dir>', 'Base output directory')
    .action(async (options) => {
        const jdlPath = path.resolve(process.cwd(), options.jdl)
        const outputDir = options.outDir
            ? path.resolve(process.cwd(), options.outDir)
            : CodegenConfig.kotlin.defaultOutputDir

        console.log(`📥 Loading JDL from: ${jdlPath}`)
        const jdlEntities = loadJdlEntities(jdlPath)
        const jdlModules = loadJdlModules(jdlPath)
        console.log(`✅ Successfully loaded ${jdlEntities.length} entities and ${jdlModules.length} modules.`)

        for (const module of jdlModules) {
            console.log(`\n🔄 Processing Module: ${module.name}`)
            const basePackage = module.config.packageName

            const entitiesInModule = jdlEntities.filter(entity =>
                module.entities?.entityList?.includes(entity.name),
            )

            console.log(`🗂️ Found ${entitiesInModule.length} entities in module: ${module.name}`)

            const codegenEntities = entitiesInModule.map(entity =>
                mapJdlEntityToCodegenEntity(entity, 'net.blugrid.api'),
            )

            // === Generate API MODEL MODULE ===
            const kotlinModelModule = mapJdlModuleToKotlinModule({
                jdlModule: module,
                moduleType: KotlinModuleType.Model,
            })

            const modelModuleOutputPath = `${outputDir}/core-${module.name}-api/${kotlinModelModule.baseName}-${kotlinModelModule.moduleType}`

            console.log(`\n📦 Generating API Model Module: ${kotlinModelModule.baseName}`)
            await generateCommonModuleFiles(kotlinModelModule, modelModuleOutputPath)

            for (const entity of codegenEntities) {
                const resourceOutputPath = path.join(modelModuleOutputPath, 'src/main/kotlin', kotlinModelModule.packageName.replace(/\./g, '/'))
                console.log(`🧱 Generating Resource Models and Interfaces for: ${entity.name}`)
                await generateKotlinResources(entity, resourceOutputPath)
                await generateKotlinServiceInterfaceFile(entity.name, kotlinModelModule.packageName, resourceOutputPath)
            }

            // === Generate DB MODULE ===
            const kotlinDbModule = mapJdlModuleToKotlinModule({
                jdlModule: module,
                moduleType: KotlinModuleType.Db,
                coreDependencies: [{ name: module.name, type: KotlinModuleType.Model }],
                includeDb: true,
                includeTest: true,
            })

            const dbModuleOutputPath = `${outputDir}/core-${module.name}-api/${kotlinDbModule.baseName}-${kotlinDbModule.moduleType}`

            console.log(`\n🗄️  Generating DB Module: ${kotlinDbModule.baseName}`)
            await generateCommonModuleFiles(kotlinDbModule, dbModuleOutputPath)

            for (const entity of codegenEntities) {
                const dbPackageOutputPath = path.join(dbModuleOutputPath, 'src/main/kotlin', entity.packageName.replace(/\./g, '/'))
                console.log(`📄 Generating DB Entity, Migration, Repository, and Service implementation for: ${entity.name}`)
                await generateKotlinDbMigrationFiles(entity, kotlinDbModule, dbPackageOutputPath)
                await generateKotlinEntityFile(entity, kotlinDbModule, dbPackageOutputPath)
                await generateKotlinGenericCrudRepositoryFile(entity, kotlinDbModule, dbPackageOutputPath)
                await generateKotlinStateServiceDbImplFile(entity.name, entity.packageName, dbPackageOutputPath)
            }

            // === Generate API REST SERVER MODULE ===
            const kotlinApiServerModule = mapJdlModuleToKotlinModule({
                jdlModule: module,
                moduleType: KotlinModuleType.Api,
                coreDependencies: [{ name: module.name, type: KotlinModuleType.Model }],
                includeDb: true,
                includeTest: true,
            })

            const apiServerModuleOutputPath = `${outputDir}/core-${module.name}-api/${kotlinApiServerModule.baseName}`

            console.log(`\n🌐 Generating API REST Server Module: ${kotlinApiServerModule.baseName}`)
            await generateCommonModuleFiles(kotlinApiServerModule, apiServerModuleOutputPath)

            for (const entity of entitiesInModule) {
                console.log(`📌 Entity processed: ${entity.name}`)
            }
        }

        console.log('\n✅ Code generation complete! 🎉')
    })

program.parse()
