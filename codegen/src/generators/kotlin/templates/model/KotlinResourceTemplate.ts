import Mustache from 'mustache'
import { MustacheListItem, MustacheListMeta } from '../../../../utils/index.js'

export type KotlinResourceTemplateVariant = 'model' | 'create' | 'update' | 'interface';

export interface KotlinResourceTemplateProps {
    variant: KotlinResourceTemplateVariant;
    packageName: string;
    name: string;
    nameLower: string;
    nameUpperSnake: string;
    fields: MustacheListItem<{
        name: string;
        type: string;
        required: boolean;
        description?: string;
        example?: string;
    }>[];
    interfaceFields?: MustacheListItem<{
        name: string;
        type: string;
        required: boolean;
    }>[];
    imports?: MustacheListItem<string>[];
}

const templates: Record<KotlinResourceTemplateVariant, string> = {
    model: String.raw`package {{packageName}}

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.Audit
import net.blugrid.api.common.model.resource.UnscopedResource
import net.blugrid.api.common.model.resource.ResourceType
import java.util.UUID
{{#imports}}
import {{value}}
{{/imports}}

@Schema(description = "Represents a {{nameLower}} within the system.")
data class {{name}}(

    override var id: Long,

    override var uuid: UUID,

{{#fields}}
    @Schema(description = "{{description}}", example = "{{example}}")
    var {{name}}: {{type}}{{^required}}? = null{{/required}},

{{/fields}}
    override val audit: Audit? = null
) : UnscopedResource<{{name}}>(audit) {

    override val resourceType: ResourceType
        get() = ResourceType.{{nameUpperSnake}}
}
`,

    create: String.raw`package {{packageName}}

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.GenericCreateResource
import java.util.UUID
{{#imports}}
import {{value}}
{{/imports}}

@Schema(description = "Model used to create a new {{nameLower}}.")
data class {{name}}Create(

    @Schema(description = "The globally unique identifier for this {{nameLower}}.", example = "123e4567-e89b-12d3-a456-426614174000")
    override var uuid: UUID,

{{#fields}}
    @Schema(description = "{{description}}", example = "{{example}}")
    var {{name}}: {{type}}{{^required}}? = null{{/required}},

{{/fields}}
) : GenericCreateResource<{{name}}Create>(uuid)
`,

    update: String.raw`package {{packageName}}

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.api.common.model.resource.GenericUpdateResource
import java.util.UUID
{{#imports}}
import {{value}}
{{/imports}}

@Schema(description = "Model used to update an existing {{nameLower}}.")
data class {{name}}Update(

    override var id: Long,

    override var uuid: UUID,

{{#fields}}
    @Schema(description = "{{description}}", example = "{{example}}")
    var {{name}}: {{type}}{{^required}}? = null{{/required}},

{{/fields}}
) : GenericUpdateResource<{{name}}Update>(id, uuid)
`,

    interface: String.raw`package {{packageName}}

import io.swagger.v3.oas.annotations.media.Schema
{{#imports}}
import {{value}}
{{/imports}}

@Schema(description = "Base {{nameLower}} interface representing a {{nameLower}} within the system.")
interface I{{name}} {
{{#interfaceFields}}
    var {{name}}: {{type}}{{^required}}?{{/required}}
{{/interfaceFields}}
}
`,
}

export const KotlinResourceTemplate = (props: KotlinResourceTemplateProps): string => {
    const { variant, ...context } = props
    const template = templates[variant]
    return Mustache.render(template, context)
}
