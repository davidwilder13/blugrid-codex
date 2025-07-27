#!/bin/bash

# Common API Framework Migration Script
# This script migrates files from old common-api-* structure to new domain structure

set -e  # Exit on any error

echo "🚀 Starting Common API Framework Migration..."

# Base directory
BASE_DIR="./common/common-kotlin"

# Create function to safely create directories
create_dir() {
    local dir="$1"
    if [ ! -d "$dir" ]; then
        mkdir -p "$dir"
        echo "✅ Created directory: $dir"
    fi
}

# Create function to move files with package updates
move_and_update() {
    local source_file="$1"
    local dest_file="$2"
    local old_package="$3"
    local new_package="$4"

    if [ -f "$source_file" ]; then
        # Create destination directory if it doesn't exist
        create_dir "$(dirname "$dest_file")"

        # Copy file first, then update package
        cp "$source_file" "$dest_file"

        # Update package declaration
        if [ -n "$old_package" ] && [ -n "$new_package" ]; then
            sed -i.bak "s|package $old_package|package $new_package|g" "$dest_file"
            # Update imports
            sed -i.bak "s|import $old_package|import $new_package|g" "$dest_file"
            # Remove backup file
            rm -f "$dest_file.bak"
        fi

        echo "✅ Migrated: $(basename "$source_file") -> $dest_file"
    else
        echo "⚠️  Source file not found: $source_file"
    fi
}

# Function to move resources without package updates
move_resource() {
    local source_file="$1"
    local dest_file="$2"

    if [ -f "$source_file" ]; then
        create_dir "$(dirname "$dest_file")"
        cp "$source_file" "$dest_file"
        echo "✅ Moved resource: $(basename "$source_file") -> $dest_file"
    else
        echo "⚠️  Resource not found: $source_file"
    fi
}

echo "📦 Phase 1: Creating new directory structure..."

# Create all required directories
create_dir "$BASE_DIR/common/common-domain/src/main/kotlin/net/blugrid/common/domain"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/audit"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/query"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/scope"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/search"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/exception"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/controller"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/organisation"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/useridentity"
create_dir "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/util"

create_dir "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization"
create_dir "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/config"
create_dir "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/exception"
create_dir "$BASE_DIR/platform/platform-serialization/src/main/resources"
create_dir "$BASE_DIR/platform/platform-serialization/src/test/kotlin/net/blugrid/platform/serialization"

create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/annotation"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/aspect"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/event"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/mapping"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/model"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/repository"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/repository/model"
create_dir "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/service"
create_dir "$BASE_DIR/audit/audit-core/src/main/resources/db/migration/auditEventLog"

create_dir "$BASE_DIR/integration/integration-http-client/src/main/kotlin/net/blugrid/integration/http/client"
create_dir "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc"
create_dir "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/mapper"
create_dir "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/model/exception"
create_dir "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/service"
create_dir "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/util"
create_dir "$BASE_DIR/integration/integration-grpc-proto/src/main/proto/common"

create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/audit"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/config"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/mapping"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/contract"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/resource"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/repository"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/repository/migration"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/scope"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/service"
create_dir "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/util"
create_dir "$BASE_DIR/data/data-persistence/src/main/resources"

create_dir "$BASE_DIR/web/web-core/src/main/kotlin/net/blugrid/web/core"
create_dir "$BASE_DIR/web/web-core/src/main/kotlin/net/blugrid/web/core/jwt"

echo "📁 Phase 2: Migrating common-api-domain files..."

# Migrate domain files
move_and_update \
    "$BASE_DIR/common-api/common-api-domain/src/main/kotlin/net/blugrid/common/domain/CreatedTimestamp.kt" \
    "$BASE_DIR/common/common-domain/src/main/kotlin/net/blugrid/common/domain/CreatedTimestamp.kt" \
    "net.blugrid.common.domain" \
    "net.blugrid.common.domain"

move_and_update \
    "$BASE_DIR/common-api/common-api-domain/src/main/kotlin/net/blugrid/common/domain/EffectiveTimestamp.kt" \
    "$BASE_DIR/common/common-domain/src/main/kotlin/net/blugrid/common/domain/EffectiveTimestamp.kt" \
    "net.blugrid.common.domain" \
    "net.blugrid.common.domain"

move_and_update \
    "$BASE_DIR/common-api/common-api-domain/src/main/kotlin/net/blugrid/common/domain/IdentityID.kt" \
    "$BASE_DIR/common/common-domain/src/main/kotlin/net/blugrid/common/domain/IdentityID.kt" \
    "net.blugrid.common.domain" \
    "net.blugrid.common.domain"

move_and_update \
    "$BASE_DIR/common-api/common-api-domain/src/main/kotlin/net/blugrid/common/domain/IdentityUUID.kt" \
    "$BASE_DIR/common/common-domain/src/main/kotlin/net/blugrid/common/domain/IdentityUUID.kt" \
    "net.blugrid.common.domain" \
    "net.blugrid.common.domain"

move_and_update \
    "$BASE_DIR/common-api/common-api-domain/src/main/kotlin/net/blugrid/common/domain/LastChangedTimestamp.kt" \
    "$BASE_DIR/common/common-domain/src/main/kotlin/net/blugrid/common/domain/LastChangedTimestamp.kt" \
    "net.blugrid.common.domain" \
    "net.blugrid.common.domain"

echo "📋 Phase 3: Migrating common-api-model files..."

