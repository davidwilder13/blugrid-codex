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
