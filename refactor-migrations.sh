#!/bin/bash

set -euo pipefail

UTIL_DIR="./common/common-kotlin/common-api/common-api-persistence/src/main/resources/db/migration/util"

cd "$UTIL_DIR"

echo "🔧 Normalizing utility migration filenames in: $UTIL_DIR"

for file in *; do
  [[ -f "$file" ]] || continue

  base=$(basename "$file" .sql)

  # Extract number and name from ANY pattern (R__, R--, R_, R-, R, etc)
  if [[ "$base" =~ ^R[-_]{0,2}?([0-9]+)[-_](.+)$ ]]; then
    num="${BASH_REMATCH[1]}"
    desc="${BASH_REMATCH[2]}"
  elif [[ "$base" =~ ^([0-9]+)[-_](.+)$ ]]; then
    num="${BASH_REMATCH[1]}"
    desc="${BASH_REMATCH[2]}"
  else
    echo "⚠️  Skipping unrecognized format: $file"
    continue
  fi

  # Normalize to snake_case
  snake_case=$(echo "$desc" | tr '[:upper:]' '[:lower:]' | tr '-' '_' | tr -s '_')
  new_name="R__${num}__${snake_case}.sql"

  if [[ "$file" != "$new_name" ]]; then
    echo "🔄 $file → $new_name"
    mv "$file" "$new_name"
  fi
done

echo "✅ Migration utility filenames normalized."