# Migrate model files - audit
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/audit/AuditEvent.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/audit/AuditEvent.kt" \
    "net.blugrid.api.common.model.audit" \
    "net.blugrid.common.model.audit"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/audit/AuditEventLog.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/audit/AuditEventLog.kt" \
    "net.blugrid.api.common.model.audit" \
    "net.blugrid.common.model.audit"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/audit/AuditEventType.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/audit/AuditEventType.kt" \
    "net.blugrid.api.common.model.audit" \
    "net.blugrid.common.model.audit"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/audit/AuditStamp.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/audit/AuditStamp.kt" \
    "net.blugrid.api.common.model.audit" \
    "net.blugrid.common.model.audit"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/audit/ResourceAudit.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/audit/ResourceAudit.kt" \
    "net.blugrid.api.common.model.audit" \
    "net.blugrid.common.model.audit"

# Migrate model files - query
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/query/DefaultPageRequest.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/query/DefaultPageRequest.kt" \
    "net.blugrid.api.common.model.query" \
    "net.blugrid.common.model.query"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/query/PageableQuery.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/query/PageableQuery.kt" \
    "net.blugrid.api.common.model.query" \
    "net.blugrid.common.model.query"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/query/PageMapping.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/query/PageMapping.kt" \
    "net.blugrid.api.common.model.query" \
    "net.blugrid.common.model.query"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/query/SortableQuery.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/query/SortableQuery.kt" \
    "net.blugrid.api.common.model.query" \
    "net.blugrid.common.model.query"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/query/SortField.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/query/SortField.kt" \
    "net.blugrid.api.common.model.query" \
    "net.blugrid.common.model.query"

# Migrate model files - resource
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/BaseAuditedResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/BaseAuditedResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/BaseBusinessUnitResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/BaseBusinessUnitResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/BaseCreateOrUpdateResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/BaseCreateOrUpdateResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/BaseCreateResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/BaseCreateResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/BaseResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/BaseResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/BaseTenantResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/BaseTenantResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/BaseUpdateResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/BaseUpdateResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/ResourceType.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/ResourceType.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/Searchable.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/Searchable.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/resource/UnscopedResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/resource/UnscopedResource.kt" \
    "net.blugrid.api.common.model.resource" \
    "net.blugrid.common.model.resource"

# Migrate model files - scope
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/scope/BusinessUnitScope.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/scope/BusinessUnitScope.kt" \
    "net.blugrid.api.common.model.scope" \
    "net.blugrid.common.model.scope"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/scope/TenantScope.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/scope/TenantScope.kt" \
    "net.blugrid.api.common.model.scope" \
    "net.blugrid.common.model.scope"

# Migrate model files - search
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/search/SearchIndex.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/search/SearchIndex.kt" \
    "net.blugrid.api.common.model.search" \
    "net.blugrid.common.model.search"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/model/search/SearchPage.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/search/SearchPage.kt" \
    "net.blugrid.api.common.model.search" \
    "net.blugrid.common.model.search"

# Migrate exception files
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/exception/APIAccessDeniedException.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/exception/APIAccessDeniedException.kt" \
    "net.blugrid.api.common.exception" \
    "net.blugrid.common.model.exception"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/exception/APIError.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/exception/APIError.kt" \
    "net.blugrid.api.common.exception" \
    "net.blugrid.common.model.exception"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/exception/APIException.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/exception/APIException.kt" \
    "net.blugrid.api.common.exception" \
    "net.blugrid.common.model.exception"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/exception/APIInternalServerException.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/exception/APIInternalServerException.kt" \
    "net.blugrid.api.common.exception" \
    "net.blugrid.common.model.exception"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/exception/NotFoundException.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/exception/NotFoundException.kt" \
    "net.blugrid.api.common.exception" \
    "net.blugrid.common.model.exception"

# Migrate controller files
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/controller/GenericCommandResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/controller/GenericCommandResource.kt" \
    "net.blugrid.api.common.controller" \
    "net.blugrid.common.model.controller"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/common/controller/GenericQueryResource.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/controller/GenericQueryResource.kt" \
    "net.blugrid.api.common.controller" \
    "net.blugrid.common.model.controller"

# Migrate organisation files
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/organisation/model/Organisation.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/organisation/Organisation.kt" \
    "net.blugrid.api.organisation.model" \
    "net.blugrid.common.model.organisation"

# Migrate user identity files
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/userIdentity/model/UserIdentity.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/model/useridentity/UserIdentity.kt" \
    "net.blugrid.api.userIdentity.model" \
    "net.blugrid.common.model.useridentity"

# Migrate util files
move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/util/CommonExtensions.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/util/CommonExtensions.kt" \
    "net.blugrid.api.util" \
    "net.blugrid.common.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/util/Either.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/util/Either.kt" \
    "net.blugrid.api.util" \
    "net.blugrid.common.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/util/EntityExtensions.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/util/EntityExtensions.kt" \
    "net.blugrid.api.util" \
    "net.blugrid.common.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/util/SwaggerContants.kt" \
    "$BASE_DIR/common/common-model/src/main/kotlin/net/blugrid/common/util/SwaggerContants.kt" \
    "net.blugrid.api.util" \
    "net.blugrid.common.util"

echo "🎨 Phase 4: Migrating common-api-json files..."

# Migrate JSON/serialization files
move_and_update \
    "$BASE_DIR/common-api/common-api-json/src/main/kotlin/net/blugrid/api/json/config/CustomObjectMapperFactory.kt" \
    "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/config/CustomObjectMapperFactory.kt" \
    "net.blugrid.api.json.config" \
    "net.blugrid.platform.serialization.config"

