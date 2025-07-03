import { camelCase, lowerCase } from 'lodash-es'
import Mustache from 'mustache'

export interface KotlinGenericCrudRepositoryProps {
    packageName: string;         // e.g., 'net.blugrid.api.organisation'
    entityName: string;          // e.g., 'Organisation'
}

// language=mustache
const template = String.raw`package {{packageName}}.repository

import io.micronaut.context.annotation.Executable
import io.micronaut.data.annotation.Repository
import net.blugrid.api.common.repository.GenericCrudRepository
import {{packageName}}.repository.model.{{entityName}}Entity

@Repository
interface {{entityName}}Repository : GenericCrudRepository<{{entityName}}Entity> {
    @Executable
    override fun update(update: {{entityName}}Entity): {{entityName}}Entity
}
`

export const KotlinGenericCrudRepositoryTemplate = ({ packageName, entityName }: KotlinGenericCrudRepositoryProps): string => {
    return Mustache.render(template, { packageName, folder: camelCase(entityName), entityName })
}
