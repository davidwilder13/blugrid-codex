import Mustache from 'mustache'

export interface KotlinStateServiceDbImplProps {
    packageName: string;         // e.g., 'net.blugrid.api.organisation'
    entityName: string;          // e.g., 'Organisation'
    entityFolder?: string;       // e.g., 'organisation' (optional, used for model imports)
}

// language=mustache
const template = String.raw`package {{packageName}}.service

import jakarta.inject.Singleton
import net.blugrid.api.common.service.GenericCrudServiceImpl
import {{packageName}}.mapping.{{entityName}}MappingService
import {{packageName}}.model.{{entityName}}
import {{packageName}}.model.{{entityName}}Create
import {{packageName}}.model.{{entityName}}Update
import {{packageName}}.repository.{{entityName}}Repository
import {{packageName}}.repository.model.{{entityName}}Entity

@Singleton
open class {{entityName}}StateServiceDbImpl(
private val repository: {{entityName}}Repository,
private val mapper: {{entityName}}MappingService
) : GenericCrudServiceImpl<{{entityName}}, {{entityName}}Create, {{entityName}}Update, {{entityName}}Entity, {{entityName}}MappingService>(repository, mapper),
{{entityName}}StateService
`

export const KotlinKotlinStateServiceDbImplTemplate = ({ packageName, entityName }: KotlinStateServiceDbImplProps): string => {
    return Mustache.render(template, { packageName, entityName })
}