move_and_update \
    "$BASE_DIR/common-api/common-api-json/src/main/kotlin/net/blugrid/api/json/config/DbLocalDateTimeDeserializer.kt" \
    "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/config/DbLocalDateTimeDeserializer.kt" \
    "net.blugrid.api.json.config" \
    "net.blugrid.platform.serialization.config"

move_and_update \
    "$BASE_DIR/common-api/common-api-json/src/main/kotlin/net/blugrid/api/json/config/ObjectMapperBeanListener.kt" \
    "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/config/ObjectMapperBeanListener.kt" \
    "net.blugrid.api.json.config" \
    "net.blugrid.platform.serialization.config"

move_and_update \
    "$BASE_DIR/common-api/common-api-json/src/main/kotlin/net/blugrid/api/json/exception/JsonException.kt" \
    "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/exception/JsonException.kt" \
    "net.blugrid.api.json.exception" \
    "net.blugrid.platform.serialization.exception"

move_and_update \
    "$BASE_DIR/common-api/common-api-json/src/main/kotlin/net/blugrid/api/json/Json.kt" \
    "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/Json.kt" \
    "net.blugrid.api.json" \
    "net.blugrid.platform.serialization"

move_and_update \
    "$BASE_DIR/common-api/common-api-json/src/main/kotlin/net/blugrid/api/json/JsonPath.kt" \
    "$BASE_DIR/platform/platform-serialization/src/main/kotlin/net/blugrid/platform/serialization/JsonPath.kt" \
    "net.blugrid.api.json" \
    "net.blugrid.platform.serialization"

# Move JSON resources
move_resource \
    "$BASE_DIR/common-api/common-api-json/src/main/resources/application-json.yml" \
    "$BASE_DIR/platform/platform-serialization/src/main/resources/application-serialization.yml"

# Move JSON tests
move_and_update \
    "$BASE_DIR/common-api/common-api-json/src/test/kotlin/net/blugrid/api/json/DeserialisationTests.kt" \
    "$BASE_DIR/platform/platform-serialization/src/test/kotlin/net/blugrid/platform/serialization/DeserialisationTests.kt" \
    "net.blugrid.api.json" \
    "net.blugrid.platform.serialization"

echo "📊 Phase 5: Migrating common-api-audit files..."

# Migrate audit files
move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/annotation/LogAuditEvent.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/annotation/LogAuditEvent.kt" \
    "net.blugrid.api.audit.annotation" \
    "net.blugrid.audit.core.annotation"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/aspect/AuditInterceptor.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/aspect/AuditInterceptor.kt" \
    "net.blugrid.api.audit.aspect" \
    "net.blugrid.audit.core.aspect"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/event/UpdateAuditLogAuditEventHandler.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/event/UpdateAuditLogAuditEventHandler.kt" \
    "net.blugrid.api.audit.event" \
    "net.blugrid.audit.core.event"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/mapping/AuditEventLogMapper.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/mapping/AuditEventLogMapper.kt" \
    "net.blugrid.api.audit.mapping" \
    "net.blugrid.audit.core.mapping"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/model/AuditEventLogQuery.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/model/AuditEventLogQuery.kt" \
    "net.blugrid.api.audit.model" \
    "net.blugrid.audit.core.model"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/repository/AuditEventLogInsertRepository.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/repository/AuditEventLogInsertRepository.kt" \
    "net.blugrid.api.audit.repository" \
    "net.blugrid.audit.core.repository"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/repository/AuditEventLogReadRepository.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/repository/AuditEventLogReadRepository.kt" \
    "net.blugrid.api.audit.repository" \
    "net.blugrid.audit.core.repository"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/repository/model/AuditEventLogInsertEntity.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/repository/model/AuditEventLogInsertEntity.kt" \
    "net.blugrid.api.audit.repository.model" \
    "net.blugrid.audit.core.repository.model"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/repository/model/AuditEventLogReadEntity.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/repository/model/AuditEventLogReadEntity.kt" \
    "net.blugrid.api.audit.repository.model" \
    "net.blugrid.audit.core.repository.model"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/service/AuditEventLogService.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/service/AuditEventLogService.kt" \
    "net.blugrid.api.audit.service" \
    "net.blugrid.audit.core.service"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/service/AuditEventPublisherService.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/service/AuditEventPublisherService.kt" \
    "net.blugrid.api.audit.service" \
    "net.blugrid.audit.core.service"

move_and_update \
    "$BASE_DIR/common-api/common-api-audit/src/main/kotlin/net/blugrid/api/audit/service/GenericAuditLoader.kt" \
    "$BASE_DIR/audit/audit-core/src/main/kotlin/net/blugrid/audit/core/service/GenericAuditLoader.kt" \
    "net.blugrid.api.audit.service" \
    "net.blugrid.audit.core.service"

# Copy audit SQL migrations
if [ -d "$BASE_DIR/common-api/common-api-audit/src/main/resources/db/migration/auditEventLog" ]; then
    cp -r "$BASE_DIR/common-api/common-api-audit/src/main/resources/db/migration/auditEventLog/"* \
        "$BASE_DIR/audit/audit-core/src/main/resources/db/migration/auditEventLog/"
    echo "✅ Copied audit SQL migrations"
fi

echo "🌐 Phase 6: Migrating common-api-client files..."

