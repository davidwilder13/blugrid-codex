"""
Generate TypeScript model classes with fromJson/toJson methods.

Generates class-based models with:
- Constructor with typed parameters
- Static fromJson() with proper type coercion (Date objects, numbers, booleans)
- toJson() for serialization
- Recursive fromJson for nested objects/arrays
"""

from jinja2 import Template
from typing import List, Dict, Any, Optional
from dataclasses import dataclass
from enum import Enum


class JdlType(Enum):
    STRING = "String"
    INTEGER = "Integer"
    LONG = "Long"
    BOOLEAN = "Boolean"
    LOCAL_DATE = "LocalDate"
    LOCAL_DATE_TIME = "LocalDateTime"
    INSTANT = "Instant"
    UUID = "UUID"
    BIG_DECIMAL = "BigDecimal"
    DOUBLE = "Double"
    FLOAT = "Float"
    ENUM = "Enum"
    BLOB = "Blob"
    TEXT_BLOB = "TextBlob"


@dataclass
class TypeScriptField:
    name: str
    jdl_type: str
    ts_type: str
    required: bool
    is_array: bool = False
    is_enum: bool = False
    enum_values: Optional[List[str]] = None
    is_relation: bool = False
    relation_entity: Optional[str] = None


# JDL to TypeScript type mapping
JDL_TO_TS_TYPE: Dict[str, str] = {
    "String": "string",
    "Integer": "number",
    "Long": "number",
    "Boolean": "boolean",
    "LocalDate": "Date",
    "LocalDateTime": "Date",
    "Instant": "Date",
    "UUID": "string",
    "BigDecimal": "number",
    "Double": "number",
    "Float": "number",
    "Blob": "Blob",
    "TextBlob": "string",
}

# Date types that need Date coercion
DATE_TYPES = {"LocalDate", "LocalDateTime", "Instant"}

# Number types that need Number coercion
NUMBER_TYPES = {"Integer", "Long", "BigDecimal", "Double", "Float"}


def get_ts_type(jdl_type: str, is_array: bool = False) -> str:
    """Convert JDL type to TypeScript type."""
    base_type = JDL_TO_TS_TYPE.get(jdl_type, jdl_type)
    if is_array:
        return f"{base_type}[]"
    return base_type


def get_from_json_coercion(field: TypeScriptField) -> str:
    """Generate the fromJson coercion expression for a field."""
    accessor = f"obj.{field.name}"

    if field.is_relation:
        # Nested object - use fromJson recursively
        if field.is_array:
            if field.required:
                return f"(obj.{field.name} as unknown[]).map({field.relation_entity}.fromJson)"
            else:
                return f"obj.{field.name} ? (obj.{field.name} as unknown[]).map({field.relation_entity}.fromJson) : undefined"
        else:
            if field.required:
                return f"{field.relation_entity}.fromJson(obj.{field.name})"
            else:
                return f"obj.{field.name} ? {field.relation_entity}.fromJson(obj.{field.name}) : undefined"

    if field.jdl_type in DATE_TYPES:
        if field.required:
            return f"new Date({accessor} as string)"
        else:
            return f"{accessor} ? new Date({accessor} as string) : undefined"

    if field.jdl_type in NUMBER_TYPES:
        if field.required:
            return f"Number({accessor})"
        else:
            return f"{accessor} !== undefined && {accessor} !== null ? Number({accessor}) : undefined"

    if field.jdl_type == "Boolean":
        if field.required:
            return f"Boolean({accessor})"
        else:
            return f"{accessor} !== undefined && {accessor} !== null ? Boolean({accessor}) : undefined"

    if field.is_array:
        if field.required:
            return f"{accessor} as {field.ts_type}"
        else:
            return f"{accessor} ? {accessor} as {field.ts_type} : undefined"

    # String and other types
    if field.required:
        return f"String({accessor})"
    else:
        return f"{accessor} ? String({accessor}) : undefined"


def get_to_json_expression(field: TypeScriptField) -> str:
    """Generate the toJson expression for a field."""
    accessor = f"this.{field.name}"

    if field.is_relation:
        if field.is_array:
            if field.required:
                return f"{accessor}.map(item => item.toJson())"
            else:
                return f"{accessor}?.map(item => item.toJson())"
        else:
            if field.required:
                return f"{accessor}.toJson()"
            else:
                return f"{accessor}?.toJson()"

    if field.jdl_type in DATE_TYPES:
        if field.required:
            return f"{accessor}.toISOString()"
        else:
            return f"{accessor}?.toISOString()"

    # All other types serialize directly
    return accessor


MODEL_CLASS_TEMPLATE = Template('''/**
 * {{ class_name }} model class.
 * Auto-generated from JDL entity definition.
 */
export class {{ class_name }} {
  constructor(
{%- for field in fields %}
    public readonly {{ field.name }}{% if not field.required %}?{% endif %}: {{ field.ts_type }},
{%- endfor %}
  ) {}

  /**
   * Create a {{ class_name }} instance from a JSON object.
   * Handles type coercion for dates, numbers, and booleans.
   */
  static fromJson(json: unknown): {{ class_name }} {
    const obj = json as Record<string, unknown>;
    return new {{ class_name }}(
{%- for field in fields %}
      {{ field.from_json_expr }},
{%- endfor %}
    );
  }

  /**
   * Convert this instance to a JSON-serializable object.
   */
  toJson(): Record<string, unknown> {
    return {
{%- for field in fields %}
      {{ field.name }}: {{ field.to_json_expr }},
{%- endfor %}
    };
  }
}
''')


