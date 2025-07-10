# 📘 Documentation Structure and Conventions Guide

This guide defines **what must be documented**, **where it lives**, and **how AI agents or devs** should create or update documentation across the `jdl-codegen-tool` and generated modules to maintain institutional knowledge and enable seamless collaboration.

---

## 📂 Documentation Overview

### 🧭 Core Principles

* **Every module must document itself** with a `README.md` in its root
* **Global architectural, design, and coding conventions** live under `/docs`
* **Maintain a living audit trail** of decisions, patterns, and solutions
* **Enable pattern recognition** for developers and AI agents
* Documentation must support:
  * Developer onboarding and usage
  * Maintenance and contribution context
  * AI agents updating docs after generating or modifying code
  * Historical context for decision-making

---

## 🗂️ Directory & File Structure

```
/docs/                              ← Global system and tooling documentation
  ├── index.md                      ← Overview & navigation hub
  ├── architecture/                 ← System design & component relationships
  │   ├── overview.md               ← High-level system architecture
  │   ├── codegen-flow.md           ← JDL → Kotlin generation process
  │   ├── module-structure.md       ← Generated module patterns
  │   └── integration-patterns.md   ← How modules work together
  ├── conventions/                  ← Standards & patterns
  │   ├── naming-conventions.md     ← Kotlin, package, file naming
  │   ├── jdl-standards.md          ← JDL authoring best practices
  │   ├── template-patterns.md      ← Mustache template guidelines
  │   └── testing-standards.md      ← Test organization & patterns
  ├── domains/                      ← Business domain specifications
  │   ├── core-domains.md           ← Core entities (Organisation, User)
  │   ├── service-domains.md        ← Business logic domains
  │   └── integration-domains.md    ← External system integrations
  ├── infrastructure/               ← Shared infrastructure docs
  │   ├── common-api-overview.md    ← Common module ecosystem
  │   ├── security-model.md         ← Authentication & authorization
  │   ├── database-patterns.md      ← JPA, migration, audit patterns
  │   └── grpc-standards.md         ← gRPC service conventions
  ├── memory/                       ← **Project memory & audit trail**
  │   ├── README.md                 ← How to use project memory
  │   ├── decisions/                ← Architecture Decision Records (ADRs)
  │   ├── patterns/                 ← Reusable solution patterns
  │   ├── troubleshooting/          ← Known issues & solutions
  │   └── evolution/                ← System evolution timeline
  └── agents/                       ← AI agent guidelines
      ├── README.md                 ← Agent contribution overview
      ├── codegen-agent.md          ← Code generation agent guide
      └── docs-agent.md             ← Documentation agent guide

<module>/
  └── README.md                     ← Local context, responsibilities, usage

examples/
  └── <domain>/
      └── README.md                 ← Example-specific documentation
```

---

## 🧠 Project Memory System

### Architecture Decision Records (ADRs)

**Location**: `/docs/memory/decisions/`

**Format**: `YYYY-MM-DD-NNN-title.md`

```markdown
# ADR-001: JDL-Based Code Generation Architecture

**Date**: 2024-07-10
**Status**: Accepted
**Deciders**: [Team/Individual]

## Context
We need to standardize API generation across multiple domains while maintaining consistency and reducing boilerplate.

## Decision
Implement JDL-based code generation using TypeScript templates to produce Kotlin microservices.

## Consequences
**Positive**: Consistent code structure, reduced development time, standardized patterns
**Negative**: Learning curve for JDL, template maintenance overhead

## Implementation Notes
- Used Mustache templates for code generation
- Integrated with existing common-api-* infrastructure
- Supports incremental generation and trait-based customization

## Follow-ups
- [ ] Add GraphQL generation support
- [ ] Implement TypeScript client generation
```

### Solution Patterns

**Location**: `/docs/memory/patterns/`

**Purpose**: Capture reusable solutions for common problems

```markdown
# Pattern: Adding Custom Entity Traits

**Problem**: Need to add new behaviors (e.g., @Versionable) to generated entities

**Solution**:
1. Update `JdlEntityOption.ts` interface
2. Modify `JdlToCodegenEntityMapper.ts` mapping logic
3. Update relevant templates (Entity, Service, etc.)
4. Add examples and tests

**Example**: See `/docs/memory/patterns/add-versionable-trait.md`

**Related ADRs**: ADR-003 (Trait System Design)
**Last Used**: 2024-07-10 (Versionable trait)
**Variations**: Multi-trait patterns, conditional trait application
```

