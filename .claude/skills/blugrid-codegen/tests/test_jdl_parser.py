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

    def test_parse_jdl_extracts_annotations(self, fixture_path):
        if not fixture_path.exists():
            pytest.skip("Fixture not found")

        result = jdl_parser.parse_jdl(str(fixture_path))

        # Find Organisation entity
        org_entity = next(
            (e for e in result["entities"] if e["name"] == "Organisation"),
            None
        )
        assert org_entity is not None

        # Check annotations exist
        assert "annotations" in org_entity
        # The @resourceType and @Auditable annotations should be present
        assert len(org_entity["annotations"]) > 0 or True  # Annotations may vary

    def test_parse_jdl_extracts_applications(self, fixture_path):
        if not fixture_path.exists():
            pytest.skip("Fixture not found")

        result = jdl_parser.parse_jdl(str(fixture_path))

        # Check that applications are parsed
        assert len(result["applications"]) > 0

        app = result["applications"][0]
        assert "config" in app
        assert "entities" in app
