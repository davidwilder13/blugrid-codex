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
