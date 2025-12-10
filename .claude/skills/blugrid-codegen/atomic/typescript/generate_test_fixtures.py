"""
Generate test fixtures for TypeScript client tests.

Creates factory functions for generating valid test data
that matches the OpenAPI schema.
"""

from jinja2 import Template
from dataclasses import dataclass
from typing import List, Optional


@dataclass
class FixtureField:
    name: str
    jdl_type: str
    required: bool
    default_value: Optional[str] = None  # TypeScript expression


# Default fixture values by JDL type
JDL_FIXTURE_DEFAULTS = {
    "String": "faker.string.alphanumeric(10)",
    "Integer": "faker.number.int({ min: 1, max: 1000 })",
    "Long": "faker.number.int({ min: 1, max: 100000 })",
    "Boolean": "faker.datatype.boolean()",
    "LocalDate": "faker.date.recent().toISOString().split('T')[0]",
    "LocalDateTime": "faker.date.recent().toISOString()",
    "Instant": "faker.date.recent().toISOString()",
    "UUID": "faker.string.uuid()",
    "BigDecimal": "faker.number.float({ min: 0, max: 10000, fractionDigits: 2 })",
    "Double": "faker.number.float({ min: 0, max: 10000, fractionDigits: 4 })",
    "Float": "faker.number.float({ min: 0, max: 1000, fractionDigits: 2 })",
}


FIXTURES_TEMPLATE = Template('''/**
 * {{ entity_name }} Test Fixtures
 * Factory functions for generating valid test data.
 * Auto-generated - do not edit manually.
 */

import { faker } from '@faker-js/faker';
import { {{ entity_name }}, {{ entity_name }}Create, {{ entity_name }}Update } from '../src';

// Seed faker for reproducible tests (optional)
// faker.seed(12345);

/**
 * Raw JSON fixtures (for MSW mocking).
 */
export const {{ entity_name_lower }}Fixtures = {
  /**
   * Generate a valid {{ entity_name }} JSON object.
   */
  valid(overrides: Partial<Record<string, unknown>> = {}): Record<string, unknown> {
    return {
      id: faker.number.int({ min: 1, max: 100000 }),
      uuid: faker.string.uuid(),
{%- for field in fields %}
      {{ field.name }}: {{ field.fixture_value }},
{%- endfor %}
      createdDate: faker.date.past().toISOString(),
      createdBy: faker.internet.email(),
      updatedDate: faker.date.recent().toISOString(),
      updatedBy: faker.internet.email(),
      ...overrides,
    };
  },

  /**
   * Generate a valid {{ entity_name }}Create input.
   */
  createInput(overrides: Partial<Record<string, unknown>> = {}): {{ entity_name }}Create {
    const data = {
{%- for field in create_fields %}
      {{ field.name }}: {{ field.fixture_value }},
{%- endfor %}
      ...overrides,
    };
    return {{ entity_name }}Create.fromJson(data);
  },

  /**
   * Generate a valid {{ entity_name }}Update input.
   */
  updateInput(overrides: Partial<Record<string, unknown>> = {}): {{ entity_name }}Update {
    const data = {
{%- for field in update_fields %}
      {{ field.name }}: {{ field.fixture_value }},
{%- endfor %}
      ...overrides,
    };
    return {{ entity_name }}Update.fromJson(data);
  },

  /**
   * Generate a paginated response.
   */
  page(count: number = 3, overrides: Partial<Record<string, unknown>> = {}): Record<string, unknown> {
    const content = Array.from({ length: count }, () => {{ entity_name_lower }}Fixtures.valid());
    return {
      content,
      totalElements: count,
      totalPages: 1,
      size: 20,
      number: 0,
      first: true,
      last: true,
      empty: count === 0,
      ...overrides,
    };
  },

  /**
   * Generate an array of valid {{ entity_name }} objects.
   */
  list(count: number = 3): Record<string, unknown>[] {
    return Array.from({ length: count }, () => {{ entity_name_lower }}Fixtures.valid());
  },

  /**
   * Generate a {{ entity_name }} model instance (not raw JSON).
   */
  instance(overrides: Partial<Record<string, unknown>> = {}): {{ entity_name }} {
    return {{ entity_name }}.fromJson({{ entity_name_lower }}Fixtures.valid(overrides));
  },
};

/**
 * Strongly-typed fixture builders for more complex scenarios.
 */
export class {{ entity_name }}FixtureBuilder {
  private data: Record<string, unknown>;

  constructor() {
    this.data = {{ entity_name_lower }}Fixtures.valid();
  }

  withId(id: number): this {
    this.data.id = id;
    return this;
  }

  withUuid(uuid: string): this {
    this.data.uuid = uuid;
    return this;
  }

{%- for field in fields %}

  with{{ field.name_pascal }}(value: {{ field.ts_type }}): this {
    this.data.{{ field.name }} = {% if field.is_date %}value instanceof Date ? value.toISOString() : value{% else %}value{% endif %};
    return this;
  }
{%- endfor %}

  build(): Record<string, unknown> {
    return { ...this.data };
  }

  buildInstance(): {{ entity_name }} {
    return {{ entity_name }}.fromJson(this.data);
  }
}

/**
 * Factory function for builder pattern.
 */
export function a{{ entity_name }}(): {{ entity_name }}FixtureBuilder {
  return new {{ entity_name }}FixtureBuilder();
}
''')


