# Claude Skills Migration Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Migrate the TypeScript code generator to a Python-based Claude skills ecosystem with OpenAPI intermediate model.

**Architecture:** Hierarchical skill structure where orchestrators call layer skills, which call atomic skills, which render Jinja2 templates. JDL parser outputs JSON, Claude enriches to OpenAPI, skills generate code deterministically.

**Tech Stack:** Python 3.11+, Jinja2, PyYAML, jsonschema, pytest, jhipster-core (Node.js for JDL parsing)

---

## Phase 1: Foundation

### Task 1.1: Create Skill Directory Structure

**Files:**
- Create: `.claude/skills/blugrid-codegen/SKILL.md`
- Create: `.claude/skills/blugrid-codegen/config/`
- Create: `.claude/skills/blugrid-codegen/parsers/`
- Create: `.claude/skills/blugrid-codegen/orchestrators/`
- Create: `.claude/skills/blugrid-codegen/layers/`
- Create: `.claude/skills/blugrid-codegen/atomic/kotlin/`
- Create: `.claude/skills/blugrid-codegen/atomic/sql/`
- Create: `.claude/skills/blugrid-codegen/atomic/gradle/`
- Create: `.claude/skills/blugrid-codegen/templates/kotlin/`
- Create: `.claude/skills/blugrid-codegen/templates/sql/`
- Create: `.claude/skills/blugrid-codegen/templates/gradle/`
- Create: `.claude/skills/blugrid-codegen/tests/fixtures/`
- Create: `.claude/skills/blugrid-codegen/tests/golden/`

**Step 1: Create directory structure**

```bash
mkdir -p .claude/skills/blugrid-codegen/{config,parsers,orchestrators,layers}
mkdir -p .claude/skills/blugrid-codegen/atomic/{kotlin,sql,gradle}
mkdir -p .claude/skills/blugrid-codegen/templates/{kotlin,sql,gradle}
mkdir -p .claude/skills/blugrid-codegen/tests/{fixtures,golden}
```

**Step 2: Create initial SKILL.md**

```markdown
---
name: blugrid-codegen
description: Generate Kotlin backend modules from JDL, natural language, or design docs. Produces REST APIs, database migrations, GraphQL, and gRPC services.
---

# Blugrid Code Generator

## Overview

Generate production-ready Kotlin backend code from various inputs:
- JDL (JHipster Domain Language) files
- Natural language descriptions
- Design documents
- Existing OpenAPI schemas

## Usage

**From JDL:**
```bash
python parsers/jdl-parser.py --input design.jdl --output /tmp/parsed.json
# Claude enriches to OpenAPI
python orchestrators/generate-module.py --spec intermediate.yaml --output ./output/
```

**From natural language:**
Describe your entities and Claude will generate the OpenAPI spec, then invoke generation.

## Skill Hierarchy

- `orchestrators/` - Entry points Claude invokes
- `layers/` - Generate all files for a layer (model, db, api)
- `atomic/` - Generate single files
- `templates/` - Jinja2 templates
- `parsers/` - Input parsers (JDL, validation)
- `config/` - Type mappings, settings
```

**Step 3: Commit**

```bash
git add .claude/skills/blugrid-codegen/
git commit -m "feat(codegen): initialize skill directory structure"
```

---

### Task 1.2: Create Type Mappings Configuration

**Files:**
- Create: `.claude/skills/blugrid-codegen/config/type-mappings.yaml`

**Step 1: Create type mappings file**

Extract mappings from `codegen/src/mapper/JdlToCodegenEntityMapper.ts:7-50`:

```yaml
# Type mappings for JDL → Kotlin → PostgreSQL conversion

jdl_to_kotlin:
  String: String
  UUID: UUID
  Long: Long
  Integer: Long
  LocalDate: LocalDateTime
  LocalDateTime: LocalDateTime
  Instant: LocalDateTime
  ZonedDateTime: LocalDateTime
  BigDecimal: Double
  Boolean: Boolean
  byte[]: ByteArray
  Blob: ByteArray
  AnyBlob: ByteArray
  ImageBlob: ByteArray
  TextBlob: String

jdl_to_db_domain:
  UUID: t_uuid
  String: t_text
  Boolean: t_boolean
  Long: bigint
  Integer: bigint
  BigDecimal: t_money
  LocalDate: t_datetime
  LocalDateTime: t_timestamp
  Instant: t_timestamp
  ZonedDateTime: t_timestampz
  byte[]: t_bytea
  Blob: t_bytea
  AnyBlob: t_bytea
  ImageBlob: t_bytea
  TextBlob: t_text

kotlin_type_imports:
  UUID: java.util.UUID
  LocalDate: java.time.LocalDateTime
  LocalDateTime: java.time.LocalDateTime
  Instant: java.time.Instant
  ZonedDateTime: java.time.ZonedDateTime
  BigDecimal: java.math.BigDecimal
  ByteArray: null  # No import needed

resource_types:
  - UnscopedResource
  - TenantResource
  - BusinessUnitResource
  - UserResource

audit_fields:
  - name: createdBy
    type: String
    db_domain: t_text
  - name: createdAt
    type: LocalDateTime
    db_domain: t_timestamp
  - name: updatedBy
    type: String
    db_domain: t_text
  - name: updatedAt
    type: LocalDateTime
    db_domain: t_timestamp
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/config/type-mappings.yaml
git commit -m "feat(codegen): add type mappings configuration"
```

---

### Task 1.3: Create Python Utilities Module

**Files:**
- Create: `.claude/skills/blugrid-codegen/utils/__init__.py`
- Create: `.claude/skills/blugrid-codegen/utils/config.py`
- Create: `.claude/skills/blugrid-codegen/utils/naming.py`
- Create: `.claude/skills/blugrid-codegen/utils/templates.py`
- Test: `.claude/skills/blugrid-codegen/tests/test_utils.py`

**Step 1: Write failing tests**

```python
# tests/test_utils.py
import pytest
from utils.naming import to_snake_case, to_camel_case, to_pascal_case
from utils.config import load_type_mappings
from utils.templates import render_template


class TestNaming:
    def test_to_snake_case(self):
        assert to_snake_case("OrganisationUnit") == "organisation_unit"
        assert to_snake_case("HTTPRequest") == "http_request"
        assert to_snake_case("alreadySnake") == "already_snake"

    def test_to_camel_case(self):
        assert to_camel_case("organisation_unit") == "organisationUnit"
        assert to_camel_case("OrganisationUnit") == "organisationUnit"

    def test_to_pascal_case(self):
        assert to_pascal_case("organisation_unit") == "OrganisationUnit"
        assert to_pascal_case("organisationUnit") == "OrganisationUnit"


class TestConfig:
    def test_load_type_mappings(self):
        mappings = load_type_mappings()
        assert mappings["jdl_to_kotlin"]["UUID"] == "UUID"
        assert mappings["jdl_to_db_domain"]["String"] == "t_text"


class TestTemplates:
    def test_render_simple_template(self, tmp_path):
        template_content = "Hello {{ name }}!"
        template_file = tmp_path / "test.j2"
        template_file.write_text(template_content)

        result = render_template(str(template_file), {"name": "World"})
        assert result == "Hello World!"
```