# Migrate client files
move_and_update \
    "$BASE_DIR/common-api/common-api-client/src/main/kotlin/net/blugrid/api/common/organisation/HttpClient.kt" \
    "$BASE_DIR/integration/integration-http-client/src/main/kotlin/net/blugrid/integration/http/client/OrganisationHttpClient.kt" \
    "net.blugrid.api.common.organisation" \
    "net.blugrid.integration.http.client"

echo "🔗 Phase 7: Migrating common-api-grpc files..."

# Migrate gRPC files
move_and_update \
    "$BASE_DIR/common-api/common-api-grpc/src/main/kotlin/net/blugrid/common/grpc/mapper/SortProtoMapper.kt" \
    "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/mapper/SortProtoMapper.kt" \
    "net.blugrid.common.grpc.mapper" \
    "net.blugrid.integration.grpc.mapper"

move_and_update \
    "$BASE_DIR/common-api/common-api-grpc/src/main/kotlin/net/blugrid/common/grpc/model/exception/InvalidRequestException.kt" \
    "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/model/exception/InvalidRequestException.kt" \
    "net.blugrid.common.grpc.model.exception" \
    "net.blugrid.integration.grpc.model.exception"

move_and_update \
    "$BASE_DIR/common-api/common-api-grpc/src/main/kotlin/net/blugrid/common/grpc/model/exception/NotFoundException.kt" \
    "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/model/exception/NotFoundException.kt" \
    "net.blugrid.common.grpc.model.exception" \
    "net.blugrid.integration.grpc.model.exception"

move_and_update \
    "$BASE_DIR/common-api/common-api-grpc/src/main/kotlin/net/blugrid/common/grpc/service/GrpcServiceBase.kt" \
    "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/service/GrpcServiceBase.kt" \
    "net.blugrid.common.grpc.service" \
    "net.blugrid.integration.grpc.service"

move_and_update \
    "$BASE_DIR/common-api/common-api-grpc/src/main/kotlin/net/blugrid/common/grpc/util/GrpcValidator.kt" \
    "$BASE_DIR/integration/integration-grpc/src/main/kotlin/net/blugrid/integration/grpc/util/GrpcValidator.kt" \
    "net.blugrid.common.grpc.util" \
    "net.blugrid.integration.grpc.util"

# Move proto files
if [ -f "$BASE_DIR/common-api/common-api-grpc-proto/src/main/proto/common/PageRequest.proto" ]; then
    cp "$BASE_DIR/common-api/common-api-grpc-proto/src/main/proto/common/PageRequest.proto" \
        "$BASE_DIR/integration/integration-grpc-proto/src/main/proto/common/PageRequest.proto"
    echo "✅ Moved PageRequest.proto"
fi

if [ -f "$BASE_DIR/common-api/common-api-grpc-proto/src/main/proto/common/Status.proto" ]; then
    cp "$BASE_DIR/common-api/common-api-grpc-proto/src/main/proto/common/Status.proto" \
        "$BASE_DIR/integration/integration-grpc-proto/src/main/proto/common/Status.proto"
    echo "✅ Moved Status.proto"
fi

echo "💾 Phase 8: Migrating common-api-persistence files..."

# Migrate persistence files
move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/audit/AuditEmbeddable.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/audit/AuditEmbeddable.kt" \
    "net.blugrid.api.common.persistence.audit" \
    "net.blugrid.data.persistence.audit"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/config/ObjectMapperSupplier.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/config/ObjectMapperSupplier.kt" \
    "net.blugrid.api.common.config" \
    "net.blugrid.data.persistence.config"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/config/PostgreSQL10JsonDialect.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/config/PostgreSQL10JsonDialect.kt" \
    "net.blugrid.api.common.config" \
    "net.blugrid.data.persistence.config"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/mapping/AuditMapper.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/mapping/AuditMapper.kt" \
    "net.blugrid.api.common.persistence.mapping" \
    "net.blugrid.data.persistence.mapping"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/mapping/ResourceEntityMapper.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/mapping/ResourceEntityMapper.kt" \
    "net.blugrid.api.common.persistence.mapping" \
    "net.blugrid.data.persistence.mapping"

# Migrate persistence model files
move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/model/AuditablePersistable.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/AuditablePersistable.kt" \
    "net.blugrid.api.common.persistence.model" \
    "net.blugrid.data.persistence.model"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/model/contract/HasUuid.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/contract/HasUuid.kt" \
    "net.blugrid.api.common.persistence.model.contract" \
    "net.blugrid.data.persistence.model.contract"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/model/PersistableResource.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/PersistableResource.kt" \
    "net.blugrid.api.common.persistence.model" \
    "net.blugrid.data.persistence.model"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/model/resource/BusinessUnitScopedResource.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/resource/BusinessUnitScopedResource.kt" \
    "net.blugrid.api.common.persistence.model.resource" \
    "net.blugrid.data.persistence.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/model/resource/TenantScopedResource.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/resource/TenantScopedResource.kt" \
    "net.blugrid.api.common.persistence.model.resource" \
    "net.blugrid.data.persistence.model.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/model/resource/UnscopedPersistable.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/model/resource/UnscopedPersistable.kt" \
    "net.blugrid.api.common.persistence.model.resource" \
    "net.blugrid.data.persistence.model.resource"

