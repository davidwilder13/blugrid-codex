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