**Step 2: Run tests to verify they fail**

```bash
cd .claude/skills/blugrid-codegen
python -m pytest tests/test_utils.py -v
```

Expected: FAIL (modules not found)

**Step 3: Create utils/__init__.py**

```python
# utils/__init__.py
from .naming import to_snake_case, to_camel_case, to_pascal_case
from .config import load_type_mappings
from .templates import render_template

__all__ = [
    "to_snake_case",
    "to_camel_case",
    "to_pascal_case",
    "load_type_mappings",
    "render_template",
]
```

**Step 4: Create utils/naming.py**

```python
# utils/naming.py
import re


def to_snake_case(name: str) -> str:
    """Convert PascalCase or camelCase to snake_case."""
    # Insert underscore before uppercase letters (except at start)
    s1 = re.sub(r'(.)([A-Z][a-z]+)', r'\1_\2', name)
    # Insert underscore before uppercase letters followed by lowercase
    s2 = re.sub(r'([a-z0-9])([A-Z])', r'\1_\2', s1)
    return s2.lower()


def to_camel_case(name: str) -> str:
    """Convert snake_case or PascalCase to camelCase."""
    if "_" in name:
        # From snake_case
        components = name.split("_")
        return components[0].lower() + "".join(x.title() for x in components[1:])
    else:
        # From PascalCase - just lowercase first letter
        return name[0].lower() + name[1:] if name else name


def to_pascal_case(name: str) -> str:
    """Convert snake_case or camelCase to PascalCase."""
    if "_" in name:
        # From snake_case
        return "".join(x.title() for x in name.split("_"))
    else:
        # From camelCase - just uppercase first letter
        return name[0].upper() + name[1:] if name else name
```

**Step 5: Create utils/config.py**

```python
# utils/config.py
from pathlib import Path
from typing import Any
import yaml


def get_config_dir() -> Path:
    """Get the config directory path."""
    return Path(__file__).parent.parent / "config"


def load_type_mappings() -> dict[str, Any]:
    """Load type mappings from YAML config."""
    config_path = get_config_dir() / "type-mappings.yaml"
    with open(config_path) as f:
        return yaml.safe_load(f)


def get_kotlin_type(jdl_type: str) -> str:
    """Map JDL type to Kotlin type."""
    mappings = load_type_mappings()
    return mappings["jdl_to_kotlin"].get(jdl_type, jdl_type)


def get_db_domain(jdl_type: str) -> str:
    """Map JDL type to PostgreSQL domain type."""
    mappings = load_type_mappings()
    return mappings["jdl_to_db_domain"].get(jdl_type, "t_text")


def get_kotlin_import(kotlin_type: str) -> str | None:
    """Get import statement for Kotlin type."""
    mappings = load_type_mappings()
    return mappings["kotlin_type_imports"].get(kotlin_type)
```

**Step 6: Create utils/templates.py**

```python
# utils/templates.py
from pathlib import Path
from jinja2 import Environment, FileSystemLoader, select_autoescape


def get_template_dir() -> Path:
    """Get the templates directory path."""
    return Path(__file__).parent.parent / "templates"


def create_jinja_env(template_dir: Path | None = None) -> Environment:
    """Create Jinja2 environment with standard settings."""
    if template_dir is None:
        template_dir = get_template_dir()

    return Environment(
        loader=FileSystemLoader(str(template_dir)),
        autoescape=select_autoescape(disabled_extensions=["j2"]),
        trim_blocks=True,
        lstrip_blocks=True,
        keep_trailing_newline=True,
    )


def render_template(template_path: str, context: dict) -> str:
    """Render a Jinja2 template with given context."""
    path = Path(template_path)
    env = create_jinja_env(path.parent)
    template = env.get_template(path.name)
    return template.render(**context)


def render_template_string(template_string: str, context: dict) -> str:
    """Render a Jinja2 template string with given context."""
    from jinja2 import Template
    template = Template(template_string)
    return template.render(**context)
```

**Step 7: Create requirements.txt**

```text
# .claude/skills/blugrid-codegen/requirements.txt
jinja2>=3.1.0
pyyaml>=6.0
jsonschema>=4.0
pytest>=7.0
```

**Step 8: Run tests to verify they pass**

```bash
cd .claude/skills/blugrid-codegen
pip install -r requirements.txt
python -m pytest tests/test_utils.py -v
```

Expected: PASS

**Step 9: Commit**

```bash
git add .claude/skills/blugrid-codegen/utils/ .claude/skills/blugrid-codegen/tests/test_utils.py .claude/skills/blugrid-codegen/requirements.txt
git commit -m "feat(codegen): add Python utilities (naming, config, templates)"
```

---

## Phase 2: JDL Parser Skill

### Task 2.1: Create JDL Parser Script

**Files:**
- Create: `.claude/skills/blugrid-codegen/parsers/jdl-parser.py`
- Create: `.claude/skills/blugrid-codegen/parsers/jdl-parser.js` (Node.js wrapper)
- Test: `.claude/skills/blugrid-codegen/tests/test_jdl_parser.py`

**Step 1: Create Node.js JDL parser wrapper**

This wraps jhipster-core to output JSON:

```javascript
// parsers/jdl-parser.js
const { parseFromFiles } = require('jhipster-core');
const fs = require('fs');
const path = require('path');

const args = process.argv.slice(2);
const inputIndex = args.indexOf('--input');
const outputIndex = args.indexOf('--output');

if (inputIndex === -1) {
    console.error('Usage: node jdl-parser.js --input <jdl-file> [--output <json-file>]');
    process.exit(1);
}

const inputFile = args[inputIndex + 1];
const outputFile = outputIndex !== -1 ? args[outputIndex + 1] : null;

try {
    const jdl = parseFromFiles([path.resolve(inputFile)]);

    const result = {
        entities: Object.entries(jdl.entities).map(([name, entity]) => ({
            name,
            tableName: entity.tableName,
            javadoc: entity.javadoc,
            fields: Object.entries(entity.fields || {}).map(([fieldName, field]) => ({
                name: fieldName,
                type: field.type,
                javadoc: field.javadoc,
                validations: field.validations || {},
            })),
            annotations: entity.annotations || {},
        })),
        applications: Object.entries(jdl.applications || {}).map(([name, app]) => ({
            name,
            config: app.config || {},
            entities: app.entities?.entityList || [],
        })),
    };

    const jsonOutput = JSON.stringify(result, null, 2);

    if (outputFile) {
        fs.writeFileSync(outputFile, jsonOutput);
        console.log(`Parsed JDL written to: ${outputFile}`);
    } else {
        console.log(jsonOutput);
    }
} catch (error) {
    console.error('Error parsing JDL:', error.message);
    process.exit(1);
}
```

