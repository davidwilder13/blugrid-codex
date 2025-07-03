# Template Components: Common

This directory contains TypeScript modules and Mustache templates for generating common build-related artifacts for Kotlin modules, including Gradle build files and wrapper scripts.

## Overview

- **GradleBuildFileTemplate.ts**: Mustache-based template for `build.gradle.kts` (Gradle Kotlin DSL) configuration.
- **GradlePropertiesTemplate.ts**: Template for `gradle.properties` file containing project-specific properties.
- **gradlew.mustache** / **gradlew.bat.mustache**: Wrapper script templates for Unix/Linux and Windows respectively.

## Coding Standards

- **Module Structure**: Export a typed props interface and a rendering function or constant per template module.
- **Template Literals**: Use `String.raw` for multi-line template literals to preserve formatting.
- **Props Typing**: Define a `<Name>TemplateProps` interface for each template's inputs.
- **Rendering**: Use `Mustache.render(template, context)` to produce the final output.
- **Naming Conventions**:
  - Template modules: `<Thing>Template.ts`
  - Raw Mustache files: lowercase names matching the generated scripts.
- **Formatting**:
  - 2-space indentation in templates.
  - No trailing whitespace.
  - Avoid interpolation of non-literal strings within template bodies.