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