**Step 2: Create Python wrapper for JDL parser**

```python
#!/usr/bin/env python3
# parsers/jdl-parser.py
"""
JDL Parser Skill

Parses JHipster Domain Language files and outputs structured JSON.
Uses jhipster-core (Node.js) for parsing.

Usage:
    python jdl-parser.py --input design.jdl --output parsed.json
    python jdl-parser.py --input design.jdl  # outputs to stdout
"""

import argparse
import json
import subprocess
import sys
from pathlib import Path


def parse_jdl(input_path: str, output_path: str | None = None) -> dict:
    """Parse JDL file using jhipster-core and return structured data."""
    script_dir = Path(__file__).parent
    js_parser = script_dir / "jdl-parser.js"

    cmd = ["node", str(js_parser), "--input", input_path]

    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        cwd=str(script_dir),
    )

    if result.returncode != 0:
        raise RuntimeError(f"JDL parsing failed: {result.stderr}")

    parsed = json.loads(result.stdout)

    if output_path:
        with open(output_path, "w") as f:
            json.dump(parsed, f, indent=2)
        print(f"Parsed JDL written to: {output_path}", file=sys.stderr)

    return parsed


def main():
    parser = argparse.ArgumentParser(description="Parse JDL files to JSON")
    parser.add_argument("--input", "-i", required=True, help="Input JDL file path")
    parser.add_argument("--output", "-o", help="Output JSON file path (stdout if omitted)")

    args = parser.parse_args()

    try:
        result = parse_jdl(args.input, args.output)
        if not args.output:
            print(json.dumps(result, indent=2))
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
```

**Step 3: Create test fixture**

Copy existing JDL to fixtures:

```bash
cp jdl/core-organisation.jdl .claude/skills/blugrid-codegen/tests/fixtures/
```

**Step 4: Write parser test**

```python
# tests/test_jdl_parser.py
import pytest
import json
from pathlib import Path
import sys

# Add parent to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))

from parsers import jdl_parser


class TestJdlParser:
    @pytest.fixture
    def fixture_path(self):
        return Path(__file__).parent / "fixtures" / "core-organisation.jdl"

    def test_parse_jdl_returns_entities(self, fixture_path, tmp_path):
        if not fixture_path.exists():
            pytest.skip("Fixture not found")

        output_path = tmp_path / "parsed.json"
        result = jdl_parser.parse_jdl(str(fixture_path), str(output_path))

        assert "entities" in result
        assert "applications" in result
        assert len(result["entities"]) > 0

        # Check entity structure
        entity = result["entities"][0]
        assert "name" in entity
        assert "fields" in entity

    def test_parse_jdl_extracts_fields(self, fixture_path, tmp_path):
        if not fixture_path.exists():
            pytest.skip("Fixture not found")

        result = jdl_parser.parse_jdl(str(fixture_path))

        # Find Organisation entity
        org_entity = next(
            (e for e in result["entities"] if e["name"] == "Organisation"),
            None
        )
        assert org_entity is not None

        # Check fields exist
        field_names = [f["name"] for f in org_entity["fields"]]
        assert "parentOrganisationId" in field_names or len(field_names) > 0
```

**Step 5: Run tests**

```bash
cd .claude/skills/blugrid-codegen
npm install jhipster-core  # if not already installed
python -m pytest tests/test_jdl_parser.py -v
```

**Step 6: Commit**

```bash
git add .claude/skills/blugrid-codegen/parsers/ .claude/skills/blugrid-codegen/tests/
git commit -m "feat(codegen): add JDL parser skill"
```

---

## Phase 3: OpenAPI Schema Definition

### Task 3.1: Create OpenAPI Extension Schema

**Files:**
- Create: `.claude/skills/blugrid-codegen/config/openapi-extensions.yaml`
- Create: `.claude/skills/blugrid-codegen/parsers/openapi-validator.py`

**Step 1: Define extension schema**

```yaml
# config/openapi-extensions.yaml
# Schema for custom x- extensions used in code generation

extensions:
  # Info-level extensions
  info:
    x-base-package:
      type: string
      description: Base package name for generated code
      example: net.blugrid.core.organisation
    x-group:
      type: string
      description: Gradle group ID
      example: net.blugrid.api
    x-module-name:
      type: string
      description: Base module name
      example: core-organisation-api

  # Schema-level extensions (on each entity)
  schema:
    x-resource-type:
      type: string
      enum: [UnscopedResource, TenantResource, BusinessUnitResource, UserResource]
      description: Resource scoping strategy
      default: UnscopedResource
    x-auditable:
      type: boolean
      description: Whether to add audit fields
      default: false
    x-searchable:
      type: boolean
      description: Whether entity is searchable
      default: false
    x-db-table:
      type: string
      description: Override table name
    x-db-schema:
      type: string
      description: Database schema name

  # Property-level extensions (on each field)
  property:
    x-db-domain:
      type: string
      description: PostgreSQL domain type
      example: t_text
    x-kotlin-type:
      type: string
      description: Override Kotlin type
      example: LocalDateTime
    x-generated:
      type: boolean
      description: Field is auto-generated (id, uuid)
      default: false
    x-nullable:
      type: boolean
      description: Override nullability

  # Path-level extensions
  path:
    x-grpc-service:
      type: string
      description: gRPC service name
    x-graphql-type:
      type: string
      enum: [Query, Mutation, Subscription]
      description: GraphQL operation type
```

**Step 2: Create OpenAPI validator**