### Troubleshooting Database

**Location**: `/docs/memory/troubleshooting/`

```markdown
# Issue: Generated Flyway Migrations Fail

**Symptoms**: Migration errors during gradle build
**Root Cause**: SQL syntax incompatibility between template and PostgreSQL version
**Solution**: Update `CreateTableSQLTemplate.ts` to use compatible syntax
**Prevention**: Add migration validation tests
**Related Issues**: #45, #67
**Last Occurrence**: 2024-07-08
```

### Evolution Timeline

**Location**: `/docs/memory/evolution/`

**Purpose**: Track major system changes and their impact

```markdown
# 2024-Q3: Code Generation Evolution

## July 2024
- **2024-07-01**: Added gRPC client generation
- **2024-07-10**: Implemented @Versionable trait system
- **2024-07-15**: Integrated with common-api-security

## Impact Assessment
- **Developer Experience**: 40% reduction in boilerplate code
- **Consistency**: 95% adherence to naming conventions
- **Maintenance**: Template updates affect all generated code

## Lessons Learned
- Start with examples before implementing templates
- Snapshot testing prevents regression issues
- Clear trait documentation reduces confusion
```

---

## 📘 Must-Have Docs by Type

### 1. **Module-Level Documentation** (`README.md`)

Each module **must contain** a `README.md` with:

| Section | Description | Examples |
|---------|-------------|----------|
| 🎯 **Purpose** | What does this module do and why? | "Provides audit logging for all API operations" |
| 🏗️ **Architecture** | Key components and their relationships | Service classes, repositories, controllers |
| 📦 **Dependencies** | Internal and external dependencies | `common-api-model`, Jackson, Micronaut |
| 🔌 **Integration** | How other modules use this one | "Include in gradle dependencies, inject services" |
| 🧪 **Testing** | Test strategies and examples | "Extends BaseServiceIntegTest, uses TestFactory" |
| 📝 **Configuration** | Required properties and setup | Environment variables, application.yml sections |
| 🚀 **Getting Started** | Quick start examples | Code snippets for common use cases |
| 🔄 **Lifecycle** | Startup, shutdown, runtime behavior | "Registers event handlers on application start" |

### 2. **Generated Module Documentation**

Generated modules include additional sections:

| Section | Description |
|---------|-------------|
| ⚡ **Generated From** | Source JDL file and generation timestamp |
| 🔧 **Customization** | Safe modification guidelines |
| 🔄 **Regeneration** | How to update when JDL changes |

### 3. **Global Documentation Standards**

| Path | Contents | Update Triggers |
|------|----------|-----------------|
| `/docs/architecture/` | System design, component diagrams | New modules, architectural changes |
| `/docs/conventions/` | Coding standards, naming rules | New patterns, style updates |
| `/docs/domains/` | Business domain models | New entities, relationship changes |
| `/docs/infrastructure/` | Shared services documentation | common-api-* changes |
| `/docs/memory/` | **Project memory system** | **Every significant change** |

---

## 🤖 Agent-Aware Documentation Rules

### When Generating Code

AI agents **must**:

✅ **Update module README.md** with generation timestamp and source JDL
✅ **Create pattern documentation** for new template types or traits
✅ **Update relevant domain docs** when entities change
✅ **Log decisions** in `/docs/memory/decisions/` for architectural choices
✅ **Document troubleshooting** for any issues encountered and resolved

### Documentation Standards

✅ **Use semantic headings** (H2 `##`, H3 `###`) for scannable structure
✅ **Prefer bullet lists** over dense paragraphs for readability
✅ **Include code examples** with syntax highlighting
✅ **Link related documentation** using relative paths
✅ **Date all entries** for temporal context
✅ **Tag with keywords** for searchability

### Memory System Updates

**For every significant change**:

```typescript
// Example: Agent updating memory after adding new trait
const memoryUpdate = {
  type: 'pattern',
  title: 'Adding @Cacheable Trait',
  file: '/docs/memory/patterns/add-cacheable-trait.md',
  relatedFiles: ['JdlEntityOption.ts', 'KotlinServiceTemplate.ts'],
  tags: ['trait', 'caching', 'template'],
  lastApplied: '2024-07-10'
}
```

---

## 🔍 Documentation Discovery & Navigation

### Search Strategies

