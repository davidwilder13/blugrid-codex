# JDL Codegen Tool

A structured, modular full-stack Kotlin/TypeScript monorepo code generator based on the JHipster Domain Language (JDL). The goal is to automate generation of Kotlin-based backend APIs, database migrations, GraphQL schemas, and client libraries.

## 🚀 Features

- **Automate boilerplate code** generation for backend and frontend.
- **Standardize structure** across core, business, and application domains.
- Leverage a **strongly-typed, domain-driven approach**.
- Ensure a **DRY, reusable, and scalable** code structure.
- Easily integrate new **domains**, **modules**, and **entities**.

## 🛠️ Prerequisites

- Node.js >= 22.x
- pnpm (preferred) or npm

## 🔧 Installation

```bash
git clone <repository-url>
cd jdl-codegen-tool
pnpm install      # or npm install
```

## ⚙️ Usage

Place your JDL files under the `jdl/` directory (e.g., `jdl/core-organisation.jdl`).

```bash
pnpm run generate
# or npm run generate
```

By default, the generator reads `jdl/core-organisation.jdl` and writes the generated Kotlin modules into the `output/` directory.

## 📁 Project Structure

```
.                              # Repository root
├── agent.md                 # Project summary and goals
├── jdl/                     # JHipster Domain Language (JDL) input files
├── src/                     # TypeScript source code (generator core)
│   ├── config/              # Codegen configuration (template paths, output)
│   ├── jdl/                 # JDL parsing and domain models
│   ├── utils/               # Utility helpers (paths, type-mappers, templates)
│   └── generators/kotlin/   # Kotlin codegen models, mappers, and file generators
├── templates/               # (unused; see src/generators/kotlin/templates)
├── output/                  # Generated modules (output folder)
├── tsconfig.json            # TypeScript compiler configuration
├── ts-register.mjs          # ESM loader shim for ts-node
└── package.json             # Scripts and dependencies
```

## 🔍 Core Components

### JDL Input (`jdl/`)

A sample JDL definition in `jdl/core-organisation.jdl`:
```jdl
/**
 * An organisation represents a legal or operational entity.
 */
@resourceType(UnscopedResource)
@Auditable
entity Organisation {
  parentOrganisationId Long required,
  effectiveTimestamp LocalDate required
}

application {
  config {
    baseName organisation
    packageName net.blugrid.core.organisation
    applicationType microservice
  }

  entities Organisation
}
```
【F:jdl/core-organisation.jdl†L1-L16】【F:jdl/core-organisation.jdl†L18-L26】

### Configuration (`src/config/codegen-config.ts`)

Central places template paths and output base directory:
```ts
import { resolveFromProjectRoot } from '../utils/resolveFromProjectRoot.js';

const kotlinTemplateBase = resolveFromProjectRoot('src/generators/kotlin/templates');

export const CodegenConfig = {
  kotlin: {
    templateBase: kotlinTemplateBase,
    dbTemplatePath: `${kotlinTemplateBase}/db`,
    modelTemplatePath: `${kotlinTemplateBase}/model`,
    outputBase: resolveFromProjectRoot('output'),
  },
};
```
【F:src/config/codegen-config.ts†L1-L11】

### Entry Point (`src/main.ts`)

The main orchestration loads JDL definitions, maps them to codegen models, and applies templates:
```ts
async function main() {
  console.log('📥 Loading JDL…');
  const jdlPath = resolveFromProjectRoot('jdl/core-organisation.jdl');
  const jdlEntities = loadJdlEntities(jdlPath);
  const jdlModules  = loadJdlModules(jdlPath);

  for (const module of jdlModules) {
    // Generate API model and resources
    const kotlinModelModule = mapJdlModuleToKotlinModule(module, KotlinModuleType.Model);
    await generateCommonModuleFiles(kotlinModelModule);
    for (const entity of jdlEntities) {
      const resource = mapJdlEntityToResourceModel(entity);
      await generateKotlinResources(resource, kotlinModelModule);
    }

    // Generate DB migrations and entities
    const kotlinDbModule = mapJdlModuleToKotlinModule(module, KotlinModuleType.Db);
    await generateCommonModuleFiles(kotlinDbModule);
    for (const entity of jdlEntities.filter(e => module.entities?.entityList?.includes(e.name))) {
      const databaseTable = new DatabaseTableModel(/* ... */);
      generateKotlinDbMigrationFiles(databaseTable, kotlinDbModule);
      generateKotlinEntityFile(databaseTable, kotlinDbModule);
    }
  }
  console.log('✅ Codegen complete.');
}
main();
```
【F:src/main.ts†L13-L42】【F:src/main.ts†L43-L62】

## 📋 Templates

Mustache templates are organized under `src/generators/kotlin/templates/`:
```
src/generators/kotlin/templates/
├── common/   # Gradle files, shared templates
├── db/       # SQL, migration, and entity templates
└── model/    # Resource/DTO templates
```
【F:src/generators/kotlin/templates/common†L1-L4】

## 📦 Generated Output

When you run the generator, it writes Kotlin modules to the `output/` directory:
```
output/
└── core-organisation-api/
    ├── core-organisation-api/       # Controllers, services, mappers
    ├── core-organisation-api-model/ # DTOs & shared models
    └── core-organisation-api-db/    # Entities, migrations
```

## ✅ Detailed Recommendations

* **Encapsulation of Path Logic**:

  * Move all module path resolution logic to `KotlinModule`.
  * Centralize naming rules (`core-<name>-api`) for consistency.

* **Custom JDL Options**:

  * Add clear custom annotations (`@dbDomain`, `@resourceType`) in JDL to guide code generation.

* **Strongly Typed Domain Models**:

  * Use strong, well-defined TypeScript classes (`JdlEntity`, `JdlField`, etc.) for type-safety and maintainability.

* **Database Type Mapping**:

  * Clearly map Kotlin/JDL types to custom PostgreSQL domain types.
  * Use override annotations (`@dbDomain`) in JDL for custom DB types.

* **Scalable JDL Modularization**:

  * Separate JDL files per domain for modular reuse.
  * Merge files before parsing due to JDL limitations (no native imports).

* **Visualization and Diagrams** (Future Task):

  * Generate PlantUML/Mermaid diagrams from parsed JDL for documentation.

---

## 📝 Outstanding Tasks Checklist

### 📍 Immediate Tasks:

* [ ] ✅ Complete `generateDbEntity()` and `generateDbRepository()` functions.
* [ ] ✅ Finish Mustache templates for all DB and entity definitions.
* [ ] ✅ Enhance `mapKotlinTypeToDbDomain()` with robust type mappings.

### 📍 Enhancements:

* [ ] 🚧 Implement custom annotation parsing (`@dbDomain`, `@resourceType`) from JDL.
* [ ] 🚧 Implement JDL modularization via loader script (simulate import statements).
* [ ] 🚧 Provide detailed README and project setup instructions.

### 📍 Future Tasks:

* [ ] 🔮 Generate TypeScript frontend code (types, REST/GraphQL clients).
* [ ] 🔮 Generate UI components based on JDL specifications.
* [ ] 🔮 Add diagram generation (PlantUML, Mermaid).

---

## 🌟 Strategic Recommendations

* **Maintain Single Responsibility**:

  * Clearly separate concerns: parsing (JDL), models (TS), codegen (Mustache).

* **Gradual Rollout**:

  * First finish backend (Kotlin) fully, then expand into frontend.

* **Documentation and Testing**:

  * Invest in good tests and clear developer docs early.
