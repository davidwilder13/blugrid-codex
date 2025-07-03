import Mustache from 'mustache'
import { CodegenEntityModel } from '../../../../../model/CodegenEntityModel.js'
import { toMustacheList } from '../../../../../utils/to-mustache-list.js'

export interface KotlinMappingExtensionsTemplateProps {
    packageName: string;
    modelPackage: string;
    repositoryPackage: string;
    entityName: string;
    createFields: { name: string }[];
    updateFields: { name: string }[];
    resourceFields: { name: string }[];
}

const template = String.raw`package {{packageName}}

import net.blugrid.api.common.mapper.toAudit
import {{modelPackage}}.{{entityName}}
import {{modelPackage}}.{{entityName}}Create
import {{modelPackage}}.{{entityName}}Update
import {{repositoryPackage}}.{{entityName}}Entity

fun {{entityName}}.toCreate(): {{entityName}}Create =
    {{entityName}}Create(
        uuid = this.uuid,
{{#createFields}}
        {{name}} = this.{{name}}{{^last}},{{/last}}
{{/createFields}}
    )

fun {{entityName}}.toUpdate(): {{entityName}}Update =
    {{entityName}}Update(
        id = this.id,
        uuid = this.uuid,
{{#updateFields}}
        {{name}} = this.{{name}}{{^last}},{{/last}}
{{/updateFields}}
    )

fun {{entityName}}Create.toEntity(): {{entityName}}Entity =
    {{entityName}}Entity(
        uuid = this.uuid,
{{#createFields}}
        {{name}} = this.{{name}}{{^last}},{{/last}}
{{/createFields}}
    )

fun {{entityName}}Update.toEntity(): {{entityName}}Entity =
    {{entityName}}Entity(
        uuid = this.uuid,
{{#updateFields}}
        {{name}} = this.{{name}}{{^last}},{{/last}}
{{/updateFields}}
    )

fun {{entityName}}Entity.toResource(): {{entityName}} =
    {{entityName}}(
        id = this.id!!,
        uuid = this.uuid,
{{#resourceFields}}
        {{name}} = this.{{name}}{{^last}}{{#isAuditable}},{{/isAuditable}}{{^isAuditable}},{{/isAuditable}}{{/last}}{{#last}}{{#isAuditable}},{{/isAuditable}}{{/last}}
{{/resourceFields}}{{#isAuditable}}
        audit = this.audit.toAudit()
{{/isAuditable}}
    )
`

export const KotlinMappingExtensionsTemplate = (entity: CodegenEntityModel): string => {
    const context: KotlinMappingExtensionsTemplateProps = {
        ...entity,
        packageName: `${entity.packageName}.mapping`,
        modelPackage: `${entity.packageName}.model`,
        repositoryPackage: `${entity.packageName}.repository.model`,
        entityName: entity.name,
        createFields: toMustacheList(
            entity.fields
                .filter(f => f.name !== 'id')  // exclude 'id' from create
                .map(f => ({ name: f.name })),
        ),
        updateFields: toMustacheList(
            entity.fields.map(f => ({ name: f.name })),
        ),
        resourceFields: toMustacheList(
            entity.fields
                .filter(f => f.name !== 'audit')  // audit handled separately
                .map(f => ({ name: f.name })),
        ),
    }

    return Mustache.render(template, context)
}