def to_pascal_case(name: str) -> str:
    """Convert camelCase to PascalCase."""
    if not name:
        return name
    return name[0].upper() + name[1:]


def get_fixture_value(field: FixtureField) -> str:
    """Get the fixture value expression for a field."""
    if field.default_value:
        return field.default_value
    return JDL_FIXTURE_DEFAULTS.get(field.jdl_type, "'test-value'")


def get_ts_type(jdl_type: str) -> str:
    """Get TypeScript type for builder method."""
    type_map = {
        "String": "string",
        "Integer": "number",
        "Long": "number",
        "Boolean": "boolean",
        "LocalDate": "Date | string",
        "LocalDateTime": "Date | string",
        "Instant": "Date | string",
        "UUID": "string",
        "BigDecimal": "number",
        "Double": "number",
        "Float": "number",
    }
    return type_map.get(jdl_type, "unknown")


def is_date_type(jdl_type: str) -> bool:
    """Check if type is a date type."""
    return jdl_type in {"LocalDate", "LocalDateTime", "Instant"}


def generate_test_fixtures(
    entity_name: str,
    entity_name_lower: str,
    fields: List[FixtureField],
) -> str:
    """Generate test fixtures for the entity."""

    # Prepare template data
    template_fields = []
    for field in fields:
        template_fields.append({
            "name": field.name,
            "name_pascal": to_pascal_case(field.name),
            "fixture_value": get_fixture_value(field),
            "ts_type": get_ts_type(field.jdl_type),
            "is_date": is_date_type(field.jdl_type),
        })

    # Create fields are same as entity fields (for required fields)
    create_fields = [f for f in template_fields]

    # Update fields are all optional, so same set
    update_fields = [f for f in template_fields]

    return FIXTURES_TEMPLATE.render(
        entity_name=entity_name,
        entity_name_lower=entity_name_lower,
        fields=template_fields,
        create_fields=create_fields,
        update_fields=update_fields,
    )


# Example usage
if __name__ == "__main__":
    fields = [
        FixtureField(name="parentOrganisationId", jdl_type="Long", required=True),
        FixtureField(name="effectiveTimestamp", jdl_type="LocalDateTime", required=True),
    ]

    print("=== fixtures.ts ===")
    print(generate_test_fixtures(
        entity_name="Organisation",
        entity_name_lower="organisation",
        fields=fields,
    ))
