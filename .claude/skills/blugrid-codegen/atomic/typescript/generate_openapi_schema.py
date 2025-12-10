"""
Generate OpenAPI 3.1 schema from entity definition.

Used for contract testing - TypeScript client validates against this schema.
The Kotlin API should also generate/validate against the same schema.
"""

from jinja2 import Template
from dataclasses import dataclass
from typing import List, Optional
import json


@dataclass
class OpenApiField:
    name: str
    jdl_type: str
    required: bool
    description: Optional[str] = None


# JDL to OpenAPI type mapping
JDL_TO_OPENAPI = {
    "String": {"type": "string"},
    "Integer": {"type": "integer", "format": "int32"},
    "Long": {"type": "integer", "format": "int64"},
    "Boolean": {"type": "boolean"},
    "LocalDate": {"type": "string", "format": "date"},
    "LocalDateTime": {"type": "string", "format": "date-time"},
    "Instant": {"type": "string", "format": "date-time"},
    "UUID": {"type": "string", "format": "uuid"},
    "BigDecimal": {"type": "number"},
    "Double": {"type": "number", "format": "double"},
    "Float": {"type": "number", "format": "float"},
}


def generate_openapi_schema(
    entity_name: str,
    entity_name_lower: str,
    entity_name_plural: str,
    fields: List[OpenApiField],
    base_path: str,
    include_audit_fields: bool = True,
) -> dict:
    """Generate OpenAPI 3.1 schema as a Python dict."""

    # Build entity schema properties
    properties = {
        "id": {"type": "integer", "format": "int64", "readOnly": True},
        "uuid": {"type": "string", "format": "uuid"},
    }
    required_fields = ["id", "uuid"]

    for field in fields:
        prop = JDL_TO_OPENAPI.get(field.jdl_type, {"type": "string"}).copy()
        if field.description:
            prop["description"] = field.description
        properties[field.name] = prop
        if field.required:
            required_fields.append(field.name)

    if include_audit_fields:
        properties.update({
            "createdDate": {"type": "string", "format": "date-time", "readOnly": True},
            "createdBy": {"type": "string", "readOnly": True},
            "updatedDate": {"type": "string", "format": "date-time", "readOnly": True},
            "updatedBy": {"type": "string", "readOnly": True},
        })

    # Build create input schema (exclude id, uuid, audit fields)
    create_properties = {}
    create_required = []
    for field in fields:
        prop = JDL_TO_OPENAPI.get(field.jdl_type, {"type": "string"}).copy()
        if field.description:
            prop["description"] = field.description
        create_properties[field.name] = prop
        if field.required:
            create_required.append(field.name)

    # Build update input schema (all fields optional)
    update_properties = {}
    for field in fields:
        prop = JDL_TO_OPENAPI.get(field.jdl_type, {"type": "string"}).copy()
        if field.description:
            prop["description"] = field.description
        update_properties[field.name] = prop

    schema = {
        "openapi": "3.1.0",
        "info": {
            "title": f"{entity_name} API",
            "version": "1.0.0",
            "description": f"API for managing {entity_name_plural}",
        },
        "servers": [
            {"url": "http://localhost:8080", "description": "Local development"},
        ],
        "paths": {
            base_path: {
                "get": {
                    "operationId": f"get{entity_name_plural}",
                    "summary": f"Get paginated list of {entity_name_plural}",
                    "tags": [entity_name],
                    "parameters": [
                        {"name": "page", "in": "query", "schema": {"type": "integer", "default": 0}},
                        {"name": "size", "in": "query", "schema": {"type": "integer", "default": 20}},
                        {"name": "sort", "in": "query", "schema": {"type": "string"}},
                    ],
                    "responses": {
                        "200": {
                            "description": f"Paginated list of {entity_name_plural}",
                            "content": {
                                "application/json": {
                                    "schema": {"$ref": f"#/components/schemas/{entity_name}Page"}
                                }
                            }
                        }
                    }
                },
                "post": {
                    "operationId": f"create{entity_name}",
                    "summary": f"Create a new {entity_name}",
                    "tags": [entity_name],
                    "requestBody": {
                        "required": True,
                        "content": {
                            "application/json": {
                                "schema": {"$ref": f"#/components/schemas/{entity_name}Create"}
                            }
                        }
                    },
                    "responses": {
                        "201": {
                            "description": f"Created {entity_name}",
                            "content": {
                                "application/json": {
                                    "schema": {"$ref": f"#/components/schemas/{entity_name}"}
                                }
                            }
                        }
                    }
                }
            },
            f"{base_path}/{{id}}": {
                "get": {
                    "operationId": f"get{entity_name}ById",
                    "summary": f"Get {entity_name} by ID",
                    "tags": [entity_name],
                    "parameters": [
                        {"name": "id", "in": "path", "required": True, "schema": {"type": "integer", "format": "int64"}}
                    ],
                    "responses": {
                        "200": {
                            "description": f"The {entity_name}",
                            "content": {
                                "application/json": {
                                    "schema": {"$ref": f"#/components/schemas/{entity_name}"}
                                }
                            }
                        },
                        "404": {"description": f"{entity_name} not found"}
                    }
                },
                "put": {
                    "operationId": f"update{entity_name}",
                    "summary": f"Update an existing {entity_name}",
                    "tags": [entity_name],
                    "parameters": [
                        {"name": "id", "in": "path", "required": True, "schema": {"type": "integer", "format": "int64"}}
                    ],
                    "requestBody": {
                        "required": True,
                        "content": {
                            "application/json": {
                                "schema": {"$ref": f"#/components/schemas/{entity_name}Update"}
                            }
                        }
                    },
                    "responses": {
                        "200": {
                            "description": f"Updated {entity_name}",
                            "content": {
                                "application/json": {
                                    "schema": {"$ref": f"#/components/schemas/{entity_name}"}
                                }
                            }
                        },
                        "404": {"description": f"{entity_name} not found"}
                    }
                },
                "delete": {
                    "operationId": f"delete{entity_name}",
                    "summary": f"Delete a {entity_name}",
                    "tags": [entity_name],
                    "parameters": [
                        {"name": "id", "in": "path", "required": True, "schema": {"type": "integer", "format": "int64"}}
                    ],
                    "responses": {
                        "204": {"description": f"{entity_name} deleted"},
                        "404": {"description": f"{entity_name} not found"}
                    }
                }
            },
            f"{base_path}/uuid/{{uuid}}": {
                "get": {
                    "operationId": f"get{entity_name}ByUuid",
                    "summary": f"Get {entity_name} by UUID",
                    "tags": [entity_name],
                    "parameters": [
                        {"name": "uuid", "in": "path", "required": True, "schema": {"type": "string", "format": "uuid"}}
                    ],
                    "responses": {
                        "200": {
                            "description": f"The {entity_name}",
                            "content": {
                                "application/json": {
                                    "schema": {"$ref": f"#/components/schemas/{entity_name}"}
                                }
                            }
                        },
                        "404": {"description": f"{entity_name} not found"}
                    }
                }
            },
            f"{base_path}/all": {
                "get": {
                    "operationId": f"getAll{entity_name_plural}",
                    "summary": f"Get all {entity_name_plural}",
                    "tags": [entity_name],
                    "responses": {
                        "200": {
                            "description": f"List of all {entity_name_plural}",
                            "content": {
                                "application/json": {
                                    "schema": {
                                        "type": "array",
                                        "items": {"$ref": f"#/components/schemas/{entity_name}"}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        "components": {
            "schemas": {
                entity_name: {
                    "type": "object",
                    "properties": properties,
                    "required": required_fields,
                },
                f"{entity_name}Create": {
                    "type": "object",
                    "properties": create_properties,
                    "required": create_required if create_required else None,
                },
                f"{entity_name}Update": {
                    "type": "object",
                    "properties": update_properties,
                },
                f"{entity_name}Page": {
                    "type": "object",
                    "properties": {
                        "content": {
                            "type": "array",
                            "items": {"$ref": f"#/components/schemas/{entity_name}"}
                        },
                        "totalElements": {"type": "integer", "format": "int64"},
                        "totalPages": {"type": "integer"},
                        "size": {"type": "integer"},
                        "number": {"type": "integer"},
                        "first": {"type": "boolean"},
                        "last": {"type": "boolean"},
                        "empty": {"type": "boolean"},
                    },
                    "required": ["content", "totalElements", "totalPages", "size", "number", "first", "last", "empty"],
                }
            }
        }
    }

    # Remove None required arrays
    if schema["components"]["schemas"][f"{entity_name}Create"]["required"] is None:
        del schema["components"]["schemas"][f"{entity_name}Create"]["required"]

    return schema


def generate_openapi_yaml(
    entity_name: str,
    entity_name_lower: str,
    entity_name_plural: str,
    fields: List[OpenApiField],
    base_path: str,
) -> str:
    """Generate OpenAPI schema as YAML string."""
    import yaml
    schema = generate_openapi_schema(
        entity_name, entity_name_lower, entity_name_plural, fields, base_path
    )
    return yaml.dump(schema, sort_keys=False, allow_unicode=True)


def generate_openapi_json(
    entity_name: str,
    entity_name_lower: str,
    entity_name_plural: str,
    fields: List[OpenApiField],
    base_path: str,
) -> str:
    """Generate OpenAPI schema as JSON string."""
    schema = generate_openapi_schema(
        entity_name, entity_name_lower, entity_name_plural, fields, base_path
    )
    return json.dumps(schema, indent=2)


# Example usage
if __name__ == "__main__":
    fields = [
        OpenApiField(name="parentOrganisationId", jdl_type="Long", required=True),
        OpenApiField(name="effectiveTimestamp", jdl_type="LocalDateTime", required=True),
    ]

    print("=== openapi.json ===")
    print(generate_openapi_json(
        entity_name="Organisation",
        entity_name_lower="organisation",
        entity_name_plural="Organisations",
        fields=fields,
        base_path="/api/organisations",
    ))
