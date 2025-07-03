import Mustache from 'mustache'

export interface KotlinServiceInterfaceProps {
    packageName: string;         // e.g., 'net.blugrid.api.organisation'
    entityFolder: string;         // e.g., 'organisation'
    entityName: string;          // e.g., 'Organisation'
}

// language=mustache
const template =
    String.raw`package {{packageName}}.service

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import net.blugrid.api.common.model.{{entityFolder}}.{{entityName}}
import net.blugrid.api.common.model.{{entityFolder}}.{{entityName}}Create
import net.blugrid.api.common.model.{{entityFolder}}.{{entityName}}Update
import java.util.Optional
import java.util.UUID

interface {{entityName}}StateService {
fun getPage(pageable: Pageable): Page<{{entityName}}>
    fun getById(id: Long): {{entityName}}
    fun getByIdOptional(id: Long): Optional<{{entityName}}>
    fun getAll(): List<{{entityName}}>
    fun getByUuid(uuid: UUID): {{entityName}}
    fun getByUuidOptional(uuid: UUID): Optional<{{entityName}}>
    fun update(id: Long, update: {{entityName}}Update): {{entityName}}
    fun create(newResource: {{entityName}}Create): {{entityName}}
    fun delete(id: Long)
}
`

export const KotlinStateServiceInterfaceTemplate = ({ packageName, entityFolder, entityName }: KotlinServiceInterfaceProps): string => {
    return Mustache.render(template, { packageName, entityName, entityFolder })
}