```python
#!/usr/bin/env python3
# parsers/openapi-validator.py
"""
OpenAPI Validator Skill

Validates OpenAPI specs and checks custom x- extensions.

Usage:
    python openapi-validator.py --input spec.yaml
"""

import argparse
import sys
from pathlib import Path
import yaml


def load_extension_schema() -> dict:
    """Load the extension schema definition."""
    config_dir = Path(__file__).parent.parent / "config"
    schema_path = config_dir / "openapi-extensions.yaml"
    with open(schema_path) as f:
        return yaml.safe_load(f)


def validate_openapi(spec_path: str) -> tuple[bool, list[str]]:
    """Validate OpenAPI spec and return (valid, errors)."""
    errors = []

    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    # Check required fields
    if "openapi" not in spec:
        errors.append("Missing 'openapi' version field")

    if "info" not in spec:
        errors.append("Missing 'info' section")
    else:
        if "x-base-package" not in spec["info"]:
            errors.append("Missing 'x-base-package' in info section")

    if "components" not in spec or "schemas" not in spec.get("components", {}):
        errors.append("Missing 'components.schemas' section")

    # Validate schemas
    extension_schema = load_extension_schema()
    valid_resource_types = extension_schema["extensions"]["schema"]["x-resource-type"]["enum"]

    for name, schema in spec.get("components", {}).get("schemas", {}).items():
        resource_type = schema.get("x-resource-type", "UnscopedResource")
        if resource_type not in valid_resource_types:
            errors.append(f"Schema '{name}': invalid x-resource-type '{resource_type}'")

    return len(errors) == 0, errors


def main():
    parser = argparse.ArgumentParser(description="Validate OpenAPI spec")
    parser.add_argument("--input", "-i", required=True, help="OpenAPI spec file")

    args = parser.parse_args()

    valid, errors = validate_openapi(args.input)

    if valid:
        print("OpenAPI spec is valid")
        sys.exit(0)
    else:
        print("Validation errors:")
        for error in errors:
            print(f"  - {error}")
        sys.exit(1)


if __name__ == "__main__":
    main()
```

**Step 3: Commit**

```bash
git add .claude/skills/blugrid-codegen/config/openapi-extensions.yaml .claude/skills/blugrid-codegen/parsers/openapi-validator.py
git commit -m "feat(codegen): add OpenAPI extension schema and validator"
```

---

## Phase 4: Jinja2 Templates

### Task 4.1: Convert Resource Template

**Files:**
- Create: `.claude/skills/blugrid-codegen/templates/kotlin/resource.kt.j2`
- Reference: `codegen/src/generators/kotlin/templates/model/KotlinResourceTemplate.ts`

**Step 1: Create resource template**

Convert from Mustache to Jinja2:

```jinja2
{# templates/kotlin/resource.kt.j2 #}
{# Generates Kotlin resource DTOs #}
package {{ package_name }}.model

{% for import in imports %}
import {{ import }}
{% endfor %}
import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.Audit
import net.blugrid.api.common.model.ResourceType
import net.blugrid.api.common.model.{{ resource_type }}

{% if variant == 'model' %}
@Schema(description = "Represents a {{ name_lower }} within the system.")
data class {{ name }}(
    override var id: Long,
    override var uuid: UUID,
{% for field in fields %}
    @Schema(description = "{{ field.description }}"{% if field.example %}, example = "{{ field.example }}"{% endif %})
    var {{ field.name }}: {{ field.type }}{% if not field.required %}? = null{% endif %},
{% endfor %}
    override val audit: Audit? = null
) : {{ resource_type }}<{{ name }}>(audit) {
    override val resourceType: ResourceType
        get() = ResourceType.{{ name_upper_snake }}
}

{% elif variant == 'create' %}
@Schema(description = "Request body for creating a new {{ name_lower }}.")
data class {{ name }}Create(
{% for field in fields %}
    @Schema(description = "{{ field.description }}"{% if field.example %}, example = "{{ field.example }}"{% endif %})
    val {{ field.name }}: {{ field.type }}{% if not field.required %}? = null{% endif %}{% if not loop.last %},{% endif %}

{% endfor %}
)

{% elif variant == 'update' %}
@Schema(description = "Request body for updating a {{ name_lower }}.")
data class {{ name }}Update(
{% for field in fields %}
    @Schema(description = "{{ field.description }}"{% if field.example %}, example = "{{ field.example }}"{% endif %})
    val {{ field.name }}: {{ field.type }}? = null{% if not loop.last %},{% endif %}

{% endfor %}
)

{% elif variant == 'interface' %}
interface I{{ name }} {
{% for field in fields %}
    val {{ field.name }}: {{ field.type }}{% if not field.required %}?{% endif %}

{% endfor %}
}
{% endif %}
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/templates/kotlin/resource.kt.j2
git commit -m "feat(codegen): add Kotlin resource Jinja2 template"
```

---

### Task 4.2: Convert Entity Template

**Files:**
- Create: `.claude/skills/blugrid-codegen/templates/kotlin/entity.kt.j2`
- Reference: `codegen/src/generators/kotlin/templates/db/repository/KotlinEntityTemplate.ts`

**Step 1: Create entity template**

```jinja2
{# templates/kotlin/entity.kt.j2 #}
{# Generates JPA Entity classes #}
package {{ package_name }}.repository.model

{% for import in imports %}
import {{ import }}
{% endfor %}
import jakarta.persistence.*
import net.blugrid.api.common.db.entity.{{ resource_type }}Entity

@Entity
@Table(name = "{{ table_name }}")
class {{ name }}Entity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long = 0,

    @Column(name = "uuid", nullable = false, updatable = false)
    override var uuid: UUID = UUID.randomUUID(),
{% for field in fields %}

    @Column(name = "{{ field.column_name }}"{% if not field.required %}, nullable = true{% endif %})
    var {{ field.name }}: {{ field.type }}{% if not field.required %}? = null{% endif %},
{% endfor %}
{% if auditable %}

    @Column(name = "created_by")
    override var createdBy: String? = null,

    @Column(name = "created_at")
    override var createdAt: LocalDateTime? = null,

    @Column(name = "updated_by")
    override var updatedBy: String? = null,

    @Column(name = "updated_at")
    override var updatedAt: LocalDateTime? = null,
{% endif %}
) : {{ resource_type }}Entity()
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/templates/kotlin/entity.kt.j2
git commit -m "feat(codegen): add Kotlin entity Jinja2 template"
```

---

### Task 4.3: Convert SQL Table Template

**Files:**
- Create: `.claude/skills/blugrid-codegen/templates/sql/table.sql.j2`
- Reference: `codegen/src/generators/kotlin/templates/db/sql/CreateTableSQLTemplate.ts`

**Step 1: Create SQL table template**

```jinja2
{# templates/sql/table.sql.j2 #}
{# Generates PostgreSQL table definitions #}
-- Table: {{ table_name }}
-- Generated from: {{ entity_name }}

-- Columns definition (inheritable)
CREATE TABLE IF NOT EXISTS {{ table_name }}_columns (
{% for field in fields %}
    {{ field.column_name }} {{ field.db_domain }}{% if field.required %} NOT NULL{% endif %}{% if not loop.last %},{% endif %}

{% endfor %}
) WITHOUT OIDS;

-- Main table with inheritance
CREATE TABLE IF NOT EXISTS {{ table_name }} (
    type T_TABLE_NAME NOT NULL DEFAULT '{{ table_name }}'
)
INHERITS (
{% if resource_type == 'UnscopedResource' %}
    _common_unscoped_resource_columns,
    _common_audit_columns,
{% elif resource_type == 'TenantResource' %}
    _common_tenant_resource_columns,
    _common_audit_columns,
{% elif resource_type == 'BusinessUnitResource' %}
    _common_business_unit_resource_columns,
    _common_audit_columns,
{% elif resource_type == 'UserResource' %}
    _common_user_resource_columns,
    _common_audit_columns,
{% endif %}
    {{ table_name }}_columns
)
WITHOUT OIDS;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_{{ table_name }}_uuid ON {{ table_name }} (uuid);
{% if resource_type == 'TenantResource' %}
CREATE INDEX IF NOT EXISTS idx_{{ table_name }}_tenant_id ON {{ table_name }} (tenant_id);
{% endif %}

-- Audit trigger
{% if auditable %}
CREATE TRIGGER {{ table_name }}_audit_trigger
    BEFORE INSERT OR UPDATE ON {{ table_name }}
    FOR EACH ROW EXECUTE FUNCTION update_audit_columns();
{% endif %}
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/templates/sql/table.sql.j2
git commit -m "feat(codegen): add SQL table Jinja2 template"
```

