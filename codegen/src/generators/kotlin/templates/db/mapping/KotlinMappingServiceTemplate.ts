import Mustache from 'mustache'

export interface KotlinMappingServiceTemplateProps {
    packageName: string
    entityName: string
}

const template = String.raw`package {{packageName}}.mapping

import jakarta.inject.Singleton
import net.blugrid.api.common.repository.model.GenericEntityMapper
import {{packageName}}.model.{{entityName}}
import {{packageName}}.model.{{entityName}}Create
import {{packageName}}.model.{{entityName}}Update
import {{packageName}}.repository.model.{{entityName}}Entity

@Singleton
class {{entityName}}MappingService : GenericEntityMapper<{{entityName}}, {{entityName}}Create, {{entityName}}Update, {{entityName}}Entity>() {
    override fun createToEntity(source: {{entityName}}Create): {{entityName}}Entity = source.toEntity()
    override fun updateToEntity(source: {{entityName}}Update): {{entityName}}Entity = source.toEntity()
    override fun entityToResource(source: {{entityName}}Entity): {{entityName}} = source.toResource()
    override fun resourceToCreate(source: {{entityName}}): {{entityName}}Create = source.toCreate()
    override fun resourceToUpdate(source: {{entityName}}): {{entityName}}Update = source.toUpdate()
}
`

export const KotlinMappingServiceTemplate = (props: KotlinMappingServiceTemplateProps): string => {
    return Mustache.render(template.trim(), props)
}
