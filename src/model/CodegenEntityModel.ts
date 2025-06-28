import { CodegenEntityFieldModel } from './CodegenEntityFieldModel.js'

export class CodegenEntityModel {
    constructor(
        public name: string,
        public packageName: string,
        public tableName: string,
        public fields: CodegenEntityFieldModel[],
        public importStatements: string[],
        public resourceType: 'UnscopedResource' | 'TenantResource' | 'BusinessUnitResource',
        public isAuditable: boolean,
        public isSearchable: boolean,
    ) {}
}