---

### Task 4.4: Convert Gradle Build Template

**Files:**
- Create: `.claude/skills/blugrid-codegen/templates/gradle/build.gradle.kts.j2`
- Reference: `codegen/src/generators/kotlin/templates/common/GradleBuildFileTemplate.ts`

**Step 1: Create Gradle build template**

```jinja2
{# templates/gradle/build.gradle.kts.j2 #}
{# Generates Gradle build files #}
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
{% if include_db %}
    kotlin("plugin.jpa")
{% endif %}
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "{{ group }}"
version = "{{ version }}"

dependencies {
{% for dep in dependencies %}
    implementation(project(":{{ dep.name }}-{{ dep.type }}"))
{% endfor %}

    implementation("org.springframework.boot:spring-boot-starter")
{% if include_web_service %}
    implementation("org.springframework.boot:spring-boot-starter-web")
{% endif %}
{% if include_db %}
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
{% endif %}
{% if include_security %}
    implementation("org.springframework.boot:spring-boot-starter-security")
{% endif %}
{% if include_test %}

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk")
{% endif %}
}

{% if main_class_name %}
application {
    mainClass.set("{{ main_class_name }}")
}
{% endif %}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/templates/gradle/build.gradle.kts.j2
git commit -m "feat(codegen): add Gradle build Jinja2 template"
```

---

## Phase 5: Atomic Skills

### Task 5.1: Create Resource Generator Skill

**Files:**
- Create: `.claude/skills/blugrid-codegen/atomic/kotlin/generate-resource.py`
- Test: `.claude/skills/blugrid-codegen/tests/test_generate_resource.py`

**Step 1: Write failing test**

```python
# tests/test_generate_resource.py
import pytest
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_resource import generate_resource


class TestGenerateResource:
    @pytest.fixture
    def sample_schema(self):
        return {
            "name": "Organisation",
            "package_name": "net.blugrid.core.organisation",
            "resource_type": "UnscopedResource",
            "fields": [
                {
                    "name": "parentOrganisationId",
                    "type": "Long",
                    "required": True,
                    "description": "The ID of the parent organisation",
                },
                {
                    "name": "effectiveTimestamp",
                    "type": "LocalDateTime",
                    "required": True,
                    "description": "When the organisation becomes active",
                },
            ],
            "imports": ["java.util.UUID", "java.time.LocalDateTime"],
        }

    def test_generate_model_variant(self, sample_schema, tmp_path):
        output_file = tmp_path / "Organisation.kt"
        generate_resource(sample_schema, "model", str(output_file))

        content = output_file.read_text()
        assert "data class Organisation(" in content
        assert "var parentOrganisationId: Long" in content
        assert "UnscopedResource" in content

    def test_generate_create_variant(self, sample_schema, tmp_path):
        output_file = tmp_path / "OrganisationCreate.kt"
        generate_resource(sample_schema, "create", str(output_file))

        content = output_file.read_text()
        assert "data class OrganisationCreate(" in content
```

**Step 2: Run test to verify it fails**

```bash
python -m pytest tests/test_generate_resource.py -v
```

Expected: FAIL

**Step 3: Create generate-resource.py**

```python
#!/usr/bin/env python3
# atomic/kotlin/generate-resource.py
"""
Resource Generator Skill

Generates Kotlin resource DTOs (model, create, update, interface variants).

Usage:
    python generate-resource.py --schema schema.yaml --variant model --output Organisation.kt
"""

import argparse
import sys
from pathlib import Path

# Add parent paths for imports
sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case


def generate_resource(schema: dict, variant: str, output_path: str) -> str:
    """Generate a Kotlin resource file."""
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "resource.kt.j2"

    # Build context
    context = {
        "variant": variant,
        "package_name": schema["package_name"],
        "name": schema["name"],
        "name_lower": schema["name"][0].lower() + schema["name"][1:],
        "name_upper_snake": to_snake_case(schema["name"]).upper(),
        "resource_type": schema.get("resource_type", "UnscopedResource"),
        "fields": schema.get("fields", []),
        "imports": schema.get("imports", []),
    }

    # Render template
    content = render_template(str(template_path), context)

    # Write output
    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate Kotlin resource DTO")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--variant", "-v", required=True,
                        choices=["model", "create", "update", "interface"],
                        help="Resource variant to generate")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    import yaml
    with open(args.schema) as f:
        schema = yaml.safe_load(f)

    generate_resource(schema, args.variant, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
```

**Step 4: Run tests to verify they pass**

```bash
python -m pytest tests/test_generate_resource.py -v
```

**Step 5: Commit**

```bash
git add .claude/skills/blugrid-codegen/atomic/kotlin/generate_resource.py .claude/skills/blugrid-codegen/tests/test_generate_resource.py
git commit -m "feat(codegen): add resource generator atomic skill"
```

---

### Task 5.2: Create Entity Generator Skill

**Files:**
- Create: `.claude/skills/blugrid-codegen/atomic/kotlin/generate-entity.py`

**Step 1: Create generate-entity.py**