1. **By Module**: Use `/docs/infrastructure/common-api-overview.md` for module relationships
2. **By Domain**: Check `/docs/domains/` for business context
3. **By Pattern**: Search `/docs/memory/patterns/` for reusable solutions
4. **By Issue**: Check `/docs/memory/troubleshooting/` for known problems

### Navigation Helpers

```markdown
<!-- In any README.md -->
## 🔗 Related Documentation
- [Architecture Overview](/docs/architecture/overview.md)
- [Naming Conventions](/docs/conventions/naming-conventions.md)
- [Testing Standards](/docs/conventions/testing-standards.md)
- [Project Memory](/docs/memory/README.md)
```

---

## 📊 Documentation Health Metrics

### Completeness Checklist

| Module Type | Required Docs | Status |
|-------------|---------------|--------|
| `common-api-*` | README.md + architecture docs | ✅ |
| `core-*-api-*` | README.md + domain docs | 🚧 |
| `codegen/` | Agent guides + patterns | ✅ |
| `/docs/memory/` | ADRs + patterns + troubleshooting | 🆕 |

### Update Triggers

| Event | Documentation Updates |
|-------|----------------------|
| New JDL entity | Domain docs, examples README |
| New template | Pattern docs, agent guides |
| Architecture change | ADR, overview docs |
| Bug fix | Troubleshooting docs |
| New common-api module | Infrastructure docs, module README |

---

## 🔄 Memory System Workflows

### For Developers

```bash
# Before starting work
1. Check /docs/memory/patterns/ for similar solutions
2. Review /docs/memory/decisions/ for architectural context
3. Search /docs/memory/troubleshooting/ for known issues

# After completing work
1. Document new patterns in /docs/memory/patterns/
2. Update troubleshooting docs if issues were resolved
3. Create ADR for architectural decisions
4. Update evolution timeline
```

### For AI Agents

```typescript
// Agent workflow for memory system
async function updateProjectMemory(change: Change) {
  // 1. Identify change type
  const changeType = classifyChange(change)
  
  // 2. Update relevant memory files
  if (changeType.isArchitectural) {
    await createADR(change)
  }
  
  if (changeType.isPattern) {
    await updatePatternDocs(change)
  }
  
  if (changeType.solvesProblem) {
    await updateTroubleshooting(change)
  }
  
  // 3. Update evolution timeline
  await logToEvolution(change)
}
```

---

## ✅ AI Contribution Checklist

### Code Changes

| Task | Memory Updates | Documentation Updates |
|------|---------------|----------------------|
| **New JDL trait** | Pattern doc, ADR if architectural | Template docs, example README |
| **New template** | Pattern doc, troubleshooting | Agent guide, conventions |
| **Module generation** | Evolution log | Module README, domain docs |
| **Bug fix** | Troubleshooting doc | Affected module READMEs |
| **Architecture change** | ADR, evolution log | Architecture docs, overview |

### Documentation Quality Gates

Before committing documentation:

- [ ] **Scannable**: Uses headings and bullet points
- [ ] **Linked**: References related docs with relative paths
- [ ] **Timestamped**: Includes dates for temporal context
- [ ] **Tagged**: Includes relevant keywords for search
- [ ] **Complete**: Covers purpose, usage, and integration
- [ ] **Memory Updated**: Relevant pattern/decision/troubleshooting docs updated

### Memory System Validation

- [ ] **Pattern Reusability**: Can another developer/agent follow this pattern?
- [ ] **Decision Context**: Is there enough context to understand why this decision was made?
- [ ] **Troubleshooting Completeness**: Symptoms, root cause, solution, and prevention documented?
- [ ] **Evolution Tracking**: Change impact and lessons learned captured?

---

## 🎯 Success Metrics

### Developer Experience

- **Time to Productivity**: New developers can contribute within 2 days
- **Pattern Reuse**: 80% of common tasks have documented patterns
- **Decision Context**: 95% of architectural decisions have traceable ADRs

### AI Agent Effectiveness

- **Memory Utilization**: Agents reference existing patterns before creating new ones
- **Documentation Quality**: Generated docs meet human readability standards
- **Knowledge Retention**: Project knowledge survives team changes

### System Health

- **Documentation Coverage**: All modules have up-to-date READMEs
- **Memory Completeness**: Major decisions and patterns are documented
- **Troubleshooting Database**: Common issues have solutions documented