# Migrate repository files
move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/repository/GenericEntityRepository.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/repository/GenericEntityRepository.kt" \
    "net.blugrid.api.common.persistence.repository" \
    "net.blugrid.data.persistence.repository"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/repository/JpaSpecificationDSL.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/repository/JpaSpecificationDSL.kt" \
    "net.blugrid.api.common.persistence.repository" \
    "net.blugrid.data.persistence.repository"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/repository/migration/DbMigration.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/repository/migration/DbMigration.kt" \
    "net.blugrid.api.common.persistence.repository.migration" \
    "net.blugrid.data.persistence.repository.migration"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/repository/migration/RepeatableDbMigration.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/repository/migration/RepeatableDbMigration.kt" \
    "net.blugrid.api.common.persistence.repository.migration" \
    "net.blugrid.data.persistence.repository.migration"

# Migrate scope files
move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/scope/BusinessUnitScopedEntity.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/scope/BusinessUnitScopedEntity.kt" \
    "net.blugrid.api.common.persistence.scope" \
    "net.blugrid.data.persistence.scope"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/scope/BusinessUnitScoped.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/scope/BusinessUnitScoped.kt" \
    "net.blugrid.api.common.persistence.scope" \
    "net.blugrid.data.persistence.scope"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/scope/BusinessUnitScopeEmbeddable.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/scope/BusinessUnitScopeEmbeddable.kt" \
    "net.blugrid.api.common.persistence.scope" \
    "net.blugrid.data.persistence.scope"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/scope/TenantScopedEntity.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/scope/TenantScopedEntity.kt" \
    "net.blugrid.api.common.persistence.scope" \
    "net.blugrid.data.persistence.scope"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/scope/TenantScoped.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/scope/TenantScoped.kt" \
    "net.blugrid.api.common.persistence.scope" \
    "net.blugrid.data.persistence.scope"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/scope/TenantScopeEmbeddable.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/scope/TenantScopeEmbeddable.kt" \
    "net.blugrid.api.common.persistence.scope" \
    "net.blugrid.data.persistence.scope"

# Migrate service files
move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/service/GenericCommandServiceImpl.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/service/GenericCommandServiceImpl.kt" \
    "net.blugrid.api.common.persistence.service" \
    "net.blugrid.data.persistence.service"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/service/GenericCommandService.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/service/GenericCommandService.kt" \
    "net.blugrid.api.common.persistence.service" \
    "net.blugrid.data.persistence.service"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/service/GenericQueryServiceImpl.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/service/GenericQueryServiceImpl.kt" \
    "net.blugrid.api.common.persistence.service" \
    "net.blugrid.data.persistence.service"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/service/GenericQueryService.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/service/GenericQueryService.kt" \
    "net.blugrid.api.common.persistence.service" \
    "net.blugrid.data.persistence.service"

# Migrate util files
move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/util/except.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/util/except.kt" \
    "net.blugrid.api.common.persistence.util" \
    "net.blugrid.data.persistence.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/util/hasAny.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/util/hasAny.kt" \
    "net.blugrid.api.common.persistence.util" \
    "net.blugrid.data.persistence.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/util/intersect.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/util/intersect.kt" \
    "net.blugrid.api.common.persistence.util" \
    "net.blugrid.data.persistence.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/util/merge.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/util/merge.kt" \
    "net.blugrid.api.common.persistence.util" \
    "net.blugrid.data.persistence.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/util/PostgresqlUtils.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/util/PostgresqlUtils.kt" \
    "net.blugrid.api.common.persistence.util" \
    "net.blugrid.data.persistence.util"

move_and_update \
    "$BASE_DIR/common-api/common-api-persistence/src/main/kotlin/net/blugrid/api/common/persistence/util/replaceCollection.kt" \
    "$BASE_DIR/data/data-persistence/src/main/kotlin/net/blugrid/data/persistence/util/replaceCollection.kt" \
    "net.blugrid.api.common.persistence.util" \
    "net.blugrid.data.persistence.util"

# Copy persistence resources
move_resource \
    "$BASE_DIR/common-api/common-api-persistence/src/main/resources/application-db-tracing.yml" \
    "$BASE_DIR/data/data-persistence/src/main/resources/application-db-tracing.yml"

move_resource \
    "$BASE_DIR/common-api/common-api-persistence/src/main/resources/application-db.yml" \
    "$BASE_DIR/data/data-persistence/src/main/resources/application-db.yml"

move_resource \
    "$BASE_DIR/common-api/common-api-persistence/src/main/resources/hibernate.properties" \
    "$BASE_DIR/data/data-persistence/src/main/resources/hibernate.properties"

# Copy database migrations
if [ -d "$BASE_DIR/common-api/common-api-persistence/src/main/resources/db" ]; then
    cp -r "$BASE_DIR/common-api/common-api-persistence/src/main/resources/db" \
        "$BASE_DIR/data/data-persistence/src/main/resources/"
    echo "✅ Copied database migrations"
fi

echo "🌐 Phase 9: Migrating common-api-web files..."

# Migrate web files
move_and_update \
    "$BASE_DIR/common-api/common-api-web/src/main/kotlin/net/blugrid/api/jwt/JwtUtils.kt" \
    "$BASE_DIR/web/web-core/src/main/kotlin/net/blugrid/web/core/jwt/JwtUtils.kt" \
    "net.blugrid.api.jwt" \
    "net.blugrid.web.core.jwt"

echo "🔧 Phase 10: Copy build files and documentation..."

