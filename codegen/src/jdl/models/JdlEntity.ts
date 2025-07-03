export class JdlAnnotation {
    constructor(public name: string, public value?: string) {}

    static fromRaw(raw: any): JdlAnnotation {
        return new JdlAnnotation(
            raw.optionName,
            raw.optionValue?.value ?? raw.optionValue // handle string literals
        )
    }
}

export class JdlValidation {
    constructor(public name: string, public value?: string) {}

    static fromRaw(raw: any): JdlValidation {
        return new JdlValidation(raw.key, raw.value)
    }
}


export class JdlField {
    name: string
    type: string
    javadoc: string
    validations: JdlValidation[]
    annotations: JdlAnnotation[]

    constructor(raw: any) {
        this.name = raw.name
        this.type = raw.type
        this.javadoc = raw.javadoc
        this.validations = (raw.validations || []).map(JdlValidation.fromRaw)
        this.annotations = (raw.annotations || []).map(JdlAnnotation.fromRaw)
    }

    get isRequired(): boolean {
        return this.validations.some(
            v => v.name === 'required' && (v.value === undefined || v.value === 'true')
        )
    }

    getAnnotation(name: string): string | undefined {
        return this.annotations.find(a => a.name === name)?.value
    }

    hasAnnotation(name: string): boolean {
        return this.annotations.some(a => a.name === name)
    }
}

export interface JdlRawEntity {
    name: string
    tableName?: string
    annotations?: JdlAnnotation[]
    body?: JdlField[]
    javadoc?: string
}

export class JdlEntity {
    name: string
    javadoc?: string
    fields: JdlField[]
    annotations: JdlAnnotation[]

    constructor(raw: any) {
        this.name = raw.name
        this.javadoc = raw.javadoc
        this.fields = (raw.body || []).map((f: any) => new JdlField(f))
        this.annotations = (raw.annotations || []).map(JdlAnnotation.fromRaw)
    }

    static fromRaw(raw: any): JdlEntity {
        return new JdlEntity(raw)
    }

    hasAnnotation(name: string): boolean {
        return this.annotations.some(a => a.name === name)
    }

    getAnnotationValue(name: string): string | undefined {
        return this.annotations.find(a => a.name === name)?.value
    }
}
