import Mustache from 'mustache';

export interface CreateVersionedMigrationProps {
    packageName: string;
    version: string;         // e.g. "001"
    migrationName: string;   // e.g. "create_organisation_table"
    nameLower: string;       // e.g. "organisation"
    tableFunction: string;   // e.g. "toUnScopedTableSQL", can be passed explicitly
}

// language=kotlin
const versionedMigrationTemplate = String.raw`package {{packageName}}.repository.migration

import net.blugrid.api.db.mapping.{{tableFunction}}
import net.blugrid.api.db.migration.DbMigration
import org.flywaydb.core.api.migration.Context

class V{{version}}__{{migrationName}} : DbMigration() {

    override fun migrate(context: Context) {
        runMigration(context, {{nameLower}}TableDefinition.{{tableFunction}}())
    }
}
`;

export const VersionedMigrationTemplate = ({
                                                     packageName,
                                                     version,
                                                     migrationName,
                                                     nameLower,
                                                     tableFunction,
                                                 }: CreateVersionedMigrationProps): string => {
    return Mustache.render(versionedMigrationTemplate, {
        packageName,
        version,
        migrationName,
        nameLower,
        tableFunction,
    });
};