# Copy important build files and documentation
for module in common-api-domain common-api-model common-api-json common-api-audit common-api-client common-api-grpc common-api-grpc-proto common-api-persistence common-api-web; do
    if [ -f "$BASE_DIR/common-api/$module/build.gradle.kts" ]; then
        echo "📝 Found build.gradle.kts for $module - manual review needed"
    fi
    if [ -f "$BASE_DIR/common-api/$module/README.md" ]; then
        echo "📝 Found README.md for $module - manual review needed"
    fi
done

echo "📦 Phase 11: Moving session models from common-api-model to security-core..."

# Create session directory in security-core
create_dir "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/session"

# Move session context files (these should be in security-core, not common-model)
if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/BusinessUnitSessionContext.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/BusinessUnitSessionContext.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/context/BusinessUnitSessionContext.kt" \
        "net.blugrid.api.security.context" \
        "net.blugrid.security.core.context"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/GuestSessionContext.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/GuestSessionContext.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/context/GuestSessionContext.kt" \
        "net.blugrid.api.security.context" \
        "net.blugrid.security.core.context"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/WebApplicationSessionContext.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/WebApplicationSessionContext.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/context/WebApplicationSessionContext.kt" \
        "net.blugrid.api.security.context" \
        "net.blugrid.security.core.context"
fi

# Move other security context files
if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/OperatorRef.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/OperatorRef.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/context/OperatorRef.kt" \
        "net.blugrid.api.security.context" \
        "net.blugrid.security.core.context"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/RequestContextProvider.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/RequestContextProvider.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/context/RequestContextProvider.kt" \
        "net.blugrid.api.security.context" \
        "net.blugrid.security.core.context"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/SessionContext.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/SessionContext.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/context/SessionContext.kt" \
        "net.blugrid.api.security.context" \
        "net.blugrid.security.core.context"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/WebApplicationRef.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/context/WebApplicationRef.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/context/WebApplicationRef.kt" \
        "net.blugrid.api.security.context" \
        "net.blugrid.security.core.context"
fi

# Move security model files that should be in security-core
if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/AuthenticatedOrganisation.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/AuthenticatedOrganisation.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/model/AuthenticatedOrganisation.kt" \
        "net.blugrid.api.security.model" \
        "net.blugrid.security.core.model"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/AuthenticatedUser.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/AuthenticatedUser.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/model/AuthenticatedUser.kt" \
        "net.blugrid.api.security.model" \
        "net.blugrid.security.core.model"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/AuthenticationType.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/AuthenticationType.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/model/AuthenticationType.kt" \
        "net.blugrid.api.security.model" \
        "net.blugrid.security.core.model"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/BaseAuthenticatedOrganisation.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/BaseAuthenticatedOrganisation.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/model/BaseAuthenticatedOrganisation.kt" \
        "net.blugrid.api.security.model" \
        "net.blugrid.security.core.model"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/BaseAuthenticatedSession.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/BaseAuthenticatedSession.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/model/BaseAuthenticatedSession.kt" \
        "net.blugrid.api.security.model" \
        "net.blugrid.security.core.model"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/BaseAuthenticatedUser.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/BaseAuthenticatedUser.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/model/BaseAuthenticatedUser.kt" \
        "net.blugrid.api.security.model" \
        "net.blugrid.security.core.model"
fi

if [ -f "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/DecoratedAuthentication.kt" ]; then
    move_and_update \
        "$BASE_DIR/common-api/common-api-model/src/main/kotlin/net/blugrid/api/security/model/DecoratedAuthentication.kt" \
        "$BASE_DIR/security/security-core/src/main/kotlin/net/blugrid/security/core/model/DecoratedAuthentication.kt" \
        "net.blugrid.api.security.model" \
        "net.blugrid.security.core.model"
fi

echo "🧪 Phase 12: Migrating common-api-test files..."

# Create test platform directories
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/controller"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/audit"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/base"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/identity"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/resource"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/scope"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/security"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/session"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/generator"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/security"
create_dir "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support"
create_dir "$BASE_DIR/platform/platform-testing/src/main/resources"

# Migrate test controller files
move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/controller/CommonControllerIntegTest.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/controller/CommonControllerIntegTest.kt" \
    "net.blugrid.api.test.controller" \
    "net.blugrid.platform.testing.controller"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/controller/CommonSearchControllerIntegTest.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/controller/CommonSearchControllerIntegTest.kt" \
    "net.blugrid.api.test.controller" \
    "net.blugrid.platform.testing.controller"

