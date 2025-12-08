# utils/templates.py
from pathlib import Path
from jinja2 import Environment, FileSystemLoader, select_autoescape


def get_template_dir() -> Path:
    """Get the templates directory path."""
    return Path(__file__).parent.parent / "templates"


def create_jinja_env(template_dir: Path | None = None) -> Environment:
    """Create Jinja2 environment with standard settings."""
    if template_dir is None:
        template_dir = get_template_dir()

    return Environment(
        loader=FileSystemLoader(str(template_dir)),
        autoescape=select_autoescape(disabled_extensions=["j2"]),
        trim_blocks=True,
        lstrip_blocks=True,
        keep_trailing_newline=True,
    )


def render_template(template_path: str, context: dict) -> str:
    """Render a Jinja2 template with given context."""
    path = Path(template_path)
    env = create_jinja_env(path.parent)
    template = env.get_template(path.name)
    return template.render(**context)


def render_template_string(template_string: str, context: dict) -> str:
    """Render a Jinja2 template string with given context."""
    from jinja2 import Template
    template = Template(template_string)
    return template.render(**context)
