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
            "packageName": "net.blugrid.core.organisation",
            "resourceType": "UnscopedResource",
            "nameLower": "organisation",
            "nameUpperSnake": "ORGANISATION",
            "fields": [
                {
                    "name": "parentOrganisationId",
                    "type": "Long",
                    "required": True,
                    "description": "The ID of the parent organisation",
                    "example": "123",
                },
                {
                    "name": "effectiveTimestamp",
                    "type": "LocalDateTime",
                    "required": True,
                    "description": "When the organisation becomes active",
                    "example": "2023-01-01T00:00:00",
                },
            ],
            "imports": [
                {"value": "java.time.LocalDateTime"}
            ],
        }

    def test_generate_model_variant(self, sample_schema, tmp_path):
        output_file = tmp_path / "Organisation.kt"
        generate_resource(sample_schema, "model", str(output_file))

        content = output_file.read_text()
        assert "data class Organisation(" in content
        assert "var parentOrganisationId: Long" in content
        assert "UnscopedResource" in content
        assert "override var id: Long" in content
        assert "override var uuid: UUID" in content

    def test_generate_create_variant(self, sample_schema, tmp_path):
        output_file = tmp_path / "OrganisationCreate.kt"
        generate_resource(sample_schema, "create", str(output_file))

        content = output_file.read_text()
        assert "data class OrganisationCreate(" in content
        assert "GenericCreateResource" in content
        assert "var parentOrganisationId: Long" in content

    def test_generate_update_variant(self, sample_schema, tmp_path):
        output_file = tmp_path / "OrganisationUpdate.kt"
        generate_resource(sample_schema, "update", str(output_file))

        content = output_file.read_text()
        assert "data class OrganisationUpdate(" in content
        assert "GenericUpdateResource" in content

    def test_generate_interface_variant(self, sample_schema, tmp_path):
        # Add interface fields for this variant
        schema = sample_schema.copy()
        schema["interfaceFields"] = sample_schema["fields"]

        output_file = tmp_path / "IOrganisation.kt"
        generate_resource(schema, "interface", str(output_file))

        content = output_file.read_text()
        assert "interface IOrganisation" in content
        assert "var parentOrganisationId: Long" in content
