import { CodegenEntityFieldModel } from './CodegenEntityFieldModel.js'

export class CodegenEntityModel {
    constructor(
        public name: string,
        public packageName: string,
        public tableName: string,
        public fields: CodegenEntityFieldModel[],
        public resourceType: 'UnscopedResource' | 'TenantResource' | 'BusinessUnitResource',
        public isAuditable: boolean,
        public isSearchable: boolean,
    ) {}

    // Helper method example
    get importStatements(): string[] {
        const imports = new Set<string>();
        this.fields.forEach(field => {
            if (field.resourceType === 'UUID') {
                imports.add('java.util.UUID');
            } else if (field.resourceType === 'LocalDateTime') {
                imports.add('java.time.LocalDateTime');
            }
        });
        return Array.from(imports);
    }
}
