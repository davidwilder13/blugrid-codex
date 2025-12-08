#!/usr/bin/env python3
# orchestrators/__init__.py
"""
Orchestrator Skills

Entry points that Claude invokes to generate complete modules.
"""

from .generate_module import generate_module

__all__ = [
    "generate_module",
]