# Migrate test factory files
move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/audit/AuditStampFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/audit/AuditStampFactory.kt" \
    "net.blugrid.api.test.factory.audit" \
    "net.blugrid.platform.testing.factory.audit"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/audit/ResourceAuditFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/audit/ResourceAuditFactory.kt" \
    "net.blugrid.api.test.factory.audit" \
    "net.blugrid.platform.testing.factory.audit"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/base/BaseFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/base/BaseFactory.kt" \
    "net.blugrid.api.test.factory.base" \
    "net.blugrid.platform.testing.factory.base"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/base/RandomizableFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/base/RandomizableFactory.kt" \
    "net.blugrid.api.test.factory.base" \
    "net.blugrid.platform.testing.factory.base"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/base/ScenarioFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/base/ScenarioFactory.kt" \
    "net.blugrid.api.test.factory.base" \
    "net.blugrid.platform.testing.factory.base"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/GenericResourceFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/GenericResourceFactory.kt" \
    "net.blugrid.api.test.factory" \
    "net.blugrid.platform.testing.factory"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/identity/UserIdentityFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/identity/UserIdentityFactory.kt" \
    "net.blugrid.api.test.factory.identity" \
    "net.blugrid.platform.testing.factory.identity"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/RandomInstanceFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/RandomInstanceFactory.kt" \
    "net.blugrid.api.test.factory" \
    "net.blugrid.platform.testing.factory"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/resource/ExampleTenantResource.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/resource/ExampleTenantResource.kt" \
    "net.blugrid.api.test.factory.resource" \
    "net.blugrid.platform.testing.factory.resource"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/scope/TenantScopeFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/scope/TenantScopeFactory.kt" \
    "net.blugrid.api.test.factory.scope" \
    "net.blugrid.platform.testing.factory.scope"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/security/AuthenticatedOrganisationFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/security/AuthenticatedOrganisationFactory.kt" \
    "net.blugrid.api.test.factory.security" \
    "net.blugrid.platform.testing.factory.security"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/security/AuthenticatedUserFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/security/AuthenticatedUserFactory.kt" \
    "net.blugrid.api.test.factory.security" \
    "net.blugrid.platform.testing.factory.security"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/session/BusinessUnitSessionFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/session/BusinessUnitSessionFactory.kt" \
    "net.blugrid.api.test.factory.session" \
    "net.blugrid.platform.testing.factory.session"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/session/GuestSessionFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/session/GuestSessionFactory.kt" \
    "net.blugrid.api.test.factory.session" \
    "net.blugrid.platform.testing.factory.session"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/factory/session/TenantSessionFactory.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/factory/session/TenantSessionFactory.kt" \
    "net.blugrid.api.test.factory.session" \
    "net.blugrid.platform.testing.factory.session"

# Migrate test generator files
move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/generator/DataGenerationExtensions.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/generator/DataGenerationExtensions.kt" \
    "net.blugrid.api.test.generator" \
    "net.blugrid.platform.testing.generator"

# Migrate test security files
move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/security/TestApplicationContext.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/security/TestApplicationContext.kt" \
    "net.blugrid.api.test.security" \
    "net.blugrid.platform.testing.security"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/security/TestAuthFilter.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/security/TestAuthFilter.kt" \
    "net.blugrid.api.test.security" \
    "net.blugrid.platform.testing.security"

# Migrate test support files
move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/BaseControllerIntegTest.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/BaseControllerIntegTest.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/BaseGrpcClientIntegTest.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/BaseGrpcClientIntegTest.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/BaseGrpcIntegTest.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/BaseGrpcIntegTest.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/BaseIntegTest.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/BaseIntegTest.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/BaseServiceIntegTest.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/BaseServiceIntegTest.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/ConsulTestSupport.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/ConsulTestSupport.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/DockerHostIpSupport.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/DockerHostIpSupport.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/EcrDockerLoginHelper.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/EcrDockerLoginHelper.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/GrpcClientTestSupport.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/GrpcClientTestSupport.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/GrpcServerTestSupport.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/GrpcServerTestSupport.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/GrpcTestDsl.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/GrpcTestDsl.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/PostgresTestSupport.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/PostgresTestSupport.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/TestcontainersBootstrap.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/TestcontainersBootstrap.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

move_and_update \
    "$BASE_DIR/common-api/common-api-test/src/main/kotlin/net/blugrid/api/test/support/TestNetwork.kt" \
    "$BASE_DIR/platform/platform-testing/src/main/kotlin/net/blugrid/platform/testing/support/TestNetwork.kt" \
    "net.blugrid.api.test.support" \
    "net.blugrid.platform.testing.support"

# Move test resources
move_resource \
    "$BASE_DIR/common-api/common-api-test/src/main/resources/application-test.yml" \
    "$BASE_DIR/platform/platform-testing/src/main/resources/application-test.yml"

# Migrate test files in test directory
if [ -f "$BASE_DIR/common-api/common-api-test/src/test/kotlin/net/blugrid/api/factory/RandomInstanceFactoryTest.kt" ]; then
    create_dir "$BASE_DIR/platform/platform-testing/src/test/kotlin/net/blugrid/platform/testing/factory"
    move_and_update \
        "$BASE_DIR/common-api/common-api-test/src/test/kotlin/net/blugrid/api/factory/RandomInstanceFactoryTest.kt" \
        "$BASE_DIR/platform/platform-testing/src/test/kotlin/net/blugrid/platform/testing/factory/RandomInstanceFactoryTest.kt" \
        "net.blugrid.api.factory" \
        "net.blugrid.platform.testing.factory"
fi

echo "🧹 Phase 13: Cleanup and validation..."