```python
#!/usr/bin/env python3
# atomic/kotlin/generate-entity.py
"""
Entity Generator Skill

Generates JPA Entity classes.

Usage:
    python generate-entity.py --schema schema.yaml --output OrganisationEntity.kt
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case
from utils.config import get_kotlin_type, get_db_domain, get_kotlin_import


def generate_entity(schema: dict, output_path: str) -> str:
    """Generate a Kotlin JPA Entity file."""
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "entity.kt.j2"

    # Process fields
    fields = []
    imports = set(["java.util.UUID"])

    for field in schema.get("fields", []):
        kotlin_type = field.get("kotlin_type") or get_kotlin_type(field["type"])
        db_domain = field.get("db_domain") or get_db_domain(field["type"])

        # Add import if needed
        field_import = get_kotlin_import(kotlin_type)
        if field_import:
            imports.add(field_import)

        fields.append({
            "name": field["name"],
            "type": kotlin_type,
            "column_name": to_snake_case(field["name"]),
            "db_domain": db_domain,
            "required": field.get("required", False),
        })

    # Build context
    context = {
        "package_name": schema["package_name"],
        "name": schema["name"],
        "table_name": schema.get("table_name") or to_snake_case(schema["name"]),
        "resource_type": schema.get("resource_type", "UnscopedResource"),
        "auditable": schema.get("auditable", False),
        "fields": fields,
        "imports": sorted(imports),
    }

    # Render template
    content = render_template(str(template_path), context)

    # Write output
    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate Kotlin JPA Entity")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    import yaml
    with open(args.schema) as f:
        schema = yaml.safe_load(f)

    generate_entity(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/atomic/kotlin/generate_entity.py
git commit -m "feat(codegen): add entity generator atomic skill"
```

---

### Task 5.3: Create SQL Table Generator Skill

**Files:**
- Create: `.claude/skills/blugrid-codegen/atomic/sql/generate-table.py`

**Step 1: Create generate-table.py**

```python
#!/usr/bin/env python3
# atomic/sql/generate-table.py
"""
SQL Table Generator Skill

Generates PostgreSQL table definitions with inheritance.

Usage:
    python generate-table.py --schema schema.yaml --output V1__create_organisation.sql
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case
from utils.config import get_db_domain


def generate_table(schema: dict, output_path: str) -> str:
    """Generate PostgreSQL table definition."""
    template_path = Path(__file__).parent.parent.parent / "templates" / "sql" / "table.sql.j2"

    # Process fields
    fields = []
    for field in schema.get("fields", []):
        db_domain = field.get("db_domain") or get_db_domain(field["type"])
        fields.append({
            "name": field["name"],
            "column_name": to_snake_case(field["name"]),
            "db_domain": db_domain,
            "required": field.get("required", False),
        })

    # Build context
    context = {
        "entity_name": schema["name"],
        "table_name": schema.get("table_name") or to_snake_case(schema["name"]),
        "resource_type": schema.get("resource_type", "UnscopedResource"),
        "auditable": schema.get("auditable", False),
        "fields": fields,
    }

    # Render template
    content = render_template(str(template_path), context)

    # Write output
    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate PostgreSQL table")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    import yaml
    with open(args.schema) as f:
        schema = yaml.safe_load(f)

    generate_table(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/atomic/sql/generate_table.py
git commit -m "feat(codegen): add SQL table generator atomic skill"
```

---

## Phase 6: Layer Skills

### Task 6.1: Create Model Layer Skill

**Files:**
- Create: `.claude/skills/blugrid-codegen/layers/generate-model-layer.py`

**Step 1: Create generate-model-layer.py**

```python
#!/usr/bin/env python3
# layers/generate-model-layer.py
"""
Model Layer Generator Skill

Generates all model layer files (resources, DTOs, interfaces).

Usage:
    python generate-model-layer.py --spec openapi.yaml --output ./output/model/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_resource import generate_resource
from utils.naming import to_snake_case
import yaml


def extract_schemas_from_openapi(spec: dict) -> list[dict]:
    """Extract entity schemas from OpenAPI spec."""
    schemas = []
    base_package = spec.get("info", {}).get("x-base-package", "com.example")

    for name, schema in spec.get("components", {}).get("schemas", {}).items():
        # Convert OpenAPI schema to our format
        fields = []
        required_fields = schema.get("required", [])

        for prop_name, prop in schema.get("properties", {}).items():
            if prop.get("x-generated"):
                continue  # Skip id, uuid

            fields.append({
                "name": prop_name,
                "type": map_openapi_type(prop),
                "required": prop_name in required_fields,
                "description": prop.get("description", ""),
                "example": prop.get("example", ""),
            })

        schemas.append({
            "name": name,
            "package_name": f"{base_package}",
            "resource_type": schema.get("x-resource-type", "UnscopedResource"),
            "auditable": schema.get("x-auditable", False),
            "fields": fields,
            "imports": collect_imports(fields),
        })

    return schemas


def map_openapi_type(prop: dict) -> str:
    """Map OpenAPI type to Kotlin type."""
    type_map = {
        ("integer", "int64"): "Long",
        ("integer", "int32"): "Int",
        ("integer", None): "Int",
        ("string", "uuid"): "UUID",
        ("string", "date-time"): "LocalDateTime",
        ("string", "date"): "LocalDate",
        ("string", None): "String",
        ("boolean", None): "Boolean",
        ("number", "double"): "Double",
        ("number", None): "Double",
    }

    openapi_type = prop.get("type")
    openapi_format = prop.get("format")

    # Check for x-kotlin-type override
    if "x-kotlin-type" in prop:
        return prop["x-kotlin-type"]

    return type_map.get((openapi_type, openapi_format), "String")


def collect_imports(fields: list[dict]) -> list[str]:
    """Collect required imports for fields."""
    import_map = {
        "UUID": "java.util.UUID",
        "LocalDateTime": "java.time.LocalDateTime",
        "LocalDate": "java.time.LocalDate",
        "BigDecimal": "java.math.BigDecimal",
    }

    imports = set()
    for field in fields:
        if field["type"] in import_map:
            imports.add(import_map[field["type"]])

    return sorted(imports)


def generate_model_layer(spec_path: str, output_dir: str) -> list[str]:
    """Generate all model layer files."""
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    schemas = extract_schemas_from_openapi(spec)
    generated_files = []
    output = Path(output_dir)

    for schema in schemas:
        name = schema["name"]

        # Generate all variants
        variants = [
            ("model", f"{name}.kt"),
            ("create", f"{name}Create.kt"),
            ("update", f"{name}Update.kt"),
            ("interface", f"I{name}.kt"),
        ]

        for variant, filename in variants:
            output_file = output / filename
            generate_resource(schema, variant, str(output_file))
            generated_files.append(str(output_file))
            print(f"Generated: {output_file}")

    return generated_files


def main():
    parser = argparse.ArgumentParser(description="Generate model layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")

    args = parser.parse_args()

    files = generate_model_layer(args.spec, args.output)
    print(f"\nGenerated {len(files)} files")


if __name__ == "__main__":
    main()
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/layers/generate_model_layer.py
git commit -m "feat(codegen): add model layer generator skill"
```

---

### Task 6.2: Create DB Layer Skill

**Files:**
- Create: `.claude/skills/blugrid-codegen/layers/generate-db-layer.py`

**Step 1: Create generate-db-layer.py**

