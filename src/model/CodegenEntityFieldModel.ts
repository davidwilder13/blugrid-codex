export class CodegenEntityFieldModel {
    constructor(
        public name: string,
        public kotlinType: string,
        public resourceType: string,
        public dbColumnName: string,
        public dbDataType: string,
        public required: boolean,
        public nullable: boolean,
        public description?: string,
        public example?: string,
    ) {}

    // Example helper method
    get kotlinDeclaration(): string {
        return `${this.name}: ${this.kotlinType}${this.nullable ? '?' : ''}`;
    }

    get dbDeclaration(): string {
        return `${this.dbColumnName} ${this.dbDataType}${this.nullable ? '' : ' NOT NULL'}`;
    }
}