# Function to update imports across all migrated files
update_imports() {
    echo "🔄 Updating imports across all migrated files..."

    # Define import mappings
    declare -A import_mappings=(
        ["net.blugrid.api.common.model"]="net.blugrid.common.model"
        ["net.blugrid.api.common.exception"]="net.blugrid.common.model.exception"
        ["net.blugrid.api.common.controller"]="net.blugrid.common.model.controller"
        ["net.blugrid.api.organisation.model"]="net.blugrid.common.model.organisation"
        ["net.blugrid.api.userIdentity.model"]="net.blugrid.common.model.useridentity"
        ["net.blugrid.api.util"]="net.blugrid.common.util"
        ["net.blugrid.api.json"]="net.blugrid.platform.serialization"
        ["net.blugrid.api.audit"]="net.blugrid.audit.core"
        ["net.blugrid.api.common.organisation"]="net.blugrid.integration.http.client"
        ["net.blugrid.common.grpc"]="net.blugrid.integration.grpc"
        ["net.blugrid.api.common.persistence"]="net.blugrid.data.persistence"
        ["net.blugrid.api.jwt"]="net.blugrid.web.core.jwt"
        ["net.blugrid.api.test"]="net.blugrid.platform.testing"
        ["net.blugrid.api.security.context"]="net.blugrid.security.core.context"
        ["net.blugrid.api.security.model"]="net.blugrid.security.core.model"
        ["net.blugrid.common.domain"]="net.blugrid.common.domain"
    )

    # Update imports in all new files
    for old_import in "${!import_mappings[@]}"; do
        new_import="${import_mappings[$old_import]}"
        echo "  Updating imports: $old_import -> $new_import"

        # Find all Kotlin files in the new structure and update imports
        find "$BASE_DIR" \( -path "*/common-api" -prune \) -o -name "*.kt" -type f -exec \
            sed -i.bak "s|import $old_import|import $new_import|g" {} \; 2>/dev/null || true

        # Clean up backup files
        find "$BASE_DIR" \( -path "*/common-api" -prune \) -o -name "*.kt.bak" -type f -delete 2>/dev/null || true
    done
}

update_imports

echo "📋 Phase 14: Creating build.gradle.kts files for new modules..."

# Function to create basic build.gradle.kts files
create_build_file() {
    local module_path="$1"
    local dependencies="$2"

    if [ ! -f "$module_path/build.gradle.kts" ]; then
        cat > "$module_path/build.gradle.kts" << EOF
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}

dependencies {
$dependencies

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies
    implementation(libs.bundles.kotlinCore)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Test dependencies
    testImplementation(libs.bundles.testing)
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.*")
    }
}
EOF
        echo "✅ Created build.gradle.kts for $module_path"
    fi
}

# Create build files for new modules
create_build_file "$BASE_DIR/common/common-domain" \
    "    // Domain value objects - minimal dependencies"

create_build_file "$BASE_DIR/common/common-model" \
    "    // API dependencies - expose to consumers
    api(project(\":common:common-kotlin:common:common-domain\"))"

create_build_file "$BASE_DIR/audit/audit-core" \
    "    // API dependencies
    api(project(\":common:common-kotlin:common:common-model\"))
    api(project(\":common:common-kotlin:platform:platform-logging\"))

    // Micronaut dependencies
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautData)"

create_build_file "$BASE_DIR/integration/integration-http-client" \
    "    // API dependencies
    api(project(\":common:common-kotlin:common:common-model\"))

    // HTTP client dependencies
    implementation(libs.bundles.micronautCore)
    implementation(libs.micronaut.http.client)"

create_build_file "$BASE_DIR/integration/integration-grpc" \
    "    // API dependencies
    api(project(\":common:common-kotlin:common:common-model\"))
    api(project(\":common:common-kotlin:integration:integration-grpc-proto\"))

    // gRPC dependencies
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.bundles.micronautCore)"

create_build_file "$BASE_DIR/integration/integration-grpc-proto" \
    "    // Proto dependencies
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)"

create_build_file "$BASE_DIR/web/web-core" \
    "    // API dependencies
    api(project(\":common:common-kotlin:security:security-tokens\"))

    // Web dependencies
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautWeb)"

echo "📊 Phase 15: Summary and next steps..."

echo ""
echo "✅ Migration completed successfully!"
echo ""
echo "📁 Files migrated to:"
echo "   • common/common-domain/        - Domain value objects"
echo "   • common/common-model/         - Shared models and DTOs"
echo "   • platform/platform-serialization/ - JSON handling"
echo "   • audit/audit-core/            - Audit functionality"
echo "   • integration/integration-*/   - HTTP and gRPC clients"
echo "   • data/data-persistence/       - JPA and persistence"
echo "   • web/web-core/                - Web utilities"
echo "   • security/security-core/      - Security models moved from common-model"
echo "   • platform/platform-testing/  - Test utilities"
echo ""
echo "🔧 What was updated:"
echo "   • Package declarations updated in all files"
echo "   • Import statements updated across files"
echo "   • Basic build.gradle.kts files created"
echo "   • Resources and SQL migrations copied"
echo ""
echo "⚠️  Manual review needed:"
echo "   • Build dependencies in new modules"
echo "   • Application.yml configurations"
echo "   • Test compilation and execution"
echo "   • Legacy common-api-* module removal"
echo ""
echo "🗑️  Ready to delete (after verification):"
echo "   • $BASE_DIR/common-api/common-api-domain/"
echo "   • $BASE_DIR/common-api/common-api-model/"
echo "   • $BASE_DIR/common-api/common-api-json/"
echo "   • $BASE_DIR/common-api/common-api-audit/"
echo "   • $BASE_DIR/common-api/common-api-client/"
echo "   • $BASE_DIR/common-api/common-api-grpc/"
echo "   • $BASE_DIR/common-api/common-api-grpc-proto/"
echo "   • $BASE_DIR/common-api/common-api-persistence/"
echo "   • $BASE_DIR/common-api/common-api-web/"
echo "   • $BASE_DIR/common-api/common-api-test/"
echo ""
echo "🚀 Next steps:"
echo "   1. Test compilation: ./gradlew build"
echo "   2. Run tests: ./gradlew test"
echo "   3. Update any remaining import statements"
echo "   4. Review and adjust build.gradle.kts dependencies"
echo "   5. Remove old common-api-* modules once verified"
echo ""
echo "🎉 Your domain-driven structure is ready for team onboarding!"