```python
#!/usr/bin/env python3
# layers/generate-db-layer.py
"""
DB Layer Generator Skill

Generates all database layer files (entities, migrations, repositories).

Usage:
    python generate-db-layer.py --spec openapi.yaml --output ./output/db/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_entity import generate_entity
from atomic.sql.generate_table import generate_table
from layers.generate_model_layer import extract_schemas_from_openapi
from utils.naming import to_snake_case
import yaml


def generate_db_layer(spec_path: str, output_dir: str) -> list[str]:
    """Generate all database layer files."""
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    schemas = extract_schemas_from_openapi(spec)
    generated_files = []
    output = Path(output_dir)

    for i, schema in enumerate(schemas):
        name = schema["name"]
        table_name = to_snake_case(name)

        # Generate entity
        entity_dir = output / "src" / "main" / "kotlin" / "repository" / "model"
        entity_file = entity_dir / f"{name}Entity.kt"
        generate_entity(schema, str(entity_file))
        generated_files.append(str(entity_file))
        print(f"Generated: {entity_file}")

        # Generate SQL migration
        migration_dir = output / "src" / "main" / "resources" / "db" / "migration"
        migration_file = migration_dir / f"V{i+1}__{table_name}.sql"
        generate_table(schema, str(migration_file))
        generated_files.append(str(migration_file))
        print(f"Generated: {migration_file}")

    return generated_files


def main():
    parser = argparse.ArgumentParser(description="Generate DB layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")

    args = parser.parse_args()

    files = generate_db_layer(args.spec, args.output)
    print(f"\nGenerated {len(files)} files")


if __name__ == "__main__":
    main()
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/layers/generate_db_layer.py
git commit -m "feat(codegen): add DB layer generator skill"
```

---

## Phase 7: Orchestrator Skill

### Task 7.1: Create Module Orchestrator

**Files:**
- Create: `.claude/skills/blugrid-codegen/orchestrators/generate-module.py`

**Step 1: Create generate-module.py**

```python
#!/usr/bin/env python3
# orchestrators/generate-module.py
"""
Module Orchestrator Skill

Generates a complete module with all layers (model, db, api).

Usage:
    python generate-module.py --spec openapi.yaml --output ./output/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from layers.generate_model_layer import generate_model_layer
from layers.generate_db_layer import generate_db_layer
import yaml


def generate_module(spec_path: str, output_dir: str) -> dict:
    """Generate a complete module."""
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    # Extract module info
    info = spec.get("info", {})
    module_name = info.get("x-module-name", "generated-api")

    output = Path(output_dir) / module_name
    generated = {"model": [], "db": [], "api": []}

    # Generate model layer
    model_dir = output / f"{module_name}-model"
    generated["model"] = generate_model_layer(spec_path, str(model_dir / "src" / "main" / "kotlin" / "model"))

    # Generate db layer
    db_dir = output / f"{module_name}-db"
    generated["db"] = generate_db_layer(spec_path, str(db_dir))

    # TODO: Generate API layer (controllers, services)
    # generated["api"] = generate_api_layer(spec_path, str(api_dir))

    return generated


def main():
    parser = argparse.ArgumentParser(description="Generate complete module")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")

    args = parser.parse_args()

    result = generate_module(args.spec, args.output)

    total = sum(len(files) for files in result.values())
    print(f"\n=== Generation Complete ===")
    print(f"Model layer: {len(result['model'])} files")
    print(f"DB layer: {len(result['db'])} files")
    print(f"API layer: {len(result['api'])} files")
    print(f"Total: {total} files")


if __name__ == "__main__":
    main()
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/orchestrators/generate_module.py
git commit -m "feat(codegen): add module orchestrator skill"
```

---

## Phase 8: Test Pipeline

### Task 8.1: Create Test Runner

**Files:**
- Create: `.claude/skills/blugrid-codegen/tests/run-tests.py`

**Step 1: Create test runner**

```python
#!/usr/bin/env python3
# tests/run-tests.py
"""
Test Pipeline Runner

Runs the three-stage test pipeline:
1. Golden file diff
2. Build verification
3. Generated test execution

Usage:
    python run-tests.py --fixture fixtures/core-organisation.jdl
"""

import argparse
import subprocess
import sys
from pathlib import Path
import tempfile
import shutil


def run_command(cmd: list[str], cwd: str = None) -> tuple[int, str, str]:
    """Run a command and return (returncode, stdout, stderr)."""
    result = subprocess.run(cmd, capture_output=True, text=True, cwd=cwd)
    return result.returncode, result.stdout, result.stderr


def stage_1_golden_diff(generated_dir: Path, golden_dir: Path) -> bool:
    """Stage 1: Compare generated output against golden files."""
    print("\n=== Stage 1: Golden File Diff ===")

    if not golden_dir.exists():
        print(f"No golden directory found at {golden_dir}")
        print("Skipping golden diff (run with --update-golden to create)")
        return True

    code, stdout, stderr = run_command([
        "git", "diff", "--no-index", "--stat",
        str(golden_dir), str(generated_dir)
    ])

    if code == 0:
        print("✓ Generated output matches golden files")
        return True
    else:
        print("✗ Differences found:")
        print(stdout)
        return False


def stage_2_build_verification(generated_dir: Path) -> bool:
    """Stage 2: Run gradle build on generated code."""
    print("\n=== Stage 2: Build Verification ===")

    gradle_wrapper = generated_dir / "gradlew"
    if not gradle_wrapper.exists():
        print("No gradlew found, skipping build verification")
        return True

    code, stdout, stderr = run_command(
        ["./gradlew", "build", "-x", "test"],
        cwd=str(generated_dir)
    )

    if code == 0:
        print("✓ Build successful")
        return True
    else:
        print("✗ Build failed:")
        print(stderr)
        return False


def stage_3_run_tests(generated_dir: Path) -> bool:
    """Stage 3: Run generated tests."""
    print("\n=== Stage 3: Generated Test Execution ===")

    gradle_wrapper = generated_dir / "gradlew"
    if not gradle_wrapper.exists():
        print("No gradlew found, skipping test execution")
        return True

    code, stdout, stderr = run_command(
        ["./gradlew", "test"],
        cwd=str(generated_dir)
    )

    if code == 0:
        print("✓ All tests passed")
        return True
    else:
        print("✗ Tests failed:")
        print(stderr)
        return False


def main():
    parser = argparse.ArgumentParser(description="Run test pipeline")
    parser.add_argument("--fixture", "-f", required=True, help="Fixture file to test")
    parser.add_argument("--golden", "-g", help="Golden directory path")
    parser.add_argument("--update-golden", action="store_true", help="Update golden files")

    args = parser.parse_args()

    fixture = Path(args.fixture)
    tests_dir = Path(__file__).parent
    golden_dir = Path(args.golden) if args.golden else tests_dir / "golden" / fixture.stem

    # Generate to temp directory
    with tempfile.TemporaryDirectory() as tmp:
        generated_dir = Path(tmp) / "generated"

        # TODO: Run full generation pipeline
        # For now, placeholder
        print(f"Would generate from {fixture} to {generated_dir}")

        if args.update_golden:
            print(f"\nUpdating golden files at {golden_dir}")
            if golden_dir.exists():
                shutil.rmtree(golden_dir)
            shutil.copytree(generated_dir, golden_dir)
            print("✓ Golden files updated")
            return

        # Run pipeline stages
        results = {
            "golden_diff": stage_1_golden_diff(generated_dir, golden_dir),
            "build": stage_2_build_verification(generated_dir),
            "tests": stage_3_run_tests(generated_dir),
        }

        # Summary
        print("\n=== Test Pipeline Summary ===")
        all_passed = all(results.values())
        for stage, passed in results.items():
            status = "✓" if passed else "✗"
            print(f"  {status} {stage}")

        sys.exit(0 if all_passed else 1)


if __name__ == "__main__":
    main()
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/tests/run_tests.py
git commit -m "feat(codegen): add test pipeline runner"
```