CREATE_INPUT_TEMPLATE = Template('''/**
 * Input for creating a new {{ entity_name }}.
 * Auto-generated from JDL entity definition.
 */
export class {{ class_name }} {
  constructor(
{%- for field in fields %}
    public readonly {{ field.name }}{% if not field.required %}?{% endif %}: {{ field.ts_type }},
{%- endfor %}
  ) {}

  /**
   * Create a {{ class_name }} instance from a JSON object.
   */
  static fromJson(json: unknown): {{ class_name }} {
    const obj = json as Record<string, unknown>;
    return new {{ class_name }}(
{%- for field in fields %}
      {{ field.from_json_expr }},
{%- endfor %}
    );
  }

  /**
   * Convert this instance to a JSON-serializable object for API requests.
   */
  toJson(): Record<string, unknown> {
    return {
{%- for field in fields %}
      {{ field.name }}: {{ field.to_json_expr }},
{%- endfor %}
    };
  }
}
''')


UPDATE_INPUT_TEMPLATE = Template('''/**
 * Input for updating an existing {{ entity_name }}.
 * All fields are optional for partial updates.
 * Auto-generated from JDL entity definition.
 */
export class {{ class_name }} {
  constructor(
{%- for field in fields %}
    public readonly {{ field.name }}?: {{ field.ts_type }},
{%- endfor %}
  ) {}

  /**
   * Create a {{ class_name }} instance from a JSON object.
   */
  static fromJson(json: unknown): {{ class_name }} {
    const obj = json as Record<string, unknown>;
    return new {{ class_name }}(
{%- for field in fields %}
      {{ field.from_json_expr_optional }},
{%- endfor %}
    );
  }

  /**
   * Convert this instance to a JSON-serializable object for API requests.
   * Only includes defined fields.
   */
  toJson(): Record<string, unknown> {
    const result: Record<string, unknown> = {};
{%- for field in fields %}
    if (this.{{ field.name }} !== undefined) {
      result.{{ field.name }} = {{ field.to_json_expr }};
    }
{%- endfor %}
    return result;
  }
}
''')


def generate_model_class(
    entity_name: str,
    fields: List[TypeScriptField],
    include_audit_fields: bool = True,
) -> str:
    """Generate a TypeScript model class."""

    # Add standard ID field
    all_fields = [
        TypeScriptField(name="id", jdl_type="Long", ts_type="number", required=True),
        TypeScriptField(name="uuid", jdl_type="UUID", ts_type="string", required=True),
    ]

    # Add entity-specific fields
    all_fields.extend(fields)

    # Add audit fields if enabled
    if include_audit_fields:
        audit_fields = [
            TypeScriptField(name="createdDate", jdl_type="Instant", ts_type="Date", required=False),
            TypeScriptField(name="createdBy", jdl_type="String", ts_type="string", required=False),
            TypeScriptField(name="updatedDate", jdl_type="Instant", ts_type="Date", required=False),
            TypeScriptField(name="updatedBy", jdl_type="String", ts_type="string", required=False),
        ]
        all_fields.extend(audit_fields)

    # Prepare template data
    template_fields = []
    for field in all_fields:
        template_fields.append({
            "name": field.name,
            "ts_type": field.ts_type,
            "required": field.required,
            "from_json_expr": get_from_json_coercion(field),
            "to_json_expr": get_to_json_expression(field),
        })

    return MODEL_CLASS_TEMPLATE.render(
        class_name=entity_name,
        fields=template_fields,
    )


def generate_create_input(
    entity_name: str,
    fields: List[TypeScriptField],
) -> str:
    """Generate a TypeScript create input class."""

    # Prepare template data (exclude id, uuid, audit fields)
    template_fields = []
    for field in fields:
        template_fields.append({
            "name": field.name,
            "ts_type": field.ts_type,
            "required": field.required,
            "from_json_expr": get_from_json_coercion(field),
            "to_json_expr": get_to_json_expression(field),
        })

    return CREATE_INPUT_TEMPLATE.render(
        entity_name=entity_name,
        class_name=f"{entity_name}Create",
        fields=template_fields,
    )


def generate_update_input(
    entity_name: str,
    fields: List[TypeScriptField],
) -> str:
    """Generate a TypeScript update input class (all fields optional)."""

    # Prepare template data with all fields optional
    template_fields = []
    for field in fields:
        # Create a copy with required=False for optional coercion
        optional_field = TypeScriptField(
            name=field.name,
            jdl_type=field.jdl_type,
            ts_type=field.ts_type,
            required=False,
            is_array=field.is_array,
            is_enum=field.is_enum,
            enum_values=field.enum_values,
            is_relation=field.is_relation,
            relation_entity=field.relation_entity,
        )
        template_fields.append({
            "name": field.name,
            "ts_type": field.ts_type,
            "from_json_expr_optional": get_from_json_coercion(optional_field),
            "to_json_expr": get_to_json_expression(optional_field),
        })

    return UPDATE_INPUT_TEMPLATE.render(
        entity_name=entity_name,
        class_name=f"{entity_name}Update",
        fields=template_fields,
    )


# Example usage
if __name__ == "__main__":
    # Example: Organisation entity
    organisation_fields = [
        TypeScriptField(
            name="parentOrganisationId",
            jdl_type="Long",
            ts_type="number",
            required=True,
        ),
        TypeScriptField(
            name="effectiveTimestamp",
            jdl_type="LocalDateTime",
            ts_type="Date",
            required=True,
        ),
    ]

    print("=== Organisation.ts ===")
    print(generate_model_class("Organisation", organisation_fields))
    print("\n=== OrganisationCreate.ts ===")
    print(generate_create_input("Organisation", organisation_fields))
    print("\n=== OrganisationUpdate.ts ===")
    print(generate_update_input("Organisation", organisation_fields))
