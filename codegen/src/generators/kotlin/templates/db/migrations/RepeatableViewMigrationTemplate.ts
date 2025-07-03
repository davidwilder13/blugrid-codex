import Mustache from 'mustache';

export interface CreateRepeatableViewMigrationProps {
    packageName: string;
    nameLower: string;
    createOrReplaceSql: string;
}

// language=kotlin
const repeatableMigrationTemplate =
    String.raw`package {{packageName}}.repository.migration

import net.blugrid.api.db.migration.RepeatableDbMigration
import org.flywaydb.core.api.migration.Context

class R__vw_{{nameLower}} : RepeatableDbMigration() {
    override fun migrate(context: Context) {
        runMigration(context, """
{{{createOrReplaceSql}}}
        """.trimIndent())
    }
}
`;

export const RepeatableViewMigrationTemplate = ({
                                                          packageName,
                                                          nameLower,
                                                          createOrReplaceSql,
                                                      }: CreateRepeatableViewMigrationProps): string => {
    return Mustache.render(repeatableMigrationTemplate, {
        packageName,
        nameLower,
        createOrReplaceSql,
    });
};