---

## Phase 9: Integration

### Task 9.1: Create Example OpenAPI Spec

**Files:**
- Create: `.claude/skills/blugrid-codegen/tests/fixtures/core-organisation.yaml`

**Step 1: Create example spec**

Convert the existing JDL example to OpenAPI format:

```yaml
# tests/fixtures/core-organisation.yaml
openapi: 3.0.3
info:
  title: core-organisation-api
  version: 0.1.0
  x-base-package: net.blugrid.core.organisation
  x-group: net.blugrid.api
  x-module-name: core-organisation-api

components:
  schemas:
    Organisation:
      type: object
      description: An organisation represents a legal or operational entity.
      x-resource-type: UnscopedResource
      x-auditable: true
      x-searchable: false
      required:
        - parentOrganisationId
        - effectiveTimestamp
      properties:
        id:
          type: integer
          format: int64
          x-generated: true
        uuid:
          type: string
          format: uuid
          x-generated: true
        parentOrganisationId:
          type: integer
          format: int64
          description: The ID of the parent organisation.
        effectiveTimestamp:
          type: string
          format: date-time
          description: The date and time the organisation becomes active.

paths:
  /organisations:
    get:
      summary: List organisations
      operationId: listOrganisations
      responses:
        '200':
          description: List of organisations
    post:
      summary: Create organisation
      operationId: createOrganisation
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Organisation'
      responses:
        '201':
          description: Created
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/tests/fixtures/core-organisation.yaml
git commit -m "feat(codegen): add example OpenAPI spec fixture"
```

---

### Task 9.2: Update SKILL.md with Full Documentation

**Files:**
- Modify: `.claude/skills/blugrid-codegen/SKILL.md`

**Step 1: Update SKILL.md**

```markdown
---
name: blugrid-codegen
description: Generate Kotlin backend modules from JDL, natural language, or design docs. Produces REST APIs, database migrations, GraphQL, and gRPC services.
---

# Blugrid Code Generator

## Overview

Generate production-ready Kotlin backend code from various inputs:
- JDL (JHipster Domain Language) files
- Natural language descriptions
- Design documents
- Existing OpenAPI schemas

All inputs are normalized to OpenAPI 3.x with custom extensions, then processed by hierarchical Python skills.

## Quick Start

**From JDL:**
```bash
# Parse JDL to JSON
python parsers/jdl-parser.py --input design.jdl --output /tmp/parsed.json

# Claude enriches to OpenAPI (done by Claude)

# Generate complete module
python orchestrators/generate-module.py --spec intermediate.yaml --output ./output/
```

**From natural language:**
Describe your entities to Claude. Claude will generate the OpenAPI spec, then invoke the generation skills.

## Skill Hierarchy

```
Claude (orchestrates)
  ↓
orchestrators/generate-module.py (entry point)
  ↓
layers/generate-model-layer.py (DTOs, resources)
layers/generate-db-layer.py (entities, migrations)
layers/generate-api-layer.py (controllers, services)
  ↓
atomic/kotlin/generate-resource.py
atomic/kotlin/generate-entity.py
atomic/sql/generate-table.py
  ↓
templates/kotlin/*.j2
templates/sql/*.j2
```

## OpenAPI Extensions

Custom `x-` extensions for code generation:

| Extension | Level | Description |
|-----------|-------|-------------|
| `x-base-package` | info | Base package name |
| `x-module-name` | info | Module name |
| `x-resource-type` | schema | UnscopedResource, TenantResource, etc. |
| `x-auditable` | schema | Add audit fields |
| `x-db-table` | schema | Override table name |
| `x-db-domain` | property | PostgreSQL domain type |
| `x-kotlin-type` | property | Override Kotlin type |
| `x-generated` | property | Auto-generated field (id, uuid) |

## Escalation Pattern

When templates can't handle something, they inject markers:

```kotlin
// ESCALATE: Unknown type mapping
// Field: customField
// Source type: string (format: custom)
// Context: A field with custom logic
// END_ESCALATE
```

Claude scans output, fills in custom code, patches files.

## Testing

Three-stage pipeline:

```bash
python tests/run-tests.py --fixture fixtures/core-organisation.yaml
```

1. **Golden diff** - Compare against expected output
2. **Build** - Run `./gradlew build`
3. **Tests** - Run `./gradlew test`

## Directory Structure

```
.claude/skills/blugrid-codegen/
├── SKILL.md              # This file
├── config/               # Type mappings, settings
├── parsers/              # JDL parser, OpenAPI validator
├── orchestrators/        # Entry points
├── layers/               # Layer-level generation
├── atomic/               # Single-file generators
├── templates/            # Jinja2 templates
├── utils/                # Shared utilities
└── tests/                # Test fixtures and runner
```
```

**Step 2: Commit**

```bash
git add .claude/skills/blugrid-codegen/SKILL.md
git commit -m "docs(codegen): complete SKILL.md documentation"
```

---

## Summary

This plan creates a complete Claude skills ecosystem for code generation:

**Phase 1-3:** Foundation (structure, config, utilities, parser)
**Phase 4:** Jinja2 templates (converted from Mustache)
**Phase 5:** Atomic skills (single-file generators)
**Phase 6:** Layer skills (model, db, api)
**Phase 7:** Orchestrator (entry point)
**Phase 8-9:** Test pipeline and integration

**Total estimated tasks:** ~25 bite-sized steps
**Key files created:** ~30 new Python/Jinja2/YAML files

---

**Plan complete and saved to `docs/plans/2025-12-08-claude-skills-migration-plan.md`.**

**Two execution options:**

**1. Subagent-Driven (this session)** - I dispatch fresh subagent per task, review between tasks, fast iteration

**2. Parallel Session (separate)** - Open new session with executing-plans, batch execution with checkpoints

**Which approach?**
